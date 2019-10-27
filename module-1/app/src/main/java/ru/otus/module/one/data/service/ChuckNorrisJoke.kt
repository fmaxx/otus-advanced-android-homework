package ru.otus.module.one.data.service

import com.google.gson.annotations.SerializedName

data class ChuckNorrisJoke(
    @SerializedName("icon_url")
    val iconUrl: String,
    val value: String

)