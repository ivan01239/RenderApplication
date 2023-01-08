package com.cgvsu.render_engine;
import com.cgvsu.Math.Matrix.Matrix4f;
import com.cgvsu.Math.Point.Point2f;
import com.cgvsu.Math.Vector.Vector;
import com.cgvsu.Math.Vector.Vector3f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        float[] matrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1};
        return new Matrix4f(matrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) throws Vector.VectorException {
        return lookAt(eye, target, new Vector3f(new float[]{0F, 1.0F, 0F}));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) throws Vector.VectorException {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ = (Vector3f) Vector.minusVector(target, eye);
        resultX.crossProduct(up, resultZ);
        resultY.crossProduct(resultZ, resultX);

        resultX.normalizeVector();
        resultY.normalizeVector();
        resultZ.normalizeVector();

        float[] resX = resultX.getVector();
        float[] resY = resultY.getVector();
        float[] resZ = resultZ.getVector();

        float[] matrix = new float[]{
                resX[0], resX[1], resX[2], 0,
                resY[0], resY[1], resY[2], 0,
                resZ[0], resZ[1], resZ[2], 0,
                -resultX.dotProduct(eye), -resultY.dotProduct(eye), -resultZ.dotProduct(eye), 1};
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, tangentMinusOnDegree / aspectRatio);
        result.set(5, tangentMinusOnDegree);
        result.set(10, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(11, 1.0F);
        result.set(14, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.get(0) * matrix.get(0)) + (vertex.get(1) * matrix.get(1)) +
                (vertex.get(2) * matrix.get(2)) + matrix.get(3);
        final float y = (vertex.get(0) * matrix.get(4)) + (vertex.get(1) * matrix.get(5)) +
                (vertex.get(2) * matrix.get(6)) + matrix.get(7);
        final float z = (vertex.get(0) * matrix.get(8)) + (vertex.get(1) * matrix.get(9)) +
                (vertex.get(2) * matrix.get(10)) + matrix.get(11);
        final float w = (vertex.get(0) * matrix.get(12)) + (vertex.get(1) * matrix.get(13)) +
                (vertex.get(2) * matrix.get(14)) + matrix.get(15);
        return new Vector3f(new float[] {x / w, y / w, z / w});
    }


    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.get(0) * width + width / 2.0F, -vertex.get(1) * height + height / 2.0F);
    }


}
