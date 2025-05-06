package com.breakout.entities.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.strategy.BallMovementStrategy;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Ball {
    public static final double RADIUS = 10;

    private double x, y;  // Posición actual
    private double prevX, prevY;  // Posición anterior (para realizar colisiones, por ejemplo)
    private double dx, dy;  // Velocidad en los ejes X e Y
    private final double radius;  // Radio de la bola
    private boolean active = true;  // Estado de la bola (activa/inactiva)

    private Image texture;  // Textura para la bola
    private BallMovementStrategy movementStrategy;  // Estrategia de movimiento

    // Constructor para crear una bola con la posición inicial
    public Ball(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.radius = ConfigLoader.getInstance().getDouble("ball.radius"); // Obtener el radio desde la configuración
        this.dx = ConfigLoader.getInstance().getDouble("ball.speed"); // Obtener la velocidad desde la configuración
        this.dy = this.dx;  // Usar la misma velocidad en X y Y por defecto
    }

    // Establecer la textura de la bola
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    // Actualizar la posición de la bola
    public void update() {
        prevX = x;  // Guardamos la posición anterior
        prevY = y;

        // Si existe una estrategia de movimiento, la usamos
        if (movementStrategy != null) {
            movementStrategy.move(this);  // Movimiento utilizando la estrategia
        } else {
            x += dx;  // Movimiento por defecto
            y += dy;
        }
    }

    // Renderizar la bola en el canvas
    public void render(GraphicsContext gc) {
        if (texture != null) {
            gc.drawImage(texture, x, y, radius * 2, radius * 2);  // Dibujar la imagen de la bola
        } else {
            gc.setFill(Color.RED);  // Si no tiene textura, usar color rojo
            gc.fillOval(x, y, radius * 2, radius * 2);  // Dibujar un círculo
        }
    }

    // Setters para manipular las propiedades de la bola
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public void setActive(boolean active) { this.active = active; }
    public void setMovementStrategy(BallMovementStrategy strategy) { this.movementStrategy = strategy; }

    // Getters para obtener las propiedades de la bola
    public double getX() { return x; }
    public double getY() { return y; }
    public double getPrevX() { return prevX; }
    public double getPrevY() { return prevY; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public double getRadius() { return radius; }
    public boolean isActive() { return active; }

    // Devuelve las dimensiones de la bola para la detección de colisiones
    public javafx.geometry.Bounds getBounds() {
        return new javafx.geometry.BoundingBox(x, y, radius * 2, radius * 2);
    }
}

