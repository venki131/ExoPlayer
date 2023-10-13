package com.example.exovideoplayer.data.remote


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Posts(
    @SerializedName("body")
    @Expose
    val body: String, // quia et suscipitsuscipit recusandae consequuntur expedita et cumreprehenderit molestiae ut ut quas totamnostrum rerum est autem sunt rem eveniet architecto
    @SerializedName("id")
    @Expose
    val id: Int, // 1
    @SerializedName("title")
    @Expose
    val title: String, // sunt aut facere repellat provident occaecati excepturi optio reprehenderit
    @SerializedName("userId")
    @Expose
    val userId: Int // 1
)