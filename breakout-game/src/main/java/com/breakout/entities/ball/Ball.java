package com.breakout.entities.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.strategy.BallMovementStrategy;
import com.breakout.entities.ball.strategy.NormalMovementStrategy;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    private double x, y;
    private double dx, dy;
    private final double radius = 10;
    private boolean active = true;

    private BallMovementStrategy movementStrategy;  // Agregamos el strategy

    public Ball(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.dx = ConfigLoader.getInstance().getDouble("ball.speed");
        this.dy = this.dx;  // Usar la misma velocidad para el eje Y

        this.movementStrategy = new NormalMovementStrategy();  // Valor por defecto
    }

    public void update() {
        movementStrategy.move(this);  // Delegar el movimiento al strategy
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

    // Método para obtener los límites de la pelota, útil para la detección de colisiones
    public Rectangle2D getBounds() {
        return new Rectangle2D(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    // Nuevos Métodos
    public boolean isActive () {
        return active;
    }

    public void setActive (boolean active) {
        this.active = active;

        if (!active) {
            // Efecto visual al desaparecer
            this.dx = 0;
            this.dy = 0;
        }
    }
}
