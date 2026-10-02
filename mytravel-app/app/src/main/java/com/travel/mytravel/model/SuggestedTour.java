package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SuggestedTour {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("destination")
    private String destination;

    @SerializedName("description")
    private String description;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    @SerializedName("durationDays")
    private Integer durationDays;

    @SerializedName("estimatedBudget")
    private Double estimatedBudget;

    @SerializedName("category")
    private String category;

    @SerializedName("rating")
    private Double rating;

    @SerializedName("reviewCount")
    private Integer reviewCount;

    @SerializedName("stops")
    private List<ItineraryItem> stops;

    public SuggestedTour() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Double getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(Double estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<ItineraryItem> getStops() {
        return stops;
    }

    public void setStops(List<ItineraryItem> stops) {
        this.stops = stops;
    }
}
