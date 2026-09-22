package com.mytravel.api.controller;

import com.mytravel.api.dto.ExpenseRequest;
import com.mytravel.api.dto.ExpenseResponse;
import com.mytravel.api.dto.ExpenseTotalResponse;
import com.mytravel.api.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseService expenseService;

  @PostMapping("/api/expenses")
  public ResponseEntity<?> createExpense(@Valid @RequestBody ExpenseRequest request) {
    try {
      ExpenseResponse response = expenseService.createExpense(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/api/trips/{tripId}/expenses")
  public ResponseEntity<?> getExpensesByTripId(@PathVariable Long tripId) {
    try {
      List<ExpenseResponse> list = expenseService.getExpensesByTripId(tripId);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/api/trips/{tripId}/expenses/total")
  public ResponseEntity<?> getTotalExpenseByTripId(@PathVariable Long tripId) {
    try {
      ExpenseTotalResponse response = expenseService.getTotalExpenseByTripId(tripId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
