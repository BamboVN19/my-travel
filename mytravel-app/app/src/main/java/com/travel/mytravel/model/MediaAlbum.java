package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MediaAlbum {
    private String id;
    private String tripId;
    private String albumTitle;
    private String description;

    @SerializedName("createdAt")
    private String createdAt;

    private List<TripMedia> photos;

    public MediaAlbum() {
    }

    public MediaAlbum(String id, String tripId, String albumTitle, String description, String createdAt, List<TripMedia> photos) {
        this.id = id;
        this.tripId = tripId;
        this.albumTitle = albumTitle;
        this.description = description;
        this.createdAt = createdAt;
        this.photos = photos;
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

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<TripMedia> getPhotos() {
        return photos;
    }

    public void setPhotos(List<TripMedia> photos) {
        this.photos = photos;
    }
}
