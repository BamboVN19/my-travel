package com.travel.mytravel.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.adapter.TripAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.controller.MainActivity;
import com.travel.mytravel.controller.MediaAlbumActivity;
import com.travel.mytravel.controller.TripDetailActivity;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.model.UserProfile;
import com.travel.mytravel.repository.GlobalDataCache;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvHomeUserName, tvHomeInitials;
    private ImageView imgHomeAvatar;
    private View cardCurrentTripHero, cardHomeAvatarFrame, layoutEmptyState;
    private TextView tvHeroTripTitle, tvHeroTripDates, tvHeroTripBudget, tvHeroTripStatus;
    private RecyclerView rvHomeTripHistory;

    private Trip currentActiveTrip;
    private final List<Trip> historyTrips = new ArrayList<>();
    private TripAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupModuleClickListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
        loadHomeTripsData();
    }

    private void initViews(View view) {
        tvHomeUserName = view.findViewById(R.id.tvHomeUserName);
        tvHomeInitials = view.findViewById(R.id.tvHomeInitials);
        imgHomeAvatar = view.findViewById(R.id.imgHomeAvatar);
        cardHomeAvatarFrame = view.findViewById(R.id.cardHomeAvatarFrame);

        cardCurrentTripHero = view.findViewById(R.id.cardCurrentTripHero);
        tvHeroTripTitle = view.findViewById(R.id.tvHeroTripTitle);
        tvHeroTripDates = view.findViewById(R.id.tvHeroTripDates);
        tvHeroTripBudget = view.findViewById(R.id.tvHeroTripBudget);
        tvHeroTripStatus = view.findViewById(R.id.tvHeroTripStatus);

        rvHomeTripHistory = view.findViewById(R.id.rvHomeTripHistory);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        if (rvHomeTripHistory != null) {
            rvHomeTripHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            historyAdapter = new TripAdapter(historyTrips, trip -> {
                Intent intent = new Intent(getContext(), TripDetailActivity.class);
                intent.putExtra("trip_id", trip.getId());
                startActivity(intent);
            });
            rvHomeTripHistory.setAdapter(historyAdapter);
        }
    }

    private void setupModuleClickListeners(View view) {
        if (cardHomeAvatarFrame != null) {
            cardHomeAvatarFrame.setOnClickListener(v -> navigateToTab(R.id.nav_profile));
        }

        if (imgHomeAvatar != null) {
            imgHomeAvatar.setOnClickListener(v -> navigateToTab(R.id.nav_profile));
        }

        if (cardCurrentTripHero != null) {
            cardCurrentTripHero.setOnClickListener(v -> {
                if (currentActiveTrip != null) {
                    Intent intent = new Intent(getContext(), TripDetailActivity.class);
                    intent.putExtra("trip_id", currentActiveTrip.getId());
                    startActivity(intent);
                } else {
                    navigateToTab(R.id.nav_add);
                }
            });
        }

        View btnModulePlan = view.findViewById(R.id.btnModulePlan);
        View btnModuleBudget = view.findViewById(R.id.btnModuleBudget);
        View btnModuleMedia = view.findViewById(R.id.btnModuleMedia);
        View btnModuleProfile = view.findViewById(R.id.btnModuleProfile);

        if (btnModulePlan != null) btnModulePlan.setOnClickListener(v -> navigateToTab(R.id.nav_add));
        if (btnModuleBudget != null) btnModuleBudget.setOnClickListener(v -> navigateToTab(R.id.nav_budget));
        if (btnModuleMedia != null) btnModuleMedia.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MediaAlbumActivity.class);
            startActivity(intent);
        });
        if (btnModuleProfile != null) btnModuleProfile.setOnClickListener(v -> navigateToTab(R.id.nav_profile));

        View btnSeeAllHistory = view.findViewById(R.id.btnSeeAllHistory);
        if (btnSeeAllHistory != null) {
            btnSeeAllHistory.setOnClickListener(v -> navigateToTab(R.id.nav_profile));
        }
    }

    private void navigateToTab(int navItemId) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectTab(navItemId);
        }
    }

    private void loadUserData() {
        GlobalDataCache.getInstance().getUserProfile(getContext(), false, new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    String name = profile.getFullName() != null && !profile.getFullName().trim().isEmpty() ? profile.getFullName() : profile.getUsername();
                    if (tvHomeUserName != null) {
                        tvHomeUserName.setText(name);
                    }

                    String initials = ApiClient.getInitialsFromName(name);
                    if (tvHomeInitials != null) {
                        tvHomeInitials.setText(initials);
                    }

                    String avatarUrl = ApiClient.formatAvatarUrl(getContext(), profile.getAvatarUrl());
                    if (avatarUrl != null && !avatarUrl.trim().isEmpty() && imgHomeAvatar != null && getContext() != null) {
                        Glide.with(getContext())
                                .load(avatarUrl)
                                .circleCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        if (imgHomeAvatar != null) imgHomeAvatar.setVisibility(View.GONE);
                                        if (tvHomeInitials != null) tvHomeInitials.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (imgHomeAvatar != null) imgHomeAvatar.setVisibility(View.VISIBLE);
                                        if (tvHomeInitials != null) tvHomeInitials.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(imgHomeAvatar);
                    } else {
                        if (imgHomeAvatar != null) imgHomeAvatar.setVisibility(View.GONE);
                        if (tvHomeInitials != null) tvHomeInitials.setVisibility(View.VISIBLE);
                    }
                } else {
                    ApiClient.showError(getContext(), response, "Không thể tải thông tin tài khoản");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                ApiClient.handleFailure(getContext(), t, "Lỗi kết nối tài khoản");
            }
        });
    }

    private void loadHomeTripsData() {
        GlobalDataCache.getInstance().getTrips(false, new Callback<PageResponse<Trip>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null && !response.body().getContent().isEmpty()) {
                    List<Trip> allTrips = response.body().getContent();

                    currentActiveTrip = null;
                    for (Trip t : allTrips) {
                        if ("ONGOING".equalsIgnoreCase(t.getStatus())) {
                            currentActiveTrip = t;
                            break;
                        }
                    }
                    if (currentActiveTrip == null) {
                        currentActiveTrip = allTrips.get(0);
                    }

                    bindHeroTripData(currentActiveTrip);

                    historyTrips.clear();
                    for (Trip t : allTrips) {
                        if (!t.getId().equals(currentActiveTrip.getId())) {
                            historyTrips.add(t);
                        }
                    }

                    if (historyAdapter != null) {
                        historyAdapter.notifyDataSetChanged();
                    }

                    if (cardCurrentTripHero != null) cardCurrentTripHero.setVisibility(View.VISIBLE);
                    if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
                } else {
                    showEmptyStateHome();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                showEmptyStateHome();
            }
        });
    }

    private void showEmptyStateHome() {
        if (cardCurrentTripHero != null) cardCurrentTripHero.setVisibility(View.GONE);
        if (rvHomeTripHistory != null) rvHomeTripHistory.setVisibility(View.GONE);
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            TextView tvEmptyTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
            TextView tvEmptySub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
            Button btnEmptyAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

            if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có chuyến đi nào");
            if (tvEmptySub != null) tvEmptySub.setText("Bắt đầu khám phá thế giới bằng cách tạo chuyến đi đầu tiên!");
            if (btnEmptyAction != null) {
                btnEmptyAction.setText("+ Tạo kế hoạch ngay");
                btnEmptyAction.setOnClickListener(v -> navigateToTab(R.id.nav_add));
            }
        }
    }

    private void bindHeroTripData(Trip trip) {
        if (trip == null) return;

        if (tvHeroTripTitle != null) tvHeroTripTitle.setText(trip.getTitle());
        if (tvHeroTripDates != null) {
            String dates = trip.getStartDate() + " — " + trip.getEndDate();
            tvHeroTripDates.setText(dates);
        }
        if (tvHeroTripBudget != null) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvHeroTripBudget.setText("Ngân sách: " + formatter.format(trip.getTotalBudget()) + "đ");
        }
        if (tvHeroTripStatus != null) {
            String status = "ONGOING".equalsIgnoreCase(trip.getStatus()) ? "Đang diễn ra" : "Sắp diễn ra";
            tvHeroTripStatus.setText(status);
        }
    }
}
