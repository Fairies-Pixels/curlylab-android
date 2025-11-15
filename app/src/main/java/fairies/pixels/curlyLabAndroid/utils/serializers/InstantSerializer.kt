package fairies.pixels.curlyLabAndroid.utils.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.format.DateTimeFormatter

@Serializer(forClass = Instant::class)
object InstantSerializer : KSerializer<Instant> {
    private val formatter = DateTimeFormatter.ISO_INSTANT

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", kotlinx.serialization.descriptors.PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(formatter.format(value))
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}