package pl.edu.pw.mini.gk_1.interfaces;

import javafx.scene.canvas.GraphicsContext;
import pl.edu.pw.mini.gk_1.helpers.DrawingMode;

public interface Drawable {
    void draw(GraphicsContext graphicsContext, DrawingMode drawingMode);
}
