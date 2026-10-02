package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class TripMedia {
    private String id;
    private String tripId;
    private String albumId;

    @SerializedName("photoUrl")
    private String photoUrl;

    private String caption;

    @SerializedName("uploadedAt")
    private String uploadedAt; // Pattern: "dd-MM-yyyy HH:mm:ss"

    public TripMedia() {
    }

    public TripMedia(String id, String tripId, String albumId, String photoUrl, String caption, String uploadedAt) {
        this.id = id;
        this.tripId = tripId;
        this.albumId = albumId;
        this.photoUrl = photoUrl;
        this.caption = caption;
        this.uploadedAt = uploadedAt;
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

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
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
