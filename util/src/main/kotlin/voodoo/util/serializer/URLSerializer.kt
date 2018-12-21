package voodoo.util.serializer

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import java.net.URL

@Serializer(forClass = URL::class)
object URLSerializer: KSerializer<URL> {
    override fun deserialize(decoder: Decoder): URL {
        return URL(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, obj: URL) {
        encoder.encodeString(obj.toExternalForm())
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}