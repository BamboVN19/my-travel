package com.travel.mytravel.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final List<Trip> tripList;
    private final OnTripClickListener listener;

    public TripAdapter(List<Trip> tripList, OnTripClickListener listener) {
        this.tripList = tripList;
        this.listener = listener;
    }

    public TripAdapter(List<Trip> tripList) {
        this(tripList, null);
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.tvTripTitle.setText(trip.getTitle());
        String dates = trip.getStartDate() + " — " + trip.getEndDate();
        holder.tvTripDates.setText(dates);

        bindStatusBadge(holder.tvTripStatus, trip.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTripClick(trip);
            }
        });
    }

    private void bindStatusBadge(TextView tvStatus, String rawStatus) {
        if (tvStatus == null || rawStatus == null) return;

        String statusText;
        int bgColor;
        int textColor;

        switch (rawStatus.toUpperCase()) {
            case "ONGOING":
            case "IN_PROGRESS":
                statusText = "Đang đi";
                bgColor = Color.parseColor("#FFF3E0");
                textColor = Color.parseColor("#FF9800");
                break;
            case "COMPLETED":
                statusText = "Đã đi";
                bgColor = Color.parseColor("#E8F5E9");
                textColor = Color.parseColor("#2E7D32");
                break;
            case "PLANNED":
            case "UPCOMING":
            default:
                statusText = "Sắp đi";
                bgColor = Color.parseColor("#E3F2FD");
                textColor = Color.parseColor("#007AFF");
                break;
        }

        tvStatus.setText(statusText);
        tvStatus.setTextColor(textColor);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(12);
        shape.setColor(bgColor);
        tvStatus.setBackground(shape);
    }

    @Override
    public int getItemCount() {
        return tripList != null ? tripList.size() : 0;
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvTripTitle, tvTripDates, tvTripStatus;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripTitle = itemView.findViewById(R.id.tvTripTitle);
            tvTripDates = itemView.findViewById(R.id.tvTripDates);
            tvTripStatus = itemView.findViewById(R.id.tvTripStatus);
        }
    }
}
