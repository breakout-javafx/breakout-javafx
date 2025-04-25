package com.breakout.entites.brick.decorator;

import com.breakout.entites.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;

public abstract class BrickDecorator extends AbstractBrick {
    protected AbstractBrick decoratedBrick;

    public BrickDecorator(AbstractBrick decoratedBrick) {
        super(decoratedBrick.getX(), decoratedBrick.getY(),
                decoratedBrick.getWidth(), decoratedBrick.getHeight());
        this.decoratedBrick = decoratedBrick;
    }


    @Override
    public void render(GraphicsContext gc) {
        decoratedBrick.render(gc);
    }
}
