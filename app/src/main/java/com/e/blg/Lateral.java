package com.e.blg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.cardiomood.android.controls.progress.CircularProgressBar;
import com.google.android.material.color.MaterialColors;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;
import com.lukedeighton.wheelview.transformer.WheelItemTransformer;
import com.triggertrap.seekarc.SeekArc;
import com.xw.repo.BubbleSeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Lateral extends AppCompatActivity {

    Button lateralData;
    TextView speedValue;
    BubbleSeekBar nLeds, bright;
    SpeedometerGauge speedometer;
    SeekArc circularProgressBar;
    WheelView wheelView;

    int current_nLeds, last_nLeds;
    int current_bright, last_bright;
    float current_speed, last_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lateral);

        // Button
        lateralData = findViewById(R.id.bt_lateral);

        final Animation springAnimation = AnimationUtils.loadAnimation(this, R.anim.spring_animation);

        lateralData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send bluetooth message
                common.write("14;3");

                // Use bounce interpolator with amplitude 0.2 and frequency 20.
                common.MyBounceInterpolator interpolator = new common.MyBounceInterpolator(0.2, 20);
                springAnimation.setInterpolator(interpolator);

                lateralData.startAnimation(springAnimation);
            }
        });

        // Number LEDs seekbar.
        nLeds = findViewById(R.id.nLeds);

        nLeds.getConfigBuilder()
                .min(0)
                .max(20)
                .progress(4)
                .sectionCount(20)
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        nLeds.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current_nLeds = nLeds.getProgress();
                if (current_nLeds != last_nLeds) {
                    common.write("13;" + current_nLeds + ";");
                    last_nLeds = current_nLeds;
                }
                return false;
            }
        });

        // Bright seekbar.
        bright = findViewById(R.id.bright);

        bright.getConfigBuilder()
                .min(0)
                .max(100)
                .progress(75)
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        bright.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current_bright = bright.getProgress();
                if (current_bright != last_bright) {
                    common.write("11;" + current_bright + ";");
                    last_bright = current_bright;
                }
                return false;
            }
        });

        // Circular speed view.
        speedometer = findViewById(R.id.speedometer);
        speedometer = findViewById(R.id.speedometer);
        speedometer.setMaxSpeed(500);
        speedometer.setMajorTickStep(50);
        speedometer.setMinorTicks(10);
        speedometer.addColoredRange(0, 100, Color.GREEN);
        speedometer.addColoredRange(100, 300, Color.YELLOW);
        speedometer.addColoredRange(300, 500, Color.RED);

        // Text view.
        speedValue = findViewById(R.id.speedValue);

        // Circular progress bar.
        circularProgressBar = findViewById(R.id.seekArc);

        circularProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current_speed = circularProgressBar.getProgress();
                if (current_speed != last_speed) {
                    common.write("11;" + current_speed*5 + ";");
                    last_speed = current_speed;
                    speedValue.setText(String.valueOf(current_speed*5));
                    speedometer.setSpeed(current_speed*5);
                }
                return false;
            }
        });

//        // Wheel view menu.
//        wheelView = findViewById(R.id.wheelview);
//
//        // Create data for the adapter.
//        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(5);
//        for (int i = 0; i < 5; i++) {
//            Map.Entry<String, Integer> entry = MaterialColor.random(this, "\\D*_500$");
//            entries.add(entry);
//        }
//
//        // Populate the adapter, that knows how to draw each item (as you would do with a ListAdapter).
//        wheelView.setAdapter(new MaterialColorAdapter(entries));
//
//        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
//            @Override
//            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
//                // The position in the adapter and whether it is closest to the selection angle.
//                Toast.makeText(getApplicationContext(), "Item position: " + position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(Lateral.this, Dashboard.class);
        startActivity(intent);
        finish();
    }

//    // Class needed by the wheel view.
//    static class MaterialColorAdapter extends WheelArrayAdapter<Map.Entry<String, Integer>> {
//        MaterialColorAdapter(List<Map.Entry<String, Integer>> entries) {
//            super(entries);
//        }
//
//        @Override
//        public Drawable getDrawable(int position) {
//            Drawable[] drawable = new Drawable[] {
//                    createOvalDrawable(getItem(position).getValue()),
//                    new TextDrawable(String.valueOf(position))
//            };
//            return new LayerDrawable(drawable);
//        }
//
//        private Drawable createOvalDrawable(int color) {
//            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
//            shapeDrawable.getPaint().setColor(color);
//            return shapeDrawable;
//        }
//    }
}
