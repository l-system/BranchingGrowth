package com.branching_growth;

import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Branch {
    private Position pos;
    private String state = "RUNNING";
    private List<Position> ownFields;
    private long age = 0; // Age in milliseconds
    private final int lifeTime;
    private final int SIZE = Constants.SIZE;
    private final int screenWidth;
    private final int screenHeight;

    private int segmentLength; // Randomized segment length
    private static final Random rand = new Random();
    private final float[] color;
    private float colorChangeSpeed = Constants.COLOR_CYCLE_SPEED; // Speed of color change
    private float[] colorDirection; // Direction of color change
    private String currentString;
    private final Map<Character, String> rules;

    private Stack<Position> positionStack = new Stack<>();
    private Stack<Float> angleStack = new Stack<>(); // Separate stack for angles
    private float currentAngle; // Starting angle

    // Track pixel occupancy
    private boolean[][] occupiedPixels;

    public Branch(Position pos, int screenWidth, int screenHeight) {
        this.pos = pos; // Start position
        this.ownFields = new ArrayList<>();
        this.ownFields.add(pos);
        this.lifeTime = Constants.DEFAULT_LIFETIME;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Initialize the occupancy tracker
        this.occupiedPixels = new boolean[screenWidth][screenHeight];

        // Generate segment length using a normal distribution
        this.segmentLength = generateSegmentLength();

        this.color = new float[]{
                rand.nextFloat(),
                rand.nextFloat(),
                rand.nextFloat(),
                1.0f
        };

        this.colorDirection = new float[]{
                rand.nextBoolean() ? 1 : -1,
                rand.nextBoolean() ? 1 : -1,
                rand.nextBoolean() ? 1 : -1
        };

        this.rules = initializeRules();
        this.currentString = generateLSystem(); // Generate L-System string
        this.currentAngle = 90; // Start pointing upwards
    }

    private Map<Character, String> initializeRules() {
        Map<Character, String> rules = new HashMap<>();
        // Dragon curve rules
        rules.put('F', "F");
        rules.put('X', "X+YF+");
        rules.put('Y', "-FX-Y");
        return rules;
    }

    private String generateLSystem() {
        StringBuilder currentString = new StringBuilder("FX"); // Starting axiom
        for (int i = 0; i < Constants.L_SYSTEM_ITERATIONS; i++) {
            StringBuilder newString = new StringBuilder();
            for (char ch : currentString.toString().toCharArray()) {
                newString.append(rules.getOrDefault(ch, String.valueOf(ch)));
            }
            currentString = newString;
        }
        return currentString.toString();
    }

    public void drawMove(Pixmap pixmap, float deltaTime) {
        if (age >= lifeTime) {
            state = "STOPPED";
            return;
        }

        age += deltaTime * 1000; // Convert seconds to milliseconds
        updateColor(deltaTime);

        // Use a stack to iterate through the commands
        Stack<Character> commandStack = new Stack<>();
        for (char command : currentString.toCharArray()) {
            commandStack.push(command);
        }

        while (!commandStack.isEmpty()) {
            char command = commandStack.pop();
            switch (command) {
                case 'F':
                    Position segmentEndPos = calculateEndPosition(segmentLength);
                    if (segmentEndPos != null) {
                        // Draw only on black pixels if enabled
                        if (!Constants.DRAW_ONLY_ON_BLACK_PIXELS || isPixelBlack(pixmap, segmentEndPos.x, segmentEndPos.y)) {
                            drawLine(pixmap, pos.x, pos.y, segmentEndPos.x, segmentEndPos.y);
                            pos = segmentEndPos; // Move to the new position
                            ownFields.add(segmentEndPos); // Keep track of all positions
                        }
                    }
                    break;
                case '+':
                    currentAngle += getRandomAngle(); // Choose a random angle to turn right
                    break;
                case '-':
                    currentAngle -= getRandomAngle(); // Choose a random angle to turn left
                    break;
                case '[':
                    positionStack.push(new Position(pos.x, pos.y)); // Save current position
                    angleStack.push(currentAngle); // Save current angle
                    break;
                case ']':
                    pos = positionStack.pop(); // Restore last position
                    currentAngle = angleStack.pop(); // Restore last angle
                    break;
            }
        }
    }

    private boolean isPixelBlack(Pixmap pixmap, int x, int y) {
        if (x < 0 || x >= screenWidth || y < 0 || y >= screenHeight) {
            return false; // Out of bounds
        }
        // Check if the pixel is black (0,0,0)
        return (pixmap.getPixel(x, y) == 0);
    }

    private int generateSegmentLength() {
        double mean = Constants.MEAN_SEGMENT_LENGTH;
        double stddev = Constants.SEGMENT_LENGTH_STD_DEV;
        double value;
        do {
            value = rand.nextGaussian() * stddev + mean; // Generate a value from a normal distribution
        } while (value < Constants.MIN_SEGMENT_LENGTH || value > Constants.MAX_SEGMENT_LENGTH);
        return (int) value;
    }

    private int getRandomAngle() {
        // Generate a random float between 0 and 1
        float randomValue = rand.nextFloat();
        float cumulativeChance = 0.0f;

        for (int i = 0; i < Constants.ANGLE_CHANCES.length; i++) {
            cumulativeChance += Constants.ANGLE_CHANCES[i];
            if (randomValue < cumulativeChance) {
                return Constants.ANGLE_OPTIONS[i];
            }
        }
        return Constants.ANGLE_OPTIONS[0]; // Fallback (shouldn't reach here)
    }

    private Position calculateEndPosition(int segmentLength) {
        int newX = pos.x + (int) (Math.cos(Math.toRadians(currentAngle)) * segmentLength);
        int newY = pos.y + (int) (Math.sin(Math.toRadians(currentAngle)) * segmentLength);

        return (newX >= 0 && newX < screenWidth && newY >= 0 && newY < screenHeight)
                ? new Position(newX, newY) : null;
    }

    private void updateColor(float deltaTime) {
        for (int i = 0; i < 3; i++) {
            color[i] += colorChangeSpeed * colorDirection[i] * deltaTime;

            if (color[i] > 1.0f) {
                color[i] = 1.0f;
                colorDirection[i] = -1; // Reverse direction
            } else if (color[i] < 0.0f) {
                color[i] = 0.0f;
                colorDirection[i] = 1; // Reverse direction
            }
        }
    }

    private void drawLine(Pixmap pixmap, int startX, int startY, int endX, int endY) {
        // Draw the line segment, checking for occupied pixels
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // Check if the pixel is black (0,0,0) if the drawing behavior is enabled
            if (!Constants.DRAW_ONLY_ON_BLACK_PIXELS || isPixelBlack(pixmap, startX, startY)) {
                pixmap.setColor(color[0], color[1], color[2], color[3]);
                pixmap.drawPixel(startX, startY); // Draw pixel
                occupiedPixels[startX][startY] = true; // Mark as occupied
            }

            // Check if we have reached the end pixel
            if (startX == endX && startY == endY) break;

            int err2 = err * 2;
            if (err2 > -dy) {
                err -= dy;
                startX += sx;
            }
            if (err2 < dx) {
                err += dx;
                startY += sy;
            }
        }
    }

    // Public getters
    public String getState() {
        return state;
    }

    public long getAge() {
        return age;
    }

    public int getLifeTime() {
        return lifeTime;
    }
}
