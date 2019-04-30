package io.swingdev.constellation.models

data class Response(
    val errorMessage: String,
    val messageHashes: List<String>
)