package com.breakout.entities.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.strategy.BallMovementStrategy;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    private double x, y;
    private double prevX, prevY; // ✅ Añadido: posición anterior

    private double dx, dy;
    private final double radius = 10;
    private boolean active = true;

    private BallMovementStrategy movementStrategy;

    public Ball(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.dx = ConfigLoader.getInstance().getDouble("ball.speed");
        this.dy = this.dx;
    }

    public void update() {
        // ✅ Guardamos la posición antes de mover
        prevX = x;
        prevY = y;

        movementStrategy.move(this);
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, radius, radius);
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }

    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }

    public double getRadius() { return radius; }

    public void invertY() {
        dy *= -1;
    }

    public void setMovementStrategy(BallMovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    public boolean isActive () {
        return active;
    }

    public void setActive (boolean active) {
        this.active = active;

        if (!active) {
            this.dx = 0;
            this.dy = 0;
        }
    }

    // ✅ Nuevos getters para colisión basada en movimiento
    public double getPrevX() { return prevX; }
    public double getPrevY() { return prevY; }
}
