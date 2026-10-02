package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class AlbumRequest {
    @SerializedName("albumTitle")
    private String albumTitle;

    @SerializedName("description")
    private String description;

    public AlbumRequest() {
    }

    public AlbumRequest(String albumTitle, String description) {
        this.albumTitle = albumTitle;
        this.description = description;
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
}
