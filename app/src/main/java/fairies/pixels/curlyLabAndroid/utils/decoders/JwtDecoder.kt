package fairies.pixels.curlyLabAndroid.utils.decoders

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JwtDecoder {
    fun decodeUserId(jwtToken: String): String? {
        return try {
            val parts = jwtToken.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> "$payload=="
                3 -> "$payload="
                else -> payload
            }

            val jsonBytes = Base64.decode(paddedPayload, Base64.URL_SAFE or Base64.NO_WRAP)
            val jsonString = String(jsonBytes, Charsets.UTF_8)

            val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
            jsonObject["sub"]?.jsonPrimitive?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}