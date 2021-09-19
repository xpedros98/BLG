package com.e.blg;

public class common {
    private static String log = null;

    // Log related functions.
    public static synchronized void addLog(String string){
        if (common.log == null) { common.log =  string; }
        else { common.log = common.log + "\n" + string; }
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
