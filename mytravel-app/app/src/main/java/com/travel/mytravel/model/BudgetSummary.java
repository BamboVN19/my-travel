package com.travel.mytravel.model;

import java.util.Map;

public class BudgetSummary {
    private Long tripId;
    private double estimatedBudget;
    private double totalExpense;
    private double remainingBudget;
    private Map<String, Double> categoryBreakdown;

    public BudgetSummary() {
    }

    public BudgetSummary(Long tripId, double estimatedBudget, double totalExpense, double remainingBudget, Map<String, Double> categoryBreakdown) {
        this.tripId = tripId;
        this.estimatedBudget = estimatedBudget;
        this.totalExpense = totalExpense;
        this.remainingBudget = remainingBudget;
        this.categoryBreakdown = categoryBreakdown;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public double getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(double estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public Map<String, Double> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, Double> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
