package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class Expense {
    private Long id;
    private Long tripId;
    private double amount;
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("expenseDate")
    private String expenseDate; // Pattern: "dd-MM-yyyy"

    private String paymentMethod;

    public Expense() {
    }

    public Expense(Long id, Long tripId, double amount, String category, String description, String expenseDate, String paymentMethod) {
        this.id = id;
        this.tripId = tripId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
