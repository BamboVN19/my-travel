package com.travel.mytravel.model;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String username;
    private UserProfile user;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, String tokenType, String username) {
        this(accessToken, null, tokenType, username, null);
    }

    public LoginResponse(String accessToken, String refreshToken, String tokenType, String username) {
        this(accessToken, refreshToken, tokenType, username, null);
    }

    public LoginResponse(String accessToken, String refreshToken, String tokenType, String username, UserProfile user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.username = username;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        if (username != null && !username.isEmpty()) {
            return username;
        }
        if (user != null && user.getUsername() != null) {
            return user.getUsername();
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }
}
