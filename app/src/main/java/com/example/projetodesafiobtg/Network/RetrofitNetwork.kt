package com.example.projetodesafiobtg.Network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetwork {
    companion object {
        fun getRetrofit(path: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}