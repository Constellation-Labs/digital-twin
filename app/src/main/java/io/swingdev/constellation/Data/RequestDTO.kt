package io.swingdev.constellation.Data

data class RequestDTO(
    val publicKey: String,
    val privateKey: String,
    val endpointUrl: String,
    val channelId: String
) {

}