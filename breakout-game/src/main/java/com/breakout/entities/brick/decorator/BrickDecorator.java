package com.breakout.entities.brick.decorator;

import com.breakout.entities.brick.AbstractBrick;
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

    @Override
    public void hit() {
        this.health--;
    }

    @Override
    public boolean isDestroyed() {
        return this.health <= 0;
    }

    @Override
    protected void initializeShape() {
        // No cambia la forma
    }

    @Override
    protected void initializeColor() {
        // No cambia el color
    }
}
