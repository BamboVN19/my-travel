package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class ExpenseSplit {
    private Long userId;
    private String userName;
    private double splitAmount;

    @SerializedName("isSettled")
    private boolean isSettled;

    public ExpenseSplit() {
    }

    public ExpenseSplit(Long userId, String userName, double splitAmount, boolean isSettled) {
        this.userId = userId;
        this.userName = userName;
        this.splitAmount = splitAmount;
        this.isSettled = isSettled;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getSplitAmount() {
        return splitAmount;
    }

    public void setSplitAmount(double splitAmount) {
        this.splitAmount = splitAmount;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }
}
