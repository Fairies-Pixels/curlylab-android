package fairies.pixels.curlyLabAndroid.utils.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeSerializerTest {

    private val json = Json

    @Serializable
    data class TestDto(
        @Serializable(with = OffsetDateTimeSerializer::class)
        val date: OffsetDateTime
    )

    @Test
    fun `serialize should convert OffsetDateTime to ISO string`() {
        val date = OffsetDateTime.of(
            2024, 5, 10,
            14, 30, 15, 0,
            ZoneOffset.ofHours(3)
        )

        val dto = TestDto(date)
        val result = json.encodeToString(dto)

        assertEquals(
            """{"date":"2024-05-10T14:30:15+03:00"}""",
            result
        )
    }

    @Test
    fun `deserialize should convert ISO string to OffsetDateTime`() {
        val jsonString = """{"date":"2024-05-10T14:30:15+03:00"}"""

        val dto = json.decodeFromString<TestDto>(jsonString)

        assertEquals(
            OffsetDateTime.of(
                2024, 5, 10,
                14, 30, 15, 0,
                ZoneOffset.ofHours(3)
            ),
            dto.date
        )
    }

    @Test
    fun `serialize and deserialize should return same value`() {
        val original = OffsetDateTime.now().withNano(0)
        val dto = TestDto(original)

        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<TestDto>(encoded)

        assertEquals(original, decoded.date)
    }

    @Test
    fun `serializer should handle negative offset`() {
        val date = OffsetDateTime.of(
            2024, 5, 10,
            10, 0, 0, 0,
            ZoneOffset.ofHours(-5)
        )

        val dto = TestDto(date)
        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<TestDto>(encoded)

        assertEquals(date, decoded.date)
    }

    @Test(expected = Exception::class)
    fun `deserialize should throw exception for invalid string`() {
        val jsonString = """{"date":"invalid-date"}"""
        json.decodeFromString<TestDto>(jsonString)
    }

    @Test
    fun `descriptor should be primitive string`() {
        val descriptor = OffsetDateTimeSerializer.descriptor

        assertEquals("OffsetDateTime", descriptor.serialName)
        assertEquals(
            kotlinx.serialization.descriptors.PrimitiveKind.STRING,
            descriptor.kind
        )
    }
}