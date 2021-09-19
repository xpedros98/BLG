package com.e.blg;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class common {
    private static BluetoothSocket socket;
    private static OutputStream outputStream = null;
    private static String log = null;

    // Bluetooth related functions.
    public static synchronized void setBluetoothSocket(BluetoothSocket socket){
        common.socket = socket;
    }

    public static synchronized void setOutputStream(OutputStream outputStream){
        common.outputStream = outputStream;
    }

    // Function to send data.
    public static void write(String input)
    {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    outputStream.write(input.getBytes());
                    addLog("  >> Sent: " + input + "\n");
                }
                catch (IOException e) {
                    addLog("  >> ERROR: sending: " + input + "\n");
                }
            }
            else {
                addLog("  >> ERROR: sending: socket is not connected.\n");
            }
        }
        else {
            addLog("  >> ERROR: sending: socket is not defined.\n");
        }
    }

    // Log related functions.
    public static synchronized void addLog(String string){
        if (common.log == null) { common.log =  string; }
        else { common.log = common.log + string; }
    }

    public static synchronized void setLog(String string){
        common.log = string;
    }

    public static synchronized String getLog(){
        return common.log;
    }

    // Spring animation related functions.
    public static class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator (double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) * Math.cos(mFrequency * time) + 1);
        }
    }
}
