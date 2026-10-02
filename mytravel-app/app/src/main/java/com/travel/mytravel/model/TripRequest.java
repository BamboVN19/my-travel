package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class TripRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("destination")
    private String destination;

    @SerializedName("startDate")
    private String startDate; // Pattern: "dd-MM-yyyy"

    @SerializedName("endDate")
    private String endDate;   // Pattern: "dd-MM-yyyy"

    @SerializedName("totalBudget")
    private double totalBudget;

    @SerializedName("status")
    private String status;

    public TripRequest() {
    }

    public TripRequest(String title, String destination, String startDate, String endDate, double totalBudget, String status) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalBudget = totalBudget;
        this.status = status;
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
}
