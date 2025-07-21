package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

/**
 * A custom serializer for converting {@link Duration} objects to their minute representation.
 * If the {@link Duration} is null, it writes a JSON null value.
 */
public class DurationToMinutesSerializer extends StdSerializer<Duration> {

    public DurationToMinutesSerializer() {
        super(Duration.class);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.toMinutes());
        }
    }
}
