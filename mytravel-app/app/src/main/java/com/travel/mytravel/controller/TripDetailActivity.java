package com.travel.mytravel.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.travel.mytravel.R;
import com.travel.mytravel.adapter.ItineraryAdapter;
import com.travel.mytravel.adapter.MediaAdapter;
import com.travel.mytravel.api.ApiClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.travel.mytravel.adapter.TripMemberAdapter;
import com.travel.mytravel.model.AddTripMemberRequest;
import com.travel.mytravel.model.AlbumRequest;
import com.travel.mytravel.model.ItineraryItem;
import com.travel.mytravel.model.MediaAlbum;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.TripMedia;
import com.travel.mytravel.model.TripMember;
import com.travel.mytravel.model.TripRequest;
import com.travel.mytravel.repository.GlobalDataCache;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailActivity extends AppCompatActivity implements ItineraryAdapter.OnItineraryClickListener {

    private String tripId = "1";
    private TextView tvDetailTitle, tvDetailDestination, tvDetailDates, tvDetailBudget;
    private RecyclerView rvDetailItineraries, rvDetailPhotos;
    private ChipGroup cgDaySelector;
    private View btnDetailUploadPhoto, layoutEmptyState;

    private ItineraryAdapter adapter;
    private MediaAdapter mediaAdapter;

    private final List<ItineraryItem> allItineraries = new ArrayList<>();
    private final List<ItineraryItem> displayedItineraries = new ArrayList<>();
    private final List<MediaAdapter.MediaItem> photoList = new ArrayList<>();

    private int currentSelectedDay = 1;
    private Trip currentTrip;

    private ActivityResultLauncher<String> albumPhotoPickerLauncher;
    private ActivityResultLauncher<String> timelinePhotoPickerLauncher;
    private ActivityResultLauncher<Intent> locationSearchLauncher;
    private EditText targetItineraryLocationEditText;
    private ItineraryItem editingTimelineItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        String idStr = getIntent().getStringExtra("trip_id");
        if (idStr != null) {
            tripId = idStr;
        } else {
            long idLong = getIntent().getLongExtra("trip_id", -1L);
            if (idLong != -1L) {
                tripId = String.valueOf(idLong);
            }
        }

        initViews();
        setupPhotoPickers();
        setupListeners();
        loadTripData();
    }

    private void initViews() {
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailDestination = findViewById(R.id.tvDetailDestination);
        tvDetailDates = findViewById(R.id.tvDetailDates);
        tvDetailBudget = findViewById(R.id.tvDetailBudget);

        rvDetailItineraries = findViewById(R.id.rvDetailItineraries);
        rvDetailPhotos = findViewById(R.id.rvDetailPhotos);
        cgDaySelector = findViewById(R.id.cgDaySelector);
        btnDetailUploadPhoto = findViewById(R.id.btnDetailUploadPhoto);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        if (rvDetailItineraries != null) {
            rvDetailItineraries.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ItineraryAdapter(displayedItineraries, this);
            rvDetailItineraries.setAdapter(adapter);
        }

        if (rvDetailPhotos != null) {
            rvDetailPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mediaAdapter = new MediaAdapter(photoList);
            rvDetailPhotos.setAdapter(mediaAdapter);
        }
    }

    private void setupPhotoPickers() {
        albumPhotoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadPhotoFile(uri, serverPhotoUrl -> {
                            loadTripPhotos();
                        });
                    }
                }
        );

        timelinePhotoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && editingTimelineItem != null) {
                        uploadPhotoFile(uri, serverPhotoUrl -> {
                            editingTimelineItem.setImageUrl(serverPhotoUrl);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            ApiClient.getService().updateItineraryItem(editingTimelineItem.getId(), editingTimelineItem).enqueue(new Callback<ItineraryItem>() {
                                @Override
                                public void onResponse(@NonNull Call<ItineraryItem> call, @NonNull Response<ItineraryItem> response) {
                                    Toast.makeText(TripDetailActivity.this, "📸 Đã lưu ảnh Timeline mốc lên máy chủ!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<ItineraryItem> call, @NonNull Throwable t) {
                                    Toast.makeText(TripDetailActivity.this, "📸 Đã đính kèm ảnh vào Timeline mốc!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                }
        );

        locationSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String selectedLoc = result.getData().getStringExtra("selected_location");
                        if (targetItineraryLocationEditText != null && selectedLoc != null) {
                            targetItineraryLocationEditText.setText(selectedLoc);
                        } else if (selectedLoc != null && currentTrip != null) {
                            currentTrip.setDestination(selectedLoc);
                            if (tvDetailDestination != null) {
                                tvDetailDestination.setText("📍 " + selectedLoc);
                            }
                            TripRequest req = new TripRequest(
                                    currentTrip.getTitle(),
                                    selectedLoc,
                                    currentTrip.getStartDate(),
                                    currentTrip.getEndDate(),
                                    currentTrip.getTotalBudget(),
                                    currentTrip.getStatus()
                            );
                            ApiClient.getService().updateTrip(tripId, req).enqueue(new Callback<Trip>() {
                                @Override
                                public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                                    Toast.makeText(TripDetailActivity.this, "📍 Đã đổi địa điểm thành: " + selectedLoc, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {}
                            });
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        View btnBackDetail = findViewById(R.id.btnBackDetail);
        if (btnBackDetail != null) {
            btnBackDetail.setOnClickListener(v -> finish());
        }

        View btnManageMembers = findViewById(R.id.btnManageMembers);
        if (btnManageMembers != null) {
            btnManageMembers.setOnClickListener(v -> showTripMembersDialog());
        }

        View cardManageMembers = findViewById(R.id.cardManageMembers);
        if (cardManageMembers != null) {
            cardManageMembers.setOnClickListener(v -> showTripMembersDialog());
        }

        if (tvDetailTitle != null) {
            tvDetailTitle.setOnClickListener(v -> showEditTripDialog());
        }

        if (tvDetailDestination != null) {
            tvDetailDestination.setOnClickListener(v -> {
                targetItineraryLocationEditText = null;
                Intent intent = new Intent(TripDetailActivity.this, SearchLocationActivity.class);
                locationSearchLauncher.launch(intent);
            });
        }

        if (btnDetailUploadPhoto != null) {
            btnDetailUploadPhoto.setOnClickListener(v -> {
                if (isTripOngoingOrCompleted()) {
                    albumPhotoPickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, "⚠️ Chuyến đi này chưa khởi hành! Chỉ có thể thêm ảnh khi đang đi hoặc đã đi.", Toast.LENGTH_LONG).show();
                }
            });
        }

        TextView btnAddDetailItinerary = findViewById(R.id.btnAddDetailItinerary);
        if (btnAddDetailItinerary != null) {
            btnAddDetailItinerary.setOnClickListener(v -> showAddNewItineraryDialog());
        }

        Button btnDeleteTrip = findViewById(R.id.btnDeleteTrip);
        if (btnDeleteTrip != null) {
            btnDeleteTrip.setOnClickListener(v -> showDeleteConfirmationDialog());
        }
    }

    private boolean isTripOngoingOrCompleted() {
        if (currentTrip == null || currentTrip.getStatus() == null) return false;
        String status = currentTrip.getStatus().toUpperCase();
        return status.contains("ONGOING") || status.contains("IN_PROGRESS") || status.contains("COMPLETED");
    }

    private void checkTripStatusForPhotoPermissions() {
        boolean canUpload = isTripOngoingOrCompleted();
        if (btnDetailUploadPhoto != null) {
            btnDetailUploadPhoto.setVisibility(canUpload ? View.VISIBLE : View.GONE);
        }
        if (rvDetailPhotos != null) {
            rvDetailPhotos.setVisibility(canUpload ? View.VISIBLE : View.GONE);
        }
    }

    private void loadTripPhotos() {
        photoList.clear();
        if (!isTripOngoingOrCompleted()) {
            if (rvDetailPhotos != null) rvDetailPhotos.setVisibility(View.GONE);
            if (mediaAdapter != null) mediaAdapter.notifyDataSetChanged();
            return;
        }

        ApiClient.getService().getAlbums(tripId).enqueue(new Callback<List<MediaAlbum>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaAlbum>> call, @NonNull Response<List<MediaAlbum>> response) {
                photoList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    for (MediaAlbum album : response.body()) {
                        if (album.getPhotos() != null) {
                            for (TripMedia photo : album.getPhotos()) {
                                String url = ApiClient.formatAvatarUrl(TripDetailActivity.this, photo.getPhotoUrl());
                                if (url != null) {
                                    photoList.add(new MediaAdapter.MediaItem(url));
                                }
                            }
                        }
                    }
                }

                if (rvDetailPhotos != null) {
                    rvDetailPhotos.setVisibility(photoList.isEmpty() ? View.GONE : View.VISIBLE);
                }
                if (mediaAdapter != null) {
                    mediaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaAlbum>> call, @NonNull Throwable t) {
                photoList.clear();
                if (rvDetailPhotos != null) rvDetailPhotos.setVisibility(View.GONE);
                if (mediaAdapter != null) mediaAdapter.notifyDataSetChanged();
            }
        });
    }

    private interface OnPhotoUploadedListener {
        void onUploaded(String serverPhotoUrl);
    }

    private void uploadPhotoFile(Uri imageUri, OnPhotoUploadedListener listener) {
        if (imageUri == null || tripId == null) return;
        Toast.makeText(this, "⏳ Đang tải ảnh lên máy chủ...", Toast.LENGTH_SHORT).show();

        ApiClient.getService().getAlbums(tripId).enqueue(new Callback<List<MediaAlbum>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaAlbum>> call, @NonNull Response<List<MediaAlbum>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String albumId = response.body().get(0).getId();
                    doUploadPhotoToAlbum(albumId, imageUri, listener);
                } else {
                    AlbumRequest newAlbumReq = new AlbumRequest("Kỷ niệm chuyến đi", "Ảnh đính kèm");
                    ApiClient.getService().createAlbum(tripId, newAlbumReq).enqueue(new Callback<MediaAlbum>() {
                        @Override
                        public void onResponse(@NonNull Call<MediaAlbum> call2, @NonNull Response<MediaAlbum> response2) {
                            if (response2.isSuccessful() && response2.body() != null) {
                                String albumId = response2.body().getId();
                                doUploadPhotoToAlbum(albumId, imageUri, listener);
                            } else {
                                ApiClient.showError(TripDetailActivity.this, response2, "Không thể tạo Album trên server!");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<MediaAlbum> call2, @NonNull Throwable t2) {
                            ApiClient.handleFailure(TripDetailActivity.this, t2, "Lỗi kết nối tạo Album!");
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaAlbum>> call, @NonNull Throwable t) {
                ApiClient.handleFailure(TripDetailActivity.this, t, "Lỗi kết nối danh sách Album!");
            }
        });
    }

    private void doUploadPhotoToAlbum(String albumId, Uri imageUri, OnPhotoUploadedListener listener) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return;
            byte[] bytes = getBytes(inputStream);
            inputStream.close();

            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", "photo_" + System.currentTimeMillis() + ".jpg", requestFile);
            List<MultipartBody.Part> files = new ArrayList<>();
            files.add(filePart);

            RequestBody captionPart = RequestBody.create(MediaType.parse("text/plain"), "Timeline photo");

            ApiClient.getService().uploadPhotosToAlbum(albumId, files, captionPart).enqueue(new Callback<List<TripMedia>>() {
                @Override
                public void onResponse(@NonNull Call<List<TripMedia>> call, @NonNull Response<List<TripMedia>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        TripMedia uploaded = response.body().get(0);
                        String photoUrl = ApiClient.formatAvatarUrl(TripDetailActivity.this, uploaded.getPhotoUrl());
                        Toast.makeText(TripDetailActivity.this, "📸 Tải ảnh lên máy chủ thành công!", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onUploaded(photoUrl);
                        }
                        loadTripPhotos();
                    } else {
                        ApiClient.showError(TripDetailActivity.this, response, "Tải ảnh lên máy chủ thất bại!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<TripMedia>> call, @NonNull Throwable t) {
                    ApiClient.handleFailure(TripDetailActivity.this, t, "Lỗi kết nối tải ảnh!");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể đọc tệp ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private int calculateDurationDays(String startDateStr, String endDateStr) {
        if (startDateStr == null || endDateStr == null) return 1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date start = sdf.parse(startDateStr);
            Date end = sdf.parse(endDateStr);
            if (start != null && end != null) {
                long diffMs = end.getTime() - start.getTime();
                long days = (diffMs / (1000 * 60 * 60 * 24)) + 1;
                return (int) Math.max(1, days);
            }
        } catch (Exception ignored) {}
        return 1;
    }

    private void setupDynamicDayChips() {
        if (cgDaySelector == null) return;
        cgDaySelector.removeAllViews();

        int maxDurationDays = 1;
        if (currentTrip != null && currentTrip.getStartDate() != null && currentTrip.getEndDate() != null) {
            maxDurationDays = calculateDurationDays(currentTrip.getStartDate(), currentTrip.getEndDate());
        }

        int maxItineraryDay = 1;
        for (ItineraryItem item : allItineraries) {
            if (item.getDayNumber() != null && item.getDayNumber() > maxItineraryDay) {
                maxItineraryDay = item.getDayNumber();
            }
        }

        int totalDaysToShow = Math.max(maxDurationDays, maxItineraryDay);

        for (int i = 1; i <= totalDaysToShow; i++) {
            final int dayNum = i;
            Chip chip = new Chip(this);
            chip.setText("Ngày " + dayNum);
            chip.setCheckable(true);
            chip.setClickable(true);
            if (dayNum == currentSelectedDay) {
                chip.setChecked(true);
            }
            chip.setOnClickListener(v -> filterItinerariesByDay(dayNum));
            cgDaySelector.addView(chip);
        }

        if (totalDaysToShow < maxDurationDays) {
            final int nextDay = totalDaysToShow + 1;
            Chip addChip = new Chip(this);
            addChip.setText("+ Ngày");
            addChip.setClickable(true);
            addChip.setOnClickListener(v -> addNewDay(nextDay));
            cgDaySelector.addView(addChip);
        }
    }

    private void addNewDay(int newDayNum) {
        int maxDurationDays = 1;
        if (currentTrip != null && currentTrip.getStartDate() != null && currentTrip.getEndDate() != null) {
            maxDurationDays = calculateDurationDays(currentTrip.getStartDate(), currentTrip.getEndDate());
        }

        if (newDayNum > maxDurationDays) {
            Toast.makeText(this, "⚠️ Đã đạt số ngày tối đa (" + maxDurationDays + " ngày)! Hãy chỉnh sửa thời gian chuyến đi để thêm ngày.", Toast.LENGTH_LONG).show();
            return;
        }

        currentSelectedDay = newDayNum;
        ItineraryItem newDayItem = new ItineraryItem(
                String.valueOf(System.currentTimeMillis()),
                tripId,
                newDayNum,
                "08:30:00",
                "Hoạt động sáng Ngày " + newDayNum,
                "Điểm đến Ngày " + newDayNum,
                0.0, 0.0, "p_day",
                "Tự do tham quan"
        );
        allItineraries.add(newDayItem);

        setupDynamicDayChips();
        filterItinerariesByDay(newDayNum);
        Toast.makeText(this, "+ Đã khởi tạo lịch trình cho Ngày " + newDayNum, Toast.LENGTH_SHORT).show();
    }

    private void filterItinerariesByDay(int dayNum) {
        currentSelectedDay = dayNum;
        displayedItineraries.clear();
        for (ItineraryItem item : allItineraries) {
            if (item.getDayNumber() != null && item.getDayNumber() == dayNum) {
                displayedItineraries.add(item);
            }
        }
        if (displayedItineraries.isEmpty()) {
            ItineraryItem placeholder = new ItineraryItem(
                    String.valueOf(System.currentTimeMillis()),
                    tripId,
                    dayNum,
                    "08:30:00",
                    "Hoạt động sáng Ngày " + dayNum,
                    "Điểm đến Ngày " + dayNum,
                    0.0, 0.0, "p_day",
                    "Tự do tham quan"
            );
            allItineraries.add(placeholder);
            displayedItineraries.add(placeholder);
        }
        updateItineraryEmptyStateUI();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateItineraryEmptyStateUI() {
        if (displayedItineraries.isEmpty()) {
            if (rvDetailItineraries != null) rvDetailItineraries.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvEmptyIcon = layoutEmptyState.findViewById(R.id.tvEmptyIcon);
                TextView tvEmptyTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvEmptySub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnEmptyAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvEmptyIcon != null) tvEmptyIcon.setText("📍");
                if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có mốc lịch trình nào");
                if (tvEmptySub != null) tvEmptySub.setText("Thêm mốc hoạt động đầu tiên cho Ngày " + currentSelectedDay + "!");
                if (btnEmptyAction != null) {
                    btnEmptyAction.setText("+ Thêm mốc lịch trình");
                    btnEmptyAction.setOnClickListener(v -> showAddNewItineraryDialog());
                }
            }
        } else {
            if (rvDetailItineraries != null) rvDetailItineraries.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private boolean isValidUuid(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            UUID.fromString(str.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void loadTripData() {
        if (!isValidUuid(tripId)) {
            ApiClient.getService().getTrips(0, 1).enqueue(new Callback<PageResponse<Trip>>() {
                @Override
                public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getContent() != null && !response.body().getContent().isEmpty()) {
                        Trip latest = response.body().getContent().get(0);
                        tripId = latest.getId();
                        loadTripDataWithValidId();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                }
            });
            return;
        }

        loadTripDataWithValidId();
    }

    private void loadTripDataWithValidId() {
        ApiClient.getService().getTripById(tripId).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentTrip = response.body();
                    if (tvDetailTitle != null) tvDetailTitle.setText(currentTrip.getTitle());
                    if (tvDetailDestination != null) tvDetailDestination.setText("📍 " + currentTrip.getDestination());
                    if (tvDetailDates != null) tvDetailDates.setText(currentTrip.getStartDate() + " ➔ " + currentTrip.getEndDate());
                    if (tvDetailBudget != null) {
                        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                        tvDetailBudget.setText(formatter.format(currentTrip.getTotalBudget()) + "đ");
                    }
                    checkTripStatusForPhotoPermissions();
                    loadTripPhotos();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
            }
        });

        loadItineraries();
    }

    private void loadItineraries() {
        ApiClient.getService().getItineraries(tripId).enqueue(new Callback<List<ItineraryItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ItineraryItem>> call, @NonNull Response<List<ItineraryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allItineraries.clear();
                    allItineraries.addAll(response.body());

                    syncItineraryPhotosWithAlbums();

                    setupDynamicDayChips();
                    filterItinerariesByDay(currentSelectedDay);
                } else {
                    allItineraries.clear();
                    filterItinerariesByDay(currentSelectedDay);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ItineraryItem>> call, @NonNull Throwable t) {
                allItineraries.clear();
                filterItinerariesByDay(currentSelectedDay);
            }
        });
    }

    private void syncItineraryPhotosWithAlbums() {
        ApiClient.getService().getAlbums(tripId).enqueue(new Callback<List<MediaAlbum>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaAlbum>> call, @NonNull Response<List<MediaAlbum>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<TripMedia> allPhotos = new ArrayList<>();
                    for (MediaAlbum album : response.body()) {
                        if (album.getPhotos() != null) {
                            allPhotos.addAll(album.getPhotos());
                        }
                    }

                    if (!allPhotos.isEmpty()) {
                        for (int i = 0; i < allItineraries.size(); i++) {
                            ItineraryItem item = allItineraries.get(i);
                            if (item.getImageUrl() == null || item.getImageUrl().trim().isEmpty()) {
                                if (i < allPhotos.size()) {
                                    String photoUrl = ApiClient.formatAvatarUrl(TripDetailActivity.this, allPhotos.get(i).getPhotoUrl());
                                    item.setImageUrl(photoUrl);
                                }
                            }
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaAlbum>> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onItemClick(ItineraryItem item, int position) {
        showEditItineraryDialog(item, position);
    }

    private void showEditItineraryDialog(ItineraryItem item, int position) {
        editingTimelineItem = item;
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_itinerary, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogItineraryTitle);
        EditText edtTime = dialogView.findViewById(R.id.edtDialogTime);
        EditText edtTitle = dialogView.findViewById(R.id.edtDialogActivityTitle);
        EditText edtLocation = dialogView.findViewById(R.id.edtDialogLocation);
        EditText edtNote = dialogView.findViewById(R.id.edtDialogNote);
        Button btnAttachPhoto = dialogView.findViewById(R.id.btnDialogAttachPhoto);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDelete);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSave);

        if (tvTitle != null) tvTitle.setText("✎ Chỉnh Sửa Lịch Trình (Ngày " + item.getDayNumber() + ")");
        if (edtTime != null) {
            edtTime.setText(item.getActivityTime());
            edtTime.setFocusable(false);
            edtTime.setClickable(true);
            edtTime.setOnClickListener(v -> showTimePickerDialog(this, edtTime));
        }
        if (edtTitle != null) edtTitle.setText(item.getActivityName());
        if (edtLocation != null) {
            edtLocation.setText(item.getLocationName());
            edtLocation.setFocusable(false);
            edtLocation.setClickable(true);
            edtLocation.setOnClickListener(v -> {
                targetItineraryLocationEditText = edtLocation;
                Intent intent = new Intent(TripDetailActivity.this, SearchLocationActivity.class);
                locationSearchLauncher.launch(intent);
            });
        }
        if (edtNote != null) edtNote.setText(item.getNote());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnAttachPhoto != null) {
            btnAttachPhoto.setOnClickListener(v -> {
                if (isTripOngoingOrCompleted()) {
                    dialog.dismiss();
                    timelinePhotoPickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, "⚠️ Chuyến đi này chưa khởi hành! Chỉ có thể thêm ảnh Timeline khi đang đi hoặc đã đi.", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                item.setActivityTime(normalizeTimeFormat(edtTime.getText().toString().trim()));
                item.setActivityName(edtTitle.getText().toString().trim());
                item.setLocationName(edtLocation.getText().toString().trim());
                item.setNote(edtNote.getText().toString().trim());

                ApiClient.getService().updateItineraryItem(item.getId(), item).enqueue(new Callback<ItineraryItem>() {
                    @Override
                    public void onResponse(@NonNull Call<ItineraryItem> call, @NonNull Response<ItineraryItem> response) {
                        if (adapter != null) {
                            adapter.notifyItemChanged(position);
                        }
                        if (response.isSuccessful()) {
                            Toast.makeText(TripDetailActivity.this, "Đã cập nhật mốc lịch trình!", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMsg = parseErrorMessage(response);
                            Toast.makeText(TripDetailActivity.this, "⚠️ " + errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ItineraryItem> call, @NonNull Throwable t) {
                        Toast.makeText(TripDetailActivity.this, "Lỗi cập nhật lịch trình! Đã lưu tạm thời.", Toast.LENGTH_SHORT).show();
                        if (adapter != null) {
                            adapter.notifyItemChanged(position);
                        }
                    }
                });
                dialog.dismiss();
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (position >= 0 && position < displayedItineraries.size()) {
                    ItineraryItem removed = displayedItineraries.remove(position);
                    allItineraries.remove(removed);
                    updateItineraryEmptyStateUI();
                    if (adapter != null) {
                        adapter.notifyItemRemoved(position);
                    }

                    ApiClient.getService().deleteItineraryItem(removed.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            Toast.makeText(TripDetailActivity.this, "Đã xóa mốc lịch trình!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                    });
                }
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void showTimePickerDialog(Context context, EditText edtTime) {
        if (context == null || edtTime == null) return;
        int hour = 8;
        int minute = 0;
        String currentText = edtTime.getText().toString().trim();
        if (!currentText.isEmpty()) {
            try {
                String[] parts = currentText.split(":");
                if (parts.length >= 2) {
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                }
            } catch (Exception ignored) {}
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, selectedMinute) -> {
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, selectedMinute);
                    edtTime.setText(formattedTime);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private String normalizeTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return "09:00:00";
        }
        String trimmed = time.trim();
        try {
            String[] parts = trimmed.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                return String.format(Locale.getDefault(), "%02d:%02d:00", hour, min);
            } else if (parts.length >= 3) {
                int hour = Integer.parseInt(parts[0]);
                int min = Integer.parseInt(parts[1]);
                int sec = Integer.parseInt(parts[2]);
                return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
            }
        } catch (Exception ignored) {}
        return trimmed;
    }

    private String parseErrorMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau!";
        }
        try {
            String errorJson = response.errorBody().string();
            JSONObject jsonObject = new JSONObject(errorJson);
            if (jsonObject.has("message")) {
                return jsonObject.getString("message");
            }
        } catch (Exception ignored) {}
        return "Đã xảy ra lỗi hệ thống (mã " + response.code() + "). Vui lòng thử lại sau!";
    }

    private void handleAddItineraryError(Response<?> response, ItineraryItem newItem) {
        String errorMsg = parseErrorMessage(response);
        Toast.makeText(TripDetailActivity.this, "⚠️ " + errorMsg, Toast.LENGTH_LONG).show();
        if (newItem.getId() == null) {
            newItem.setId(String.valueOf(System.currentTimeMillis()));
        }
        allItineraries.add(newItem);
        setupDynamicDayChips();
        filterItinerariesByDay(currentSelectedDay);
    }

    private void handleAddItineraryFallbackLocally(ItineraryItem newItem) {
        Toast.makeText(TripDetailActivity.this, "⚠️ Không thể kết nối máy chủ. Đã lưu lịch trình tạm thời!", Toast.LENGTH_SHORT).show();
        if (newItem.getId() == null) {
            newItem.setId(String.valueOf(System.currentTimeMillis()));
        }
        allItineraries.add(newItem);
        setupDynamicDayChips();
        filterItinerariesByDay(currentSelectedDay);
    }

    private void showAddNewItineraryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_itinerary, null);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogItineraryTitle);
        EditText edtTime = dialogView.findViewById(R.id.edtDialogTime);
        EditText edtTitle = dialogView.findViewById(R.id.edtDialogActivityTitle);
        EditText edtLocation = dialogView.findViewById(R.id.edtDialogLocation);
        EditText edtNote = dialogView.findViewById(R.id.edtDialogNote);
        Button btnAttachPhoto = dialogView.findViewById(R.id.btnDialogAttachPhoto);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDelete);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSave);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (tvTitle != null) tvTitle.setText("+ Thêm Mốc Lịch Trình (Ngày " + currentSelectedDay + ")");
        if (btnDelete != null) btnDelete.setVisibility(View.GONE);
        if (btnAttachPhoto != null) {
            btnAttachPhoto.setVisibility(View.VISIBLE);
            btnAttachPhoto.setOnClickListener(v -> {
                if (isTripOngoingOrCompleted()) {
                    dialog.dismiss();
                    timelinePhotoPickerLauncher.launch("image/*");
                } else {
                    Toast.makeText(this, "⚠️ Chuyến đi này chưa khởi hành! Chỉ có thể thêm ảnh Timeline khi đang đi hoặc đã đi.", Toast.LENGTH_LONG).show();
                }
            });
        }
        if (btnSave != null) {
            btnSave.setText("Thêm mốc mới");
            btnSave.setOnClickListener(v -> {
                String time = edtTime.getText().toString().trim();
                String title = edtTitle.getText().toString().trim();
                String loc = edtLocation.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                String formattedTime = normalizeTimeFormat(time);

                ItineraryItem newItem = new ItineraryItem(
                        null,
                        tripId,
                        currentSelectedDay,
                        formattedTime,
                        title.isEmpty() ? "Hoạt động tự chọn" : title,
                        loc,
                        0.0, 0.0, "p_new",
                        note
                );

                ApiClient.getService().addItineraryItem(newItem).enqueue(new Callback<ItineraryItem>() {
                    @Override
                    public void onResponse(@NonNull Call<ItineraryItem> call, @NonNull Response<ItineraryItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            allItineraries.add(response.body());
                            setupDynamicDayChips();
                            filterItinerariesByDay(currentSelectedDay);
                            Toast.makeText(TripDetailActivity.this, "+ Thêm mốc lịch trình Ngày " + currentSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Primary API failed (e.g. 500 error), try addItineraryItemToTrip endpoint
                            ApiClient.getService().addItineraryItemToTrip(tripId, newItem).enqueue(new Callback<ItineraryItem>() {
                                @Override
                                public void onResponse(@NonNull Call<ItineraryItem> call2, @NonNull Response<ItineraryItem> response2) {
                                    if (response2.isSuccessful() && response2.body() != null) {
                                        allItineraries.add(response2.body());
                                        setupDynamicDayChips();
                                        filterItinerariesByDay(currentSelectedDay);
                                        Toast.makeText(TripDetailActivity.this, "+ Thêm mốc lịch trình Ngày " + currentSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        handleAddItineraryError(response, newItem);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ItineraryItem> call2, @NonNull Throwable t2) {
                                    handleAddItineraryError(response, newItem);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ItineraryItem> call, @NonNull Throwable t) {
                        ApiClient.getService().addItineraryItemToTrip(tripId, newItem).enqueue(new Callback<ItineraryItem>() {
                            @Override
                            public void onResponse(@NonNull Call<ItineraryItem> call2, @NonNull Response<ItineraryItem> response2) {
                                if (response2.isSuccessful() && response2.body() != null) {
                                    allItineraries.add(response2.body());
                                    setupDynamicDayChips();
                                    filterItinerariesByDay(currentSelectedDay);
                                    Toast.makeText(TripDetailActivity.this, "+ Thêm mốc lịch trình Ngày " + currentSelectedDay + " thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    handleAddItineraryFallbackLocally(newItem);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ItineraryItem> call2, @NonNull Throwable t2) {
                                handleAddItineraryFallbackLocally(newItem);
                            }
                        });
                    }
                });

                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void showEditTripDialog() {
        if (currentTrip == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✎ Chỉnh Sửa Chuyến Đi");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText edtTitle = new EditText(this);
        edtTitle.setHint("Tên chuyến đi");
        edtTitle.setText(currentTrip.getTitle());
        layout.addView(edtTitle);

        final EditText edtDest = new EditText(this);
        edtDest.setHint("Điểm đến");
        edtDest.setText(currentTrip.getDestination());
        layout.addView(edtDest);

        final EditText edtBudget = new EditText(this);
        edtBudget.setHint("Ngân sách dự kiến");
        edtBudget.setText(String.valueOf(currentTrip.getTotalBudget()));
        layout.addView(edtBudget);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = edtTitle.getText().toString().trim();
            String dest = edtDest.getText().toString().trim();
            double budget = currentTrip.getTotalBudget();
            try {
                budget = Double.parseDouble(edtBudget.getText().toString().trim());
            } catch (Exception ignored) {}

            TripRequest req = new TripRequest(
                    title,
                    dest,
                    currentTrip.getStartDate(),
                    currentTrip.getEndDate(),
                    budget,
                    currentTrip.getStatus()
            );

            ApiClient.getService().updateTrip(tripId, req).enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(@NonNull Call<Trip> call, @NonNull Response<Trip> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentTrip = response.body();
                        GlobalDataCache.getInstance().invalidateTripsCache();
                        if (tvDetailTitle != null) tvDetailTitle.setText(currentTrip.getTitle());
                        if (tvDetailDestination != null) tvDetailDestination.setText("📍 " + currentTrip.getDestination());
                        if (tvDetailBudget != null) {
                            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                            tvDetailBudget.setText(formatter.format(currentTrip.getTotalBudget()) + "đ");
                        }
                        Toast.makeText(TripDetailActivity.this, "Cập nhật chuyến đi thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        ApiClient.showError(TripDetailActivity.this, response, "Cập nhật chuyến đi thất bại!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Trip> call, @NonNull Throwable t) {
                    ApiClient.handleFailure(TripDetailActivity.this, t, "Lỗi kết nối cập nhật chuyến đi");
                }
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showTripMembersDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_manage_members, null);

        EditText edtEmail = dialogView.findViewById(R.id.edtMemberEmail);
        Spinner spnRole = dialogView.findViewById(R.id.spnMemberRole);
        Button btnInvite = dialogView.findViewById(R.id.btnInviteMember);
        Button btnClose = dialogView.findViewById(R.id.btnCloseMembers);
        TextView tvHeader = dialogView.findViewById(R.id.tvMemberListHeader);
        RecyclerView rvMembers = dialogView.findViewById(R.id.rvTripMembers);

        List<TripMember> memberList = new ArrayList<>();
        final TripMemberAdapter[] adapterHolder = new TripMemberAdapter[1];

        TripMemberAdapter memberAdapter = new TripMemberAdapter(memberList, (member, position) -> {
            String targetUserId = member.getUserId() != null ? member.getUserId() : member.getId();
            ApiClient.getService().removeTripMember(tripId, targetUserId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (position >= 0 && position < memberList.size()) {
                        memberList.remove(position);
                        if (adapterHolder[0] != null) {
                            adapterHolder[0].notifyItemRemoved(position);
                        }
                        if (tvHeader != null) tvHeader.setText("Danh sách thành viên hiện tại (" + memberList.size() + ")");
                        Toast.makeText(TripDetailActivity.this, "Đã xóa thành viên khỏi nhóm!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(TripDetailActivity.this, "Lỗi khi xóa thành viên!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        adapterHolder[0] = memberAdapter;

        if (rvMembers != null) {
            rvMembers.setLayoutManager(new LinearLayoutManager(this));
            rvMembers.setAdapter(memberAdapter);
        }

        if (spnRole != null) {
            String[] roles = new String[]{"Biên tập viên (Có quyền chỉnh sửa)", "Người xem (Chỉ được xem)"};
            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnRole.setAdapter(roleAdapter);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Runnable loadMembersAction = () -> {
            ApiClient.getService().getTripMembers(tripId).enqueue(new Callback<List<TripMember>>() {
                @Override
                public void onResponse(@NonNull Call<List<TripMember>> call, @NonNull Response<List<TripMember>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        memberList.clear();
                        memberList.addAll(response.body());
                        memberAdapter.notifyDataSetChanged();
                        if (tvHeader != null) tvHeader.setText("Danh sách thành viên hiện tại (" + memberList.size() + ")");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<TripMember>> call, @NonNull Throwable t) {}
            });
        };

        loadMembersAction.run();

        if (btnInvite != null) {
            btnInvite.setOnClickListener(v -> {
                String email = edtEmail != null ? edtEmail.getText().toString().trim() : "";
                if (email.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập email thành viên cần mời!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedRole = (spnRole != null && spnRole.getSelectedItemPosition() == 1) ? "VIEWER" : "EDITOR";
                AddTripMemberRequest req = new AddTripMemberRequest(email, selectedRole);

                ApiClient.getService().addTripMember(tripId, req).enqueue(new Callback<TripMember>() {
                    @Override
                    public void onResponse(@NonNull Call<TripMember> call, @NonNull Response<TripMember> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            memberList.add(response.body());
                            memberAdapter.notifyItemInserted(memberList.size() - 1);
                            if (tvHeader != null) tvHeader.setText("Danh sách thành viên hiện tại (" + memberList.size() + ")");
                            if (edtEmail != null) edtEmail.setText("");
                            Toast.makeText(TripDetailActivity.this, "Đã gửi lời mời tới " + email + "!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TripDetailActivity.this, "Không thể mời thành viên!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TripMember> call, @NonNull Throwable t) {
                        Toast.makeText(TripDetailActivity.this, "Lỗi kết nối khi mời thành viên!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa Chuyến Đi");
        builder.setMessage("Bạn có chắc chắn muốn xóa chuyến đi này khỏi danh sách không?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ApiClient.getService().deleteTrip(tripId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    GlobalDataCache.getInstance().invalidateTripsCache();
                    Toast.makeText(TripDetailActivity.this, "Đã xóa chuyến đi!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                }
            });
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
