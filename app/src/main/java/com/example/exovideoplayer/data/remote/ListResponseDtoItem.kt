package com.example.exovideoplayer.data.remote


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ListResponseDtoItem(
    @SerializedName("albumId")
    @Expose
    val albumId: Int, // 1
    @SerializedName("id")
    @Expose
    val id: Int, // 1
    @SerializedName("thumbnailUrl")
    @Expose
    val thumbnailUrl: String, // https://via.placeholder.com/150/92c952
    @SerializedName("title")
    @Expose
    val title: String, // accusamus beatae ad facilis cum similique qui sunt
    @SerializedName("url")
    @Expose
    val url: String // https://via.placeholder.com/600/92c952
)