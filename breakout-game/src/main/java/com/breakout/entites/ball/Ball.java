package com.breakout.entites.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    private double x, y;
    private double dx, dy;
    private final double radius = 10;

    public Ball(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.dx = ConfigLoader.getInstance().getDouble("ball.speed");
        this.dy = this.dx;  // Usar la misma velocidad para el eje Y
    }

    public void update() {
        x += dx;
        y += dy;

        if (x <= 0 || x >= GameApp.WIDTH - radius) dx *= -1;
        if (y <= 0 || y >= GameApp.HEIGHT - radius) dy *= -1;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillOval(x, y, radius, radius);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }

    public void invertY() {
        dy *= -1;
    }
}
