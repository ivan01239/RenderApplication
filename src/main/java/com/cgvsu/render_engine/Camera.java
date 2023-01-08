package com.cgvsu.render_engine;


import com.cgvsu.Math.Matrix.Matrix4f;
import com.cgvsu.Math.Vector.Vector;
import com.cgvsu.Math.Vector.Vector3f;

public class Camera {

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) throws Vector.VectorException {
        this.position = (Vector3f) Vector.sumVector(position, translation);
    }

    public void moveTarget(final Vector3f translation) throws Vector.VectorException {
        this.target = (Vector3f) Vector.sumVector(target, target);
    }

    Matrix4f getViewMatrix() throws Vector.VectorException {
        return GraphicConveyor.lookAt(position, target);
    }

    Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}
