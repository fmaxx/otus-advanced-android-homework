package ru.otus.module.one.data.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ChuckNorrisService {
    @GET("/jokes/categories")
    suspend fun categories(): ArrayList<String>

    @GET("/jokes/random/")
    suspend fun random(@Query (value = "category") category: String): ChuckNorrisJoke

    companion object {

        val instance: ChuckNorrisService by lazy {
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.chucknorris.io")
                .build()
                .create(ChuckNorrisService::class.java)
        }
    }
}
