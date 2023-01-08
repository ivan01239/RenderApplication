package com.cgvsu.Math.Matrix;

import com.cgvsu.Math.Vector.Vector;
import com.cgvsu.Math.Vector.Vector3f;
import com.cgvsu.Math.Vector.Vector4f;


public class Matrix4f extends Matrix {

    private static final int length = 16;

    private static final int size = 4;

    private float[] vector = new float[length];

    public Matrix4f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Matrix4f() {
        super(new float[length], size);
    }

    @Override
    public Matrix getZeroMatrix(final int size) {
        return new Matrix4f(new float[length]);
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector4f(new float[size]);
    }

    public static Matrix getZeroMatrix() {
        return new Matrix4f(new float[length]);
    }

    public Matrix createIdentityMatrix(final float value) {
        Matrix4f matrix = new Matrix4f(new float[size * size]);

        int indexMainDiagonal = 0;
        for (int index = 0; index < matrix.getLength(); index++) {

            if (index == indexMainDiagonal * size + indexMainDiagonal) {
                matrix.set(index, value);
                indexMainDiagonal++;
            }
        }

        return matrix;
    }

    @Override
    public Matrix createIdentityMatrix() {
        return createIdentityMatrix(1);
    }

    /**
     * Метод раскладывает матрицу 4х4 по первой строке на 4 матрицы размера 3х3, затем для каждой матрицы вызывает
     * метод поиска определителя в Matrix3x
     * @param matrix
     * @return возвращает определитель
     */
    public static float getMatrixDeterminant(final Matrix matrix) {
        float determinant = 0.0f;
        int indexCol, indexCol1, indexCol2, indexCol3;
        int indexRow = 0;

        for (int index = 0; index < matrix.getSize(); index++) {
            indexCol = index % matrix.getSize();
            indexCol1 = 0;
            int sign = (int) Math.pow(-1, indexCol + indexRow);

            if (indexCol1 == indexCol) {
                indexCol1++;
            }

            indexCol2 = indexCol1 + 1;
            if (indexCol2 == indexCol) {
                indexCol2++;
            }

            indexCol3 = indexCol2 + 1;
            if (indexCol3 == indexCol) {
                indexCol3++;
            }

            float[] m = new float[9];
            for (int index1 = 0; index1 < m.length; index1 += 3) {
                m[index1] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol1);
                m[index1 + 1] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol2);
                m[index1 + 2] = matrix.get((indexRow + 1) * matrix.getSize() + indexCol3);
                indexRow++;
            }
            indexRow = 0;

            determinant += sign * matrix.get(index) * Matrix3f.getMatrixDeterminant(new Matrix3f(m));
        }

        return determinant;
    }

    public static javax.vecmath.Matrix4f ourToJava (Matrix4f ownMatrix) {
        float[] vector = ownMatrix.getVector();
        return new javax.vecmath.Matrix4f(vector);
    }

    public static Matrix4f javaToOur (javax.vecmath.Matrix4f javaMatrix) {
        float[] vector0 = new float[4];
        float[] vector1 = new float[4];
        float[] vector2 = new float[4];
        float[] vector3 = new float[4];
        float[] newVector = new float[16];
        javaMatrix.getRow(0, vector0);
        javaMatrix.getRow(1, vector1);
        javaMatrix.getRow(2, vector2);
        javaMatrix.getRow(3, vector3);
        for (int i = 0; i < 4; i++){
            newVector[i] = vector0[i];
            newVector[4 + i] = vector1[i];
            newVector[8 + i] = vector2[i];
            newVector[12 + i] = vector3[i];
        }
        return new Matrix4f(newVector);
    }
}