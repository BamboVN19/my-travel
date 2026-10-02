package com.travel.mytravel.controller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.ResetPasswordRequest;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtOtp, edtNewPass, edtConfirmNewPass;
    private Button btnResetPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = getIntent().getStringExtra("email");
        if (email == null) email = "";

        initViews();

        btnResetPassword.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String confirmNewPass = edtConfirmNewPass.getText().toString().trim();

            if (otp.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ OTP và Mật khẩu mới", Toast.LENGTH_SHORT).show();
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

            performResetPassword(otp, newPass);
        });
    }

    private void initViews() {
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmNewPass = findViewById(R.id.edtConfirmNewPass);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    private void performResetPassword(String otp, String newPass) {
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPass);

        ApiClient.getService().resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    ApiClient.showError(ResetPasswordActivity.this, response, "Mã OTP không hợp lệ!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ApiClient.handleFailure(ResetPasswordActivity.this, t, "Lỗi kết nối đặt lại mật khẩu");
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
