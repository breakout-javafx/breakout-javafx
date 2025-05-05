package com.breakout.entities.paddle;

import com.breakout.config.ConfigLoader;
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
        this.speed = ConfigLoader.getInstance().getInt("paddle.speed");

        this.x = initialX;
        this.y = initialY;

        String texturePath = System.getProperty("selected.paddle");
        if (texturePath != null) {
            texture = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(texturePath)));
        }
    }

    public void render(GraphicsContext gc) {
        if (texture != null && !texture.isError()) {
            gc.drawImage(texture, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    public void moveRight() {
        x += speed;
        if (x + width > com.breakout.core.GameApp.WIDTH)
            x = com.breakout.core.GameApp.WIDTH - width;
    }

    public void resetPosition() {
        this.x = com.breakout.core.GameApp.WIDTH / 2.0 - width / 2.0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
