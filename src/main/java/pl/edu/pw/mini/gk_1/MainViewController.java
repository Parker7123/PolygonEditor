package pl.edu.pw.mini.gk_1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.managers.AnimationManager;
import pl.edu.pw.mini.gk_1.managers.DeletingManager;
import pl.edu.pw.mini.gk_1.managers.DrawingManager;
import pl.edu.pw.mini.gk_1.managers.ManipulationManager;
import pl.edu.pw.mini.gk_1.shapes.Polygon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private Canvas canvas;

    @FXML
    private ToggleButton deletingButton;

    @FXML
    private ToggleButton drawingButton;

    @FXML
    private ToggleButton manipiulatingButton;
    @FXML
    private RadioButton normalDrawingRadioButton;
    @FXML
    private RadioButton bresenhamDrawingRadioButton;
    private DrawingManager drawingManager;
    private DeletingManager deletingManager;
    private AnimationManager animationManager;
    private ManipulationManager manipulationManager;

    @FXML
    void onCanvasMouseClicked(MouseEvent event) {
        if(drawingButton.isSelected()) {
            drawingManager.onMouseClick(event);
        } else if(deletingButton.isSelected()) {
            deletingManager.onMouseClick(event);
            drawingManager.clearCanvas();
            drawingManager.drawPolygons();
        } else if (manipiulatingButton.isSelected()) {
            manipulationManager.onMouseClick(event);
            drawingManager.clearCanvas();
            drawingManager.drawPolygons();
        }
    }

    @FXML
    void onCanvasMouseMoved(MouseEvent event) {
        drawingManager.onMouseMoved(event);
        animationManager.onMouseMoved(event);
    }

    @FXML
    void onKeyReleased(KeyEvent event) {
        drawingManager.onKeyReleased(event);
    }

    @FXML
    void onMouseDragged(MouseEvent event) {
        if (manipiulatingButton.isSelected()) {
            manipulationManager.onMouseDragged(event);
            drawingManager.clearCanvas();
            drawingManager.drawPolygons();
            animationManager.onMouseMoved(event);
        }
    }

    @FXML
    void onMousePressed(MouseEvent event) {
        if (manipiulatingButton.isSelected()) {
            manipulationManager.onMousePressed(event);
        }
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Polygon> polygons = new ArrayList<>();
        drawingManager = new DrawingManager(canvas.getGraphicsContext2D(), polygons);
        deletingManager = new DeletingManager(canvas.getGraphicsContext2D(), polygons);
        animationManager = new AnimationManager(canvas.getGraphicsContext2D(), polygons);
        manipulationManager = new ManipulationManager(canvas.getGraphicsContext2D(), polygons);
        normalDrawingRadioButton.setOnAction(event -> setDrawingMode(DrawingMode.NORMAL));
        bresenhamDrawingRadioButton.setOnAction(event -> setDrawingMode(DrawingMode.BRESENHAM));
    }

    private void setDrawingMode(DrawingMode drawingMode) {
        drawingManager.setDrawingMode(drawingMode);
        deletingManager.setDrawingMode(drawingMode);
        animationManager.setDrawingMode(drawingMode);
        manipulationManager.setDrawingMode(drawingMode);

        drawingManager.clearCanvas();
        drawingManager.drawPolygons();
    }
}