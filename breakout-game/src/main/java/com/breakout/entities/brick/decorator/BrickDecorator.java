package com.breakout.entities.brick.decorator;

import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;

public abstract class BrickDecorator extends AbstractBrick {
    protected AbstractBrick decoratedBrick;

    public BrickDecorator(AbstractBrick decoratedBrick) {
        super(decoratedBrick.getX(), decoratedBrick.getY(),
              decoratedBrick.getWidth(), decoratedBrick.getHeight());
        this.decoratedBrick = decoratedBrick;
        // No sumamos aquí — cada decorador específico ajusta el score como quiera
    }

    @Override
    public void render(GraphicsContext gc) {
        decoratedBrick.render(gc);
    }

    @Override
    public void hit() {
        decoratedBrick.hit();
    }

    @Override
    public boolean isDestroyed() {
        return decoratedBrick.isDestroyed();
    }
}
