package com.branching_growth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Camera {
    private Vector3 position;
    private float zoom;
    private final float minZoom;
    private final float maxZoom;

    public Camera(float startX, float startY, float startZoom) {
        this.position = new Vector3(startX, startY, 0);
        this.zoom = startZoom;
        this.minZoom = 0.1f; // Minimum zoom level
        this.maxZoom = 2.0f; // Maximum zoom level
    }

    public void move(float deltaX, float deltaY) {
        position.add(deltaX, deltaY, 0);
    }

    public void zoom(float amount) {
        zoom = Math.max(minZoom, Math.min(maxZoom, zoom + amount));
    }

    public Matrix4 getCombinedMatrix() {
        return new Matrix4().setToOrtho2D(
                position.x - (Gdx.graphics.getWidth() / 2) / zoom,
                position.y - (Gdx.graphics.getHeight() / 2) / zoom,
                position.x + (Gdx.graphics.getWidth() / 2) / zoom,
                position.y + (Gdx.graphics.getHeight() / 2) / zoom
        );
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getZoom() {
        return zoom;
    }
}
