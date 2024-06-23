package com.example.spirala1

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    companion object {
        lateinit var db: SupportSQLiteDatabase
        lateinit var context: Context
        lateinit var roomDb: BiljkaDatabase
        lateinit var biljkaDAO: BiljkaDAO

        @BeforeClass
        @JvmStatic
        fun createDB() = runBlocking {
            context = ApplicationProvider.getApplicationContext()
            roomDb = Room.inMemoryDatabaseBuilder(context, BiljkaDatabase::class.java).build()
            biljkaDAO = roomDb.biljkaDao()
            db = roomDb.openHelper.readableDatabase
        }
    }

    @get:Rule
    val intentsTestRule = ActivityScenarioRule(MainActivity::class.java)

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test1_addBiljkaAndVerify() = runBlocking {
        val biljka = Biljka(
            id = 1,
            naziv = "Test Biljka",
            porodica = "Test Family",
            medicinskoUpozorenje = "Ništa",
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Test Jela"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
            zemljisniTipovi = listOf(Zemljiste.CRNICA),
            medicinskeKoristi = listOf(MedicinskaKorist.PROTUUPALNO)
        )
        biljkaDAO.saveBiljka(biljka)
        val biljke = biljkaDAO.getAllBiljkas()

        assertThat(biljke.size, equalTo(1))
        assertThat(biljke[0], equalTo(biljka))
    }

    @Test
    fun test2_clearDataAndVerify() = runBlocking {
        biljkaDAO.clearData()
        val biljke = biljkaDAO.getAllBiljkas()

        assertThat(biljke.isEmpty(), equalTo(true))
    }
}
