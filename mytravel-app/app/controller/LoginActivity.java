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

import androidx.appcompat.app.AppCompatActivity;

import com.travel.mytravel.R;
import com.travel.mytravel.api.ApiClient;
import com.travel.mytravel.model.LoginResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

  private EditText edtUsername, edtPassword;
  private Button btnLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    initViews();

    btnLogin.setOnClickListener(v -> {
      String user = edtUsername.getText().toString().trim();
      String pass = edtPassword.getText().toString().trim();

      if (user.isEmpty() || pass.isEmpty()) {
        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
      } else {
        handleLogin(user, pass);
      }
    });
  }

  private void initViews() {
    edtUsername = findViewById(R.id.edtUsername);
    edtPassword = findViewById(R.id.edtPassword);
    btnLogin = findViewById(R.id.btnLogin);
  }

  private void handleLogin(String user, String pass) {
    Map<String, String> credentials = new HashMap<>();
    credentials.put("username", user);
    credentials.put("password", pass);

    ApiClient.getService().login(credentials).enqueue(new Callback<LoginResponse>() {
      @Override
      public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          LoginResponse res = response.body();
          Toast.makeText(LoginActivity.this,
            "Chào mừng " + res.getFullName() + "!", Toast.LENGTH_LONG).show();

          // TODO: Chuyển sang HomeActivity tại đây
          // Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
          // startActivity(intent);
          // finish();
        } else {
          Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<LoginResponse> call, Throwable t) {
        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      View v = getCurrentFocus();
      if (v instanceof EditText) {
        Rect outRect = new Rect();
        v.getGlobalVisibleRect(outRect);
        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
          v.clearFocus();
          InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
          }
        }
      }
    }
    return super.dispatchTouchEvent(event);
  }
}