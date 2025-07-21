package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationToMillisSerializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToMillisSerializer());
        mapper.registerModule(module);
    }

    @Test
    public void testSerializeDurationToMillis() throws Exception {
        Duration duration = Duration.ofMillis(5000);
        String result = mapper.writeValueAsString(duration);

        assertEquals("5000", result);
    }

    @Test
    public void testSerializeNullDuration() throws Exception {
        Duration duration = null;
        String result = mapper.writeValueAsString(duration);

        assertEquals("null", result);
    }

    @Test
    public void testSerializeZeroDuration() throws Exception {
        Duration duration = Duration.ZERO;
        String result = mapper.writeValueAsString(duration);

        assertEquals("0", result);
    }

    @Test
    public void testSerializeLargeDuration() throws Exception {
        Duration duration = Duration.ofDays(365);
        String result = mapper.writeValueAsString(duration);

        assertEquals("31536000000", result); // 365 days * 24 hours/day * 60 minutes/hour * 60 seconds/minute * 1000 ms/second
    }

    @Test
    public void testSerializeNegativeDuration() throws Exception {
        Duration duration = Duration.ofMillis(-5000);
        String result = mapper.writeValueAsString(duration);

        assertEquals("-5000", result);
    }
}
