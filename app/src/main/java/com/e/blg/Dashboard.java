package com.e.blg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
import java.util.Set;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {

    // GUI objects.
    RelativeLayout relativeLayout;
    Button btBtn, iBtn, logBtn, refreshBtn, eraseBtn;
    LinearLayout logLayout;
    TextView feedback;
    ListView devicesList;

    private ArrayAdapter devicesArray;

    // Animations.
    Animation logDownAnimation, logUpAnimation, transparentAnimation, refresh_rev, logBtnDownAnimation, logBtnUpAnimation;
    AnimationDrawable animationDrawable;

    // Bluetooth related.
    BluetoothAdapter bluetoothAdapter;
    private static final String MODULE_ADRESS = "00:19:04:EE:A3:5C";
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket bluetoothSocket = null;
    BluetoothServerSocket bluetoothServerSocket = null;
    Set<BluetoothDevice>  pairedDevices;
    InputStream inputStream = null;
    OutputStream outputStream = null;

    int counter;
    int counter_10;

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

        logLayout = findViewById(R.id.log_layout);
        feedback = findViewById(R.id.feedback);
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
                    Toast.makeText(getBaseContext(), "PROBLEMS: This device do not support Bluetooth.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getBaseContext(), "PROBLEMS: This device do not support Bluetooth.", Toast.LENGTH_SHORT).show();
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

        btBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothSocket == null) {
                    // Check if bluetooth is available.
                    if (bluetoothAdapter == null) {
                        logFeedback("PROBLEMS: This device do not support Bluetooth.");
                    } else {
                        if (bluetoothAdapter.isEnabled()) { // Is turned ON.
                            Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_SHORT).show();

                            // Create a bluetooth device by its MAC adress.
                            BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice(MODULE_ADRESS);
                            logFeedback("Connecting to device: " + hc05.getName());

                            // Create the socket.
                            try {
                                bluetoothSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                                logFeedback("1/4. Socket creation done.");
                            } catch (IOException e) {
                                logFeedback("ERROR: socket creation failed.");
                            }

                            // Create the server socket.
                            try {
                                bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BLG", mUUID);
                                logFeedback("2/4. Server socket creation done.");
                            } catch (IOException e) {
                                logFeedback("ERROR: server socket creation failed.");
                            }

                            // Connect the socket.
                            socketConnect();
                            while (!bluetoothSocket.isConnected()) {
                                counter_10 += 10;
                                logFeedback("ERROR: socket connection failed. Intents: " + counter_10);
                                socketConnect();
                            }

                            if (bluetoothSocket.isConnected()) {
                                logFeedback("3/4. Socket connection established. Intent: " + counter + "/" + counter_10);

                                // Bluetooth communication need.
                                try {
                                    inputStream = bluetoothSocket.getInputStream();
                                    outputStream = bluetoothSocket.getOutputStream();
                                    logFeedback("4/4. Streams created. The communication is available.");
                                } catch (IOException e) {
                                    logFeedback("ERROR: getting Streams.");
                                }

                                btBtn.setBackground(getResources().getDrawable(R.drawable.bluetooth_icon));
                                Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();

                                common.setBluetoothSocket(bluetoothSocket);
                                common.setOutputStream(outputStream);
                            }
                        }
                        else{ // Is turned OFF.
                        // Ask user to activate bluetooth.
                        Intent intent_BT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent_BT, 1);
                        }
                    }
                }
                else if (bluetoothSocket.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Disconnecting...", Toast.LENGTH_SHORT).show();

                    try {
                        bluetoothSocket.close();
                        logFeedback("1/2. Socket closed.");

                        btBtn.setBackground(getResources().getDrawable(R.drawable.bluetooth_icon_off));
                    } catch (IOException e) {
                        logFeedback("ERROR: socket not closed.");
                    }

                    try {
                        bluetoothServerSocket.close();
                        logFeedback("2/2. Server socket closed. Bluetooth connection finished.");
                        Toast.makeText(getApplicationContext(), "Disconnected.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        logFeedback("ERROR: server socket not closed.");
                    }

                    bluetoothSocket = null;
                    bluetoothServerSocket = null;
                }
                else {
                    Toast.makeText(getBaseContext(), "PROBLEMS: on BT button click listener.", Toast.LENGTH_SHORT).show();
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
    }

    // Function to connect the socket with several attempts if applicable.
    public void socketConnect() {
        counter = 0;

        do {
            try {
                bluetoothSocket.connect();
            }
            catch (IOException e) {}
            counter++;
        } while (!bluetoothSocket.isConnected() && counter < 10);
    }

    // Function to refresh the paired devices.
    public void refreshPairedDevices() {
        // Initialize Array adapter that requires a .xml file (new "Layout resource file" in the "layout" directory) with a single TextView.
        devicesArray = new ArrayAdapter(Dashboard.this, R.layout.devices_tv);

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
            }
            logFeedback("Paired devices list refreshed.");
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
            // Get MAC adress from device (last 17 characters of the View).
            String info = ((TextView) v).getText().toString();
            String name = info.substring(0, info.length() - 17);
            logFeedback("Selected device: " + name);
        }
    };

    // Append the feedback to its own tv and scrolls to end.
    private void logFeedback(String msg) {
        feedback.append(">> " + msg + "\n");
        final int scrollAmount = feedback.getLayout().getLineTop(feedback.getLineCount()) - feedback.getHeight();
        if (scrollAmount > 0)
            feedback.scrollTo(0, scrollAmount);
        else
            feedback.scrollTo(0, 0);
    }
}
