package com.breakout.entities.paddle;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Objects;

public class Paddle {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private final double speed;
    private Image texture;

    public Paddle(double initialX, double initialY) {
        this.width = ConfigLoader.getInstance().getInt("paddle.width");
        this.height = ConfigLoader.getInstance().getInt("paddle.height");
        this.speed = ConfigLoader.getInstance().getDouble("paddle.speed"); // Cambiado a getDouble para más precisión
        this.x = initialX;
        this.y = initialY;
        loadTexture();
    }

    private void loadTexture() {
        String texturePath = System.getProperty("selected.paddle");
        if (texturePath != null) {
            try {
                this.texture = new Image(Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(texturePath)
                ));
            } catch (Exception e) {
                System.err.println("Error loading paddle texture: " + e.getMessage());
                this.texture = null;
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (texture != null && !texture.isError()) {
            gc.drawImage(texture, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
            
            // Opcional: borde para mejor visibilidad
            gc.setStroke(Color.BLACK);
            gc.strokeRect(x, y, width, height);
        }
    }

    public void moveLeft() {
        x = Math.max(0, x - speed); // Más eficiente que if separado
    }

    public void moveRight() {
        x = Math.min(GameApp.WIDTH - width, x + speed);
    }

    public void resetPosition() {
        this.x = GameApp.WIDTH / 2.0 - width / 2.0;
    }

    // Método para obtener los límites (útil para colisiones)
    public javafx.geometry.Bounds getBounds() {
        return new javafx.geometry.BoundingBox(x, y, width, height);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getSpeed() { return speed; }
}