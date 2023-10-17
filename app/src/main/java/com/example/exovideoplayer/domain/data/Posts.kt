package com.example.exovideoplayer.domain.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Posts(
    val body: String, // quia et suscipitsuscipit recusandae consequuntur expedita et cumreprehenderit molestiae ut ut quas totamnostrum rerum est autem sunt rem eveniet architecto
    val id: Int, // 1
    val title: String, // sunt aut facere repellat provident occaecati excepturi optio reprehenderit
    val userId: Int // 1
) : Parcelable