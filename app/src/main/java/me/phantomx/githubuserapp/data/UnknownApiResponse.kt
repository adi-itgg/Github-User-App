package me.phantomx.githubuserapp.data

import com.google.gson.annotations.SerializedName

data class UnknownApiResponse(
    @SerializedName("message")
    val message: String
)
