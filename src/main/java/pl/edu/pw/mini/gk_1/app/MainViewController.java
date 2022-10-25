package pl.edu.pw.mini.gk_1.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;
import pl.edu.pw.mini.gk_1.helpers.ShapeMode;
import pl.edu.pw.mini.gk_1.managers.*;
import pl.edu.pw.mini.gk_1.relations.LengthRelation;
import pl.edu.pw.mini.gk_1.relations.PerpendicularRelation;
import pl.edu.pw.mini.gk_1.relations.RelationMode;
import pl.edu.pw.mini.gk_1.shapes.*;

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
    private RadioButton polygonRadioButton;
    @FXML
    private RadioButton circleRadioButton;
    @FXML
    private Button removeRelationButton;
    private DrawingManager drawingManager;
    private DeletingManager deletingManager;
    private AnimationManager animationManager;
    private ManipulationManager manipulationManager;
    private RelationManager relationManager;
    private final List<Polygon> polygons = new ArrayList<>();
    private final List<Oval>ovals = new ArrayList<>();

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
        drawingManager = new DrawingManager(canvas.getGraphicsContext2D(), polygons, ovals);
        deletingManager = new DeletingManager(canvas.getGraphicsContext2D(), polygons, ovals);
        animationManager = new AnimationManager(canvas.getGraphicsContext2D(), polygons, ovals);
        relationManager = new RelationManager(canvas.getGraphicsContext2D(), polygons, lengthTextField.textProperty(), ovals);
        manipulationManager = new ManipulationManager(canvas.getGraphicsContext2D(), polygons, relationManager, ovals);
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
        lengthRadioButton.setOnAction(event -> relationManager.setRelationMode(RelationMode.LENGTH));
        perpendicularRadioButton.setOnAction(event -> relationManager.setRelationMode(RelationMode.PERPENDICULAR));

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
            } else if (perpendicularRadioButton.isSelected()) {
                removePerpendicularRelation();
            }
        });
        lengthTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && lengthRadioButton.isSelected()) {
                if (lengthRadioButton.isSelected()) {
                    addLengthRelation();
                }
            }
        });
        polygonRadioButton.setOnAction(event -> {
            drawingManager.setShapeMode(ShapeMode.POLYGON);
        });
        circleRadioButton.setOnAction(event -> {
            drawingManager.setShapeMode(ShapeMode.OVAL);
        });
    }

    @FXML
    void generateTestScene() {
        polygons.clear();

        // rectangle
        VerticesList vertices = new VerticesList();
        vertices.add(new Vertex(200, 320));
        vertices.add(new Vertex(400, 320));
        vertices.add(new Vertex(400, 520));
        vertices.add(new Vertex(200, 520));
        Polygon polygon = new Polygon(vertices);
        var edges = polygon.getEdges();
        edges.get(0).setLengthRelation(new LengthRelation(200));
        edges.get(0).setPerpendicularRelation(new PerpendicularRelation(new PolygonEdgePair(polygon, edges.get(1))));
        edges.get(1).setPerpendicularRelation(new PerpendicularRelation(new PolygonEdgePair(polygon, edges.get(0))));
        polygons.add(polygon);

        // strange polygon
        vertices = new VerticesList();
        vertices.add(new Vertex(300, 100));
        vertices.add(new Vertex(400, 200));
        vertices.add(new Vertex(500, 300));
        vertices.add(new Vertex(400, 250));
        vertices.add(new Vertex(250, 250));
        vertices.add(new Vertex(300, 200));
        polygon = new Polygon(vertices);
        edges = polygon.getEdges();
        edges.get(5).setLengthRelation(new LengthRelation(100));
        edges.get(2).setLengthRelation(new LengthRelation(100));
        edges.get(0).setLengthRelation(new LengthRelation(141.42));
        edges.get(3).setPerpendicularRelation(new PerpendicularRelation(new PolygonEdgePair(polygon, edges.get(5))));
        edges.get(5).setPerpendicularRelation(new PerpendicularRelation(new PolygonEdgePair(polygon, edges.get(3))));
        polygons.add(polygon);
        ovals.add(new Oval(new Point2D(130, 130), 80));
        redrawPolygons();
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

    private void removePerpendicularRelation() {
        relationManager.removePerpendicularRelation();
        redrawPolygons();
        relationManager.highlightSelectedEdges();
    }
}