package ru.practicum.stat.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CustomLocalDateTimeDeserializer() {
        this(null);
    }

    public CustomLocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            String value = p.getText().trim();
            return LocalDateTime.parse(value, FORMATTER);
        }
        throw ctxt.wrongTokenException(p, handledType(), JsonToken.VALUE_STRING,
                "Expected string with format 'yyyy-MM-dd HH:mm:ss'");
    }
}
