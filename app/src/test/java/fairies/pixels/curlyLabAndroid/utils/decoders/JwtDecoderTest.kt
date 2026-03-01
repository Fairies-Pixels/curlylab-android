package fairies.pixels.curlyLabAndroid.utils.decoders

import android.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.nio.charset.StandardCharsets

@RunWith(RobolectricTestRunner::class)
class JwtDecoderTest {

    private fun createToken(payload: Map<String, String>): String {
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"

        val payloadJson = payload.entries.joinToString(
            separator = ",",
            prefix = "{",
            postfix = "}"
        ) { "\"${it.key}\":\"${it.value}\"" }

        val payloadBase64 = Base64.encodeToString(
            payloadJson.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )

        return "$header.$payloadBase64.signature"
    }

    @Test
    fun `декодирование возвращает userId, когда токен валидный`() {
        val token = createToken(mapOf("sub" to "user123"))

        val result = JwtDecoder.decodeUserId(token)

        assertEquals("user123", result)
    }

    @Test
    fun `декодирование возвращает userId при наличии других полей в payload`() {
        val token = createToken(
            mapOf(
                "sub" to "user123",
                "name" to "Lina",
                "email" to "lina@gmail.com"
            )
        )

        val result = JwtDecoder.decodeUserId(token)

        assertEquals("user123", result)
    }

    @Test
    fun `декодирование возвращает null, когда поле sub отсутствует`() {
        val token = createToken(mapOf("email" to "test@test.com"))

        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда токен имеет некорректное количество частей`() {
        val token = "header.payload"

        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда токен пустой`() {
        val result = JwtDecoder.decodeUserId("")

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда токен невалидный`() {
        val token = "not.a.valid.token"

        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда payload не в base64`() {
        val token = "header.invalid-payload.signature"

        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда payload не является JSON`() {
        val invalidJson = Base64.encodeToString(
            "not a json".toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP
        )
        val token = "header.$invalidJson.signature"

        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }

    @Test
    fun `декодирование возвращает null, когда sub пустой`() {
        val token = createToken(mapOf("sub" to ""))

        val result = JwtDecoder.decodeUserId(token)

        assertEquals("", result)
    }

    @Test
    fun `декодирование возвращает null, когда sub содержит пробелы`() {
        val token = createToken(mapOf("sub" to "user 123"))

        val result = JwtDecoder.decodeUserId(token)

        assertEquals("user 123", result)
    }

    @Test
    fun `декодирование корректно обрабатывает payload с паддингом 2 символа`() {
        val token = createToken(mapOf("sub" to "aaaa"))
        val result = JwtDecoder.decodeUserId(token)

        assertEquals("aaaa", result)
    }

    @Test
    fun `декодирование корректно обрабатывает payload с паддингом 1 символ`() {
        val token = createToken(mapOf("sub" to "1"))
        val result = JwtDecoder.decodeUserId(token)

        assertEquals("1", result)
    }

    @Test
    fun `декодирование возвращает null при исключении`() {
        val token = "header.payload.signature"
        val result = JwtDecoder.decodeUserId(token)

        assertNull(result)
    }
}