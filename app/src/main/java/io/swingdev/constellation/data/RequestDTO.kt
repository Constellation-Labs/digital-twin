package io.swingdev.constellation.data

data class RequestDTO(
    val publicKey: String,
    val privateKey: String,
    val endpointUrl: String,
    val channelId: String
)