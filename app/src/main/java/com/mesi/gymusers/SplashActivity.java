package com.mesi.gymusers;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchToMainActivity();
                finish();
            }
        }, 2000);

    }

    private void switchToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}