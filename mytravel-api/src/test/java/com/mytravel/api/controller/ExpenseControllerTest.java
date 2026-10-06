package com.mytravel.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mytravel.api.dto.ExpenseRequest;
import com.mytravel.api.dto.ExpenseResponse;
import com.mytravel.api.dto.ExpenseSummaryResponse;
import com.mytravel.api.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseController Unit Tests")
class ExpenseControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private UUID sampleTripId;
    private UUID sampleExpenseId;
    private ExpenseResponse sampleExpenseResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();

        sampleTripId = UUID.randomUUID();
        sampleExpenseId = UUID.randomUUID();

        sampleExpenseResponse = ExpenseResponse.builder()
                .id(sampleExpenseId)
                .tripId(sampleTripId)
                .amount(new BigDecimal("500000"))
                .category("FOOD")
                .expenseDate(LocalDate.now())
                .paymentMethod("CASH")
                .description("Ăn trưa")
                .build();
    }

    @Test
    @DisplayName("GET /trips/{tripId}/expenses - Success returns 200")
    void getExpensesByTripId_Success() throws Exception {
        when(expenseService.getExpensesByTripId(sampleTripId)).thenReturn(List.of(sampleExpenseResponse));

        mockMvc.perform(get("/trips/{tripId}/expenses", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleExpenseId.toString()))
                .andExpect(jsonPath("$[0].category").value("FOOD"));

        verify(expenseService, times(1)).getExpensesByTripId(sampleTripId);
    }

    @Test
    @DisplayName("POST /trips/{tripId}/expenses - Success returns 201")
    void createExpense_Success() throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("500000"))
                .category("FOOD")
                .expenseDate(LocalDate.now())
                .paymentMethod("CASH")
                .description("Ăn trưa")
                .build();

        when(expenseService.createExpense(eq(sampleTripId), any(ExpenseRequest.class)))
                .thenReturn(sampleExpenseResponse);

        mockMvc.perform(post("/trips/{tripId}/expenses", sampleTripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleExpenseId.toString()));

        verify(expenseService, times(1)).createExpense(eq(sampleTripId), any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("PUT /expenses/{id} - Success returns 200")
    void updateExpense_Success() throws Exception {
        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("700000"))
                .category("FOOD")
                .expenseDate(LocalDate.now())
                .paymentMethod("TRANSFER")
                .description("Ăn trưa buffet")
                .build();

        when(expenseService.updateExpense(eq(sampleExpenseId), any(ExpenseRequest.class)))
                .thenReturn(sampleExpenseResponse);

        mockMvc.perform(put("/expenses/{id}", sampleExpenseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleExpenseId.toString()));

        verify(expenseService, times(1)).updateExpense(eq(sampleExpenseId), any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("DELETE /expenses/{id} - Success returns 200")
    void deleteExpense_Success() throws Exception {
        doNothing().when(expenseService).deleteExpense(sampleExpenseId);

        mockMvc.perform(delete("/expenses/{id}", sampleExpenseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa khoản chi tiêu thành công!"));

        verify(expenseService, times(1)).deleteExpense(sampleExpenseId);
    }

    @Test
    @DisplayName("GET /trips/{tripId}/expenses/summary - Success returns 200")
    void getExpenseSummary_Success() throws Exception {
        ExpenseSummaryResponse summary = ExpenseSummaryResponse.builder()
                .tripId(sampleTripId)
                .totalBudget(new BigDecimal("10000000"))
                .totalSpent(new BigDecimal("500000"))
                .remainingBudget(new BigDecimal("9500000"))
                .build();

        when(expenseService.getExpenseSummary(sampleTripId)).thenReturn(summary);

        mockMvc.perform(get("/trips/{tripId}/expenses/summary", sampleTripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(500000))
                .andExpect(jsonPath("$.remainingBudget").value(9500000));

        verify(expenseService, times(1)).getExpenseSummary(sampleTripId);
    }
}
