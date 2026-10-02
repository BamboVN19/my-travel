package com.travel.mytravel.model;

public class ExpenseTotalResponse {
    private String tripId;
    private double totalExpense;

    public ExpenseTotalResponse() {
    }

    public ExpenseTotalResponse(String tripId, double totalExpense) {
        this.tripId = tripId;
        this.totalExpense = totalExpense;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
