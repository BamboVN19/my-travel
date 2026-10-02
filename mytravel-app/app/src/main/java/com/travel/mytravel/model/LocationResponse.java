package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationResponse {
    @SerializedName("placeId")
    private String placeId;

    @SerializedName("name")
    private String name;

    @SerializedName("formattedAddress")
    private String formattedAddress;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("types")
    private List<String> types;

    public LocationResponse() {
    }

    public LocationResponse(String placeId, String name, String formattedAddress, Double latitude, Double longitude) {
        this.placeId = placeId;
        this.name = name;
        this.formattedAddress = formattedAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getDisplayText() {
        if (formattedAddress != null && !formattedAddress.trim().isEmpty()) {
            return formattedAddress;
        }
        return name != null ? name : "";
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
