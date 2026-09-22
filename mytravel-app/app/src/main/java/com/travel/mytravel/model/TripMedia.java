package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class TripMedia {
    private Long id;
    private Long tripId;
    private Long albumId;

    @SerializedName("photoUrl")
    private String photoUrl;

    private String caption;

    @SerializedName("uploadedAt")
    private String uploadedAt; // Pattern: "dd-MM-yyyy HH:mm:ss"

    public TripMedia() {
    }

    public TripMedia(Long id, Long tripId, Long albumId, String photoUrl, String caption, String uploadedAt) {
        this.id = id;
        this.tripId = tripId;
        this.albumId = albumId;
        this.photoUrl = photoUrl;
        this.caption = caption;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
