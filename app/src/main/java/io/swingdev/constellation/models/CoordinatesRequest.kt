package io.swingdev.constellation.models

data class CoordinatesRequest(
    val messages: List<String>,
    val channelId: String
)