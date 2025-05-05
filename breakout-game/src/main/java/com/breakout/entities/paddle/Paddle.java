package com.breakout.entities.paddle;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import com.breakout.entities.brick.decorator.StandardBrick;
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

    private static final Image TEXTURE;

    static {
        TEXTURE = new Image(
                Objects.requireNonNull(StandardBrick.class.getResourceAsStream(
                        ConfigLoader.getInstance().get("paddle.texture"))),
                0, 0, false, false
        );
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de la bola.");
        }
    }

    public Paddle(double initialX, double initialY) {
        // Leer las configuraciones desde el archivo .properties
        this.width = ConfigLoader.getInstance().getInt("paddle.width");
        this.height = ConfigLoader.getInstance().getInt("paddle.height");
        this.speed = ConfigLoader.getInstance().getInt("paddle.speed");

        this.x = initialX;
        this.y = initialY;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;  // No dejar que el paddle se salga de la pantalla
    }

    public void moveRight() {
        x += speed;
        if (x > GameApp.WIDTH - width) x = GameApp.WIDTH - width;  // Limitar movimiento a la derecha
    }

    public void render(GraphicsContext gc) {
        if (TEXTURE != null && !TEXTURE.isError()) {
            gc.drawImage(TEXTURE, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void resetPosition() {
        this.x = GameApp.WIDTH / 2.0 - this.width / 2.0;
        this.y = GameApp.HEIGHT - ConfigLoader.getInstance().getInt("paddle.height");
    }
}
