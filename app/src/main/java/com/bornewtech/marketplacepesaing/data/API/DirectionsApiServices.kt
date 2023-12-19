package com.bornewtech.marketplacepesaing.data.API

import com.bornewtech.marketplacepesaing.data.response.DirectionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiServices {
    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionResponse>
}