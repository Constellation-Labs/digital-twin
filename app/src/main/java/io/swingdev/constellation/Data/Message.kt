package io.swingdev.constellation.data

data class Message(
    val publicKey: String,
    val lat: String,
    val lon: String,
    val signature: String
) {
    override fun toString(): String {
        return "{publicKey: $publicKey, " +
                "lat: $lat, " +
                "lon: $lon, " +
                "signature: $signature}"

    }
}