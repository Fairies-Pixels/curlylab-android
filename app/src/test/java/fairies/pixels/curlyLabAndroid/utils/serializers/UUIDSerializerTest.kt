package fairies.pixels.curlyLabAndroid.utils.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class UUIDSerializerTest {

    private val json = Json

    @Serializable
    data class TestDto(
        @Serializable(with = UUIDSerializer::class)
        val id: UUID
    )

    @Test
    fun `serialize should convert UUID to string`() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        val dto = TestDto(uuid)

        val result = json.encodeToString(dto)

        assertEquals(
            """{"id":"123e4567-e89b-12d3-a456-426614174000"}""",
            result
        )
    }

    @Test
    fun `deserialize should convert string to UUID`() {
        val jsonString = """{"id":"123e4567-e89b-12d3-a456-426614174000"}"""

        val dto = json.decodeFromString<TestDto>(jsonString)

        assertEquals(
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            dto.id
        )
    }

    @Test
    fun `serialize and deserialize should return same UUID`() {
        val original = UUID.randomUUID()
        val dto = TestDto(original)

        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<TestDto>(encoded)

        assertEquals(original, decoded.id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `deserialize should throw exception for invalid UUID`() {
        val jsonString = """{"id":"invalid-uuid"}"""
        json.decodeFromString<TestDto>(jsonString)
    }

    @Test
    fun `descriptor should be primitive string`() {
        val descriptor = UUIDSerializer.descriptor

        assertEquals("UUID", descriptor.serialName)
        assertEquals(
            kotlinx.serialization.descriptors.PrimitiveKind.STRING,
            descriptor.kind
        )
    }
}