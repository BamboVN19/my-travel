package com.mytravel.api.controller;

import com.mytravel.api.dto.ExpenseRequest;
import com.mytravel.api.dto.ExpenseResponse;
import com.mytravel.api.dto.ExpenseSummaryResponse;
import com.mytravel.api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseService expenseService;

  @GetMapping("/trips/{tripId}/expenses")
  public ResponseEntity<List<ExpenseResponse>> getExpensesByTripId(@PathVariable UUID tripId) {
    List<ExpenseResponse> list = expenseService.getExpensesByTripId(tripId);
    return ResponseEntity.ok(list);
  }

  @PostMapping("/trips/{tripId}/expenses")
  public ResponseEntity<ExpenseResponse> createExpense(
      @PathVariable UUID tripId,
      @Valid @RequestBody ExpenseRequest request) {
    ExpenseResponse response = expenseService.createExpense(tripId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/expenses/{id}")
  public ResponseEntity<ExpenseResponse> updateExpense(
      @PathVariable UUID id,
      @Valid @RequestBody ExpenseRequest request) {
    ExpenseResponse response = expenseService.updateExpense(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/expenses/{id}")
  public ResponseEntity<Map<String, String>> deleteExpense(@PathVariable UUID id) {
    expenseService.deleteExpense(id);
    return ResponseEntity.ok(Map.of("message", "Xóa khoản chi tiêu thành công!"));
  }

  @GetMapping("/trips/{tripId}/expenses/summary")
  public ResponseEntity<ExpenseSummaryResponse> getExpenseSummary(@PathVariable UUID tripId) {
    ExpenseSummaryResponse summary = expenseService.getExpenseSummary(tripId);
    return ResponseEntity.ok(summary);
  }
}
