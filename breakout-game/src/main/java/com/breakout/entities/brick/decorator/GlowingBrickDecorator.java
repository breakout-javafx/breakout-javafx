package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GlowingBrickDecorator extends BrickDecorator {

    public GlowingBrickDecorator(AbstractBrick decoratedBrick) {
        super(decoratedBrick);
        this.score = ConfigLoader.getInstance().getInt("ball.decorator.glowing.score");
        this.health = ConfigLoader.getInstance().getInt("ball.decorator.glowing.health");
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);
    }

    @Override
    protected void initializeShape() {}
    @Override
    protected void initializeColor() {}
}
