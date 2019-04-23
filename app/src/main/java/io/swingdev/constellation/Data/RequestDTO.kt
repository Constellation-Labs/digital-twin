package io.swingdev.constellation.Data

data class RequestDTO(
    val publicKey: String,
    val privateKey: String,
    val endpoitUrl: String,
    val channelId: String
) {

}