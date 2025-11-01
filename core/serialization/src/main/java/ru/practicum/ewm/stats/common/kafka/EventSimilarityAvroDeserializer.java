package ru.practicum.ewm.stats.common.kafka;

import java.io.IOException;
import java.util.Map;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.ewm.stats.kafka.EventSimilarityAvro;

public class EventSimilarityAvroDeserializer implements Deserializer<EventSimilarityAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public EventSimilarityAvro deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            DatumReader<EventSimilarityAvro> reader = new SpecificDatumReader<>(EventSimilarityAvro.class);
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing Avro message", e);
        }
    }

    @Override
    public void close() {
        // No cleanup needed
    }
}

