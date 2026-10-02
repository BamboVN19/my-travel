package com.travel.mytravel.model;

import com.google.gson.annotations.SerializedName;

public class TripMember {
    private String id;
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String role;

    @SerializedName("joinedAt")
    private String joinedAt;

    public TripMember() {
    }

    public TripMember(String id, String userId, String username, String fullName, String email, String avatarUrl, String role, String joinedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }
}
