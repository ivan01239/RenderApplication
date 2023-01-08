package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.Math.Matrix.Matrix;
import com.cgvsu.Math.Matrix.Matrix4f;
import com.cgvsu.Math.Point.Point2f;
import com.cgvsu.Math.Vector.Vector;
import com.cgvsu.Math.Vector.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import com.cgvsu.model.Model;


import static com.cgvsu.render_engine.GraphicConveyor.*;


public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) throws Vector.VectorException, Matrix.MatrixException {
        Matrix4f modelMatrix = mesh.rotateScaleTranslate;
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        viewMatrix = (Matrix4f) Matrix4f.transposeMatrix(viewMatrix);
        projectionMatrix = (Matrix4f) Matrix4f.transposeMatrix(projectionMatrix);
        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix.getVector());
        modelViewProjectionMatrix = (Matrix4f) Matrix4f.multiplicateMatrices(projectionMatrix, viewMatrix);
        modelViewProjectionMatrix = (Matrix4f) Matrix4f.multiplicateMatrices(modelViewProjectionMatrix, modelMatrix);


        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                Vector3f vertexVecmath = new Vector3f(new float[]{vertex.get(0), vertex.get(1), vertex.get(2)});

               Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);
                resultPoints.add(resultPoint);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).getX(),
                        resultPoints.get(vertexInPolygonInd - 1).getY(),
                        resultPoints.get(vertexInPolygonInd).getX(),
                        resultPoints.get(vertexInPolygonInd).getY());
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).getX(),
                        resultPoints.get(nVerticesInPolygon - 1).getY(),
                        resultPoints.get(0).getX(),
                        resultPoints.get(0).getY());
        }
    }
 }