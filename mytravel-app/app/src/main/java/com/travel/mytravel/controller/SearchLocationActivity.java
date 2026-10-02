package com.travel.mytravel.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.travel.mytravel.R;
import com.travel.mytravel.adapter.LocationAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.ImportTourRequest;
import com.travel.mytravel.model.LocationResponse;
import com.travel.mytravel.model.SuggestedTour;
import com.travel.mytravel.model.Trip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchLocationActivity extends AppCompatActivity implements LocationAdapter.OnLocationClickListener {

    private EditText edtSearchLocation;
    private View btnClearInput;
    private RecyclerView rvSearchHistory;
    private View layoutEmptyState;
    private LocationAdapter adapter;
    private final List<LocationAdapter.LocationItem> allLocations = new ArrayList<>();
    private final List<LocationAdapter.LocationItem> filteredList = new ArrayList<>();
    private final List<SuggestedTour> suggestedToursList = new ArrayList<>();
    private ActivityResultLauncher<Intent> mapPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        mapPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String selectedLoc = result.getData().getStringExtra("selected_location");
                        if (selectedLoc != null) {
                            returnSelectedLocation(selectedLoc);
                        }
                    }
                }
        );

        initViews();
        initData();
        setupListeners();
        loadSuggestedTours();
    }

    private void initViews() {
        edtSearchLocation = findViewById(R.id.edtSearchLocation);
        btnClearInput = findViewById(R.id.btnClearInput);
        rvSearchHistory = findViewById(R.id.rvSearchHistory);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        View btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (rvSearchHistory != null) {
            rvSearchHistory.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LocationAdapter(filteredList, this);
            rvSearchHistory.setAdapter(adapter);
        }
    }

    private void initData() {
        allLocations.clear();
        allLocations.add(new LocationAdapter.LocationItem("Đà Nẵng", "Thành phố Đà Nẵng, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Hồ Gươm", "Đinh Tiên Hoàng, Hàng Trống, Hoàn Kiếm, Hà Nội"));
        allLocations.add(new LocationAdapter.LocationItem("Bà Nà Hills", "Hòa Vang, Đà Nẵng, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Quảng trường Ba Đình", "Hùng Vương, Điện Biên, Ba Đình, Hà Nội"));
        allLocations.add(new LocationAdapter.LocationItem("Phố Cổ Hội An", "Hội An, Quảng Nam, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Đà Lạt, Lâm Đồng", "Thành phố Đà Lạt, Lâm Đồng, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Vịnh Hạ Long", "Thành phố Hạ Long, Quảng Ninh, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Phú Quốc, Kiên Giang", "Thành phố Phú Quốc, Kiên Giang, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Sa Pa, Lào Cai", "Thị xã Sa Pa, Lào Cai, Việt Nam"));
        allLocations.add(new LocationAdapter.LocationItem("Nha Trang, Khánh Hòa", "Thành phố Nha Trang, Khánh Hòa, Việt Nam"));

        filteredList.clear();
        filteredList.addAll(allLocations);
        updateEmptyStateUI();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadSuggestedTours() {
        ApiClient.getService().getSuggestedTours().enqueue(new Callback<List<SuggestedTour>>() {
            @Override
            public void onResponse(@NonNull Call<List<SuggestedTour>> call, @NonNull Response<List<SuggestedTour>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    suggestedToursList.clear();
                    suggestedToursList.addAll(response.body());

                    for (SuggestedTour tour : response.body()) {
                        String title = "🚀 [Tour Gợi Ý] " + tour.getTitle();
                        String sub = (tour.getDestination() != null ? tour.getDestination() : "") 
                                + (tour.getDurationDays() != null ? " • " + tour.getDurationDays() + " ngày" : "");
                        allLocations.add(0, new LocationAdapter.LocationItem(title, sub));
                    }

                    filteredList.clear();
                    filteredList.addAll(allLocations);
                    updateEmptyStateUI();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SuggestedTour>> call, @NonNull Throwable t) {
            }
        });
    }

    private void setupListeners() {
        if (btnClearInput != null) {
            btnClearInput.setOnClickListener(v -> {
                if (edtSearchLocation != null) {
                    edtSearchLocation.setText("");
                }
            });
        }

        if (edtSearchLocation != null) {
            edtSearchLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (btnClearInput != null) {
                        btnClearInput.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                    }
                    filterLocations(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        TextView btnDoSearch = findViewById(R.id.btnDoSearch);
        if (btnDoSearch != null) {
            btnDoSearch.setOnClickListener(v -> {
                String query = edtSearchLocation != null ? edtSearchLocation.getText().toString().trim() : "";
                if (!query.isEmpty()) {
                    returnSelectedLocation(query);
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        TextView btnClearHistory = findViewById(R.id.btnClearHistory);
        if (btnClearHistory != null) {
            btnClearHistory.setOnClickListener(v -> {
                filteredList.clear();
                updateEmptyStateUI();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(this, "Đã xóa lịch sử tìm kiếm!", Toast.LENGTH_SHORT).show();
            });
        }

        // Popular Destination Chips
        setupPopularChip(R.id.chipDaNang, "Đà Nẵng, Việt Nam");
        setupPopularChip(R.id.chipHaNoi, "Hà Nội, Việt Nam");
        setupPopularChip(R.id.chipHCM, "TP. Hồ Chí Minh, Việt Nam");
        setupPopularChip(R.id.chipDaLat, "Đà Lạt, Lâm Đồng, Việt Nam");
        setupPopularChip(R.id.chipHaLong, "Vịnh Hạ Long, Quảng Ninh, Việt Nam");
        setupPopularChip(R.id.chipHoiAn, "Phố Cổ Hội An, Quảng Nam, Việt Nam");
        setupPopularChip(R.id.chipPhuQuoc, "Phú Quốc, Kiên Giang, Việt Nam");
        setupPopularChip(R.id.chipSaPa, "Sa Pa, Lào Cai, Việt Nam");
        setupPopularChip(R.id.chipNhaTrang, "Nha Trang, Khánh Hòa, Việt Nam");
    }

    private void setupPopularChip(int chipId, String locationString) {
        Chip chip = findViewById(chipId);
        if (chip != null) {
            chip.setOnClickListener(v -> returnSelectedLocation(locationString));
        }
    }

    private void filterLocations(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredList.clear();
            filteredList.addAll(allLocations);
            updateEmptyStateUI();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }

        ApiClient.getService().searchLocations(query).enqueue(new Callback<List<LocationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationResponse>> call, @NonNull Response<List<LocationResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    filteredList.clear();
                    for (LocationResponse loc : response.body()) {
                        String title = loc.getName();
                        if (loc.getTypes() != null && loc.getTypes().contains("RECOMMENDED_TOUR")) {
                            title = "🚀 [Tour Gợi Ý] " + title;
                        }
                        filteredList.add(new LocationAdapter.LocationItem(title, loc.getFormattedAddress()));
                    }
                    updateEmptyStateUI();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    filterLocationsOfflineFallback(query);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationResponse>> call, @NonNull Throwable t) {
                filterLocationsOfflineFallback(query);
            }
        });
    }

    private void filterLocationsOfflineFallback(String query) {
        filteredList.clear();
        String lower = query.trim().toLowerCase();
        for (LocationAdapter.LocationItem item : allLocations) {
            if (item.getName().toLowerCase().contains(lower) || item.getAddress().toLowerCase().contains(lower)) {
                filteredList.add(item);
            }
        }
        updateEmptyStateUI();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyStateUI() {
        if (filteredList.isEmpty()) {
            if (rvSearchHistory != null) rvSearchHistory.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvIcon = layoutEmptyState.findViewById(R.id.tvEmptyIcon);
                TextView tvTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvSub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvIcon != null) tvIcon.setText("🔍");
                if (tvTitle != null) tvTitle.setText("Không tìm thấy địa điểm");
                if (tvSub != null) tvSub.setText("Vui lòng thử lại với từ khóa tìm kiếm khác!");
                if (btnAction != null) btnAction.setVisibility(View.GONE);
            }
        } else {
            if (rvSearchHistory != null) rvSearchHistory.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLocationClick(LocationAdapter.LocationItem item) {
        if (item.getName() != null && item.getName().contains("[Tour Gợi Ý]")) {
            String cleanTitle = item.getName().replace("🚀 [Tour Gợi Ý]", "").trim();
            SuggestedTour matchedTour = null;
            for (SuggestedTour st : suggestedToursList) {
                if (st.getTitle() != null && st.getTitle().equalsIgnoreCase(cleanTitle)) {
                    matchedTour = st;
                    break;
                }
            }
            if (matchedTour != null) {
                showSuggestedTourPreviewDialog(
                        matchedTour.getId(),
                        matchedTour.getTitle(),
                        matchedTour.getDestination() != null ? matchedTour.getDestination() : cleanTitle,
                        matchedTour.getDurationDays() != null ? matchedTour.getDurationDays() : 3,
                        matchedTour.getEstimatedBudget() != null ? matchedTour.getEstimatedBudget() : 5000000.0
                );
                return;
            }
        }

        returnSelectedLocation(item.getName() + (item.getAddress() != null && !item.getAddress().isEmpty() ? ", " + item.getAddress() : ""));
    }

    private void showSuggestedTourPreviewDialog(String tourId, String title, String destination, int durationDays, double budget) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🗺️ Tour Gợi Ý: " + title);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String infoMsg = "📍 Điểm đến: " + destination + "\n"
                       + "⏱️ Thời gian: " + durationDays + " ngày\n"
                       + "💰 Ngân sách dự kiến: " + formatter.format(budget) + "đ\n\n"
                       + "Bạn có muốn áp dụng (Import) Tour gợi ý này để tự động khởi tạo chuyến đi cá nhân kèm toàn bộ lịch trình chi tiết không?";

        builder.setMessage(infoMsg);
        builder.setPositiveButton("🚀 Áp Dụng Tour Này", (dialog, which) -> {
            performImportTour(tourId, title);
        });
        builder.setNegativeButton("Chỉ chọn địa điểm", (dialog, which) -> {
            returnSelectedLocation(destination);
        });
        builder.setNeutralButton("Đóng", null);
        builder.show();
    }

    private void performImportTour(String tourId, String title) {
        String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        ImportTourRequest req = new ImportTourRequest(todayDate, title);

        Toast.makeText(this, "⏳ Đang tự động khởi tạo chuyến đi từ Tour...", Toast.LENGTH_SHORT).show();

        ApiClient.getService().importTour(tourId, req).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Trip createdTrip = response.body();
                    Toast.makeText(SearchLocationActivity.this, "🎉 Đã tạo chuyến đi từ Tour thành công!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SearchLocationActivity.this, TripDetailActivity.class);
                    intent.putExtra("trip_id", createdTrip.getId());
                    startActivity(intent);
                    finish();
                } else {
                    ApiClient.showError(SearchLocationActivity.this, response, "Khởi tạo chuyến đi từ Tour gợi ý thất bại!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
                ApiClient.handleFailure(SearchLocationActivity.this, t, "Lỗi kết nối khởi tạo Tour");
            }
        });
    }

    private void returnSelectedLocation(String locationString) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_location", locationString);
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "📍 Đã chọn: " + locationString, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
