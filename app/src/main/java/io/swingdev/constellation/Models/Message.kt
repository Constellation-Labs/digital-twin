package io.swingdev.constellation.Models

data class Message(
    val publicKey: String,
    val lat: String,
    val lon: String,
    val signature: String) {
}