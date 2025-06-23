package com.branching_growth;

public class Position {
    public int x;
    public int y;
    public float z; // Add a z coordinate

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0; // Initialize z to 0 or any desired default value
    }

    // Optional: Add a constructor that includes z
    public Position(int x, int y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
