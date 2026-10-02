package com.travel.mytravel.controller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPass, edtConfirmPass;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> finish());
        }

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                String fullName = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                String confirmPass = edtConfirmPass.getText().toString().trim();

                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPass)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                } else {
                    String username = email.contains("@") ? email.split("@")[0] : email;
                    performRegister(username, email, password, fullName, phone);
                }
            });
        }
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPass = findViewById(R.id.edtPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }

    private void performRegister(String username, String email, String password, String fullName, String phone) {
        RegisterRequest request = new RegisterRequest(username, email, password, fullName, phone);

        ApiClient.getService().registerWithRequest(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Chào mừng " + fullName, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    ApiClient.showError(RegisterActivity.this, response, "Đăng ký thất bại!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                ApiClient.handleFailure(RegisterActivity.this, t, "Lỗi kết nối đăng ký");
            }
        });
    }
}
