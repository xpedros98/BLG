package com.e.blg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class About extends AppCompatActivity {

    com.airbnb.lottie.LottieAnimationView kandinsky;

    Animation fadeInAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.in_fade);

        kandinsky = findViewById(R.id.kandinsnky);
        kandinsky.setVisibility(View.VISIBLE);
        kandinsky.setAnimation(fadeInAnimation);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                kandinsky.setVisibility(View.VISIBLE);
//                kandinsky.setAnimation(fadeInAnimation);
//            }
//        }, 2000);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                kandinsky.playAnimation();
//            }
//        }, 3000);
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(About.this, Dashboard.class);
        startActivity(intent);
        finish();
    }
}
