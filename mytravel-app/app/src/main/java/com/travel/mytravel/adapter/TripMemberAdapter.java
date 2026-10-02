package com.travel.mytravel.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travel.mytravel.R;
import com.travel.mytravel.model.TripMember;

import java.util.List;

public class TripMemberAdapter extends RecyclerView.Adapter<TripMemberAdapter.TripMemberViewHolder> {

    public interface OnMemberDeleteListener {
        void onDeleteClick(TripMember member, int position);
    }

    private final List<TripMember> memberList;
    private final OnMemberDeleteListener deleteListener;

    public TripMemberAdapter(List<TripMember> memberList, OnMemberDeleteListener deleteListener) {
        this.memberList = memberList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TripMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_member, parent, false);
        return new TripMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripMemberViewHolder holder, int position) {
        TripMember member = memberList.get(position);

        String name = member.getFullName() != null && !member.getFullName().isEmpty()
                ? member.getFullName()
                : (member.getUsername() != null ? member.getUsername() : "Thành viên");

        holder.tvMemberName.setText(name);
        holder.tvMemberEmail.setText(member.getEmail() != null ? member.getEmail() : "");

        String initials = name.length() > 2 ? name.substring(name.length() - 2) : "ND";
        holder.tvMemberInitials.setText(initials.toUpperCase());

        String role = member.getRole() != null ? member.getRole().toUpperCase() : "MEMBER";

        String roleDisplay;
        if ("OWNER".equalsIgnoreCase(role)) {
            roleDisplay = "Trưởng nhóm";
            holder.tvMemberRole.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvMemberRole.setTextColor(Color.parseColor("#2E7D32"));
            holder.btnRemoveMember.setVisibility(View.GONE);
        } else if ("EDITOR".equalsIgnoreCase(role)) {
            roleDisplay = "Biên tập viên";
            holder.tvMemberRole.setBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvMemberRole.setTextColor(Color.parseColor("#0277BD"));
            holder.btnRemoveMember.setVisibility(View.VISIBLE);
        } else if ("VIEWER".equalsIgnoreCase(role)) {
            roleDisplay = "Người xem";
            holder.tvMemberRole.setBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.tvMemberRole.setTextColor(Color.parseColor("#E65100"));
            holder.btnRemoveMember.setVisibility(View.VISIBLE);
        } else {
            roleDisplay = role;
            holder.tvMemberRole.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.tvMemberRole.setTextColor(Color.parseColor("#616161"));
            holder.btnRemoveMember.setVisibility(View.VISIBLE);
        }
        holder.tvMemberRole.setText(roleDisplay);

        holder.btnRemoveMember.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(member, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList != null ? memberList.size() : 0;
    }

    static class TripMemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberInitials, tvMemberName, tvMemberEmail, tvMemberRole, btnRemoveMember;

        public TripMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberInitials = itemView.findViewById(R.id.tvMemberInitials);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvMemberEmail = itemView.findViewById(R.id.tvMemberEmail);
            tvMemberRole = itemView.findViewById(R.id.tvMemberRole);
            btnRemoveMember = itemView.findViewById(R.id.btnRemoveMember);
        }
    }
}
