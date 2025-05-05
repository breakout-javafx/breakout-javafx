package com.breakout.entities.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import com.breakout.entities.ball.strategy.BallMovementStrategy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Ball {
    private double x, y;
    private double prevX, prevY;
    private double dx, dy;
    private final double radius = ConfigLoader.getInstance().getDouble("ball.radius");
    private boolean active = true;

    private Image texture;
    private BallMovementStrategy movementStrategy;

    public static final double RADIUS = 10;

    public Ball(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.dx = ConfigLoader.getInstance().getDouble("ball.speed");
        this.dy = this.dx;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public void update() {
        prevX = x;
        prevY = y;

        if (movementStrategy != null) {
            movementStrategy.move(this);
        } else {
            x += dx;
            y += dy;
        }
    }

    public void render(GraphicsContext gc) {
        if (texture != null && !texture.isError()) {
            gc.drawImage(texture, x, y, radius * 2, radius * 2);
        } else {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, radius * 2, radius * 2);
        }
    }

    // Setters para compatibilidad
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public void setActive(boolean active) { this.active = active; }
    public void setMovementStrategy(BallMovementStrategy strategy) { this.movementStrategy = strategy; }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getPrevX() { return prevX; }
    public double getPrevY() { return prevY; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public double getRadius() { return radius; }
    public boolean isActive() { return active; }

    public javafx.geometry.Bounds getBounds() {
        return new javafx.geometry.BoundingBox(x, y, radius * 2, radius * 2);
    }
}
