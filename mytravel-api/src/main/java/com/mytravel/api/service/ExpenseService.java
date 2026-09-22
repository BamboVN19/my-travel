package com.mytravel.api.service;

import com.mytravel.api.dto.ExpenseRequest;
import com.mytravel.api.dto.ExpenseResponse;
import com.mytravel.api.dto.ExpenseTotalResponse;
import com.mytravel.api.entity.Expense;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final TripService tripService;

  public ExpenseResponse createExpense(ExpenseRequest request) {
    Trip trip = tripService.getTripEntityByIdAndValidateUser(request.getTripId());

    Expense expense = Expense.builder()
        .trip(trip)
        .amount(request.getAmount())
        .category(request.getCategory())
        .description(request.getDescription())
        .expenseDate(request.getExpenseDate())
        .paymentMethod(request.getPaymentMethod())
        .build();

    Expense saved = expenseRepository.save(expense);
    return mapToResponse(saved);
  }

  public List<ExpenseResponse> getExpensesByTripId(Long tripId) {
    // Validate quyền truy cập
    tripService.getTripEntityByIdAndValidateUser(tripId);

    return expenseRepository.findByTripIdOrderByExpenseDateDesc(tripId)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public ExpenseTotalResponse getTotalExpenseByTripId(Long tripId) {
    // Validate quyền truy cập
    tripService.getTripEntityByIdAndValidateUser(tripId);

    BigDecimal total = expenseRepository.sumAmountByTripId(tripId);
    return ExpenseTotalResponse.builder()
        .tripId(tripId)
        .totalExpense(total != null ? total : BigDecimal.ZERO)
        .build();
  }

  private ExpenseResponse mapToResponse(Expense expense) {
    return ExpenseResponse.builder()
        .id(expense.getId())
        .tripId(expense.getTrip() != null ? expense.getTrip().getId() : null)
        .amount(expense.getAmount())
        .category(expense.getCategory())
        .description(expense.getDescription())
        .expenseDate(expense.getExpenseDate())
        .paymentMethod(expense.getPaymentMethod())
        .build();
  }
}
