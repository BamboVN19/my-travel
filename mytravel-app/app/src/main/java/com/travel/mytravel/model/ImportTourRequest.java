package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class ImportTourRequest {
    @SerializedName("startDate")
    private String startDate;

    @SerializedName("customTitle")
    private String customTitle;

    public ImportTourRequest() {
    }

    public ImportTourRequest(String startDate, String customTitle) {
        this.startDate = startDate;
        this.customTitle = customTitle;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }
}
