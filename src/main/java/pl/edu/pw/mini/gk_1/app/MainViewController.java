package pl.edu.pw.mini.gk_1.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.managers.*;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.shapes.Polygon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private Canvas canvas;

    @FXML
    private ToggleButton relationToggleButton;

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
    @FXML
    private RadioButton perpendicularRadioButton;
    @FXML
    private TextField lengthTextField;
    @FXML
    private ToggleButton lengthRadioButton;
    @FXML
    private Button addRelationButton;
    @FXML
    private Button removeRelationButton;
    private DrawingManager drawingManager;
    private DeletingManager deletingManager;
    private AnimationManager animationManager;
    private ManipulationManager manipulationManager;
    private RelationManager relationManager;

    @FXML
    void onCanvasMouseClicked(MouseEvent event) {
        if (drawingButton.isSelected()) {
            drawingManager.onMouseClick(event);
        } else if (deletingButton.isSelected()) {
            deletingManager.onMouseClick(event);
            redrawPolygons();
        } else if (manipiulatingButton.isSelected()) {
            manipulationManager.onMouseClick(event);
            redrawPolygons();
        } else if (relationToggleButton.isSelected()) {
            relationManager.onMouseClick(event);
        }
    }

    @FXML
    void onCanvasMouseMoved(MouseEvent event) {
        drawingManager.onMouseMoved(event);
        animationManager.onMouseMoved(event);
        if (relationToggleButton.isSelected()) {
            relationManager.onMouseMoved(event);
        }
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
        relationManager = new RelationManager(canvas.getGraphicsContext2D(), polygons, lengthTextField.textProperty());
        manipulationManager = new ManipulationManager(canvas.getGraphicsContext2D(), polygons, relationManager);
        normalDrawingRadioButton.setOnAction(event -> setDrawingMode(DrawingMode.NORMAL));
        bresenhamDrawingRadioButton.setOnAction(event -> setDrawingMode(DrawingMode.BRESENHAM));
        lengthTextField.disableProperty().bind(lengthRadioButton.selectedProperty().not()
                .or(relationToggleButton.selectedProperty().not()));
        lengthRadioButton.disableProperty().bind(relationToggleButton.selectedProperty().not());
        perpendicularRadioButton.disableProperty().bind(relationToggleButton.selectedProperty().not());
        addRelationButton.disableProperty().bind(relationToggleButton.selectedProperty().not());
        removeRelationButton.disableProperty().bind(relationToggleButton.selectedProperty().not());
        relationToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                relationManager.resetRelationMode();
            }
        });
        lengthRadioButton.setOnAction(event -> {
            relationManager.setRelationMode(RelationMode.LENGTH);
        });
        perpendicularRadioButton.setOnAction(event -> {

            relationManager.setRelationMode(RelationMode.PERPENDICULAR);
        });

        relationManager.setRelationMode(RelationMode.LENGTH);
        addRelationButton.setOnAction(event -> {
            if (lengthRadioButton.isSelected()) {
                addLengthRelation();
            } else if(perpendicularRadioButton.isSelected()) {
                addPerpendicularRelation();
            }
        });
        removeRelationButton.setOnAction(event -> {
            if (lengthRadioButton.isSelected()) {
                removeLengthRelation();
            }
        });
        lengthTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && lengthRadioButton.isSelected()) {
                if (lengthRadioButton.isSelected()) {
                    addLengthRelation();
                }
            }
        });
    }

    private void redrawPolygons() {
        drawingManager.clearCanvas();
        drawingManager.drawPolygons();
    }

    private void setDrawingMode(DrawingMode drawingMode) {
        drawingManager.setDrawingMode(drawingMode);
        deletingManager.setDrawingMode(drawingMode);
        animationManager.setDrawingMode(drawingMode);
        manipulationManager.setDrawingMode(drawingMode);
        relationManager.setDrawingMode(drawingMode);

        drawingManager.clearCanvas();
        drawingManager.drawPolygons();
    }

    private void addLengthRelation() {
        relationManager.addLengthRelation();
        redrawPolygons();
        relationManager.highlightSelectedEdges();
    }

    private void addPerpendicularRelation() {
        relationManager.addPerpendicularRelation();
        redrawPolygons();
        relationManager.highlightSelectedEdges();
    }

    private void removeLengthRelation() {
        relationManager.removeLengthRelation();
        redrawPolygons();
        relationManager.highlightSelectedEdges();
    }
}