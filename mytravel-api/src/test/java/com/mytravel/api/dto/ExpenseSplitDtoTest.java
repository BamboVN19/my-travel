package com.mytravel.api.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseSplitDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserializeWithIsSettled() throws Exception {
        String json = "{\"userId\":\"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\",\"splitAmount\":300000.0,\"isSettled\":true}";
        ExpenseSplitDto dto = objectMapper.readValue(json, ExpenseSplitDto.class);

        assertNotNull(dto);
        assertEquals(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"), dto.getUserId());
        assertEquals(new BigDecimal("300000.0"), dto.getSplitAmount());
        assertTrue(dto.isSettled());
    }

    @Test
    void testDeserializeWithSettledAlias() throws Exception {
        String json = "{\"userId\":\"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\",\"splitAmount\":300000.0,\"settled\":true}";
        ExpenseSplitDto dto = objectMapper.readValue(json, ExpenseSplitDto.class);

        assertNotNull(dto);
        assertTrue(dto.isSettled());
    }

    @Test
    void testSerialize() throws Exception {
        ExpenseSplitDto dto = ExpenseSplitDto.builder()
                .userId(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"))
                .userName("Test User")
                .splitAmount(new BigDecimal("300000.0"))
                .isSettled(true)
                .build();

        String json = objectMapper.writeValueAsString(dto);
        assertTrue(json.contains("\"isSettled\":true"));
    }
}
