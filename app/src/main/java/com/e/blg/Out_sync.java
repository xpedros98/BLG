package com.e.blg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Out_sync extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_sync);
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Out_sync.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
