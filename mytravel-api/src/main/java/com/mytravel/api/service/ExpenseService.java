package com.mytravel.api.service;

import com.mytravel.api.dto.*;
import com.mytravel.api.entity.Expense;
import com.mytravel.api.entity.ExpenseSplit;
import com.mytravel.api.entity.Trip;
import com.mytravel.api.entity.User;
import com.mytravel.api.exception.ResourceNotFoundException;
import com.mytravel.api.repository.ExpenseRepository;
import com.mytravel.api.repository.ExpenseSplitRepository;
import com.mytravel.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final ExpenseSplitRepository expenseSplitRepository;
  private final UserRepository userRepository;
  private final TripService tripService;
  private final UserService userService;

  @Transactional(readOnly = true)
  public List<ExpenseResponse> getExpensesByTripId(UUID tripId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserTripAccess(trip, currentUser.getId());

    List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(tripId);
    return expenses.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public ExpenseResponse createExpense(UUID tripId, ExpenseRequest request) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserCanEdit(trip, currentUser.getId());

    Expense expense = Expense.builder()
        .trip(trip)
        .paidByUser(currentUser)
        .amount(request.getAmount())
        .category(request.getCategory().toUpperCase().trim())
        .expenseDate(request.getExpenseDate())
        .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod().toUpperCase().trim() : "CASH")
        .description(request.getDescription())
        .build();

    Expense savedExpense = expenseRepository.save(expense);

    List<ExpenseSplit> splits = saveSplits(savedExpense, request.getSplits());

    return mapToResponse(savedExpense, splits);
  }

  @Transactional
  public ExpenseResponse updateExpense(UUID id, ExpenseRequest request) {
    User currentUser = userService.getCurrentUser();
    Expense expense = expenseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoản chi tiêu với ID: " + id));

    tripService.checkUserCanEdit(expense.getTrip(), currentUser.getId());

    expense.setAmount(request.getAmount());
    expense.setCategory(request.getCategory().toUpperCase().trim());
    expense.setExpenseDate(request.getExpenseDate());
    if (request.getPaymentMethod() != null) {
      expense.setPaymentMethod(request.getPaymentMethod().toUpperCase().trim());
    }
    expense.setDescription(request.getDescription());

    Expense updatedExpense = expenseRepository.save(expense);

    expenseSplitRepository.deleteByExpenseId(id);
    List<ExpenseSplit> splits = saveSplits(updatedExpense, request.getSplits());

    return mapToResponse(updatedExpense, splits);
  }

  @Transactional
  public void deleteExpense(UUID id) {
    User currentUser = userService.getCurrentUser();
    Expense expense = expenseRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khoản chi tiêu với ID: " + id));

    tripService.checkUserCanEdit(expense.getTrip(), currentUser.getId());
    expenseRepository.delete(expense);
  }

  @Transactional(readOnly = true)
  public ExpenseSummaryResponse getExpenseSummary(UUID tripId) {
    User currentUser = userService.getCurrentUser();
    Trip trip = tripService.findTripById(tripId);
    tripService.checkUserTripAccess(trip, currentUser.getId());

    List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(tripId);

    BigDecimal totalBudget = trip.getTotalBudget() != null ? trip.getTotalBudget() : BigDecimal.ZERO;
    BigDecimal totalSpent = expenses.stream()
        .map(Expense::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal remainingBudget = totalBudget.subtract(totalSpent);

    Map<String, BigDecimal> categoryMap = expenses.stream()
        .collect(Collectors.groupingBy(
            Expense::getCategory,
            Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)
        ));

    List<CategoryBreakdownDto> breakdownList = new ArrayList<>();
    if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
      categoryMap.forEach((category, amount) -> {
        double percentage = amount.multiply(BigDecimal.valueOf(100))
            .divide(totalSpent, 2, RoundingMode.HALF_UP)
            .doubleValue();

        breakdownList.add(CategoryBreakdownDto.builder()
            .category(category)
            .total(amount)
            .percentage(percentage)
            .build());
      });
    }

    return ExpenseSummaryResponse.builder()
        .tripId(tripId)
        .totalBudget(totalBudget)
        .totalSpent(totalSpent)
        .remainingBudget(remainingBudget)
        .categoryBreakdown(breakdownList)
        .build();
  }

  private List<ExpenseSplit> saveSplits(Expense expense, List<ExpenseSplitDto> splitDtos) {
    if (splitDtos == null || splitDtos.isEmpty()) {
      return List.of();
    }
    List<ExpenseSplit> splits = new ArrayList<>();
    for (ExpenseSplitDto dto : splitDtos) {
      User user = userRepository.findById(dto.getUserId())
          .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng split ID: " + dto.getUserId()));

      ExpenseSplit split = ExpenseSplit.builder()
          .expense(expense)
          .user(user)
          .splitAmount(dto.getSplitAmount())
          .isSettled(dto.isSettled())
          .build();
      splits.add(split);
    }
    return expenseSplitRepository.saveAll(splits);
  }

  private ExpenseResponse mapToResponse(Expense expense) {
    List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expense.getId());
    return mapToResponse(expense, splits);
  }

  private ExpenseResponse mapToResponse(Expense expense, List<ExpenseSplit> splits) {
    List<ExpenseSplitDto> splitDtos = splits.stream()
        .map(s -> ExpenseSplitDto.builder()
            .userId(s.getUser().getId())
            .userName(s.getUser().getFullName() != null ? s.getUser().getFullName() : s.getUser().getUsername())
            .splitAmount(s.getSplitAmount())
            .isSettled(s.isSettled())
            .build())
        .collect(Collectors.toList());

    return ExpenseResponse.builder()
        .id(expense.getId())
        .tripId(expense.getTrip().getId())
        .paidByUserId(expense.getPaidByUser().getId())
        .paidByUserName(expense.getPaidByUser().getFullName() != null ? expense.getPaidByUser().getFullName() : expense.getPaidByUser().getUsername())
        .amount(expense.getAmount())
        .category(expense.getCategory())
        .expenseDate(expense.getExpenseDate())
        .paymentMethod(expense.getPaymentMethod())
        .description(expense.getDescription())
        .splits(splitDtos)
        .build();
  }
}
