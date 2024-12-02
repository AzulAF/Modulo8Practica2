package com.azul.mod6prac2.data


import com.azul.mod6prac2.data.remote.ArtistsApi
import com.azul.mod6prac2.data.remote.model.ArtistDetailDto
import com.azul.mod6prac2.data.remote.model.ArtistDto
import retrofit2.Call
import retrofit2.Retrofit

class ArtistRepository (
    private val retrofit: Retrofit
) {

    private val gamesApi: ArtistsApi = retrofit.create(ArtistsApi::class.java)


    //Para Apiary
    fun getArtistsApiary(): Call<MutableList<ArtistDto>> = gamesApi.getArtistsApiary()

    fun getArtistDetailApiary(id: String?): Call<ArtistDetailDto> =
        gamesApi.getArtistDetailApiary(id)
}