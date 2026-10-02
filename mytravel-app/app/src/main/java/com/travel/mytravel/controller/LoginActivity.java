package com.travel.mytravel.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
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
import com.travel.mytravel.api.TokenManager;
import com.travel.mytravel.model.LoginRequest;
import com.travel.mytravel.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ApiClient.init(getApplicationContext());

        initViews();

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String user = edtUsername.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(user, pass);
            }
        });
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void performLogin(String user, String pass) {
        LoginRequest request = new LoginRequest(user, pass);

        Log.d("MYTRAVEL_API", "Attempting login for user: " + user);

        ApiClient.getService().loginWithRequest(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();
                    String loggedUser = res.getUsername() != null ? res.getUsername() : user;

                    TokenManager tokenManager = ApiClient.getTokenManager();
                    if (tokenManager == null) {
                        tokenManager = new TokenManager(getApplicationContext());
                    }
                    tokenManager.saveTokens(res.getAccessToken(), res.getRefreshToken(), loggedUser);

                    Log.d("MYTRAVEL_API", "✅ Login SUCCESS for user: " + loggedUser
                        + " | Token: " + res.getAccessToken());

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công! Chào " + loggedUser, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ApiClient.showError(LoginActivity.this, response, "Sai tài khoản hoặc mật khẩu!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                ApiClient.handleFailure(LoginActivity.this, t, "Lỗi kết nối đăng nhập");
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
