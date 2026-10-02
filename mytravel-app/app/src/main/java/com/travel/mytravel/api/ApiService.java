package com.travel.mytravel.api;

import com.travel.mytravel.model.AddTripMemberRequest;
import com.travel.mytravel.model.AlbumRequest;
import com.travel.mytravel.model.ChangePasswordRequest;
import com.travel.mytravel.model.Expense;
import com.travel.mytravel.model.ExpenseSummary;
import com.travel.mytravel.model.ForgotPasswordRequest;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.LocationResponse;
import com.travel.mytravel.model.LoginRequest;
import com.travel.mytravel.model.LoginResponse;
import com.travel.mytravel.model.MediaAlbum;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.RefreshTokenRequest;
import com.travel.mytravel.model.RegisterRequest;
import com.travel.mytravel.model.ResetPasswordRequest;
import com.travel.mytravel.model.ImportTourRequest;
import com.travel.mytravel.model.SuggestedTour;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.TripMedia;
import com.travel.mytravel.model.TripMember;
import com.travel.mytravel.model.TripRequest;
import com.travel.mytravel.model.UpdateProfileRequest;
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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==========================================
    // 1. AUTHENTICATION MODULE (/auth)
    // ==========================================
    @POST("auth/register")
    Call<Void> register(@Body Map<String, String> userData);

    @POST("auth/register")
    Call<Void> registerWithRequest(@Body RegisterRequest registerRequest);

    @POST("auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);

    @POST("auth/login")
    Call<LoginResponse> loginWithRequest(@Body LoginRequest loginRequest);

    @POST("auth/refresh-token")
    Call<LoginResponse> refreshToken(@Body RefreshTokenRequest request);

    @POST("auth/logout")
    Call<Void> logout(@Body RefreshTokenRequest request);

    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequest request);


    // ==========================================
    // 2. USER PROFILE MODULE (/users)
    // ==========================================
    @GET("users/me")
    Call<UserProfile> getUserProfile();

    @PUT("users/me")
    Call<UserProfile> updateUserProfile(@Body Map<String, String> profileData);

    @PUT("users/me")
    Call<UserProfile> updateProfile(@Body UpdateProfileRequest request);

    @Multipart
    @POST("users/me/avatar")
    Call<UserProfile> uploadAvatar(@Part MultipartBody.Part file);


    // ==========================================
    // 3. TRIPS MODULE (/trips)
    // ==========================================
    @POST("trips")
    Call<Trip> createTrip(@Body TripRequest request);

    @GET("trips")
    Call<PageResponse<Trip>> getTrips();

    @GET("trips")
    Call<PageResponse<Trip>> getTrips(
        @Query("page") Integer page,
        @Query("size") Integer size
    );

    @GET("trips")
    Call<PageResponse<Trip>> searchTrips(
        @Query("status") String status,
        @Query("search") String search,
        @Query("page") Integer page,
        @Query("size") Integer size,
        @Query("sort") String sort
    );

    @GET("trips/{id}")
    Call<Trip> getTripById(@Path("id") Object id);

    @PUT("trips/{id}")
    Call<Trip> updateTrip(@Path("id") Object id, @Body TripRequest request);

    @DELETE("trips/{id}")
    Call<Void> deleteTrip(@Path("id") Object id);

    @POST("trips/{id}/members")
    Call<TripMember> addTripMember(@Path("id") Object tripId, @Body AddTripMemberRequest request);

    @GET("trips/{id}/members")
    Call<List<TripMember>> getTripMembers(@Path("id") Object tripId);

    @DELETE("trips/{id}/members/{userId}")
    Call<Void> removeTripMember(@Path("id") Object tripId, @Path("userId") Object userId);


    // ==========================================
    // 4. ITINERARIES MODULE (/itineraries)
    // ==========================================
    @POST("trips/{tripId}/itineraries")
    Call<ItineraryItem> addItineraryItemToTrip(@Path("tripId") Object tripId, @Body ItineraryItem item);

    @POST("itineraries")
    Call<ItineraryItem> addItineraryItem(@Body ItineraryItem item);

    @GET("trips/{tripId}/itineraries")
    Call<List<ItineraryItem>> getItineraries(@Path("tripId") Object tripId);

    @PUT("itineraries/{id}")
    Call<ItineraryItem> updateItineraryItem(@Path("id") Object id, @Body ItineraryItem item);

    @DELETE("itineraries/{id}")
    Call<Void> deleteItineraryItem(@Path("id") Object id);


    // ==========================================
    // 5. EXPENSES MODULE (/expenses)
    // ==========================================
    @POST("trips/{tripId}/expenses")
    Call<Expense> addExpenseToTrip(@Path("tripId") Object tripId, @Body Expense expense);

    @POST("expenses")
    Call<Expense> addExpense(@Body Expense expense);

    @GET("trips/{tripId}/expenses")
    Call<List<Expense>> getExpenses(@Path("tripId") Object tripId);

    @GET("trips/{tripId}/expenses/summary")
    Call<ExpenseSummary> getExpenseSummary(@Path("tripId") Object tripId);

    @PUT("expenses/{id}")
    Call<Expense> updateExpense(@Path("id") Object id, @Body Expense expense);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") Object id);


    // ==========================================
    // 6. MEDIA ALBUMS MODULE (/media)
    // ==========================================
    @POST("trips/{tripId}/albums")
    Call<MediaAlbum> createAlbum(@Path("tripId") Object tripId, @Body AlbumRequest request);

    @GET("trips/{tripId}/albums")
    Call<List<MediaAlbum>> getAlbums(@Path("tripId") Object tripId);

    @Multipart
    @POST("albums/{albumId}/photos")
    Call<List<TripMedia>> uploadPhotosToAlbum(
        @Path("albumId") Object albumId,
        @Part List<MultipartBody.Part> files,
        @Part("caption") RequestBody caption
    );

    @Multipart
    @POST("media/upload")
    Call<TripMedia> uploadMedia(
        @Part MultipartBody.Part file,
        @Part("tripId") RequestBody tripId
    );

    @DELETE("photos/{photoId}")
    Call<Void> deletePhoto(@Path("photoId") Object photoId);


    // ==========================================
    // 7. LOCATIONS MODULE (/locations)
    // ==========================================
    @GET("locations/search")
    Call<List<LocationResponse>> searchLocations(
        @Query("query") String query,
        @Query("provider") String provider
    );

    @GET("locations/search")
    Call<List<LocationResponse>> searchLocations(
        @Query("query") String query
    );

    @GET("locations/tours")
    Call<List<SuggestedTour>> getSuggestedTours(
        @Query("query") String query
    );

    @GET("locations/tours")
    Call<List<SuggestedTour>> getSuggestedTours();

    @GET("locations/tours/{id}")
    Call<SuggestedTour> getSuggestedTourById(
        @Path("id") Object id
    );

    @POST("locations/tours/{id}/import")
    Call<Trip> importTour(
        @Path("id") Object id,
        @Body ImportTourRequest request
    );

    @GET("locations/details/{placeId}")
    Call<LocationResponse> getLocationDetails(
        @Path("placeId") String placeId,
        @Query("provider") String provider
    );
}
