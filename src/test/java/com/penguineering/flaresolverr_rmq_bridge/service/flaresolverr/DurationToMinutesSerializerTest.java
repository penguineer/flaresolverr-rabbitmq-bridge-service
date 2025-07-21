package com.penguineering.flaresolverr_rmq_bridge.service.flaresolverr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationToMinutesSerializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToMinutesSerializer());
        mapper.registerModule(module);
    }

    @Test
    public void testSerializeDurationToMinutes() throws Exception {
        Duration duration = Duration.ofMinutes(5);
        String result = mapper.writeValueAsString(duration);

        assertEquals("5", result);
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

        assertEquals("525600", result); // 365 days * 1440 minutes/day
    }

    @Test
    public void testSerializeNegativeDuration() throws Exception {
        Duration duration = Duration.ofMinutes(-5);
        String result = mapper.writeValueAsString(duration);

        assertEquals("-5", result);
    }
}
