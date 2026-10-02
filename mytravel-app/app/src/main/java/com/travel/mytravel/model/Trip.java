package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class Trip {
    private String id;

    @SerializedName(value = "ownerId", alternate = {"userId"})
    private String ownerId;

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

    public Trip(String id, String ownerId, String title, String destination, String startDate, String endDate, double totalBudget, String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getUserId() {
        return ownerId;
    }

    public void setUserId(String userId) {
        this.ownerId = userId;
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
