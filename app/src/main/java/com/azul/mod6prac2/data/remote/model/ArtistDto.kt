package com.azul.mod6prac2.data.remote.model

import com.google.gson.annotations.SerializedName

data class ArtistDto(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("piso")
    var piso: String? = null,

    @SerializedName("mesa")
    var mesa: String? = null,

    @SerializedName("nombre")
    var nombre: String? = null,

    @SerializedName("sellos")
    var sellos: String? = null,

    @SerializedName("pagoefectivo")
    var pagoefectivo: String? = null,

    @SerializedName("pagotarjeta")
    var pagotarjeta: String? = null,

    @SerializedName("transferencia")
    var transferecia: String? = null,

    @SerializedName("imagen")
    var imagen: String? = null,

    @SerializedName("url_video")
    var url_video: String? = null




)
