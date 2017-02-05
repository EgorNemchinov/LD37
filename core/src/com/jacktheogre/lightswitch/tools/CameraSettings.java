package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;

/**
 * Created by luna on 05.02.17.
 */

public class CameraSettings {
    private float x, y;
    private float zoom;

    public CameraSettings(float x, float y, float zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public CameraSettings(CameraSettings cameraSettings) {
        this.x = cameraSettings.x;
        this.y = cameraSettings.y;
        this.zoom = cameraSettings.zoom;
    }

    public CameraSettings(Camera camera) {
        this.x = camera.position.x;
        this.y = camera.position.y;
        if(ClassReflection.isInstance(OrthographicCamera.class, camera)) {
            this.zoom = ((OrthographicCamera) camera).zoom;
        } else
            this.zoom = 1f;
    }

    public void applyTo(Camera camera) {
        camera.position.x = x;
        camera.position.y = y;
    }

    public void applyTo(OrthographicCamera camera) {
        camera.position.x = x;
        camera.position.y = y;
        camera.zoom = this.zoom;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}
