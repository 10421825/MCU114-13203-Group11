package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CALL_PHONE = 100;

    private TextView txtShow;
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnsk, btn0, btnhk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- View 綁定 ---
        txtShow = findViewById(R.id.txtShow);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnsk = findViewById(R.id.btnsk);
        btn0  = findViewById(R.id.btn0);
        btnhk = findViewById(R.id.btnhk);

        // 數字鍵監聽
        btn1.setOnClickListener(myListener);
        btn2.setOnClickListener(myListener);
        btn3.setOnClickListener(myListener);
        btn4.setOnClickListener(myListener);
        btn5.setOnClickListener(myListener);
        btn6.setOnClickListener(myListener);
        btn7.setOnClickListener(myListener);
        btn8.setOnClickListener(myListener);
        btn9.setOnClickListener(myListener);
        btnsk.setOnClickListener(myListener);
        btn0.setOnClickListener(myListener);
        btnhk.setOnClickListener(myListener);

        // 📞 撥號鍵
        Button callout = findViewById(R.id.callout);
        callout.setOnClickListener(v -> {
            String raw = txtShow.getText().toString().trim();
            if (raw.isEmpty()) {
                Toast.makeText(this, "請先輸入電話號碼", Toast.LENGTH_SHORT).show();
                return;
            }
            // 編碼，確保 * 與 # 可用
            String encoded = Uri.encode(raw);

            // 裝置若沒有能處理 ACTION_CALL 的 App 或無電話功能，改用撥號介面
            if (!canPlaceCalls(encoded)) {
                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + encoded));
                startActivity(dial);
                return;
            }

            // 權限檢查
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL_PHONE);
            } else {
                callNumber(encoded); // 直接撥出
            }
        });
    }

    // 直接撥號（已確認有權限後呼叫）
    private void callNumber(String encodedNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    // 檢查是否真的能直接撥出：有撥號器且裝置具備 telephony 功能
    private boolean canPlaceCalls(String encodedNumber) {
        Intent test = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + encodedNumber));
        PackageManager pm = getPackageManager();
        boolean hasHandler = test.resolveActivity(pm) != null;
        boolean hasTelephony = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        return hasHandler && hasTelephony;
    }

    // 權限回覆處理
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String raw = txtShow.getText().toString().trim();
                if (!raw.isEmpty()) {
                    callNumber(Uri.encode(raw)); // 剛授權就直接撥
                } else {
                    Toast.makeText(this, "請先輸入電話號碼", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "未授權電話權限，無法直接撥打（改用撥號介面）", Toast.LENGTH_SHORT).show();
                String raw = txtShow.getText().toString().trim();
                if (!raw.isEmpty()) {
                    startActivity(new Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + Uri.encode(raw))
                    ));
                }
            }
        }
    }

    // 統一數字鍵處理
    private final View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String s = txtShow.getText().toString();
            switch (view.getId()) {
                case R.id.btn1: txtShow.setText(s + "1"); break;
                case R.id.btn2: txtShow.setText(s + "2"); break;
                case R.id.btn3: txtShow.setText(s + "3"); break;
                case R.id.btn4: txtShow.setText(s + "4"); break;
                case R.id.btn5: txtShow.setText(s + "5"); break;
                case R.id.btn6: txtShow.setText(s + "6"); break;
                case R.id.btn7: txtShow.setText(s + "7"); break;
                case R.id.btn8: txtShow.setText(s + "8"); break;
                case R.id.btn9: txtShow.setText(s + "9"); break;
                case R.id.btnsk: txtShow.setText(s + "*"); break;
                case R.id.btn0:  txtShow.setText(s + "0"); break;
                case R.id.btnhk: txtShow.setText(s + "#"); break;
            }
        }
    };
}
