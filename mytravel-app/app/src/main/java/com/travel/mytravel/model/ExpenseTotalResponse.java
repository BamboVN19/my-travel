package com.travel.mytravel.model;

public class ExpenseTotalResponse {
    private Long tripId;
    private double totalExpense;

    public ExpenseTotalResponse() {
    }

    public ExpenseTotalResponse(Long tripId, double totalExpense) {
        this.tripId = tripId;
        this.totalExpense = totalExpense;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
