package com.travel.mytravel;

import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.api.ApiService;
import com.travel.mytravel.model.ChangePasswordRequest;
import com.travel.mytravel.model.Expense;
import com.travel.mytravel.model.ExpenseSummary;
import com.travel.mytravel.model.ForgotPasswordRequest;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.LoginResponse;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.ResetPasswordRequest;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.UserProfile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class ApiIntegrationTest {

    private ApiService apiService;

    @Before
    public void setUp() {
        apiService = ApiClient.getService();
        Assert.assertNotNull("ApiService instance should not be null", apiService);
    }

    @Test
    public void test01_LoginApi() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "admin");
        credentials.put("password", "123");

        Response<LoginResponse> response = apiService.login(credentials).execute();
        Assert.assertTrue("Login response should be successful", response.isSuccessful());

        LoginResponse loginResponse = response.body();
        Assert.assertNotNull("LoginResponse body should not be null", loginResponse);
        Assert.assertNotNull("Access Token should be present", loginResponse.getAccessToken());
        Assert.assertEquals("admin", loginResponse.getUsername());

        System.out.println("✅ [API Test 1] Login API Success: Token=" + loginResponse.getAccessToken());
    }

    @Test
    public void test02_GetUserProfileApi() throws Exception {
        Response<UserProfile> response = apiService.getUserProfile().execute();
        Assert.assertTrue("GetUserProfile should be successful", response.isSuccessful());

        UserProfile profile = response.body();
        Assert.assertNotNull("UserProfile should not be null", profile);
        Assert.assertEquals("Bùi Ngọc Đ", profile.getFullName());
        Assert.assertEquals("admin@travel.com", profile.getEmail());

        System.out.println("✅ [API Test 2] GetUserProfile API Success: Name=" + profile.getFullName());
    }

    @Test
    public void test03_CreateTripApi() throws Exception {
        Trip newTrip = new Trip(
                null,
                "1",
                "Chuyến đi Hà Nội 3N2Đ",
                "Thủ đô Hà Nội",
                "15-11-2026",
                "18-11-2026",
                15000000.0,
                "PLANNED"
        );

        Response<Trip> response = apiService.createTrip(newTrip).execute();
        Assert.assertTrue("CreateTrip should be successful", response.isSuccessful());

        Trip created = response.body();
        Assert.assertNotNull("Created trip should not be null", created);
        Assert.assertNotNull("Generated trip ID should not be null", created.getId());
        Assert.assertEquals("Chuyến đi Hà Nội 3N2Đ", created.getTitle());

        System.out.println("✅ [API Test 3] CreateTrip API Success: ID=" + created.getId() + ", Title=" + created.getTitle());
    }

    @Test
    public void test04_GetTripsApi() throws Exception {
        Response<PageResponse<Trip>> response = apiService.getTrips(0, 10).execute();
        Assert.assertTrue("GetTrips should be successful", response.isSuccessful());

        PageResponse<Trip> page = response.body();
        Assert.assertNotNull("PageResponse should not be null", page);
        List<Trip> trips = page.getContent();
        Assert.assertNotNull("Trips list should not be null", trips);
        Assert.assertTrue("Trips list should contain items", trips.size() > 0);

        System.out.println("✅ [API Test 4] GetTrips API Success: Total Trips=" + trips.size());
        for (Trip t : trips) {
            System.out.println("   -> Trip ID=" + t.getId() + " | " + t.getTitle() + " (" + t.getStatus() + ")");
        }
    }

    @Test
    public void test05_GetItinerariesApi() throws Exception {
        String tripId = "1";
        Response<List<ItineraryItem>> response = apiService.getItineraries(tripId).execute();
        Assert.assertTrue("GetItineraries should be successful", response.isSuccessful());

        List<ItineraryItem> items = response.body();
        Assert.assertNotNull("Itineraries list should not be null", items);
        Assert.assertTrue("Itineraries list should contain items", items.size() > 0);

        System.out.println("✅ [API Test 5] GetItineraries API Success: Total Itinerary Items=" + items.size());
        for (ItineraryItem item : items) {
            System.out.println("   -> Day " + item.getDayNumber() + " | " + item.getActivityTime() + " - " + item.getActivityName() + " (" + item.getLocationName() + ")");
        }
    }

    @Test
    public void test06_AddItineraryItemApi() throws Exception {
        String tripId = "1";
        ItineraryItem newItem = new ItineraryItem(
                null,
                tripId,
                1,
                "20:00:00",
                "Thưởng thức chè Bát Bảo phố cổ",
                "Phố Hàng Bạc, Hà Nội",
                21.0333, 105.8500,
                "p_test",
                "Trải nghiệm ẩm thực đêm"
        );

        Response<ItineraryItem> response = apiService.addItineraryItem(newItem).execute();
        Assert.assertTrue("AddItineraryItem should be successful", response.isSuccessful());

        ItineraryItem createdItem = response.body();
        Assert.assertNotNull("Created itinerary item should not be null", createdItem);
        Assert.assertNotNull("Created item ID should not be null", createdItem.getId());

        System.out.println("✅ [API Test 6] AddItineraryItem API Success: Item ID=" + createdItem.getId() + ", Activity=" + createdItem.getActivityName());
    }

    @Test
    public void test07_ExpensesApi() throws Exception {
        String tripId = "1";
        Expense newExpense = new Expense(
                null,
                tripId,
                450000.0,
                "FOOD",
                "Ăn trưa Bún chả Hương Liên",
                "15-11-2026",
                "CASH"
        );

        Response<Expense> addResponse = apiService.addExpense(newExpense).execute();
        Assert.assertTrue("AddExpense should be successful", addResponse.isSuccessful());

        Response<List<Expense>> listResponse = apiService.getExpenses(tripId).execute();
        Assert.assertTrue("GetExpenses should be successful", listResponse.isSuccessful());

        List<Expense> expenses = listResponse.body();
        Assert.assertNotNull("Expenses list should not be null", expenses);

        Response<ExpenseSummary> totalResponse = apiService.getExpenseSummary(tripId).execute();
        Assert.assertTrue("GetExpenseSummary should be successful", totalResponse.isSuccessful());

        System.out.println("✅ [API Test 7] Expenses API Success: Total Expenses Items=" + expenses.size() + ", Total Spent=" + totalResponse.body().getTotalSpent() + "đ");
    }

    @Test
    public void test08_AuthOperationsApi() throws Exception {
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest("admin@travel.com");
        Response<Void> forgotRes = apiService.forgotPassword(forgotReq).execute();
        Assert.assertTrue("ForgotPassword API should be successful", forgotRes.isSuccessful());

        ResetPasswordRequest resetReq = new ResetPasswordRequest("admin@travel.com", "123456", "NewAdmin123@");
        Response<Void> resetRes = apiService.resetPassword(resetReq).execute();
        Assert.assertTrue("ResetPassword API should be successful", resetRes.isSuccessful());

        ChangePasswordRequest changeReq = new ChangePasswordRequest("123", "NewAdmin123@");
        Response<Void> changeRes = apiService.changePassword(changeReq).execute();
        Assert.assertTrue("ChangePassword API should be successful", changeRes.isSuccessful());

        System.out.println("✅ [API Test 8] Auth Operations API (Forgot/Reset/Change Pass) Success!");
    }
}
