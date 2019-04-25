package io.swingdev.constellation.models

data class Request(
    val messages: Array<String>,
    val channelId: String
)