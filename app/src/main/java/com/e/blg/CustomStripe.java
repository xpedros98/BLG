package com.e.blg;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Util;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.nightonke.jellytogglebutton.State;
import com.triggertrap.seekarc.SeekArc;
import com.xw.repo.BubbleSeekBar;

import java.util.Arrays;

import co.aenterhy.toggleswitch.ToggleSwitchButton;

public class CustomStripe extends Dashboard { // I extended the class Dashboard to be able to access to MyBTclass object created there.

    PercentRelativeLayout customLayout;
    Button add2, add3;
    TextView speedValue;
    BubbleSeekBar ledsNum;
    SpeedometerGauge speedometer;
    SeekArc circularProgressBar, bright;
    WheelView wheelView;
    ImageView color_sample_1, color_sample_2, color_sample_3;
    HSLColorPicker colorPicker;
    ToggleSwitchButton joystick;
    JellyToggleButton toggle;
    BoomMenuButton leftMenu, rightMenu;

    Animation add_inAnimation, add_outAnimation, turn_outAnimation, turn_inAnimation;

    // Variables related to the color picker.
    int colorId = 1;
    int color1 = Color.parseColor("#000000");
    int color2 = Color.parseColor("#000000");
    int color3 = Color.parseColor("#000000");
    String[] customGroup = {"color_picker", "color_sample1", "add2", "color_sample2", "add3", "color_sample3", "joystick"};
    String[][] prohibited = {{"color_sample2", "add3", "color_sample3"}, {"add2", "color_sample3"}, {"add2", "add3"}};

    // Variables related to the data frame.
    int palette = 0;
    int brigth = 0;
    int num = 0;
    int rotation = 0;
    int flag_num = 1;
    float currSpeed = 0;
    String RGB1 = "0_0_0";
    String RGB2 = "0_0_0";
    String RGB3 = "0_0_0";


    private int[] items = {R.drawable.rand, R.drawable.rainbow, R.drawable.fire, R.drawable.water_wave, R.drawable.leaves, R.drawable.flamingo, R.drawable.police, R.drawable.color_ball, R.drawable.palm, R.drawable.sol};

    Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_stripe);

        // Define the animations.
        add_inAnimation = AnimationUtils.loadAnimation(this, R.anim.add_in);
        add_outAnimation = AnimationUtils.loadAnimation(this, R.anim.add_out);
        turn_outAnimation = AnimationUtils.loadAnimation(this, R.anim.turn_right_out);
        turn_inAnimation = AnimationUtils.loadAnimation(this, R.anim.turn_left_in);

        customLayout = findViewById(R.id.layout_Custom);

        // Color picker arcs.
        colorPicker = findViewById(R.id.color_picker);

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                switch (colorId) {
                    case 1:
                        color_sample_1.setColorFilter(color);
                        color1 = color;
                        break;
                    case 2:
                        color_sample_2.setColorFilter(color);
                        color2 = color;
                        break;
                    case 3:
                        color_sample_3.setColorFilter(color);
                        color3 = color;
                        break;
                }
            }
        });

        colorPicker.setColor(color1);

        // Color samples.
        color_sample_1 = findViewById(R.id.color_sample1);
        color_sample_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 1;
                colorPicker.setColor(color1);
            }
        });

        color_sample_2 = findViewById(R.id.color_sample2);
        color_sample_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 2;
                colorPicker.setColor(color2);
            }
        });
        color_sample_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (add3.getVisibility() == View.VISIBLE) {
                    flag_num = 1;
                    add3.startAnimation(add_outAnimation);
                    add3.setVisibility(View.INVISIBLE);
                    color_sample_2.startAnimation(add_outAnimation);
                    color_sample_2.setVisibility(View.INVISIBLE);
                    add2.startAnimation(add_inAnimation);
                    add2.setVisibility(View.VISIBLE);

                    if (colorId == 2) {
                        colorId = 1;
                        colorPicker.setColor(color1);
                    }
                }
                return false;
            }
        });

        color_sample_3 = findViewById(R.id.color_sample3);
        color_sample_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorId = 3;
                colorPicker.setColor(color3);
            }
        });
        color_sample_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                flag_num = 2;
                color_sample_3.startAnimation(add_outAnimation);
                color_sample_3.setVisibility(View.INVISIBLE);
                add3.startAnimation(add_inAnimation);
                add3.setVisibility(View.VISIBLE);

                if (colorId == 3) {
                    colorId = 2;
                    colorPicker.setColor(color2);
                }
                return false;
            }
        });

        // Add buttons.
        add2 = findViewById(R.id.add2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_num = 2;
                colorId = 2;
                add2.startAnimation(add_outAnimation);
                add2.setVisibility(View.INVISIBLE);
                add3.startAnimation(add_inAnimation);
                add3.setVisibility(View.VISIBLE);
                color_sample_2.startAnimation(add_inAnimation);
                color_sample_2.setVisibility(View.VISIBLE);
            }
        });

        add3 = findViewById(R.id.add3);
        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_num = 3;
                colorId = 3;
                add3.startAnimation(add_outAnimation);
                add3.setVisibility(View.INVISIBLE);
                color_sample_3.startAnimation(add_inAnimation);
                color_sample_3.setVisibility(View.VISIBLE);
            }
        });

        // Number LEDs seekbar.
        ledsNum = findViewById(R.id.ledsNum);

        ledsNum.getConfigBuilder()
                .min(0)
                .max(20)
                .progress(4)
                .sectionCount(20)
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        ledsNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        // Circular progress bar for bright.
        bright = findViewById(R.id.bright);
        bright.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
                int curr = bright.getProgress();
                int R_day_end = 251;
                int G_day_end = 251;
                int B_day_end = 132;
                int R_day_start = 128;
                int G_day_start = 222;
                int B_day_start = 234;
                int R_night_end = 42;
                int G_night_end = 53;
                int B_night_end = 94;
                int R_night_start = 3;
                int G_night_start = 8;
                int B_night_start = 30;

                GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[] {Color.rgb(R_night_end + (R_day_end - R_night_end)*curr/bright.getSweepAngle(),G_night_end + (G_day_end - G_night_end)*curr/bright.getSweepAngle(),B_night_end + (B_day_end - B_night_end)*curr/bright.getSweepAngle()),
                               Color.rgb(R_night_start + (R_day_start - R_night_start)*curr/bright.getSweepAngle(),G_night_start + (G_day_start - G_night_start)*curr/bright.getSweepAngle(),B_night_start + (B_day_start - B_night_start)*curr/bright.getSweepAngle())});
                gd.setCornerRadius(0f);
                customLayout.setBackgroundDrawable(gd);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        // Default-customizable modes switch.
        toggle = findViewById(R.id.toggle);

        // Set the layout according the initial default configuration (left).
        setGroupVisibility(customGroup, View.INVISIBLE);
        toggle.setOnStateChangeListener(new JellyToggleButton.OnStateChangeListener() {
            @Override
            public void onStateChange(float process, State state, JellyToggleButton jtb) {
                if (state == State.RIGHT_TO_LEFT) { // Default
                    setGroupAnimation(customGroup, add_outAnimation);
                    setGroupVisibility(customGroup, View.INVISIBLE);
                    wheelView.startAnimation(add_inAnimation);
                    wheelView.setVisibility(View.VISIBLE);
                }
                else if (state == State.LEFT_TO_RIGHT) { // Custom
                    wheelView.startAnimation(add_outAnimation);
                    wheelView.setVisibility(View.INVISIBLE);
                    setGroupAnimation(customGroup, add_inAnimation);
                    setGroupVisibility(customGroup, View.VISIBLE);
                }
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
        speedValue = findViewById(R.id.speed_val);

        // Circular progress bar for speed.
        circularProgressBar = findViewById(R.id.seekArc);

        circularProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currSpeed = circularProgressBar.getProgress();
                speedValue.setText(String.valueOf(currSpeed*5) + " Hz");
                speedometer.setSpeed(currSpeed*5);
                return false;
            }
        });

        // Wheel view menu.
        wheelView = findViewById(R.id.wheelview);
        int itemsNum = items.length;
        wheelView.setWheelItemCount(itemsNum);

        Drawable[] drawables = new Drawable[itemsNum];
        for (int i=0; i < itemsNum; i++) {
            drawables[i] = getResources().getDrawable(items[i]);
        }

        // Populate the adapter, that knows how to draw each item (as you would do with a ListAdapter).
        wheelView.setAdapter(new WheelAdapter() {
             @Override
             public Drawable getDrawable(int position) {
                 return drawables[position];
             }

             @Override
             public int getCount() {
                 return itemsNum;
             }
         });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                // The position in the adapter and whether it is closest to the selection angle.
                Toast.makeText(getApplicationContext(), "Item position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        // Define the switch between custom colors and default palettes.
        joystick = findViewById(R.id.joystick);
        joystick.setOnTriggerListener(new ToggleSwitchButton.OnTriggerListener() {
            @Override
            public void toggledUp() {

            }

            @Override
            public void toggledDown() {

            }
        });

        // Left splash menu.
        leftMenu = findViewById(R.id.left_menu);

        leftMenu.setButtonEnum(ButtonEnum.SimpleCircle);

        for (int i = 0; i < leftMenu.getButtonPlaceEnum().buttonNumber(); i++) {
            leftMenu.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.add_icon));
        }

        // Right splash menu.
        rightMenu = findViewById(R.id.right_menu);

        rightMenu.setButtonEnum(ButtonEnum.SimpleCircle);

        for (int i = 0; i < rightMenu.getButtonPlaceEnum().buttonNumber(); i++) {
            rightMenu.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.add_icon));
        }
    }

    // Back button callback.
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(CustomStripe.this, Dashboard.class);
        startActivity(intent);
        finish();
    }


    public static void RgbToHsl (int red, int green, int blue, float hsl[]) {
        float r = (float) red / 255;
        float g = (float) green / 255;
        float b = (float) blue / 255;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float f;/*from  w  ww. j ava2s  . co  m*/
        if (max == min)
            f = 0;
        else if (max == r && g >= b)
            f = (60 * (g - b)) / (max - min);
        else if (max == r && g < b)
            f = 360 + (60 * (g - b)) / (max - min);
        else if (max == g)
            f = 120 + (60 * (b - r)) / (max - min);
        else if (max == b)
            f = 240F + (60F * (r - g)) / (max - min);
        else
            f = 0;
        float f1 = (max + min) / 2;
        float f2;
        if (f1 != 0 && max != min) {
            if (0 < f1 && f1 <= 0.5) {
                f2 = (max - min) / (max + min);
            } else if (f1 == 0.5) {
                f2 = (max - min) / (2.0F - (max + min));
            } else {
                f2 = 0;
            }
        } else {
            f2 = 0.0F;
        }
        hsl[0] = f;
        hsl[1] = f2;
        hsl[2] = f1;
    }

    public static float[] ColorToHsl(int color) {
        float[] hsl = new float[0];
        RgbToHsl(0xff & color >>> 16, 0xff & color >>> 8, color & 0xff, hsl);
        return hsl;
    }

    private void setCurrColor(int id) {
        switch (id) {
            case 1: colorPicker.setColor(color1); break;
            case 2: colorPicker.setColor(color2); break;
            case 3: colorPicker.setColor(color3); break;
        }
    }

    private void setGroupAnimation(String[] group, Animation animation) {
        for (String id: group) {
            int Id = getResources().getIdentifier(id, "id", this.getPackageName());
            if (!(group == customGroup & Arrays.asList(prohibited[flag_num - 1]).contains(id))) {
                findViewById(Id).startAnimation(animation);
            }
        }
    }

    private void setGroupVisibility(String[] group, int visibility) {
        for (String id: group) {
            int Id = getResources().getIdentifier(id, "id", this.getPackageName());
            if (!(group == customGroup & Arrays.asList(prohibited[flag_num - 1]).contains(id))) {
                findViewById(Id).setVisibility(visibility);
            }
        }
    }
}
