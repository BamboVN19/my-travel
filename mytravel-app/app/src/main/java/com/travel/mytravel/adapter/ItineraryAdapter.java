package com.travel.mytravel.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.ItineraryItem;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {

    public interface OnItineraryClickListener {
        void onItemClick(ItineraryItem item, int position);
    }

    private final List<ItineraryItem> itineraryList;
    private final OnItineraryClickListener listener;

    public ItineraryAdapter(List<ItineraryItem> itineraryList, OnItineraryClickListener listener) {
        this.itineraryList = itineraryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_itinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        ItineraryItem item = itineraryList.get(position);
        holder.tvItineraryTime.setText(item.getActivityTime());
        holder.tvItineraryTitle.setText(item.getActivityName());
        String loc = "📍 " + (item.getLocationName() != null ? item.getLocationName() : "");
        holder.tvItineraryLocation.setText(loc);
        if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
            holder.tvItineraryNote.setText(item.getNote());
            holder.tvItineraryNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvItineraryNote.setVisibility(View.GONE);
        }

        String url = item.getImageUrl();
        if (url != null && !url.trim().isEmpty()) {
            String formattedUrl = ApiClient.formatAvatarUrl(holder.itemView.getContext(), url);
            holder.imgItineraryPhoto.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(formattedUrl)
                    .centerCrop()
                    .into(holder.imgItineraryPhoto);
        } else {
            holder.imgItineraryPhoto.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itineraryList != null ? itineraryList.size() : 0;
    }

    static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        TextView tvItineraryTime, tvItineraryTitle, tvItineraryLocation, tvItineraryNote;
        ImageView imgItineraryPhoto;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItineraryTime = itemView.findViewById(R.id.tvItineraryTime);
            tvItineraryTitle = itemView.findViewById(R.id.tvItineraryTitle);
            tvItineraryLocation = itemView.findViewById(R.id.tvItineraryLocation);
            tvItineraryNote = itemView.findViewById(R.id.tvItineraryNote);
            imgItineraryPhoto = itemView.findViewById(R.id.imgItineraryPhoto);
        }
    }
}
