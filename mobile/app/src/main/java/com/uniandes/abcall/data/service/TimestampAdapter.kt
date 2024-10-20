package com.uniandes.abcall.data.service

import com.google.gson.*
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimestampAdapter : JsonDeserializer<Timestamp>, JsonSerializer<Timestamp> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Timestamp {
        return try {
            val date = dateFormat.parse(json.asString)
            Timestamp(date?.time ?: 0)
        } catch (e: ParseException) {
            throw JsonParseException(e)
        }
    }

    override fun serialize(src: Timestamp, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(dateFormat.format(Date(src.time)))
    }
}
