package br.senai.sp.jandira.safdupla.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactory {


    private val URL_BASE = "http://10.107.132.4:3000"

    private val retrofitFactory = Retrofit
        .Builder()
        .baseUrl(URL_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun cadastroService(): ApiService {
        return retrofitFactory.create(ApiService::class.java)
    }


}