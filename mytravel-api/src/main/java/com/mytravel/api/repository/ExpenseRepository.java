package com.mytravel.api.repository;

import com.mytravel.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findByTripIdOrderByExpenseDateDesc(Long tripId);

  @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.trip.id = :tripId")
  BigDecimal sumAmountByTripId(@Param("tripId") Long tripId);
}
