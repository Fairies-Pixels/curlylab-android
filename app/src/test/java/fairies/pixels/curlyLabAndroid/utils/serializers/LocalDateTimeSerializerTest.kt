package fairies.pixels.curlyLabAndroid.utils.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class LocalDateTimeSerializerTest {

    private val json = Json

    @Serializable
    data class TestDto(
        @Serializable(with = LocalDateTimeSerializer::class)
        val date: LocalDateTime
    )

    @Test
    fun `serialize should convert LocalDateTime to string`() {
        val date = LocalDateTime.of(2024, 5, 10, 14, 30, 15)
        val dto = TestDto(date)

        val result = json.encodeToString(dto)

        assertEquals("""{"date":"2024-05-10T14:30:15"}""", result)
    }

    @Test
    fun `deserialize should convert string to LocalDateTime`() {
        val jsonString = """{"date":"2024-05-10T14:30:15"}"""

        val dto = json.decodeFromString<TestDto>(jsonString)

        assertEquals(LocalDateTime.of(2024, 5, 10, 14, 30, 15), dto.date)
    }

    @Test
    fun `serialize and deserialize should return same value`() {
        val original = LocalDateTime.now().withNano(0)
        val dto = TestDto(original)

        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<TestDto>(encoded)

        assertEquals(original, decoded.date)
    }

    @Test(expected = Exception::class)
    fun `deserialize should throw exception for invalid date`() {
        val jsonString = """{"date":"invalid-date"}"""
        json.decodeFromString<TestDto>(jsonString)
    }

    @Test
    fun `descriptor should be primitive string`() {
        val descriptor = LocalDateTimeSerializer.descriptor

        assertEquals("LocalDateTime", descriptor.serialName)
        assertEquals(
            kotlinx.serialization.descriptors.PrimitiveKind.STRING,
            descriptor.kind
        )
    }
}