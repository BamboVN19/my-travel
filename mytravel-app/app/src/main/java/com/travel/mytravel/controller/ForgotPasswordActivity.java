package com.travel.mytravel.controller;

import android.content.Context;
import android.content.Intent;
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
import com.travel.mytravel.model.ForgotPasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendOtp;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        tvBackToLogin.setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email của bạn", Toast.LENGTH_SHORT).show();
            } else {
                performForgotPassword(email);
            }
        });
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void performForgotPassword(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        ApiClient.getService().forgotPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Đã gửi mã OTP về email của bạn!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else {
                    ApiClient.showError(ForgotPasswordActivity.this, response, "Email không tồn tại trong hệ thống!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ApiClient.handleFailure(ForgotPasswordActivity.this, t, "Lỗi kết nối gửi OTP");
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
