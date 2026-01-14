package ru.practicum.stats.analyzer.kafka;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    final DatumReader<T> reader;
    final DecoderFactory decoder;

    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        reader = new SpecificDatumReader<>(schema);
        decoder = decoderFactory;
    }
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder d = decoder.binaryDecoder(data, null);
                return this.reader.read(null, d);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации");
        }
    }
}
