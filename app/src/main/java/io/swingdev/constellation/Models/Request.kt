package io.swingdev.constellation.Models

data class Request(
    val messages: Array<Message>,
    val channelId: String
)