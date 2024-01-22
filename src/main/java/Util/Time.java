package Util;

public class Time {
    // Initialization at start up
    public static float timeStarted = System.nanoTime();

    public static float getTime() {
        return (float) ((System.nanoTime() - Time.timeStarted) * 1E-9);
    }
}
