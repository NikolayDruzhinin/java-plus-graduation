package ru.practicum.stats.collector.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserActionSerializer implements Serializer<UserActionAvro> {
    @Override
    public byte[] serialize(String s, UserActionAvro userActionAvro) {
        if (userActionAvro == null) {
            return null;
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(stream, null);
            DatumWriter<UserActionAvro> writer = new SpecificDatumWriter<>(UserActionAvro.class);

            writer.write(userActionAvro, encoder);
            encoder.flush();

            return stream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации UserAction", e);
        }
    }
}
