package com.example.goblin1001;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class gettingstarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gettingstarted);

        // Delay for 2 seconds and then slide to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(gettingstarted.this, gettingstarted2.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish(); // Finish this activity so the user can't go back to it
        }, 2000); // Delay for 2 seconds
    }
}