package me.phantomx.githubuserapp.data


import com.google.gson.annotations.SerializedName

data class SearchData(
    @SerializedName("items")
    val shortUsers: List<ShortUser> = listOf()
)