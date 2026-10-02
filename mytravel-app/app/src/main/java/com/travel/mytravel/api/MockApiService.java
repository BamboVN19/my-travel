package com.travel.mytravel.api;

import com.travel.mytravel.model.AddTripMemberRequest;
import com.travel.mytravel.model.AlbumRequest;
import com.travel.mytravel.model.CategoryBreakdown;
import com.travel.mytravel.model.ChangePasswordRequest;
import com.travel.mytravel.model.Expense;
import com.travel.mytravel.model.ExpenseSummary;
import com.travel.mytravel.model.ForgotPasswordRequest;
import com.travel.mytravel.model.ImportTourRequest;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.LocationResponse;
import com.travel.mytravel.model.LoginRequest;
import com.travel.mytravel.model.LoginResponse;
import com.travel.mytravel.model.MediaAlbum;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.RefreshTokenRequest;
import com.travel.mytravel.model.RegisterRequest;
import com.travel.mytravel.model.ResetPasswordRequest;
import com.travel.mytravel.model.SuggestedTour;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.TripMedia;
import com.travel.mytravel.model.TripMember;
import com.travel.mytravel.model.TripRequest;
import com.travel.mytravel.model.UpdateProfileRequest;
import com.travel.mytravel.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.mock.Calls;

public class MockApiService implements ApiService {

    private static final List<Trip> tripList = new ArrayList<>();
    private static final Map<String, List<ItineraryItem>> itinerariesMap = new HashMap<>();
    private static final Map<String, List<Expense>> expensesMap = new HashMap<>();
    private static final Map<String, List<TripMember>> membersMap = new HashMap<>();
    private static final Map<String, List<MediaAlbum>> albumsMap = new HashMap<>();
    private static final Map<String, UserProfile> userProfileMap = new HashMap<>();
    private static String currentLoggedInUser = "admin";

    static {
        userProfileMap.put("admin", new UserProfile("1", "admin", "admin@mytravel.com", "Bùi Ngọc Đại", "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));
        userProfileMap.put("ngocdai", new UserProfile("2", "ngocdai", "ngocdai@mytravel.com", "Bùi Ngọc Đại", "0912345678", "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));

        // Dữ liệu chuyến đi ban đầu
        Trip t1 = new Trip("1", "1", "Chuyến đi Phố Cổ Hội An, Quảng Nam", "Hội An, Quảng Nam", "29-09-2026", "01-10-2026", 12000000.0, "PLANNED");
        Trip t2 = new Trip("2", "1", "Chuyến đi Đà Nẵng", "Đà Nẵng", "01-06-2025", "05-06-2025", 10000000.0, "ONGOING");
        Trip t3 = new Trip("3", "1", "Khám phá Đà Lạt", "Đà Lạt", "10-07-2025", "14-07-2025", 8000000.0, "COMPLETED");

        tripList.add(t1);
        tripList.add(t2);
        tripList.add(t3);

        List<ItineraryItem> items1 = new ArrayList<>();
        items1.add(new ItineraryItem("101", "1", 1, "08:00:00", "Bay đến Đà Nẵng & Di chuyển Hội An", "Sân bay Đà Nẵng ➔ Phố cổ", 15.8801, 108.3380, "p1", "Khởi hành chuyến đi"));
        items1.add(new ItineraryItem("102", "1", 1, "11:30:00", "Thưởng thức Cơm gà Bà Buổi", "Phố cổ Hội An", 15.8770, 108.3280, "p2", "Thưởng thức đặc sản nổi tiếng"));
        items1.add(new ItineraryItem("103", "1", 1, "15:00:00", "Check-in Resort & Tắm biển Cửa Đại", "Biển Cửa Đại", 15.8900, 108.3700, "p3", "Nghỉ ngơi thư giãn"));
        items1.add(new ItineraryItem("104", "1", 1, "18:30:00", "Dạo phố đèn lồng & Đi thuyền Sông Hoài", "Sông Hoài, Hội An", 15.8760, 108.3270, "p4", "Thả hoa đăng cầu may mắn"));

        items1.add(new ItineraryItem("105", "1", 2, "07:30:00", "Ăn bánh mì Phượng & Cà phê Mắt Đèn", "Đường Phan Chu Trinh", 15.8780, 108.3290, "p5", "Ăn sáng điểm tâm"));
        items1.add(new ItineraryItem("106", "1", 2, "09:30:00", "Tham quan Thánh địa Mỹ Sơn", "Thánh địa Mỹ Sơn", 15.7958, 108.1250, "p6", "Khám phá kiến trúc Chăm Pa"));
        items1.add(new ItineraryItem("107", "1", 2, "14:30:00", "Trải nghiệm làm gốm Thanh Hà", "Làng gốm Thanh Hà", 15.8820, 108.3050, "p7", "Tự tay nặn sản phẩm gốm"));
        items1.add(new ItineraryItem("108", "1", 2, "19:00:00", "Xem show Ký Hức Hội An", "Công viên Ấn Tượng Hội An", 15.8720, 108.3350, "p8", "Chương trình nghệ thuật thực cảnh"));

        items1.add(new ItineraryItem("109", "1", 3, "08:00:00", "Mua sắm đặc sản quà lưu niệm", "Chợ Hội An", 15.8765, 108.3300, "p9", "Mua Mắm Cẩm Thanh, Bánh tổ"));
        items1.add(new ItineraryItem("110", "1", 3, "11:30:00", "Trả phòng & Di chuyển ra sân bay trở về", "Sân bay Đà Nẵng", 16.0544, 108.2022, "p10", "Kết thúc chuyến đi ý nghĩa"));

        itinerariesMap.put("1", items1);

        List<Expense> expenses1 = new ArrayList<>();
        expenses1.add(new Expense("201", "1", 2500000.0, "TICKET", "Vé máy bay khứ hồi", "29-09-2026", "CASH"));
        expenses1.add(new Expense("202", "1", 3200000.0, "ACCOMMODATION", "Resort 3 đêm", "29-09-2026", "CARD"));
        expenses1.add(new Expense("203", "1", 1800000.0, "FOOD", "Ăn uống & Đặc sản", "30-09-2026", "CASH"));
        expensesMap.put("1", expenses1);

        List<TripMember> m1 = new ArrayList<>();
        m1.add(new TripMember("1", "1", "admin", "Bùi Ngọc Đại (Admin)", "admin@mytravel.com", "https://i.pravatar.cc/300?img=11", "OWNER", "28-09-2026 18:45:41"));
        m1.add(new TripMember("2", "2", "ngocdai", "Bùi Ngọc Đại", "ngocdai@mytravel.com", "https://i.pravatar.cc/300?img=12", "EDITOR", "28-09-2026 18:45:41"));
        membersMap.put("1", m1);

        List<MediaAlbum> alb1 = new ArrayList<>();
        List<TripMedia> photos1 = new ArrayList<>();
        photos1.add(new TripMedia("501", "1", "10", "https://cdn.mytravel.com/media/2026/10/photo_dalat_1.jpg", "Ảnh tập thể", "19-10-2026 20:15:00"));
        alb1.add(new MediaAlbum("10", "1", "Ảnh kỷ niệm Ngày 1 - Hồ Tuyền Lâm", "Chụp lúc hoàng hôn", "19-10-2026 20:00:00", photos1));
        albumsMap.put("1", alb1);
    }

    @Override
    public Call<Void> register(Map<String, String> userData) {
        String user = userData.getOrDefault("username", "user").toLowerCase();
        String name = userData.getOrDefault("fullName", user);
        userProfileMap.put(user, new UserProfile(String.valueOf(System.currentTimeMillis()), user, userData.getOrDefault("email", user + "@mytravel.com"), name, userData.getOrDefault("phone", "0987654321"), "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));
        return Calls.response(null);
    }

    @Override
    public Call<Void> registerWithRequest(RegisterRequest registerRequest) {
        String user = registerRequest.getUsername() != null ? registerRequest.getUsername().toLowerCase() : "user";
        String name = registerRequest.getFullName() != null && !registerRequest.getFullName().isEmpty() ? registerRequest.getFullName() : user;
        userProfileMap.put(user, new UserProfile(String.valueOf(System.currentTimeMillis()), user, registerRequest.getEmail(), name, registerRequest.getPhoneNumber(), "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));
        return Calls.response(null);
    }

    @Override
    public Call<LoginResponse> login(Map<String, String> credentials) {
        String user = credentials.get("username");
        if (user != null) currentLoggedInUser = user.toLowerCase();

        if (!userProfileMap.containsKey(currentLoggedInUser)) {
            userProfileMap.put(currentLoggedInUser, new UserProfile(String.valueOf(System.currentTimeMillis()), currentLoggedInUser, currentLoggedInUser + "@mytravel.com", currentLoggedInUser, "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));
        }

        LoginResponse mockResponse = new LoginResponse("mock-token-xyz-123", "mock-refresh-xyz-456", "Bearer", currentLoggedInUser);
        return Calls.response(mockResponse);
    }

    @Override
    public Call<LoginResponse> loginWithRequest(LoginRequest loginRequest) {
        String user = loginRequest.getUsername() != null ? loginRequest.getUsername().toLowerCase() : "admin";
        currentLoggedInUser = user;

        if (!userProfileMap.containsKey(currentLoggedInUser)) {
            String name = user.length() > 1 ? user.substring(0, 1).toUpperCase() + user.substring(1) : user;
            userProfileMap.put(currentLoggedInUser, new UserProfile(String.valueOf(System.currentTimeMillis()), user, user + "@mytravel.com", name, "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00"));
        }

        LoginResponse mockResponse = new LoginResponse("mock-token-xyz-123", "mock-refresh-xyz-456", "Bearer", currentLoggedInUser);
        return Calls.response(mockResponse);
    }

    @Override
    public Call<LoginResponse> refreshToken(RefreshTokenRequest request) {
        LoginResponse mockResponse = new LoginResponse("mock-refreshed-token-xyz-456", request.getRefreshToken(), "Bearer", currentLoggedInUser);
        return Calls.response(mockResponse);
    }

    @Override
    public Call<Void> logout(RefreshTokenRequest request) {
        return Calls.response(null);
    }

    @Override
    public Call<Void> forgotPassword(ForgotPasswordRequest request) {
        return Calls.response(null);
    }

    @Override
    public Call<Void> resetPassword(ResetPasswordRequest request) {
        return Calls.response(null);
    }

    @Override
    public Call<Void> changePassword(ChangePasswordRequest request) {
        return Calls.response(null);
    }

    @Override
    public Call<UserProfile> getUserProfile() {
        UserProfile profile = userProfileMap.get(currentLoggedInUser);
        if (profile == null) {
            profile = new UserProfile("1", currentLoggedInUser, currentLoggedInUser + "@mytravel.com", "Bùi Ngọc Đại", "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00");
        }
        return Calls.response(profile);
    }

    @Override
    public Call<UserProfile> updateUserProfile(Map<String, String> profileData) {
        UserProfile profile = userProfileMap.get(currentLoggedInUser);
        if (profile != null) {
            if (profileData.containsKey("fullName")) profile.setFullName(profileData.get("fullName"));
            if (profileData.containsKey("phoneNumber")) profile.setPhoneNumber(profileData.get("phoneNumber"));
        } else {
            profile = new UserProfile("1", currentLoggedInUser, currentLoggedInUser + "@mytravel.com", profileData.getOrDefault("fullName", "Bùi Ngọc Đại"), profileData.getOrDefault("phoneNumber", "0987654321"), "https://i.pravatar.cc/300", "01-01-2025 08:00:00");
            userProfileMap.put(currentLoggedInUser, profile);
        }
        return Calls.response(profile);
    }

    @Override
    public Call<UserProfile> updateProfile(UpdateProfileRequest request) {
        UserProfile profile = userProfileMap.get(currentLoggedInUser);
        if (profile != null) {
            if (request.getFullName() != null) profile.setFullName(request.getFullName());
            if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        }
        return Calls.response(profile);
    }

    @Override
    public Call<UserProfile> uploadAvatar(MultipartBody.Part file) {
        UserProfile profile = userProfileMap.get(currentLoggedInUser);
        if (profile == null) {
            profile = new UserProfile("1", currentLoggedInUser, currentLoggedInUser + "@mytravel.com", "Bùi Ngọc Đại", "0987654321", "https://i.pravatar.cc/300", "01-01-2025 08:00:00");
        }
        return Calls.response(profile);
    }

    @Override
    public Call<Trip> createTrip(TripRequest request) {
        String newId = String.valueOf(System.currentTimeMillis());
        Trip trip = new Trip(
                newId,
                "1",
                request.getTitle() != null ? request.getTitle() : "Chuyến đi mới",
                request.getDestination() != null ? request.getDestination() : "Địa điểm",
                request.getStartDate() != null ? request.getStartDate() : "18-10-2026",
                request.getEndDate() != null ? request.getEndDate() : "22-10-2026",
                request.getTotalBudget(),
                request.getStatus() != null ? request.getStatus() : "PLANNED"
        );

        tripList.add(0, trip);

        String dest = trip.getDestination() != null ? trip.getDestination() : "địa điểm";
        List<ItineraryItem> aiItineraries = new ArrayList<>();

        aiItineraries.add(new ItineraryItem(newId + "_1", newId, 1, "08:00:00", "Khởi hành đến " + dest, dest, 11.9404, 108.4583, "p1", "Check-in đường đi"));
        aiItineraries.add(new ItineraryItem(newId + "_2", newId, 1, "11:30:00", "Thưởng thức ẩm thực đặc sản " + dest, dest, 11.9450, 108.4500, "p2", "Ăn trưa đặc sản địa phương"));
        aiItineraries.add(new ItineraryItem(newId + "_3", newId, 1, "15:00:00", "Nhận phòng khách sạn & Nghỉ ngơi", dest, 11.9500, 108.4600, "p3", "Thư giãn nhận phòng"));
        aiItineraries.add(new ItineraryItem(newId + "_4", newId, 1, "18:30:00", "Dạo phố & Thưởng thức tiệc đêm", dest, 11.9420, 108.4550, "p4", "Tận hưởng không khí đêm"));

        aiItineraries.add(new ItineraryItem(newId + "_5", newId, 2, "07:30:00", "Ăn sáng & Thưởng thức cà phê", dest, 11.9430, 108.4520, "p5", "Khởi đầu ngày mới rạng rỡ"));
        aiItineraries.add(new ItineraryItem(newId + "_6", newId, 2, "09:30:00", "Tham quan danh lam thắng cảnh chính", dest, 11.9480, 108.4580, "p6", "Chụp ảnh check-in sống ảo"));
        aiItineraries.add(new ItineraryItem(newId + "_7", newId, 2, "14:30:00", "Trải nghiệm văn hóa & Mua sắm", dest, 11.9520, 108.4620, "p7", "Mua quà lưu niệm"));

        aiItineraries.add(new ItineraryItem(newId + "_8", newId, 3, "08:30:00", "Ăn sáng & Mua sắm quà lưu niệm", dest, 11.9400, 108.4510, "p8", "Dạo chợ địa phương"));
        aiItineraries.add(new ItineraryItem(newId + "_9", newId, 3, "11:30:00", "Trả phòng khách sạn & Trở về", dest, 11.9410, 108.4530, "p9", "Kết thúc chuyến đi tuyệt vời"));

        itinerariesMap.put(newId, aiItineraries);

        return Calls.response(trip);
    }

    @Override
    public Call<PageResponse<Trip>> getTrips() {
        PageResponse<Trip> page = new PageResponse<>();
        page.setContent(new ArrayList<>(tripList));
        page.setTotalElements(tripList.size());
        page.setTotalPages(1);
        return Calls.response(page);
    }

    @Override
    public Call<PageResponse<Trip>> getTrips(Integer page, Integer size) {
        return getTrips();
    }

    @Override
    public Call<PageResponse<Trip>> searchTrips(String status, String search, Integer page, Integer size, String sort) {
        List<Trip> filtered = new ArrayList<>();
        for (Trip t : tripList) {
            boolean matchStatus = status == null || status.equalsIgnoreCase(t.getStatus());
            boolean matchSearch = search == null || t.getTitle().toLowerCase().contains(search.toLowerCase()) || t.getDestination().toLowerCase().contains(search.toLowerCase());
            if (matchStatus && matchSearch) {
                filtered.add(t);
            }
        }
        PageResponse<Trip> pageRes = new PageResponse<>();
        pageRes.setContent(filtered);
        pageRes.setTotalElements(filtered.size());
        pageRes.setTotalPages(1);
        return Calls.response(pageRes);
    }

    @Override
    public Call<Trip> getTripById(Object id) {
        String key = String.valueOf(id);
        for (Trip t : tripList) {
            if (t.getId().equals(key)) {
                return Calls.response(t);
            }
        }
        return Calls.response(tripList.isEmpty() ? null : tripList.get(0));
    }

    @Override
    public Call<Trip> updateTrip(Object id, TripRequest request) {
        String key = String.valueOf(id);
        for (int i = 0; i < tripList.size(); i++) {
            if (tripList.get(i).getId().equals(key)) {
                Trip trip = tripList.get(i);
                if (request.getTitle() != null) trip.setTitle(request.getTitle());
                if (request.getDestination() != null) trip.setDestination(request.getDestination());
                if (request.getStartDate() != null) trip.setStartDate(request.getStartDate());
                if (request.getEndDate() != null) trip.setEndDate(request.getEndDate());
                if (request.getTotalBudget() > 0) trip.setTotalBudget(request.getTotalBudget());
                if (request.getStatus() != null) trip.setStatus(request.getStatus());
                tripList.set(i, trip);
                return Calls.response(trip);
            }
        }
        Trip trip = new Trip(key, "1", request.getTitle(), request.getDestination(), request.getStartDate(), request.getEndDate(), request.getTotalBudget(), request.getStatus());
        return Calls.response(trip);
    }

    @Override
    public Call<Void> deleteTrip(Object id) {
        String key = String.valueOf(id);
        tripList.removeIf(t -> t.getId().equals(key));
        itinerariesMap.remove(key);
        expensesMap.remove(key);
        membersMap.remove(key);
        albumsMap.remove(key);
        return Calls.response(null);
    }

    @Override
    public Call<TripMember> addTripMember(Object tripId, AddTripMemberRequest request) {
        String key = String.valueOf(tripId);
        TripMember member = new TripMember(String.valueOf(System.currentTimeMillis()), "3", "friend", "Trần Việt Anh", request.getEmail(), "https://i.pravatar.cc/300?img=13", request.getRole(), "28-09-2026 22:00:00");
        List<TripMember> members = membersMap.computeIfAbsent(key, k -> new ArrayList<>());
        members.add(member);
        return Calls.response(member);
    }

    @Override
    public Call<List<TripMember>> getTripMembers(Object tripId) {
        String key = String.valueOf(tripId);
        List<TripMember> members = membersMap.get(key);
        if (members == null) {
            members = new ArrayList<>();
        }
        return Calls.response(members);
    }

    @Override
    public Call<Void> removeTripMember(Object tripId, Object userId) {
        String tKey = String.valueOf(tripId);
        String uKey = String.valueOf(userId);
        List<TripMember> members = membersMap.get(tKey);
        if (members != null) {
            members.removeIf(m -> m.getUserId().equals(uKey));
        }
        return Calls.response(null);
    }

    @Override
    public Call<ItineraryItem> addItineraryItemToTrip(Object tripId, ItineraryItem item) {
        item.setTripId(String.valueOf(tripId));
        return addItineraryItem(item);
    }

    @Override
    public Call<ItineraryItem> addItineraryItem(ItineraryItem item) {
        item.setId(String.valueOf(System.currentTimeMillis()));
        List<ItineraryItem> items = itinerariesMap.computeIfAbsent(item.getTripId(), k -> new ArrayList<>());
        items.add(item);
        return Calls.response(item);
    }

    @Override
    public Call<List<ItineraryItem>> getItineraries(Object tripId) {
        String key = String.valueOf(tripId);
        List<ItineraryItem> items = itinerariesMap.get(key);
        if (items == null) {
            items = new ArrayList<>();
        }
        return Calls.response(items);
    }

    @Override
    public Call<ItineraryItem> updateItineraryItem(Object id, ItineraryItem item) {
        item.setId(String.valueOf(id));
        return Calls.response(item);
    }

    @Override
    public Call<Void> deleteItineraryItem(Object id) {
        return Calls.response(null);
    }

    @Override
    public Call<Expense> addExpenseToTrip(Object tripId, Expense expense) {
        expense.setTripId(String.valueOf(tripId));
        return addExpense(expense);
    }

    @Override
    public Call<Expense> addExpense(Expense expense) {
        expense.setId(String.valueOf(System.currentTimeMillis()));
        List<Expense> expenses = expensesMap.computeIfAbsent(expense.getTripId(), k -> new ArrayList<>());
        expenses.add(0, expense);
        return Calls.response(expense);
    }

    @Override
    public Call<List<Expense>> getExpenses(Object tripId) {
        String key = String.valueOf(tripId);
        List<Expense> expenses = expensesMap.get(key);
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        return Calls.response(expenses);
    }

    @Override
    public Call<ExpenseSummary> getExpenseSummary(Object tripId) {
        String key = String.valueOf(tripId);
        List<Expense> expenses = expensesMap.get(key);
        double totalSpent = 0;
        List<CategoryBreakdown> breakdown = new ArrayList<>();
        Map<String, Double> categoryTotals = new HashMap<>();

        if (expenses != null) {
            for (Expense e : expenses) {
                totalSpent += e.getAmount();
                categoryTotals.put(e.getCategory(), categoryTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
            }
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double pct = totalSpent > 0 ? (entry.getValue() / totalSpent) * 100 : 0;
            breakdown.add(new CategoryBreakdown(entry.getKey(), entry.getValue(), pct));
        }

        ExpenseSummary summary = new ExpenseSummary(key, 12000000.0, totalSpent, 12000000.0 - totalSpent, breakdown);
        return Calls.response(summary);
    }

    @Override
    public Call<Expense> updateExpense(Object id, Expense expense) {
        expense.setId(String.valueOf(id));
        return Calls.response(expense);
    }

    @Override
    public Call<Void> deleteExpense(Object id) {
        return Calls.response(null);
    }

    @Override
    public Call<MediaAlbum> createAlbum(Object tripId, AlbumRequest request) {
        String key = String.valueOf(tripId);
        MediaAlbum album = new MediaAlbum(String.valueOf(System.currentTimeMillis()), key, request.getAlbumTitle(), request.getDescription(), "01-10-2026 12:00:00", new ArrayList<>());
        List<MediaAlbum> albums = albumsMap.computeIfAbsent(key, k -> new ArrayList<>());
        albums.add(album);
        return Calls.response(album);
    }

    @Override
    public Call<List<MediaAlbum>> getAlbums(Object tripId) {
        String key = String.valueOf(tripId);
        List<MediaAlbum> albums = albumsMap.get(key);
        if (albums == null) {
            albums = new ArrayList<>();
        }
        return Calls.response(albums);
    }

    @Override
    public Call<List<TripMedia>> uploadPhotosToAlbum(Object albumId, List<MultipartBody.Part> files, RequestBody caption) {
        List<TripMedia> mediaList = new ArrayList<>();
        mediaList.add(new TripMedia(String.valueOf(System.currentTimeMillis()), "1", String.valueOf(albumId), "https://cdn.mytravel.com/media/2026/10/photo_dalat_1.jpg", caption != null ? caption.toString() : "Ảnh album mới", "19-10-2026 20:15:00"));
        return Calls.response(mediaList);
    }

    @Override
    public Call<TripMedia> uploadMedia(MultipartBody.Part file, RequestBody tripId) {
        TripMedia media = new TripMedia(String.valueOf(System.currentTimeMillis()), "1", "10", "https://cdn.mytravel.com/media/2026/10/photo_dalat_1.jpg", "Ảnh kỷ niệm chuyến đi", "19-10-2026 20:15:00");
        return Calls.response(media);
    }

    @Override
    public Call<Void> deletePhoto(Object photoId) {
        return Calls.response(null);
    }

    @Override
    public Call<List<LocationResponse>> searchLocations(String query, String provider) {
        return searchLocations(query);
    }

    @Override
    public Call<List<LocationResponse>> searchLocations(String query) {
        List<LocationResponse> sampleLocations = new ArrayList<>();
        sampleLocations.add(new LocationResponse("loc_1", "Phố Cổ Hội An", "Phố Cổ Hội An, Quảng Nam", 15.8801, 108.3380));
        sampleLocations.add(new LocationResponse("loc_2", "Đảo Phú Quốc", "Đảo Phú Quốc, Kiên Giang", 10.2899, 103.9840));
        sampleLocations.add(new LocationResponse("loc_3", "Bảo tàng Phụ nữ VN", "Bảo tàng Phụ nữ VN, Hà Nội", 21.0227, 105.8518));
        sampleLocations.add(new LocationResponse("loc_4", "Phố cổ Hàng Mã", "Phố cổ Hàng Mã, Hà Nội", 21.0362, 105.8481));
        sampleLocations.add(new LocationResponse("loc_5", "Đà Lạt", "Đà Lạt, Lâm Đồng", 11.9404, 108.4583));
        sampleLocations.add(new LocationResponse("loc_6", "Đà Nẵng", "Đà Nẵng, Việt Nam", 16.0544, 108.2022));
        sampleLocations.add(new LocationResponse("loc_7", "Vịnh Hạ Long", "Vịnh Hạ Long, Quảng Ninh", 20.9101, 107.1839));
        sampleLocations.add(new LocationResponse("loc_8", "Sapa", "Sapa, Lào Cai", 22.3364, 103.8438));
        sampleLocations.add(new LocationResponse("loc_9", "Nha Trang", "Nha Trang, Khánh Hòa", 12.2388, 109.1967));
        sampleLocations.add(new LocationResponse("loc_10", "Hồ Gươm", "Hồ Gươm, Hoàn Kiếm, Hà Nội", 21.0285, 105.8542));
        sampleLocations.add(new LocationResponse("loc_11", "Quảng trường Ba Đình", "Quảng trường Ba Đình, Hà Nội", 21.0368, 105.8347));
        sampleLocations.add(new LocationResponse("loc_12", "Thủ đô Hà Nội", "Thủ đô Hà Nội", 21.0285, 105.8542));
        sampleLocations.add(new LocationResponse("loc_13", "TP. Hồ Chí Minh", "TP. Hồ Chí Minh", 10.8231, 106.6297));

        if (query == null || query.trim().isEmpty()) {
            return Calls.response(sampleLocations);
        }

        String q = query.toLowerCase().trim();
        List<LocationResponse> filtered = new ArrayList<>();
        for (LocationResponse loc : sampleLocations) {
            if ((loc.getName() != null && loc.getName().toLowerCase().contains(q)) ||
                (loc.getFormattedAddress() != null && loc.getFormattedAddress().toLowerCase().contains(q))) {
                filtered.add(loc);
            }
        }
        return Calls.response(filtered);
    }

    @Override
    public Call<List<SuggestedTour>> getSuggestedTours(String query) {
        return getSuggestedTours();
    }

    @Override
    public Call<List<SuggestedTour>> getSuggestedTours() {
        List<SuggestedTour> tours = new ArrayList<>();

        SuggestedTour t1 = new SuggestedTour();
        t1.setId("tour_1");
        t1.setTitle("Hành trình Chinh phục Hà Giang Loop & Sông Nho Quế");
        t1.setDestination("Hà Giang");
        t1.setDurationDays(4);
        t1.setEstimatedBudget(9500000.0);
        t1.setCategory("MOUNTAIN");
        t1.setRating(4.95);
        t1.setReviewCount(142);
        tours.add(t1);

        SuggestedTour t2 = new SuggestedTour();
        t2.setId("tour_2");
        t2.setTitle("Chuyến đi Đà Lạt - Mộng Mơ & Ngàn Hoa");
        t2.setDestination("Đà Lạt, Lâm Đồng");
        t2.setDurationDays(3);
        t2.setEstimatedBudget(4500000.0);
        t2.setCategory("RELAX");
        t2.setRating(4.9);
        t2.setReviewCount(98);
        tours.add(t2);

        return Calls.response(tours);
    }

    @Override
    public Call<SuggestedTour> getSuggestedTourById(Object id) {
        try {
            List<SuggestedTour> tours = getSuggestedTours().execute().body();
            if (tours != null && !tours.isEmpty()) {
                return Calls.response(tours.get(0));
            }
        } catch (Exception ignored) {}
        return Calls.response(null);
    }

    @Override
    public Call<Trip> importTour(Object id, ImportTourRequest request) {
        Trip trip = new Trip(
            String.valueOf(System.currentTimeMillis()),
            "1",
            request != null && request.getCustomTitle() != null ? request.getCustomTitle() : "Chuyến đi Hà Giang tự túc 2026",
            "Hà Giang",
            request != null && request.getStartDate() != null ? request.getStartDate() : "18-10-2026",
            "21-10-2026",
            9500000.0,
            "PLANNED"
        );
        return Calls.response(trip);
    }

    @Override
    public Call<LocationResponse> getLocationDetails(String placeId, String provider) {
        return Calls.response(new LocationResponse(placeId, "Đà Nẵng", "Thành phố Đà Nẵng, Việt Nam", 16.0544, 108.2022));
    }
}
