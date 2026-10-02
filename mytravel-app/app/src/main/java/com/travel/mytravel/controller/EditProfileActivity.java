package com.travel.mytravel.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.api.TokenManager;
import com.travel.mytravel.model.RefreshTokenRequest;
import com.travel.mytravel.model.UpdateProfileRequest;
import com.travel.mytravel.model.UserProfile;
import com.travel.mytravel.repository.GlobalDataCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone;
    private TextView tvEditAvatarInitials;
    private Button btnSaveProfile, btnGoToChangePassword, btnLogoutAccount;
    private ImageView imgEditAvatar;
    private View btnChangeAvatar;

    private ActivityResultLauncher<String> avatarPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        if (imgEditAvatar != null) {
                            imgEditAvatar.setVisibility(View.VISIBLE);
                            if (tvEditAvatarInitials != null) tvEditAvatarInitials.setVisibility(View.GONE);
                            Glide.with(EditProfileActivity.this)
                                    .load(uri)
                                    .circleCrop()
                                    .into(imgEditAvatar);
                        }
                        TokenManager tm = ApiClient.getTokenManager();
                        if (tm == null) tm = new TokenManager(getApplicationContext());
                        tm.saveAvatarUri(uri.toString());

                        uploadAvatarFile(uri);
                    }
                }
        );

        initViews();
        setupListeners();
        loadProfile();
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        tvEditAvatarInitials = findViewById(R.id.tvEditAvatarInitials);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnGoToChangePassword = findViewById(R.id.btnGoToChangePassword);
        btnLogoutAccount = findViewById(R.id.btnLogoutAccount);
        imgEditAvatar = findViewById(R.id.imgEditAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
    }

    private void setupListeners() {
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnChangeAvatar != null) {
            btnChangeAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        }

        if (imgEditAvatar != null) {
            imgEditAvatar.setOnClickListener(v -> avatarPickerLauncher.launch("image/*"));
        }

        if (btnGoToChangePassword != null) {
            btnGoToChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            });
        }

        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> saveProfile());
        }

        if (btnLogoutAccount != null) {
            btnLogoutAccount.setOnClickListener(v -> showLogoutConfirmationDialog());
        }
    }

    private void uploadAvatarFile(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) return;
            byte[] bytes = getBytes(inputStream);
            inputStream.close();

            String mimeType = getContentResolver().getType(imageUri);
            if (mimeType == null) mimeType = "image/jpeg";

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(mimeType),
                    bytes
            );

            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile);

            ApiClient.getService().uploadAvatar(body).enqueue(new Callback<UserProfile>() {
                @Override
                public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GlobalDataCache.getInstance().setCachedProfile(response.body());
                        Toast.makeText(EditProfileActivity.this, "📸 Đổi ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                        if (response.body().getAvatarUrl() != null && !response.body().getAvatarUrl().isEmpty()) {
                            TokenManager tm = ApiClient.getTokenManager();
                            if (tm == null) tm = new TokenManager(getApplicationContext());
                            tm.saveAvatarUri(response.body().getAvatarUrl());
                        }
                        String formattedUrl = ApiClient.formatAvatarUrl(EditProfileActivity.this, response.body().getAvatarUrl());
                        if (formattedUrl != null && !formattedUrl.trim().isEmpty() && imgEditAvatar != null) {
                            imgEditAvatar.setVisibility(View.VISIBLE);
                            if (tvEditAvatarInitials != null) tvEditAvatarInitials.setVisibility(View.GONE);
                            Glide.with(EditProfileActivity.this)
                                    .load(formattedUrl)
                                    .circleCrop()
                                    .into(imgEditAvatar);
                        }
                    } else {
                        ApiClient.showError(EditProfileActivity.this, response, "Tải ảnh đại diện thất bại!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                    ApiClient.handleFailure(EditProfileActivity.this, t, "Lỗi kết nối tải ảnh đại diện");
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

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng Xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?");
        builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
            ApiClient.init(getApplicationContext());
            TokenManager tokenManager = ApiClient.getTokenManager();
            if (tokenManager == null) {
                tokenManager = new TokenManager(getApplicationContext());
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
            GlobalDataCache.getInstance().clearAllCache();

            Toast.makeText(EditProfileActivity.this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void loadProfile() {
        ApiClient.getService().getUserProfile().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    String name = profile.getFullName() != null && !profile.getFullName().trim().isEmpty() ? profile.getFullName() : profile.getUsername();
                    if (edtFullName != null) edtFullName.setText(name);
                    if (edtEmail != null && profile.getEmail() != null) edtEmail.setText(profile.getEmail());
                    if (edtPhone != null && profile.getPhoneNumber() != null) edtPhone.setText(profile.getPhoneNumber());

                    String initials = ApiClient.getInitialsFromName(name);
                    if (tvEditAvatarInitials != null) {
                        tvEditAvatarInitials.setText(initials);
                    }

                    String avatarUrl = ApiClient.formatAvatarUrl(EditProfileActivity.this, profile.getAvatarUrl());
                    if (avatarUrl != null && !avatarUrl.trim().isEmpty() && imgEditAvatar != null) {
                        Glide.with(EditProfileActivity.this)
                                .load(avatarUrl)
                                .circleCrop()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        if (imgEditAvatar != null) imgEditAvatar.setVisibility(View.GONE);
                                        if (tvEditAvatarInitials != null) tvEditAvatarInitials.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (imgEditAvatar != null) imgEditAvatar.setVisibility(View.VISIBLE);
                                        if (tvEditAvatarInitials != null) tvEditAvatarInitials.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(imgEditAvatar);
                    } else {
                        if (imgEditAvatar != null) imgEditAvatar.setVisibility(View.GONE);
                        if (tvEditAvatarInitials != null) tvEditAvatarInitials.setVisibility(View.VISIBLE);
                    }
                } else {
                    ApiClient.showError(EditProfileActivity.this, response, "Không thể tải thông tin hồ sơ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                ApiClient.handleFailure(EditProfileActivity.this, t, "Lỗi kết nối tải hồ sơ");
            }
        });
    }

    private void saveProfile() {
        String name = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(name, phone);

        ApiClient.getService().updateProfile(request).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        GlobalDataCache.getInstance().setCachedProfile(response.body());
                    } else {
                        GlobalDataCache.getInstance().setCachedProfile(null);
                    }
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ApiClient.showError(EditProfileActivity.this, response, "Cập nhật hồ sơ thất bại!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                ApiClient.handleFailure(EditProfileActivity.this, t, "Lỗi kết nối cập nhật hồ sơ");
            }
        });
    }
}
