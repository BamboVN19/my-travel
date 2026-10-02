package com.travel.mytravel.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.adapter.TripAdapter;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.controller.MainActivity;
import com.travel.mytravel.controller.TripDetailActivity;
import com.travel.mytravel.model.PageResponse;
import com.travel.mytravel.model.Trip;
import com.travel.mytravel.repository.GlobalDataCache;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTripFragment extends Fragment {

    private RecyclerView rvPlannedTrips;
    private View layoutEmptyState;
    private TripAdapter tripAdapter;
    private final List<Trip> plannedTripList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_trip, container, false);

        initViews(view);
        setupListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPlannedTrips();
    }

    private void initViews(View view) {
        rvPlannedTrips = view.findViewById(R.id.rvPlannedTrips);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        if (rvPlannedTrips != null) {
            rvPlannedTrips.setLayoutManager(new LinearLayoutManager(getContext()));
            tripAdapter = new TripAdapter(plannedTripList, trip -> {
                Intent intent = new Intent(getContext(), TripDetailActivity.class);
                intent.putExtra("trip_id", trip.getId());
                startActivity(intent);
            });
            rvPlannedTrips.setAdapter(tripAdapter);
        }
    }

    private void setupListeners(View view) {
        Button btnCreateNewPlanTop = view.findViewById(R.id.btnCreateNewPlanTop);
        if (btnCreateNewPlanTop != null) {
            btnCreateNewPlanTop.setOnClickListener(v -> navigateToCreateForm());
        }
    }

    private void navigateToCreateForm() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectTab(R.id.nav_add);
        }
    }

    private void loadPlannedTrips() {
        GlobalDataCache.getInstance().getTrips(false, new Callback<PageResponse<Trip>>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<Trip>> call, @NonNull Response<PageResponse<Trip>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContent() != null) {
                    List<Trip> allTrips = response.body().getContent();
                    plannedTripList.clear();

                    for (Trip t : allTrips) {
                        String status = t.getStatus() != null ? t.getStatus().toUpperCase() : "";
                        if (status.contains("PLANNED") || status.contains("UPCOMING") || status.contains("ONGOING") || status.contains("IN_PROGRESS")) {
                            plannedTripList.add(t);
                        }
                    }

                    if (plannedTripList.isEmpty() && !allTrips.isEmpty()) {
                        plannedTripList.addAll(allTrips);
                    }

                    updateEmptyStateUI();
                    if (tripAdapter != null) {
                        tripAdapter.notifyDataSetChanged();
                    }
                } else {
                    plannedTripList.clear();
                    updateEmptyStateUI();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<Trip>> call, @NonNull Throwable t) {
                plannedTripList.clear();
                updateEmptyStateUI();
            }
        });
    }

    private void updateEmptyStateUI() {
        if (plannedTripList.isEmpty()) {
            if (rvPlannedTrips != null) rvPlannedTrips.setVisibility(View.GONE);
            if (layoutEmptyState != null) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                TextView tvEmptyIcon = layoutEmptyState.findViewById(R.id.tvEmptyIcon);
                TextView tvEmptyTitle = layoutEmptyState.findViewById(R.id.tvEmptyTitle);
                TextView tvEmptySub = layoutEmptyState.findViewById(R.id.tvEmptySubtitle);
                Button btnEmptyAction = layoutEmptyState.findViewById(R.id.btnEmptyAction);

                if (tvEmptyIcon != null) tvEmptyIcon.setText("🧳");
                if (tvEmptyTitle != null) tvEmptyTitle.setText("Chưa có kế hoạch chuyến đi nào");
                if (tvEmptySub != null) tvEmptySub.setText("Hãy bắt đầu lập kế hoạch cho chuyến đi tiếp theo của bạn ngay bây giờ!");
                if (btnEmptyAction != null) {
                    btnEmptyAction.setText("+ Tạo kế hoạch ngay");
                    btnEmptyAction.setOnClickListener(v -> navigateToCreateForm());
                }
            }
        } else {
            if (rvPlannedTrips != null) rvPlannedTrips.setVisibility(View.VISIBLE);
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        }
    }
}
