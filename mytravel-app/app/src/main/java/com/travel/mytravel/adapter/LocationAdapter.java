package com.travel.mytravel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    public static class LocationItem {
        private String name;
        private String address;

        public LocationItem(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(LocationItem item);
    }

    private final List<LocationItem> locationList;
    private final OnLocationClickListener listener;

    public LocationAdapter(List<LocationItem> locationList, OnLocationClickListener listener) {
        this.locationList = locationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_history, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        LocationItem item = locationList.get(position);
        holder.tvLocationName.setText(item.getName());
        holder.tvLocationAddress.setText(item.getAddress());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLocationClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList != null ? locationList.size() : 0;
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName, tvLocationAddress;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tvLocationName);
            tvLocationAddress = itemView.findViewById(R.id.tvLocationAddress);
        }
    }
}
