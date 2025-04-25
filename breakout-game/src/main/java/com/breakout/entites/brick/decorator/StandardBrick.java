package com.breakout.entites.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entites.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StandardBrick extends AbstractBrick {
    private Color color;

    public StandardBrick(double x, double y, double width, double height) {
        super(
                x,
                y,
                ConfigLoader.getInstance().getInt("brick.width"),
                ConfigLoader.getInstance().getInt("brick.height")
        );
    }

    @Override
    protected void initializeShape() {
        // No hay forma compleja, el rectángulo es suficiente
    }

    @Override
    protected void initializeColor() {
        this.color = Color.RED; // Color base del ladrillo estándar
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }
}
