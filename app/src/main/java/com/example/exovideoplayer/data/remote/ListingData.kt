package com.example.exovideoplayer.data.remote

class ListingData(
    val dataList: List<ListResponseDtoItem>,
    val after: String?,
    val before: String?,
    val start: String?,
    val limit: String?
)