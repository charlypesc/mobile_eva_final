package com.example.evafinal.ws

import retrofit2.http.GET

interface DolarService {
    //https://mindicador.cl/api
    @GET("/api")
    suspend fun buscar():ResponseData
}