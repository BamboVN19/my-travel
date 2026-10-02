package com.travel.mytravel.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.travel.mytravel.R;
import com.travel.mytravel.adapter.TripAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.api.TokenManager;
import com.travel.mytravel.controller.EditProfileActivity;
import com.travel.mytravel.controller.LoginActivity;
import com.travel.mytravel.controller.MainActivity;
import com.travel.mytravel.controller.TripDetailActivity;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.RefreshTokenRequest;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.UserProfile;
import com.travel.mytravel.repository.GlobalDataCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvAvatarInitials, tvTripCount, tvDestCount, tvMediaCount;
    private ImageView imgAvatar;
    private View cardAvatarFrame, layoutEmptyState;
    private RecyclerView rvRecentTrips;
    private ChipGroup cgTripFilter;

    private final List<Trip> allTrips = new ArrayList<>();
    private final List<Trip> displayedTrips = new ArrayList<>();
    private TripAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        setupListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
        loadRecentTrips();
    }

    private void initViews(View view) {
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvAvatarInitials = view.findViewById(R.id.tvAvatarInitials);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        cardAvatarFrame = view.findViewById(R.id.cardAvatarFrame);
        tvTripCount = view.findViewById(R.id.tvTripCount);
        tvDestCount = view.findViewById(R.id.tvDestCount);
        tvMediaCount = view.findViewById(R.id.tvMediaCount);
        rvRecentTrips = view.findViewById(R.id.rvRecentTrips);
        cgTripFilter = view.findViewById(R.id.cgTripFilter);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        if (rvRecentTrips != null) {
            rvRecentTrips.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new TripAdapter(displayedTrips, trip -> {
                Intent intent = new Intent(getContext(), TripDetailActivity.class);
                intent.putExtra("trip_id", trip.getId());
                startActivity(intent);
            });
            rvRecentTrips.setAdapter(adapter);
        }
    }

    private void setupListeners(View view) {
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            });
        }

        if (cardAvatarFrame != null) {
            cardAvatarFrame.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            });
        }

        Button btnLogoutProfile = view.findViewById(R.id.btnLogoutProfile);
        if (btnLogoutProfile != null) {
            btnLogoutProfile.setOnClickListener(v -> showLogoutConfirmationDialog());
        }

        if (cgTripFilter != null) {
            cgTripFilter.setOnCheckedChangeListener((group, checkedId) -> filterTrips(checkedId));
        }
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Đăng Xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            ApiClient.init(requireContext());
            TokenManager tokenManager = ApiClient.getTokenManager();
            if (tokenManager == null) {
                tokenManager = new TokenManager(requireContext());
            }

            String refreshToken = tokenManager.getRefreshToken();
            if (refreshToken != null && !refreshToken.isEmpty()) {
                ApiClient.getService().logout(new RefreshTokenRequest(refreshToken)).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                });
            }

            tokenManager.clearTokens();

            Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void filterTrips(int checkedChipId) {
        displayedTrips.clear();
        if (checkedChipId == R.id.chipPlannedTrips) {
            for (Trip t : allTrips) {
                if ("PLANNED".equalsIgnoreCase(t.getStatus()) || "UPCOMING".equalsIgnoreCase(t.getStatus())) {
                    displayedTrips.add(t);
                }
            }
        } else if (checkedChipId == R.id.chipOngoingTrips) {
            for (Trip t : allTrips) {
                if ("ONGOING".equalsIgnoreCase(t.getStatus()) || "IN_PROGRESS".equalsIgnoreCase(t.getStatus())) {
                    displayedTrips.add(t);
                }
            }
        } else if (checkedChipId == R.id.chipCompletedTrips) {
            for (Trip t : allTrips) {
                if ("COMPLETED".equalsIgnoreCase(t.getStatus())) {
                    displayedTrips.add(t);
                }
            }
        } else {
            displayedTrips.addAll(allTrips);
        }

        updateEmptyStateUI();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyStateUI() {
        if (displayedTrips.isEmpty()) {
            if (rvRecentTrips != null) rvRecentTrips.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvEmptyTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvEmptySub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnEmptyAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có chuyến đi nào");
                if (tvEmptySub != null) tvEmptySub.setText("Hãy tạo kế hoạch chuyến đi đầu tiên của bạn ngay bây giờ!");
                if (btnEmptyAction != null) {
                    btnEmptyAction.setText("+ Tạo chuyến đi ngay");
                    btnEmptyAction.setOnClickListener(v -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).selectTab(R.id.nav_add);
                        }
                    });
                }
            }
        } else {
            if (rvRecentTrips != null) rvRecentTrips.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void loadUserProfile() {
        GlobalDataCache.getInstance().getUserProfile(getContext(), false, new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    String name = profile.getFullName() != null && !profile.getFullName().trim().isEmpty() ? profile.getFullName() : profile.getUsername();
                    if (tvProfileName != null) {
                        tvProfileName.setText(name);
                    }

                    String initials = ApiClient.getInitialsFromName(name);
                    if (tvAvatarInitials != null) {
                        tvAvatarInitials.setText(initials);
                    }

                    String avatarUrl = ApiClient.formatAvatarUrl(getContext(), profile.getAvatarUrl());
                    if (avatarUrl != null && !avatarUrl.trim().isEmpty() && imgAvatar != null && getContext() != null) {
                        Glide.with(getContext())
                                .load(avatarUrl)
                                .circleCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        if (imgAvatar != null) imgAvatar.setVisibility(View.GONE);
                                        if (tvAvatarInitials != null) tvAvatarInitials.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (imgAvatar != null) imgAvatar.setVisibility(View.VISIBLE);
                                        if (tvAvatarInitials != null) tvAvatarInitials.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(imgAvatar);
                    } else {
                        if (imgAvatar != null) imgAvatar.setVisibility(View.GONE);
                        if (tvAvatarInitials != null) tvAvatarInitials.setVisibility(View.VISIBLE);
                    }
                } else {
                    ApiClient.showError(getContext(), response, "Không thể tải hồ sơ người dùng");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                ApiClient.handleFailure(getContext(), t, "Lỗi kết nối tải hồ sơ");
            }
        });
    }

    private void loadRecentTrips() {
        GlobalDataCache.getInstance().getTrips(false, new Callback<PageResponse<Trip>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    List<Trip> trips = response.body().getContent();
                    allTrips.clear();
                    allTrips.addAll(trips);

                    updateProfileStats();

                    int checkedId = cgTripFilter != null ? cgTripFilter.getCheckedChipId() : View.NO_ID;
                    filterTrips(checkedId);
                } else {
                    allTrips.clear();
                    updateProfileStats();
                    filterTrips(View.NO_ID);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                allTrips.clear();
                updateProfileStats();
                filterTrips(View.NO_ID);
            }
        });
    }

    private void updateProfileStats() {
        if (tvTripCount != null) {
            tvTripCount.setText(String.valueOf(allTrips.size()));
        }

        Set<String> uniqueDests = new HashSet<>();
        for (Trip t : allTrips) {
            if (t.getDestination() != null && !t.getDestination().trim().isEmpty()) {
                uniqueDests.add(t.getDestination().trim());
            }
        }

        if (tvDestCount != null) {
            tvDestCount.setText(String.valueOf(uniqueDests.size()));
        }

        if (tvMediaCount != null) {
            int totalMedia = allTrips.isEmpty() ? 0 : allTrips.size() * 5;
            tvMediaCount.setText(String.valueOf(totalMedia));
        }
    }
}
