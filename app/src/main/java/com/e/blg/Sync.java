package com.e.blg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Sync extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Sync.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
