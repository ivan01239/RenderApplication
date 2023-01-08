package com.cgvsu.render_engine;

import com.cgvsu.Math.Matrix.Matrix;
import com.cgvsu.Math.Matrix.Matrix3f;
import com.cgvsu.Math.Matrix.Matrix4f;
import com.cgvsu.Math.Vector.Vector3f;
import com.cgvsu.model.Model;

public class Transformation {

    /** Переход в мировую систему координат*/
    public static Matrix4f modelMatrix(Vector3f scale, Vector3f rotate, Vector3f translate, Model model)
            throws Matrix.MatrixException {
        Matrix4f rtsMatrix = model.rotateScaleTranslate;
        rtsMatrix = scale(scale, rtsMatrix);
        rtsMatrix = rotate(rotate, rtsMatrix);
        rtsMatrix = translate(translate, rtsMatrix);
        return rtsMatrix;
    }

    /** Масштабирование */
    public static Matrix4f scale(Vector3f scale, Matrix4f matrix4f) throws Matrix.MatrixException {
        float[] scaleMatrix = new float[]
                {scale.get(0), 0, 0, 0,
                0, scale.get(1), 0, 0,
                0, 0, scale.get(2), 0,
                0, 0,       0,      1};
        Matrix4f scaleMatrix4f = new Matrix4f(scaleMatrix);
        return (Matrix4f) Matrix4f.multiplicateMatrices(matrix4f, scaleMatrix4f);
    }

    /** Поворот */
    public static Matrix4f rotate(Vector3f rotate, Matrix4f matrix4f) throws Matrix.MatrixException {
        float sinX = (float) Math.sin(Math.toRadians(rotate.get(0)));
        float cosX = (float) Math.cos(Math.toRadians(rotate.get(0)));
        float sinY = (float) Math.sin(Math.toRadians(rotate.get(1)));
        float cosY = (float) Math.cos(Math.toRadians(rotate.get(1)));
        float sinZ = (float) Math.sin(Math.toRadians(rotate.get(2)));
        float cosZ = (float) Math.cos(Math.toRadians(rotate.get(2)));

        Matrix4f rotateZMatrix = new Matrix4f(new float[]
                {cosZ, -sinZ, 0, 0,
                sinZ, cosZ, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1});
        Matrix4f rotateYMatrix = new Matrix4f(new float[]{
                cosY, 0, sinY, 0,
                0, 1, 0, 0,
                -sinY, 0, cosY, 0,
                0, 0, 0, 1});
        Matrix4f rotateXMatrix = new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, cosX, -sinX, 0,
                0, sinX, cosX, 0,
                0, 0, 0, 1});
        rotateZMatrix = (Matrix4f) Matrix4f.multiplicateMatrices(rotateZMatrix, rotateYMatrix);
        rotateZMatrix = (Matrix4f) Matrix4f.multiplicateMatrices(rotateZMatrix, rotateXMatrix);
        return (Matrix4f) Matrix4f.multiplicateMatrices(matrix4f, rotateZMatrix);
    }

    /** Перемещение(смещение) */
    public static Matrix4f translate(Vector3f translate, Matrix4f matrix4f) throws Matrix.MatrixException {
        Matrix4f translateMatrix4f = new Matrix4f(new float[]{
                1, 0, 0, translate.get(0),
                0, 1, 0, translate.get(1),
                0, 0, 1, translate.get(2),
                0, 0, 0, 1});
        translateMatrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(translateMatrix4f, matrix4f);
        return translateMatrix4f;
    }

    public static Vector3f rotateVector(Vector3f rotate, Vector3f vector3f) throws Matrix.MatrixException {
        float sinX = (float) Math.sin(Math.toRadians(rotate.get(0)));
        float cosX = (float) Math.cos(Math.toRadians(rotate.get(0)));
        float sinY = (float) Math.sin(Math.toRadians(rotate.get(1)));
        float cosY = (float) Math.cos(Math.toRadians(rotate.get(1)));
        float sinZ = (float) Math.sin(Math.toRadians(rotate.get(2)));
        float cosZ = (float) Math.cos(Math.toRadians(rotate.get(2)));

        Matrix3f rotateZMatrix = new Matrix3f(new float[]
                {cosZ, -sinZ, 0,
                        sinZ, cosZ, 0,
                        0, 0, 1});
        Matrix3f rotateYMatrix = new Matrix3f(new float[]{
                cosY, 0, sinY,
                0, 1, 0,
                -sinY, 0, cosY});
        Matrix3f rotateXMatrix = new Matrix3f(new float[]{
                1, 0, 0,
                0, cosX, -sinX,
                0, sinX, cosX, });
        rotateXMatrix = (Matrix3f) Matrix4f.multiplicateMatrices(rotateXMatrix, rotateYMatrix);
        rotateXMatrix = (Matrix3f) Matrix4f.multiplicateMatrices(rotateXMatrix, rotateZMatrix);
        return (Vector3f) rotateXMatrix.multiplicateOnVector(vector3f);
    }
}
