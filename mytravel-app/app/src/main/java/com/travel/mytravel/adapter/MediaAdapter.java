package com.travel.mytravel.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    public static class MediaItem {
        private final Uri imageUri;
        private final int drawableRes;

        public MediaItem(Uri imageUri) {
            this.imageUri = imageUri;
            this.drawableRes = 0;
        }

        public MediaItem(String urlString) {
            this.imageUri = urlString != null ? Uri.parse(urlString) : null;
            this.drawableRes = 0;
        }

        public MediaItem(int drawableRes) {
            this.imageUri = null;
            this.drawableRes = drawableRes;
        }

        public Uri getImageUri() {
            return imageUri;
        }

        public int getDrawableRes() {
            return drawableRes;
        }
    }

    private final List<MediaItem> mediaList;

    public MediaAdapter(List<MediaItem> mediaList) {
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_photo, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem item = mediaList.get(position);
        if (item.getImageUri() != null) {
            String formattedUrl = ApiClient.formatAvatarUrl(holder.itemView.getContext(), item.getImageUri().toString());
            Glide.with(holder.itemView.getContext())
                    .load(formattedUrl)
                    .centerCrop()
                    .into(holder.imgPhoto);
        } else if (item.getDrawableRes() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getDrawableRes())
                    .centerCrop()
                    .into(holder.imgPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mediaList != null ? mediaList.size() : 0;
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}
