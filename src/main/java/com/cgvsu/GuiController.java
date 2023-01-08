package com.cgvsu;

import com.cgvsu.Math.Matrix.Matrix;
import com.cgvsu.Math.Matrix.Matrix4f;
import com.cgvsu.Math.Vector.Vector;
import com.cgvsu.Math.Vector.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.GraphicConveyor;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.Transformation;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GuiController {

    final private float TRANSLATION = 1.15F;


    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField modelNumber;

    @FXML
    private CheckBox transformForAll;
    @FXML
    private RadioButton setXButton;
    @FXML
    private RadioButton setYButton;
    @FXML
    private RadioButton setZButton;


    private int mouseClick = 0;
    private int chooseModel = -1;
    private final List<Model> modelList = new ArrayList<>();

    private final Camera camera = new Camera(
            new Vector3f(new float[] {0, 0, 100}),
            new Vector3f(new float[]{0, 0, 0}),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (!modelList.isEmpty()) {
                try {
                    for (int i = 0; i < modelList.size(); i++) {
                        RenderEngine.render(canvas.getGraphicsContext2D(), camera, modelList.get(i), (int) width, (int) height);
                    }
                } catch (Vector.VectorException e) {
                    e.printStackTrace();
                } catch (Matrix.MatrixException e) {
                    e.printStackTrace();
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            modelList.add(ObjReader.read(fileContent));
            chooseModel++;
            modelNumber.setText(String.valueOf(chooseModel + 1));
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }


    @FXML
    public void onSaveModelMenuItemClick() throws IOException, Matrix.MatrixException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");

        File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());

        Matrix4f matrix4f = modelList.get(chooseModel).rotateScaleTranslate;
        for (int i = 0; i < modelList.get(chooseModel).vertices.size(); i++) {
            modelList.get(chooseModel).vertices.set
                    (i, GraphicConveyor.multiplyMatrix4ByVector3(matrix4f, modelList.get(chooseModel).vertices.get(i)));
        }
        modelList.get(chooseModel).rotateScaleTranslate = GraphicConveyor.rotateScaleTranslate();
        ObjWriter.writeToFile(modelList.get(chooseModel), file);
    }

    public void handleCamera(float translation) throws Vector.VectorException {
        camera.setPosition((Vector3f) camera.getPosition().multiplicateVectorOnConstant(translation));
    }

    @FXML
    public void handleCameraLeftRightUpDown(float upDown, float leftRight) throws Vector.VectorException, Matrix.MatrixException {
        camera.setPosition(Transformation.rotateVector(new Vector3f(new float[] {upDown, leftRight, 0}),
                camera.getPosition()));
    }

    public void handleTarget(float upDown, float leftRight) throws Vector.VectorException, Matrix.MatrixException {
        camera.moveTarget(new Vector3f(new float[] {0, upDown, leftRight}));
        for (float val: camera.getTarget().getVector()) {
            System.out.print(val);
            System.out.print(" ");
        }
        System.out.println();
    }

    public void scale (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f v = getTransformVector(TRANSLATION, 1);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(v, zeroVector, zeroVector);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(v, zeroVector, zeroVector, modelList.get(chooseModel));}
    }

    @FXML
    public void scaleMinus (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f v = getTransformVector(1 / TRANSLATION, 1);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(v, zeroVector, zeroVector);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(v, zeroVector, zeroVector, modelList.get(chooseModel));}
    }

    @FXML
    public void rotate (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f unitVector = new Vector3f(new float[] {1,1,1});
        Vector3f v = getTransformVector(TRANSLATION, 0);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(unitVector, v, zeroVector);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(unitVector, v, zeroVector, modelList.get(chooseModel));}
    }

    @FXML
    public void rotateMinus (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f unitVector = new Vector3f(new float[] {1,1,1});
        Vector3f v = getTransformVector(-TRANSLATION, 0);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(unitVector, v, zeroVector);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(unitVector, v, zeroVector, modelList.get(chooseModel));}
    }

    @FXML
    public void translate (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f unitVector = new Vector3f(new float[] {1,1,1});
        Vector3f v = getTransformVector(TRANSLATION, 0);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(unitVector, zeroVector, v);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(unitVector, zeroVector, v, modelList.get(chooseModel));}
    }

    @FXML
    public void translateMinus (ActionEvent event) throws Matrix.MatrixException {
        Vector3f zeroVector = new Vector3f(new float[]{0,0,0});
        Vector3f unitVector = new Vector3f(new float[] {1,1,1});
        Vector3f v = getTransformVector(-TRANSLATION, 0);
        if (transformForAll.isSelected()){
            rotateScaleTranslateForAll(unitVector, zeroVector, v);
        } else {
        modelList.get(chooseModel).rotateScaleTranslate =
                Transformation.modelMatrix(unitVector, zeroVector, v, modelList.get(chooseModel));}
    }

    @FXML
    public void onDark (ActionEvent event) {
        String css = Simple3DViewer.class.getResource("css/dark_theme.css").toExternalForm();
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(css);
    }

    public void onLight(ActionEvent actionEvent) {
        String css = Simple3DViewer.class.getResource("css/light_theme.css").toExternalForm();
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(css);
    }

    private void rotateScaleTranslateForAll(Vector3f scale, Vector3f rotate, Vector3f translate) throws Matrix.MatrixException {
        for (Model model : modelList) {
            model.rotateScaleTranslate = Transformation.modelMatrix(scale, rotate, translate, model);
        }
    }

    public void choosePrevModel(ActionEvent actionEvent) {
        if (chooseModel != 0) {
            chooseModel --;
        } else {
            chooseModel = modelList.size() - 1;
        }
        modelNumber.setText(String.valueOf(chooseModel + 1));
    }

    public void chooseNextModel(ActionEvent actionEvent) {
        if (chooseModel < modelList.size() - 1) {
            chooseModel++;
        } else {
            chooseModel = 0;
        }
        modelNumber.setText(String.valueOf(chooseModel + 1));
    }

    public Vector3f getTransformVector(float translation, float value) {
        if (setXButton.isSelected()){
            return new Vector3f(new float[] {translation, value, value});
        } else if (setYButton.isSelected()){
            return new Vector3f(new float[] {value, translation, value});
        } else {
            return new Vector3f(new float[] {value, value, translation});
        }
    }

   public void mouseScroll(ScrollEvent scrollEvent) throws Vector.VectorException {
        float deltaY = (float) scrollEvent.getDeltaY();
        if (deltaY > 0) {
            deltaY = 28f / deltaY;
        } else {
            deltaY = Math.abs(deltaY) / 28f;
        }
        handleCamera(deltaY);
    }

    public void handleCameraBackward(ActionEvent actionEvent) throws Vector.VectorException {
        handleCamera(TRANSLATION / 1.1f);
    }

    public void handleCameraForward(ActionEvent actionEvent) throws Vector.VectorException {
        handleCamera(1.1f / TRANSLATION);
    }

    public void mouseDrag(MouseEvent mouseEvent) throws Matrix.MatrixException, Vector.VectorException {
        if (mouseEvent.isSecondaryButtonDown()){

        }
    }
    public void handleCameraLeft(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleCameraLeftRightUpDown(0, -TRANSLATION);
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleCameraLeftRightUpDown(0, TRANSLATION);
    }

    public void handleCameraUp(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleCameraLeftRightUpDown(TRANSLATION, 0);
    }

    public void handleCameraDown(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleCameraLeftRightUpDown(-TRANSLATION, 0);
    }

    public void handleTargetLeft(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleTarget(0, -TRANSLATION);
    }
    public void handleTargetRight(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleTarget(0, TRANSLATION);
    }
    public void handleTargetUp(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleTarget(TRANSLATION, 0);
    }
    public void handleTargetDown(ActionEvent actionEvent) throws Vector.VectorException, Matrix.MatrixException {
        handleTarget(-TRANSLATION, 0);
    }


    public void setDefaultRts(ActionEvent actionEvent) {
        modelList.get(chooseModel).rotateScaleTranslate = GraphicConveyor.rotateScaleTranslate();
    }
}