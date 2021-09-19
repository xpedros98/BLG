package com.e.blg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.circlemenu.CircleMenuView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {

    // GUI objects.
    RelativeLayout relativeLayout;
    Button btBtn, iBtn, logBtn, refreshBtn, eraseBtn, sendBtn;
    LinearLayout logLayout;
    TextView feedback, lastTv;
    com.google.android.material.textfield.TextInputEditText to_send;
    ListView devicesList;

    // Animations.
    Animation logDownAnimation, logUpAnimation, transparentAnimation, refresh_rev, logBtnDownAnimation, logBtnUpAnimation;
    AnimationDrawable animationDrawable;

    // Bluetooth related.
    public MyBTclass bt = new MyBTclass();
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket = null;
    Set<BluetoothDevice>  pairedDevices;

    int refresh_counter; // Counter for the refresh function to know the first iteration to show special feedback.

    // Time control variables
    private long t_click_logBtn = 0;
    private long t_last_click_logBtn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        relativeLayout = findViewById(R.id.layout_Dashboard);

        // Declarations
        btBtn = findViewById(R.id.bt_button);
        iBtn = findViewById(R.id.i_button);
        refreshBtn = findViewById(R.id.refresh_button);
        eraseBtn = findViewById(R.id.erase_button);
        logBtn = findViewById(R.id.log_button);
        sendBtn = findViewById(R.id.send_button);

        logLayout = findViewById(R.id.log_layout);
        feedback = findViewById(R.id.feedback);
        feedback.setMovementMethod(new ScrollingMovementMethod());
        to_send = findViewById(R.id.to_send);
        devicesList = findViewById(R.id.devices_list);

        // Animations
        logDownAnimation = AnimationUtils.loadAnimation(this,R.anim.log_down_animation);
        logUpAnimation = AnimationUtils.loadAnimation(this,R.anim.log_up_animation);
        transparentAnimation = AnimationUtils.loadAnimation(this,R.anim.transparent_animation);
        logBtnDownAnimation = AnimationUtils.loadAnimation(this,R.anim.log_button_down_animation);
        logBtnUpAnimation = AnimationUtils.loadAnimation(this,R.anim.log_button_up_animation);
        refresh_rev = AnimationUtils.loadAnimation(this, R.anim.rotate_360);
        refresh_rev.setDuration(1000);

        // Set gradient animation on background.
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(500);
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();

        // Add all commands collected by the log on the common class.
        feedback.setText(common.getLog());

        // Print the date on the log.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" * * dd/MM/yyyy - HH:mm:ss * * ");
        Date d = Calendar.getInstance().getTime();
        String timestamp = simpleDateFormat.format(d);
        feedback.append(timestamp + "\n");

        // Bluetooth communication need.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Data exchange between activities need.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Get the string with the specified key.
            String extraString = extras.getString("main_act");
            if (extraString.equals("check_BT")) {
                // Check if bluetooth is available.
                if (bluetoothAdapter == null) {
                    Toast.makeText(getBaseContext(), "PROBLEMS: this device do not support Bluetooth.", Toast.LENGTH_SHORT).show();
                } else {
                    if (bluetoothAdapter.isEnabled()) { // Is turned ON.
                        Toast.makeText(getBaseContext(), "Bluetooth is already turned ON.", Toast.LENGTH_SHORT).show();

                    } else { // Is turned OFF.
                        // Ask user to activate bluetooth.
                        Intent intent_BT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent_BT, 1);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "KEY: main_act; DATA: " + extraString, Toast.LENGTH_SHORT).show();
            }
        }

        // Circle Menu
        final CircleMenuView circleMenuView = findViewById(R.id.circle_menu_1);

        circleMenuView.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
                // Check if bluetooth is available.
                if (bluetoothAdapter == null) {
                    Toast.makeText(getBaseContext(), "PROBLEMS: this device do not support Bluetooth.", Toast.LENGTH_SHORT).show();
                } else {
                    if (bluetoothAdapter.isEnabled()) { // Is turned ON.
                        // Do nothing.
                    } else { // Is turned OFF.
                        // Ask user to activate bluetooth.
                        Intent intent_BT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent_BT, 1);
                    }
                }

                // Check the circle menu option clicked.
                switch (buttonIndex) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Synced", Toast.LENGTH_SHORT).show();

                        // Open new activity and close the current one.
                        Intent intent0 = new Intent(Dashboard.this, Sync.class);
                        startActivity(intent0);
                        finish();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "Out of sync", Toast.LENGTH_SHORT).show();

                        // Open new activity and close the current one.
                        Intent intent1 = new Intent(Dashboard.this, Out_sync.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Lateral light", Toast.LENGTH_SHORT).show();

                        // Open new activity and close the current one.
                        Intent intent2 = new Intent(Dashboard.this, Lateral.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "Down light", Toast.LENGTH_SHORT).show();

                        // Open new activity and close the current one.
                        Intent intent3 = new Intent(Dashboard.this, Down.class);
                        startActivity(intent3);
                        finish();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "Focus lantern", Toast.LENGTH_SHORT).show();

                        // Open new activity and close the current one.
                        Intent intent4 = new Intent(Dashboard.this, Lantern.class);
                        startActivity(intent4);
                        finish();
                        break;
                }
            }
        });

        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open new activity and close the current one.
                Intent intent = new Intent(Dashboard.this, About.class);
                startActivity(intent);
                finish();

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.write(Objects.requireNonNull(to_send.getText()).toString(), bluetoothSocket);
            }
        });

        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket == null) {
                    boolean connected = bt.connect(getApplicationContext(), bluetoothAdapter, bluetoothSocket);

                    if (connected) {
                        Toast.makeText(getApplicationContext(), "Disconnected.", Toast.LENGTH_SHORT).show();
                        btBtn.setBackground(getResources().getDrawable(R.drawable.bluetooth_icon));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Not connected.", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (bluetoothSocket.isConnected()) {
                    boolean disconnected = bt.disconnect(getApplicationContext(), bluetoothSocket);

                    if (disconnected) {
                        Toast.makeText(getApplicationContext(), "Disconnected.", Toast.LENGTH_SHORT).show();
                        btBtn.setBackground(getResources().getDrawable(R.drawable.bluetooth_icon_off));
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Not disconnected.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "BT problems...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshPairedDevices();
                refreshBtn.startAnimation(refresh_rev);
            }
        });

        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Print the date on the log.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" * * dd/MM/yyyy - HH:mm:ss * * ");
                Date d = Calendar.getInstance().getTime();
                String timestamp = simpleDateFormat.format(d);
                feedback.setText(timestamp);
            }
        });

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                t_click_logBtn = SystemClock.elapsedRealtime();

                if (t_click_logBtn > t_last_click_logBtn + 1000) {
                    if (logLayout.getVisibility() == View.INVISIBLE) {
                        iBtn.setVisibility(View.INVISIBLE);

                        logLayout.setVisibility(View.VISIBLE);
                        logBtn.startAnimation(logBtnDownAnimation);
                        logLayout.startAnimation(logDownAnimation);

                        logBtn.setRotation(180);
                        logBtn.setTranslationY(30);

                        refreshPairedDevices();
                    } else {
                        logBtn.setRotation(0);
                        logBtn.setTranslationY(0);

                        logBtn.startAnimation(logBtnUpAnimation);
                        logLayout.startAnimation(logUpAnimation);
                        iBtn.startAnimation(transparentAnimation);

    //                    try {
    //                        Thread.sleep(1000); // [ms].
    //                    }catch (Exception e){}

                        iBtn.setVisibility(View.VISIBLE);
                        logLayout.setVisibility(View.INVISIBLE);
                    }

                    t_last_click_logBtn = SystemClock.elapsedRealtime();

                }
            }
        });

        refreshPairedDevices();
    }

    // Function to refresh the paired devices.
    public void refreshPairedDevices() {
        refresh_counter += 1;
        // Initialize Array adapter that requires a .xml file (new "Layout resource file" in the "layout" directory) with a single TextView.
        ArrayAdapter devicesArray = new ArrayAdapter(Dashboard.this, R.layout.devices_tv);

        // Initialize listView.
        devicesList.setAdapter(devicesArray);
        devicesList.setOnItemClickListener(mDeviceClickListener);

        // Get the linked BT devices.
        pairedDevices = bluetoothAdapter.getBondedDevices();

        // Set the linked BT devices to the listView.
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device: pairedDevices) {
                devicesArray.add(device.getName() + "\n" + device.getAddress());

                // Get feedback about the desired device to pair by the automatic button.
                if (device.getName().equals("BT420") && refresh_counter == 1) {
                    Toast.makeText(getBaseContext(), "Alright, BT420 is available!", Toast.LENGTH_SHORT).show();
                }
            }
            logFeedback("Paired devices refreshed.");
        }
    }

    @Override
    protected void onDestroy() {
        common.setLog(feedback.getText().toString());
        super.onDestroy();
    }

    // List item click callback.
    final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            if (lastTv != null) {
                lastTv = (TextView) v; // Update lastTv.
                lastTv.setTypeface(null, Typeface.BOLD);
            }
            // Get MAC adress from device (last 17 characters of the View).
            String info = ((TextView) v).getText().toString();
            String name = info.substring(0, info.length() - 17);
            logFeedback("Selected device: " + name);

        }
    };

    // Append the feedback to its own tv and scrolls to end.
    public void logFeedback(String msg) {
        // Check if there are log messages and add them to the feedback textview.
        if (common.getLog() != null) {
            feedback.append("-> Log: \n  " + common.getLog() + "\n");
            common.setLog("");
        }

        feedback.append(">> " + msg + "\n");

        // Automatically scroll down the feedback textview.
        if (logLayout.getVisibility() == View.VISIBLE) {
            final int scrollAmount = feedback.getLayout().getLineTop(feedback.getLineCount()) - feedback.getHeight();
            if (scrollAmount > 0) {
                feedback.scrollTo(0, scrollAmount);
            }
            else {
                feedback.scrollTo(0, 0);
            }
        }
    }
}
