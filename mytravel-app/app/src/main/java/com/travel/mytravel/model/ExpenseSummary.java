package com.travel.mytravel.model;

import java.util.List;

public class ExpenseSummary {
    private String tripId;
    private double totalBudget;
    private double totalSpent;
    private double remainingBudget;
    private List<CategoryBreakdown> categoryBreakdown;

    public ExpenseSummary() {
    }

    public ExpenseSummary(String tripId, double totalBudget, double totalSpent, double remainingBudget, List<CategoryBreakdown> categoryBreakdown) {
        this.tripId = tripId;
        this.totalBudget = totalBudget;
        this.totalSpent = totalSpent;
        this.remainingBudget = remainingBudget;
        this.categoryBreakdown = categoryBreakdown;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public List<CategoryBreakdown> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategoryBreakdown> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
}
