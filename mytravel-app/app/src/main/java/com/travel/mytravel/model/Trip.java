package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class Trip {
    private Long id;
    private Long userId;
    private String title;
    private String destination;

    @SerializedName("startDate")
    private String startDate; // Pattern: "dd-MM-yyyy"

    @SerializedName("endDate")
    private String endDate;   // Pattern: "dd-MM-yyyy"

    @SerializedName("totalBudget")
    private double totalBudget;

    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public Trip() {
    }

    public Trip(Long id, Long userId, String title, String destination, String startDate, String endDate, double totalBudget, String status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
