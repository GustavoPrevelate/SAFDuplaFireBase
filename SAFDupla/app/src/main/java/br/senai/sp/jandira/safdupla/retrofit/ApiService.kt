package br.senai.sp.jandira.safdupla.retrofit

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/usuario/cadastrarUsuario")
    fun createUser(@Body body: JsonObject): Call<ApiResponse>
}