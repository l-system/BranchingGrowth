package com.branching_growth;

public class Constants {
    public static final int SIZE = 1;

    // Control for drawing behavior
    public static final boolean DRAW_ONLY_ON_BLACK_PIXELS = false; // Set to true to enable this behavior

    // Segment Lengths
    public static final int MIN_SEGMENT_LENGTH = 10;
    public static final int MAX_SEGMENT_LENGTH = 15;
    public static final double MEAN_SEGMENT_LENGTH = 10; // Center of the distribution
    public static final double SEGMENT_LENGTH_STD_DEV = 20; // Adjusted for range 3-20

    // Angle Choices
    public static final int[] ANGLE_OPTIONS = {0, 45, 90, -90};
    public static final float[] ANGLE_CHANCES = {0.1f, 0.0f, 0.3f, 0.6f}; // Normalized chances for each angle

    public static final int BRANCH_COUNT = 3;

    public static final int L_SYSTEM_ITERATIONS = 1;
    public static final int MAX_BRANCHES = 10;
    public static final int DEFAULT_LIFETIME = 60000;

    // Glow parameters
    public static final float GLOW_RADIUS = 8.0f;
    public static final float MIN_GLOW_INTENSITY = 0.2f;
    public static final float MAX_GLOW_INTENSITY = 0.35f;
    public static final float GLOW_FREQUENCY = 1.0f;
    public static final float WEIGHT_EXPONENT = 2.0f;
    public static final float LINE_BRIGHTNESS_MULTIPLIER = 8.0f;
    public static final float COLOR_CYCLE_SPEED = 0.02f;

}
