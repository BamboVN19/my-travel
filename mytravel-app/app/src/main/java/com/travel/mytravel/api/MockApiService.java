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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class MockApiService implements ApiService {

    @Override
    public Call<Void> register(Map<String, String> userData) {
        return Calls.response(null);
    }

    @Override
    public Call<Void> registerWithRequest(RegisterRequest registerRequest) {
        return Calls.response(null);
    }

    @Override
    public Call<LoginResponse> login(Map<String, String> credentials) {
        String user = credentials.get("username");
        String pass = credentials.get("password");

        if ("admin".equals(user) && "123".equals(pass)) {
            LoginResponse mockResponse = new LoginResponse("mock-token-xyz-123", "Bearer", "admin");
            return Calls.response(mockResponse);
        } else {
            return Calls.failure(new Exception("Sai tài khoản hoặc mật khẩu!"));
        }
    }

    @Override
    public Call<LoginResponse> loginWithRequest(LoginRequest loginRequest) {
        LoginResponse mockResponse = new LoginResponse("mock-token-xyz-123", "Bearer", "admin");
        return Calls.response(mockResponse);
    }

    @Override
    public Call<UserProfile> getUserProfile() {
        UserProfile profile = new UserProfile(1L, "admin", "admin@travel.com", "Bùi Ngọc Đ", "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00");
        return Calls.response(profile);
    }

    @Override
    public Call<Trip> createTrip(Trip trip) {
        trip.setId(System.currentTimeMillis());
        return Calls.response(trip);
    }

    @Override
    public Call<List<Trip>> getTrips() {
        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip(1L, 1L, "Chuyến đi Đà Nẵng", "Đà Nẵng", "01-06-2025", "05-06-2025", 10000000.0, "PLANNED"));
        trips.add(new Trip(2L, 1L, "Khám phá Đà Lạt", "Đà Lạt", "10-07-2025", "14-07-2025", 8000000.0, "PLANNED"));
        return Calls.response(trips);
    }

    @Override
    public Call<Trip> getTripById(Long id) {
        Trip trip = new Trip(id, 1L, "Chuyến đi Đà Nẵng", "Đà Nẵng", "01-06-2025", "05-06-2025", 10000000.0, "PLANNED");
        return Calls.response(trip);
    }

    @Override
    public Call<Void> deleteTrip(Long id) {
        return Calls.response(null);
    }

    @Override
    public Call<ItineraryItem> addItineraryItem(ItineraryItem item) {
        item.setId(System.currentTimeMillis());
        return Calls.response(item);
    }

    @Override
    public Call<List<ItineraryItem>> getItineraries(Long tripId) {
        List<ItineraryItem> items = new ArrayList<>();
        items.add(new ItineraryItem(1L, tripId, 1, "08:00:00", "Bay đến Đà Nẵng", "Sân bay Đà Nẵng", 16.0544, 108.2022, "place_123", "Đáp sân bay Đà Nẵng"));
        items.add(new ItineraryItem(2L, tripId, 1, "11:30:00", "Ăn trưa Mì Quảng", "Mì Quảng Bếp Trang", 16.0678, 108.2208, "place_456", "Thưởng thức đặc sản"));
        return Calls.response(items);
    }

    @Override
    public Call<Expense> addExpense(Expense expense) {
        expense.setId(System.currentTimeMillis());
        return Calls.response(expense);
    }

    @Override
    public Call<List<Expense>> getExpenses(Long tripId) {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(1L, tripId, 2000000.0, "TICKET", "Vé máy bay khứ hồi", "01-06-2025", "CASH"));
        expenses.add(new Expense(2L, tripId, 1500000.0, "ACCOMMODATION", "Khách sạn 3 đêm", "01-06-2025", "CARD"));
        return Calls.response(expenses);
    }

    @Override
    public Call<ExpenseTotalResponse> getExpenseTotal(Long tripId) {
        ExpenseTotalResponse response = new ExpenseTotalResponse(tripId, 3500000.0);
        return Calls.response(response);
    }

    @Override
    public Call<TripMedia> uploadMedia(MultipartBody.Part file, RequestBody tripId) {
        TripMedia media = new TripMedia(1L, 1L, 10L, "https://example.com/photo1.jpg", "Ảnh đẹp Đà Nẵng", "01-06-2025 10:00:00");
        return Calls.response(media);
    }
}
