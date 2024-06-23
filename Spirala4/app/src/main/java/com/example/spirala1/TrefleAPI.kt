package com.example.spirala1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.serialization.Serializable

interface TrefleAPI {
    @GET("plants/search")
    fun searchPlants(
        @Query("q") query: String,
        @Query("token") apiKey: String
    ): Call<TrefleResponse>
}

data class TrefleResponse(
    val data: List<PlantData>
)

data class PlantData(
    val image_url: String?,
)

@Serializable
data class PlantResponse(
    val data: List<PlantDataPretraga>
)

@Serializable
data class PlantDataPretraga(
    val common_name: String? = null,
    val scientific_name: String? = null,
    val family: String? = null
)