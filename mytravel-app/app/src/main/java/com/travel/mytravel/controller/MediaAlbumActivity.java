package com.travel.mytravel.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.adapter.MediaAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.MediaAlbum;
import com.travel.mytravel.model.TripMedia;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaAlbumActivity extends AppCompatActivity {

    private Long tripId = 1L;
    private RecyclerView rvMediaPhotos;
    private View layoutEmptyState;
    private MediaAdapter adapter;
    private final List<MediaAdapter.MediaItem> photoList = new ArrayList<>();
    private ActivityResultLauncher<String> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_album);

        tripId = getIntent().getLongExtra("trip_id", 1L);

        initViews();
        setupPhotoPicker();
        setupListeners();
        loadAlbumsAndPhotos();
    }

    private void initViews() {
        rvMediaPhotos = findViewById(R.id.rvMediaPhotos);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        if (rvMediaPhotos != null) {
            rvMediaPhotos.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new MediaAdapter(photoList);
            rvMediaPhotos.setAdapter(adapter);
        }
    }

    private void setupPhotoPicker() {
        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        photoList.add(0, new MediaAdapter.MediaItem(uri));
                        updateEmptyStateUI();
                        if (adapter != null) {
                            adapter.notifyItemInserted(0);
                        }
                        if (rvMediaPhotos != null) {
                            rvMediaPhotos.scrollToPosition(0);
                        }

                        List<MultipartBody.Part> parts = new ArrayList<>();
                        RequestBody captionBody = RequestBody.create(MediaType.parse("text/plain"), "Ảnh kỷ niệm chuyến đi");

                        ApiClient.getService().uploadPhotosToAlbum(10L, parts, captionBody).enqueue(new Callback<List<TripMedia>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<TripMedia>> call, @NonNull Response<List<TripMedia>> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MediaAlbumActivity.this, "📸 Tải ảnh lên album thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    ApiClient.showError(MediaAlbumActivity.this, response, "Tải ảnh lên album thất bại!");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<TripMedia>> call, @NonNull Throwable t) {
                                ApiClient.handleFailure(MediaAlbumActivity.this, t, "Lỗi kết nối tải ảnh lên");
                            }
                        });
                    }
                }
        );
    }

    private void setupListeners() {
        View btnBackMedia = findViewById(R.id.btnBackMedia);
        if (btnBackMedia != null) {
            btnBackMedia.setOnClickListener(v -> finish());
        }

        View btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        if (btnUploadPhoto != null) {
            btnUploadPhoto.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));
        }
    }

    private void loadAlbumsAndPhotos() {
        ApiClient.getService().getAlbums(tripId).enqueue(new Callback<List<MediaAlbum>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaAlbum>> call, @NonNull Response<List<MediaAlbum>> response) {
                photoList.clear();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (MediaAlbum album : response.body()) {
                        if (album.getPhotos() != null) {
                            for (TripMedia photo : album.getPhotos()) {
                                if (photo.getPhotoUrl() != null && photo.getPhotoUrl().startsWith("http")) {
                                    photoList.add(new MediaAdapter.MediaItem(photo.getPhotoUrl()));
                                }
                            }
                        }
                    }
                }

                if (photoList.isEmpty()) {
                    loadSamplePhotos();
                } else {
                    updateEmptyStateUI();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaAlbum>> call, @NonNull Throwable t) {
                loadSamplePhotos();
            }
        });
    }

    private void loadSamplePhotos() {
        photoList.clear();
        photoList.add(new MediaAdapter.MediaItem(R.drawable.img_ha_long));
        photoList.add(new MediaAdapter.MediaItem(R.drawable.img_sapa));
        photoList.add(new MediaAdapter.MediaItem(R.drawable.img_hoi_an));
        photoList.add(new MediaAdapter.MediaItem(R.drawable.banner_home));
        photoList.add(new MediaAdapter.MediaItem(R.drawable.bg_profile));
        updateEmptyStateUI();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyStateUI() {
        if (photoList.isEmpty()) {
            if (rvMediaPhotos != null) rvMediaPhotos.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvIcon = layoutEmptyState.findViewById(R.id.tvEmptyIcon);
                TextView tvTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvSub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvIcon != null) tvIcon.setText("🖼️");
                if (tvTitle != null) tvTitle.setText("Chưa có ảnh/video nào");
                if (tvSub != null) tvSub.setText("Tải lên bức ảnh đầu tiên để lưu giữ kỷ niệm chuyến đi!");
                if (btnAction != null) {
                    btnAction.setText("+ Tải ảnh mới lên");
                    btnAction.setOnClickListener(v -> photoPickerLauncher.launch("image/*"));
                }
            }
        } else {
            if (rvMediaPhotos != null) rvMediaPhotos.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }
}
