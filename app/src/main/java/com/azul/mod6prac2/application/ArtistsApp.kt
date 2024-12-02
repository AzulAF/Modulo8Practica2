package com.azul.mod6prac2.application

import android.app.Application
import com.azul.mod6prac2.data.ArtistRepository
import com.azul.mod6prac2.data.remote.RetrofitHelper

class ArtistsApp: Application() {

    private val retrofit by lazy{
        RetrofitHelper().getRetrofit()
    }

    val repository by lazy {
        ArtistRepository(retrofit)
    }

}