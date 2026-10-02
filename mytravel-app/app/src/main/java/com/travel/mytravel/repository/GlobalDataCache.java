package com.travel.mytravel.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GlobalDataCache {

    private static GlobalDataCache instance;

    private UserProfile cachedProfile;
    private List<Trip> cachedTrips;
    private final Map<String, Trip> cachedTripDetails = new HashMap<>();

    private GlobalDataCache() {
    }

    public static synchronized GlobalDataCache getInstance() {
        if (instance == null) {
            instance = new GlobalDataCache();
        }
        return instance;
    }

    // ==========================================
    // 1. USER PROFILE CACHE
    // ==========================================
    public UserProfile getCachedProfile() {
        return cachedProfile;
    }

    public void setCachedProfile(UserProfile profile) {
        this.cachedProfile = profile;
    }

    public void getUserProfile(Context context, boolean forceRefresh, @NonNull Callback<UserProfile> callback) {
        if (!forceRefresh && cachedProfile != null) {
            Log.d("GlobalDataCache", "⚡ Returning UserProfile from memory cache!");
            callback.onResponse(null, Response.success(cachedProfile));
            return;
        }

        ApiClient.getService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachedProfile = response.body();
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    // ==========================================
    // 2. TRIPS LIST CACHE
    // ==========================================
    public List<Trip> getCachedTrips() {
        return cachedTrips;
    }

    public void setCachedTrips(List<Trip> trips) {
        this.cachedTrips = trips != null ? new ArrayList<>(trips) : null;
    }

    public void invalidateTripsCache() {
        Log.d("GlobalDataCache", "🔄 Trips cache invalidated.");
        this.cachedTrips = null;
        this.cachedTripDetails.clear();
    }

    public void getTrips(boolean forceRefresh, @NonNull Callback<PageResponse<Trip>> callback) {
        if (!forceRefresh && cachedTrips != null && !cachedTrips.isEmpty()) {
            Log.d("GlobalDataCache", "⚡ Returning " + cachedTrips.size() + " trips from memory cache!");
            PageResponse<Trip> page = new PageResponse<>();
            page.setContent(new ArrayList<>(cachedTrips));
            page.setTotalElements(cachedTrips.size());
            page.setTotalPages(1);
            callback.onResponse(null, Response.success(page));
            return;
        }

        ApiClient.getService().getTrips(0, 50).enqueue(new Callback<PageResponse<Trip>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    cachedTrips = new ArrayList<>(response.body().getContent());
                }
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    // ==========================================
    // 3. CLEAR ALL CACHE ON LOGOUT
    // ==========================================
    public void clearAllCache() {
        Log.d("GlobalDataCache", "🧹 Clearing all memory cache on logout.");
        this.cachedProfile = null;
        this.cachedTrips = null;
        this.cachedTripDetails.clear();
    }
}
