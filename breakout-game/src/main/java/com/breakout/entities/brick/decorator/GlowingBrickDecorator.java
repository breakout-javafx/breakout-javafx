package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class GlowingBrickDecorator extends BrickDecorator {

    private static final Image TEXTURE;

    static {
        TEXTURE = new Image(Objects.requireNonNull(GlowingBrickDecorator
                .class.getResourceAsStream(ConfigLoader.getInstance().get("ball.decorator.glowing.texture"))));
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de GlowingBrickDecorator.");
        }
    }

    public GlowingBrickDecorator(AbstractBrick decoratedBrick) {
        super(decoratedBrick);
        this.score = ConfigLoader.getInstance().getInt("ball.decorator.glowing.score");
        this.health = ConfigLoader.getInstance().getInt("ball.decorator.glowing.health");
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc); // Render base
        gc.drawImage(TEXTURE, x, y, width, height); // Textura encima
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);
    }

    @Override
    protected void initializeShape() {}
    @Override
    protected void initializeColor() {}
}
