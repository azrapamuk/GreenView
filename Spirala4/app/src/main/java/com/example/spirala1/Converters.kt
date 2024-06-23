package com.example.spirala1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    private val MAX_SIZE = 400
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height

        val aspectRatio = width.toFloat() / height.toFloat()
        val targetWidth = if (aspectRatio > 1) MAX_SIZE else (MAX_SIZE * aspectRatio).toInt()
        val targetHeight = if (aspectRatio > 1) (MAX_SIZE / aspectRatio).toInt() else MAX_SIZE

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    @TypeConverter
    fun toBitmap(encodedString: String): Bitmap {
        val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
/*
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }*/



    @TypeConverter
    fun fromMedicinskaKoristList(value: List<MedicinskaKorist>?): String? {
        return value?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toMedicinskaKoristList(value: String?): List<MedicinskaKorist>? {
        return value?.split(",")?.mapNotNull {
            try {
                MedicinskaKorist.valueOf(it)
            } catch (ex: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromKlimatskiTipList(value: List<KlimatskiTip>?): String? {
        return value?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toKlimatskiTipList(value: String?): List<KlimatskiTip>? {
        return value?.split(",")?.mapNotNull {
            try {
                KlimatskiTip.valueOf(it)
            } catch (ex: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun fromZemljisteList(value: List<Zemljiste>?): String? {
        return value?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toZemljisteList(value: String?): List<Zemljiste>? {
        return value?.split(",")?.mapNotNull {
            try {
                Zemljiste.valueOf(it)
            } catch (ex: IllegalArgumentException) {
                null
            }
        }
    }

}