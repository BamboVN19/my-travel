package com.mytravel.api.repository;

import com.mytravel.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
  List<Expense> findByTripIdOrderByExpenseDateDescCreatedAtDesc(UUID tripId);
}
