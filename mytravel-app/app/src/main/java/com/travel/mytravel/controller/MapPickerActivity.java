package com.travel.mytravel.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.travel.mytravel.R;

public class MapPickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        View btnBackMap = findViewById(R.id.btnBackMap);
        if (btnBackMap != null) {
            btnBackMap.setOnClickListener(v -> finish());
        }

        Button btnConfirmMapLocation = findViewById(R.id.btnConfirmMapLocation);
        TextView tvPinLocationName = findViewById(R.id.tvPinLocationName);

        if (btnConfirmMapLocation != null) {
            btnConfirmMapLocation.setOnClickListener(v -> {
                String loc = tvPinLocationName != null ? tvPinLocationName.getText().toString() : "Vị trí đã chọn trên Bản đồ";
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", loc);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(this, "Đã ghim vị trí: " + loc, Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
