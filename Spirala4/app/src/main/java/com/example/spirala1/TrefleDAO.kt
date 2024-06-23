package com.example.spirala1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import java.io.IOException
import java.net.MalformedURLException
import android.content.Context
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.regex.Pattern
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString


class TrefleDAO{
    private val apiKey = BuildConfig.API_KEY
    private val retrofit = Retrofit.Builder().baseUrl("http://trefle.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val trefleApi = retrofit.create(TrefleAPI::class.java)
    private lateinit var context: Context
    private lateinit var defaultSlika: Bitmap

    private val json = Json { ignoreUnknownKeys = true }

    fun setContext (contextMain: Context){
        context=contextMain
        defaultSlika =  BitmapFactory.decodeResource(context.resources, R.drawable.biljka)
    }

   suspend fun fixData(biljka: Biljka):Biljka{
        return withContext(Dispatchers.IO) {
            var returnbiljka = biljka
            val ime = biljka.naziv
            val pattern = Pattern.compile("\\((.*?)\\)")
            val matcher = pattern.matcher(ime)
            var latinName = ime
            if (matcher.find()) {
                latinName = matcher.group(1)?.toString() ?: ""
            }
            val query = latinName.replace(" ","%20")
            val url1 = "http://trefle.io/api/v1/plants?token=$apiKey&filter[scientific_name]=$query"
            try {
                val connection1 = URL(url1).openConnection() as HttpURLConnection
                val inputStream1: InputStream = BufferedInputStream(connection1.inputStream)
                val response1 = inputStream1.bufferedReader().use { it.readText() }
                val jsonResponse1 = JSONObject(response1)
                if (jsonResponse1.has("data") && jsonResponse1.getJSONArray("data").length() > 0) {
                    val plantsArray = jsonResponse1.getJSONArray("data")
                    val plantData = plantsArray.getJSONObject(0)
                    val id = plantData.optInt("id", -1).toString()
                    val url2 = "http://trefle.io/api/v1/plants/$id?token=$apiKey"
                    try {
                        val connection2 = URL(url2).openConnection() as HttpURLConnection
                        val inputStream2: InputStream = BufferedInputStream(connection2.inputStream)
                        val response2 = inputStream2.bufferedReader().use { it.readText() }
                        val jsonResponse2 = JSONObject(response2)
                        if (jsonResponse2.has("data")) {
                            val plantObject = jsonResponse2.getJSONObject("data")
                            val plantFamily = plantObject.getJSONObject("family")
                            val family = plantFamily.optString("name", "")
                            val plantMainSpecies = plantObject.getJSONObject("main_species")
                            val edible = plantMainSpecies.optBoolean("edible", true)
                            val plantSpecifications = plantMainSpecies.getJSONObject("specifications")
                            val toxicity = plantSpecifications.optString("toxicity","null")
                            val plantGrowth = plantMainSpecies.getJSONObject("growth")
                            val soil_texture = plantGrowth.optInt("soil_texture",-1)
                            val light = plantGrowth.optInt("light",-1)
                            val atmospheric_humidity = plantGrowth.optInt("atmospheric_humidity",-1)

                            var returnUpozorenje = biljka.medicinskoUpozorenje
                            var returnJela = biljka.jela
                            if (!edible){
                                returnJela = emptyList()
                                returnUpozorenje+= "NIJE JESTIVO"
                            }

                            if (toxicity != "none" && toxicity != "null") {
                                if (!(returnUpozorenje?.contains("TOKSIČNO"))!!) {
                                    returnUpozorenje += " TOKSIČNO"
                                }

                            }
                            val tipKlima = mutableListOf<KlimatskiTip>()
                            if (light in 6..9 && atmospheric_humidity in 1..5){
                                tipKlima.add(KlimatskiTip.SREDOZEMNA)
                            }
                            if (light in 8..10 && atmospheric_humidity in 7..10){
                                tipKlima.add(KlimatskiTip.TROPSKA)
                            }
                            if (light in 6..9 && atmospheric_humidity in 5..8){
                                tipKlima.add(KlimatskiTip.SUBTROPSKA)
                            }
                            if (light in 4..7 && atmospheric_humidity in 3..7){
                                tipKlima.add(KlimatskiTip.UMJERENA)
                            }
                            if (light in 7..9 && atmospheric_humidity in 1..2){
                                tipKlima.add(KlimatskiTip.SUHA)
                            }
                            if (light in 0..5 && atmospheric_humidity in 3..7){
                                tipKlima.add(KlimatskiTip.PLANINSKA)
                            }

                            var returnKlima = biljka.klimatskiTipovi
                            if (tipKlima.size>0){
                                returnKlima=tipKlima.toList()
                            }

                            val tipZemljista : Zemljiste?=
                                    when (soil_texture){
                                        9 -> Zemljiste.SLJUNOVITO
                                        10 -> Zemljiste.KRECNJACKO
                                        1, 2 -> Zemljiste.GLINENO
                                        3, 4 -> Zemljiste.PJESKOVITO
                                        5, 6 -> Zemljiste.ILOVACA
                                        7, 8 -> Zemljiste.CRNICA
                                        else -> null
                                }

                            var returnZemljista = biljka.zemljisniTipovi
                            if (tipZemljista!=null){
                                returnZemljista = listOf<Zemljiste>(tipZemljista)
                            }

                            return@withContext Biljka(
                                naziv = biljka.naziv,
                                porodica = family,
                                medicinskoUpozorenje = returnUpozorenje,
                                medicinskeKoristi =biljka.medicinskeKoristi,
                                profilOkusa =biljka.profilOkusa,
                                jela = returnJela,
                                klimatskiTipovi = returnKlima,
                                zemljisniTipovi = returnZemljista,
                                onlineChecked = true
                            )

                        }


                    } catch (e: Exception) {
                        return@withContext returnbiljka
                    }
                }
            } catch (e: Exception) {
                val message = e.message
                return@withContext returnbiljka
           }
            return@withContext returnbiljka
        }
   }


    suspend fun getPlantsWithFlowerColor(flower_color: String, substr: String)
            : List<Biljka> {
        return withContext(Dispatchers.IO) {
            var biljke = mutableListOf<Biljka>()
            try {
                for (page in 1..10) {
                    val url = "http://trefle.io/api/v1/plants?filter[flower_color]=$flower_color&token=$apiKey&page=$page"
                    val connection = URL(url).openConnection() as HttpURLConnection
                    connection.inputStream.use { inputStream ->
                        val response = inputStream.bufferedReader().use { it.readText() }

                        val plantResponse: PlantResponse = json.decodeFromString(response)
                        for (plantData in plantResponse.data) {
                            val name = plantData.common_name ?: ""
                            val latinName = plantData.scientific_name ?: ""
                            if ((name.contains(substr, ignoreCase = true) || latinName.contains(substr, ignoreCase = true) && name.isNotEmpty())
                            ) {
                                val naziv ="$name ($latinName)"
                                val family = plantData.family ?: ""

                                val foundBiljka = Biljka(
                                    naziv = naziv,
                                    porodica = family,
                                    medicinskoUpozorenje = "",
                                    medicinskeKoristi = emptyList(),
                                    profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
                                    jela = emptyList(),
                                    klimatskiTipovi = emptyList(),
                                    zemljisniTipovi = emptyList()
                                )
                                biljke.add(foundBiljka)
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext biljke
        }
    }


    suspend fun getImage(biljka: Biljka): Bitmap {
        return withContext(Dispatchers.IO) {
            val ime = biljka.naziv
            val pattern = Pattern.compile("\\((.*?)\\)")
            val matcher = pattern.matcher(ime)
            var naziv = ime
            if (matcher.find()) {
                naziv = matcher.group(1)?.toString() ?: ""
            }
            return@withContext try {
                val response = trefleApi.searchPlants(naziv, apiKey).execute()
                if (response.isSuccessful) {
                    val plants = response.body()?.data
                    val imageUrl = plants?.firstOrNull()?.image_url
                    if (!imageUrl.isNullOrEmpty()) {
                        val url = URL(imageUrl)
                        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        val input = connection.inputStream
                        BitmapFactory.decodeStream(input)
                    } else {
                        defaultSlika
                    }
                } else {
                            defaultSlika
                }
            } catch (e: Exception) {
                defaultSlika
            }
        }
    }

}




