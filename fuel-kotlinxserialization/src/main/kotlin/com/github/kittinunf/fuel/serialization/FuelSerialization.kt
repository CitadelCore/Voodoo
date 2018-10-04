package com.github.kittinunf.fuel.serialization

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Handler
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.result.Result
import kotlinx.io.InputStream
import kotlinx.io.Reader
import kotlinx.serialization.KSerialLoader
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import java.nio.charset.Charset

val defaultJSON = JSON.plain

inline fun <reified T : Any> Request.responseObject(noinline handler: (Request, Response, Result<T, FuelError>) -> Unit, loader: KSerialLoader<T> = T::class.serializer(), json: JSON = defaultJSON) {
    response(kotlinxDeserializerOf(loader, json), handler)
}

inline fun <reified T : Any> Request.responseObject(handler: Handler<T>, loader: KSerialLoader<T> = T::class.serializer(), json: JSON = defaultJSON) = response(kotlinxDeserializerOf(loader, json), handler)

inline fun <reified T : Any> Request.responseObject(loader: KSerialLoader<T> = T::class.serializer(), json: JSON = defaultJSON) = response(kotlinxDeserializerOf<T>(loader, json))

inline fun <reified T : Any> kotlinxDeserializerOf(loader: KSerialLoader<T> = T::class.serializer(), json: JSON = defaultJSON) = object : ResponseDeserializable<T> {
    override fun deserialize(reader: Reader): T? {
        return deserialize(reader.readText())
    }

    override fun deserialize(content: String): T? {
        return json.parse(loader, content)
    }

    override fun deserialize(bytes: ByteArray): T? {
        return deserialize(String(bytes))
    }

    override fun deserialize(inputStream: InputStream): T? {
        inputStream.bufferedReader().use {
            return deserialize(it)
        }
    }
}

fun Request.jsonBody(body: String, charset: Charset = Charsets.UTF_8): Request {
    headers["Content-Type"] = "application/json"
    return body(body, charset)
}