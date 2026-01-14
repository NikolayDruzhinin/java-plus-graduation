package ru.practicum.stats.aggregator.kafka;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SimilaritySerializer implements Serializer<EventSimilarityAvro> {
    @Override
    public byte[] serialize(String s, EventSimilarityAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(stream, null);
            DatumWriter<EventSimilarityAvro> writer = new SpecificDatumWriter<>(EventSimilarityAvro.class);

            writer.write(event, encoder);
            encoder.flush();

            return stream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Ошибка сериализации EventSimilarity", e);
        }
    }
}
