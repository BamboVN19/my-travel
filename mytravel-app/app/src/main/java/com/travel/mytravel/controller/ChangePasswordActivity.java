package com.travel.mytravel.controller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.ChangePasswordRequest;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPass, edtNewPass, edtConfirmNewPass;
    private Button btnSaveChangePassword;
    private View btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        btnBack.setOnClickListener(v -> finish());

        btnSaveChangePassword.setOnClickListener(v -> {
            String oldPass = edtOldPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String confirmNewPass = edtConfirmNewPass.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại và mật khẩu mới", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Objects.equals(newPass, confirmNewPass)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            performChangePassword(oldPass, newPass);
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtOldPass = findViewById(R.id.edtOldPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmNewPass = findViewById(R.id.edtConfirmNewPass);
        btnSaveChangePassword = findViewById(R.id.btnSaveChangePassword);
    }

    private void performChangePassword(String oldPass, String newPass) {
        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass);

        ApiClient.getService().changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ApiClient.showError(ChangePasswordActivity.this, response, "Mật khẩu hiện tại không chính xác!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ApiClient.handleFailure(ChangePasswordActivity.this, t, "Lỗi kết nối đổi mật khẩu");
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
