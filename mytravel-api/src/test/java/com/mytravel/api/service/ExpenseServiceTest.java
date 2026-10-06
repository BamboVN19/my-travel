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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Unit Tests")
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private User currentUser;
    private User otherUser;
    private Trip sampleTrip;
    private Expense sampleExpense;
    private ExpenseSplit sampleSplit;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(UUID.randomUUID())
                .username("payer_user")
                .fullName("Nguyen Van Payer")
                .email("payer@example.com")
                .build();

        otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("member_user")
                .fullName("Tran Van Member")
                .email("member@example.com")
                .build();

        sampleTrip = Trip.builder()
                .id(UUID.randomUUID())
                .owner(currentUser)
                .title("Trip to Da Nang")
                .totalBudget(new BigDecimal("10000000"))
                .build();

        sampleExpense = Expense.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .paidByUser(currentUser)
                .amount(new BigDecimal("600000"))
                .category("FOOD")
                .expenseDate(LocalDate.now())
                .paymentMethod("CASH")
                .description("Bữa tối hải sản")
                .build();

        sampleSplit = ExpenseSplit.builder()
                .id(UUID.randomUUID())
                .expense(sampleExpense)
                .user(otherUser)
                .splitAmount(new BigDecimal("300000"))
                .isSettled(false)
                .build();
    }

    @Test
    @DisplayName("getExpensesByTripId - Success")
    void getExpensesByTripId_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(sampleTrip.getId()))
                .thenReturn(List.of(sampleExpense));
        when(expenseSplitRepository.findByExpenseId(sampleExpense.getId())).thenReturn(List.of(sampleSplit));

        List<ExpenseResponse> results = expenseService.getExpensesByTripId(sampleTrip.getId());

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("FOOD", results.get(0).getCategory());
        assertEquals(1, results.get(0).getSplits().size());
        verify(tripService, times(1)).checkUserTripAccess(sampleTrip, currentUser.getId());
    }

    @Test
    @DisplayName("createExpense - Success with splits")
    void createExpense_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(expenseSplitRepository.saveAll(anyList())).thenReturn(List.of(sampleSplit));

        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("600000"))
                .category("FOOD")
                .expenseDate(LocalDate.now())
                .paymentMethod("CASH")
                .description("Bữa tối hải sản")
                .splits(List.of(ExpenseSplitDto.builder()
                        .userId(otherUser.getId())
                        .splitAmount(new BigDecimal("300000"))
                        .isSettled(false)
                        .build()))
                .build();

        ExpenseResponse response = expenseService.createExpense(sampleTrip.getId(), request);

        assertNotNull(response);
        assertEquals(new BigDecimal("600000"), response.getAmount());
        assertEquals("FOOD", response.getCategory());
        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, currentUser.getId());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("updateExpense - Success")
    void updateExpense_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(expenseRepository.findById(sampleExpense.getId())).thenReturn(Optional.of(sampleExpense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(expenseSplitRepository.saveAll(anyList())).thenReturn(List.of(sampleSplit));

        ExpenseRequest request = ExpenseRequest.builder()
                .amount(new BigDecimal("800000"))
                .category("SHOPPING")
                .expenseDate(LocalDate.now())
                .paymentMethod("TRANSFER")
                .description("Mua quà lưu niệm")
                .splits(List.of(ExpenseSplitDto.builder()
                        .userId(otherUser.getId())
                        .splitAmount(new BigDecimal("400000"))
                        .isSettled(true)
                        .build()))
                .build();

        ExpenseResponse response = expenseService.updateExpense(sampleExpense.getId(), request);

        assertNotNull(response);
        assertEquals(new BigDecimal("800000"), response.getAmount());
        assertEquals("SHOPPING", response.getCategory());
        verify(expenseSplitRepository, times(1)).deleteByExpenseId(sampleExpense.getId());
    }

    @Test
    @DisplayName("updateExpense - Not found throws ResourceNotFoundException")
    void updateExpense_NotFound_ThrowsException() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        UUID randomId = UUID.randomUUID();
        when(expenseRepository.findById(randomId)).thenReturn(Optional.empty());

        ExpenseRequest request = ExpenseRequest.builder().build();
        assertThrows(ResourceNotFoundException.class, () -> expenseService.updateExpense(randomId, request));
    }

    @Test
    @DisplayName("deleteExpense - Success")
    void deleteExpense_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(expenseRepository.findById(sampleExpense.getId())).thenReturn(Optional.of(sampleExpense));

        expenseService.deleteExpense(sampleExpense.getId());

        verify(tripService, times(1)).checkUserCanEdit(sampleTrip, currentUser.getId());
        verify(expenseRepository, times(1)).delete(sampleExpense);
    }

    @Test
    @DisplayName("getExpenseSummary - Success calculates totals and category breakdown")
    void getExpenseSummary_Success() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(tripService.findTripById(sampleTrip.getId())).thenReturn(sampleTrip);

        Expense exp1 = Expense.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .amount(new BigDecimal("600000"))
                .category("FOOD")
                .build();
        Expense exp2 = Expense.builder()
                .id(UUID.randomUUID())
                .trip(sampleTrip)
                .amount(new BigDecimal("400000"))
                .category("HOTEL")
                .build();

        when(expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(sampleTrip.getId()))
                .thenReturn(List.of(exp1, exp2));

        ExpenseSummaryResponse summary = expenseService.getExpenseSummary(sampleTrip.getId());

        assertNotNull(summary);
        assertEquals(new BigDecimal("10000000"), summary.getTotalBudget());
        assertEquals(new BigDecimal("1000000"), summary.getTotalSpent());
        assertEquals(new BigDecimal("9000000"), summary.getRemainingBudget());
        assertEquals(2, summary.getCategoryBreakdown().size());
    }
}
