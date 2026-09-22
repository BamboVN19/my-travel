package com.travel.mytravel.api;

import com.travel.mytravel.model.Expense;
import com.travel.mytravel.model.ExpenseTotalResponse;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.LoginRequest;
import com.travel.mytravel.model.LoginResponse;
import com.travel.mytravel.model.RegisterRequest;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.TripMedia;
import com.travel.mytravel.model.UserProfile;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ==========================================
    // 1. AUTH & USER MODULE
    // ==========================================
    @POST("api/auth/register")
    Call<Void> register(@Body Map<String, String> userData);

    @POST("api/auth/register")
    Call<Void> registerWithRequest(@Body RegisterRequest registerRequest);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);

    @POST("api/auth/login")
    Call<LoginResponse> loginWithRequest(@Body LoginRequest loginRequest);

    @GET("api/users/me")
    Call<UserProfile> getUserProfile();


    // ==========================================
    // 2. TRIP MODULE
    // ==========================================
    @POST("api/trips")
    Call<Trip> createTrip(@Body Trip trip);

    @GET("api/trips")
    Call<List<Trip>> getTrips();

    @GET("api/trips/{id}")
    Call<Trip> getTripById(@Path("id") Long id);

    @DELETE("api/trips/{id}")
    Call<Void> deleteTrip(@Path("id") Long id);


    // ==========================================
    // 3. ITINERARY MODULE
    // ==========================================
    @POST("api/itineraries")
    Call<ItineraryItem> addItineraryItem(@Body ItineraryItem item);

    @GET("api/trips/{tripId}/itineraries")
    Call<List<ItineraryItem>> getItineraries(@Path("tripId") Long tripId);


    // ==========================================
    // 4. EXPENSE MODULE
    // ==========================================
    @POST("api/expenses")
    Call<Expense> addExpense(@Body Expense expense);

    @GET("api/trips/{tripId}/expenses")
    Call<List<Expense>> getExpenses(@Path("tripId") Long tripId);

    @GET("api/trips/{tripId}/expenses/total")
    Call<ExpenseTotalResponse> getExpenseTotal(@Path("tripId") Long tripId);


    // ==========================================
    // 5. MEDIA MODULE
    // ==========================================
    @Multipart
    @POST("api/media/upload")
    Call<TripMedia> uploadMedia(
        @Part MultipartBody.Part file,
        @Part("tripId") RequestBody tripId
    );
}
