package com.e.blg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Down extends AppCompatActivity {

    Button downData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);

        downData = (Button) findViewById(R.id.bt_lateral);

        downData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To do...
            }
        });
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Down.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
