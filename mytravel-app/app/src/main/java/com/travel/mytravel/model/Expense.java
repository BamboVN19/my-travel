package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Expense {
    private String id;
    private String tripId;
    private String paidByUserId;
    private String paidByUserName;
    private double amount;
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("expenseDate")
    private String expenseDate; // Pattern: "dd-MM-yyyy"

    private String paymentMethod;
    private List<ExpenseSplit> splits;

    public Expense() {
    }

    public Expense(String id, String tripId, double amount, String category, String description, String expenseDate, String paymentMethod) {
        this.id = id;
        this.tripId = tripId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
    }

    public Expense(String id, String tripId, String paidByUserId, String paidByUserName, double amount, String category, String description, String expenseDate, String paymentMethod, List<ExpenseSplit> splits) {
        this.id = id;
        this.tripId = tripId;
        this.paidByUserId = paidByUserId;
        this.paidByUserName = paidByUserName;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expenseDate = expenseDate;
        this.paymentMethod = paymentMethod;
        this.splits = splits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(String paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public String getPaidByUserName() {
        return paidByUserName;
    }

    public void setPaidByUserName(String paidByUserName) {
        this.paidByUserName = paidByUserName;
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

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }
}
