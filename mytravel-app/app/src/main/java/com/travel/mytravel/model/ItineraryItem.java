package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class ItineraryItem {
    private String id;
    private String tripId;
    private Integer dayNumber;

    @SerializedName("activityTime")
    private String activityTime; // Pattern: "HH:mm:ss" hoặc "HH:mm"

    @SerializedName("activityName")
    private String activityName;

    private String locationName;
    private Double latitude;
    private Double longitude;
    private String placeId;

    @SerializedName("note")
    private String note;

    private String imageUrl;

    public ItineraryItem() {
    }

    public ItineraryItem(String id, String tripId, Integer dayNumber, String activityTime, String activityName,
                         String locationName, Double latitude, Double longitude, String placeId, String note) {
        this(id, tripId, dayNumber, activityTime, activityName, locationName, latitude, longitude, placeId, note, null);
    }

    public ItineraryItem(String id, String tripId, Integer dayNumber, String activityTime, String activityName,
                         String locationName, Double latitude, Double longitude, String placeId, String note, String imageUrl) {
        this.id = id;
        this.tripId = tripId;
        this.dayNumber = dayNumber;
        this.activityTime = activityTime;
        this.activityName = activityName;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.note = note;
        this.imageUrl = imageUrl;
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

    public Integer getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
