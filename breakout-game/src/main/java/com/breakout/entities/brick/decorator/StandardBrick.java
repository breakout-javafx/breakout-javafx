package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class StandardBrick extends AbstractBrick {
    private Color color;

    public StandardBrick(double x, double y, double width, double height) {
        super(x, y,
              ConfigLoader.getInstance().getInt("brick.width"),
              ConfigLoader.getInstance().getInt("brick.height"));
        this.score = ConfigLoader.getInstance().getInt("ball.decorator.standard.score");
        this.health = ConfigLoader.getInstance().getInt("ball.decorator.standard.health");

    }

    @Override
    protected void initializeShape() {}

    @Override
    protected void initializeColor() {
        this.color = Color.RED;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }

    @Override
    public void hit() {
        this.health--;
    }

    @Override
    public boolean isDestroyed() {
        return this.health <= 0;
    }

}
