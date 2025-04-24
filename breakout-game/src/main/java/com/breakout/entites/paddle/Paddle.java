package com.breakout.entites.paddle;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Paddle {
    private double x;
    private double y;
    private final double width;
    private final double height;
    private final double speed;

    public Paddle() {
        // Leer las configuraciones desde el archivo .properties
        this.width = ConfigLoader.getInstance().getInt("paddle.width");
        this.height = ConfigLoader.getInstance().getInt("paddle.height");
        this.speed = ConfigLoader.getInstance().getInt("paddle.speed");

        // Configuración de posición Y desde el archivo .properties
        // Si no existe, calculamos la posición Y de acuerdo con la altura de la pantalla
        this.y = ConfigLoader.getInstance().getInt("paddle.positionY");

        // Calcular posición X para centrar el paddle
        this.x = (GameApp.WIDTH - width) / 2.0;
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
        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
