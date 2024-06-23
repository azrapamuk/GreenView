package com.example.spirala1

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
@TypeConverters(Converters::class)
interface BiljkaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiljka(biljka: Biljka): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiljkaBitmap(biljkaBitmap: BiljkaBitmap): Long

    @Query("SELECT * FROM biljka WHERE onlineChecked = 0")
    suspend fun getOfflineBiljke(): List<Biljka>

    @Query("SELECT * FROM biljka WHERE id = :idBiljke")
    suspend fun getBiljkaById(idBiljke: Long): Biljka?

    @Query("SELECT * FROM BiljkaBitmap WHERE idBiljke = :idBiljke")
    suspend fun getBiljkaBitmapById(idBiljke: Long): BiljkaBitmap?

    @Update
    suspend fun updateBiljka(biljka: Biljka)

    @Query("SELECT bitmap FROM BiljkaBitmap WHERE idBiljke = :idBiljke")
    suspend fun getBitmapById(idBiljke: Long): String?

    suspend fun getBitmap(id: Long): Bitmap? {
        val bitmapString=getBitmapById(id)
        val bitmapBiljka= bitmapString?.let { Converters().toBitmap(it) };
        return bitmapBiljka
    }

    suspend fun saveBiljka(biljka: Biljka): Boolean = withContext(Dispatchers.IO) {
        try {
            insertBiljka(biljka) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fixOfflineBiljka(): Int = withContext(Dispatchers.IO) {
        val offlineBiljke = getOfflineBiljke()

        var azurirano = 0

        for (biljkaEntity in offlineBiljke) {
            if (biljkaEntity.onlineChecked == false) {

                val originalBiljka = biljkaEntity
                var trefleDAO = TrefleDAO()
                val fixedBiljka = trefleDAO.fixData(originalBiljka)

                if (originalBiljka != fixedBiljka) {
                    val updatedBiljkaEntity = biljkaEntity.copy(
                        porodica = fixedBiljka.porodica,
                        medicinskoUpozorenje = fixedBiljka.medicinskoUpozorenje,
                        medicinskeKoristi = fixedBiljka.medicinskeKoristi,
                        profilOkusa = fixedBiljka.profilOkusa,
                        jela = fixedBiljka.jela,
                        klimatskiTipovi = fixedBiljka.klimatskiTipovi,
                        zemljisniTipovi = fixedBiljka.zemljisniTipovi,
                        onlineChecked = true
                    )
                    updateBiljka(updatedBiljkaEntity)
                    azurirano++
                }
            }
        }
        azurirano
    }

    suspend fun addImage(idBiljke: Long, bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
        try {
            val biljka = getBiljkaById(idBiljke)
            val existingImage = getBiljkaBitmapById(idBiljke)

            if (biljka != null && existingImage == null) {
                val biljkaBitmapEntity = BiljkaBitmap(id = null, idBiljke = idBiljke, bitmap = Converters().fromBitmap(bitmap))
                insertBiljkaBitmap(biljkaBitmapEntity) > 0
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Query("SELECT * FROM biljka")
    suspend fun getAllBiljkas(): List<Biljka>

    @Query("DELETE FROM biljka")
    suspend fun deleteAllBiljkas()

    @Query("DELETE FROM BiljkaBitmap")
    suspend fun deleteAllBiljkaBitmaps()

    suspend fun clearData() = withContext(Dispatchers.IO) {
        try {
            deleteAllBiljkas()
            deleteAllBiljkaBitmaps()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}