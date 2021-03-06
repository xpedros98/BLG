package com.e.blg;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MyBTclass {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static BluetoothServerSocket bluetoothServerSocket = null;
    static InputStream inputStream = null;
    static OutputStream outputStream = null;
    static BluetoothSocket bluetoothSocket = null;
    int counter; // Counter to check the attempts until being available to connect the socket.
    int counter_10;  // Auxiliary counter to check the attempts until being available to connect the socket.
    String moduleAdress = "94:B9:7E:E4:AB:8A";

    // Class method to connect the smartphone with the device.
    public boolean connect(Context context, BluetoothAdapter bluetoothAdapter) {
        // RETURN LEGEND: -2 bluetoothAdapter is null ; -1 = bluetoothAdapter is OFF ; 0 = bluetoothSocket not connected ; 1 = bluetoothSocket properly connected.
        // Check if bluetooth is available.
        if (bluetoothAdapter == null) {
            common.addLog("PROBLEMS: this device do not support Bluetooth.");
            return false;
        }
        else {
            if (bluetoothAdapter.isEnabled() && bluetoothSocket == null) { // BluetoothAdapter is ON.
                Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();

                // Create a bluetooth device by its MAC adress.
                BluetoothDevice btDevice = bluetoothAdapter.getRemoteDevice(moduleAdress);
                common.addLog("Connecting to device: " + btDevice.getName());

                // Create the socket.
                try {
                    bluetoothSocket = btDevice.createRfcommSocketToServiceRecord(mUUID);
                    common.addLog("1/4. Socket creation done.");
                } catch (IOException e) {
                    common.addLog("ERROR: socket creation failed.");
                }

                // Create the server socket.
                try {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BLG", mUUID);
                    common.addLog("2/4. Server socket creation done.");
                } catch (IOException e) {
                    common.addLog("ERROR: server socket creation failed.");
                }

                // Connect the socket.
                socketConnect(bluetoothSocket);
                while (!bluetoothSocket.isConnected()) {
                    common.addLog("ERROR: socket connection failed. Attempts: " + counter_10);
                    socketConnect(bluetoothSocket);
                }

                if (bluetoothSocket.isConnected()) {
                    String msg = "3/4. Socket connection established.";
                    if (counter > 1) msg = msg + "Attempt: " + counter + "/" + counter_10;
                    common.addLog(msg);

                    // Bluetooth communication need.
                    try {
                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                        common.addLog("4/4. Streams created. The communication is available.");
                    } catch (IOException e) {
                        common.addLog("ERROR: getting Streams.");
                    }

                    return true;
                }
                else {
                    common.addLog("PROBLEMS: bluetoothSocket not connected properly.");
                    return false;
                }
            }
            else {
                common.addLog("PROBLEMS: bluetoothAdapter is OFF.");
                return false;
            }
        }
    }

    public boolean disconnect(Context context) {
        Toast.makeText(context, "Disconnecting...", Toast.LENGTH_SHORT).show();

        try {
            bluetoothSocket.close();
            common.addLog("1/2. Socket closed.");
        } catch (IOException e) {
            common.addLog("ERROR: socket not closed.");
            return false;
        }

        try {
            bluetoothServerSocket.close();
            common.addLog("2/2. Server socket closed. The communication is finished.");
        } catch (IOException e) {
            common.addLog("ERROR: server socket not closed.");
            return false;
        }

        bluetoothSocket = null;
        bluetoothServerSocket = null;

        return true;
    }

    public boolean newConnect(Context context, BluetoothAdapter btAdapter, String MAC) {
        moduleAdress = MAC;
        if (bluetoothSocket == null) return connect(context, btAdapter);
        else {
            boolean disconnected = disconnect(context);
            if (disconnected) return connect(context, btAdapter);
            else return false;
        }
    }

    // Function to connect the socket with several attempts if applicable.
    public void socketConnect(BluetoothSocket bluetoothSocket) {
        counter = 0;
        counter_10 += 10;
        do {
            try {
                bluetoothSocket.connect();
            }
            catch (IOException e) {}
            counter++;
        } while (!bluetoothSocket.isConnected() && counter < 10);
    }

    // Function to send data.
    public void write(String input) {
        if (bluetoothSocket != null) {
            if (bluetoothSocket.isConnected()) {
                try {
                    outputStream.write(input.getBytes());
                    common.addLog("  >> Sent: " + input + "\n");
                }
                catch (IOException e) {
                    common.addLog("  >> ERROR: sending: " + input + "\n");
                }
            }
            else {
                common.addLog("  >> ERROR: sending: socket is not connected.\n");
            }
        }
        else {
            common.addLog("  >> ERROR: sending: socket is not defined.\n");
        }
    }
}
