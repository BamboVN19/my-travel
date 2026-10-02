package com.travel.mytravel.api;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "mytravel_auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_AVATAR_URI = "avatar_uri";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String accessToken, String refreshToken, String username) {
        SharedPreferences.Editor editor = prefs.edit();
        if (accessToken != null) editor.putString(KEY_ACCESS_TOKEN, accessToken);
        if (refreshToken != null) editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        if (username != null) editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "admin");
    }

    public void saveAvatarUri(String uri) {
        if (uri != null) {
            prefs.edit().putString(KEY_AVATAR_URI, uri).apply();
        }
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR_URI, null);
    }

    public void clearTokens() {
        prefs.edit().clear().apply();
    }
}
