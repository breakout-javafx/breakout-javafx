package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class GlowingBrickDecorator extends BrickDecorator {

    private static final Image TEXTURE;
    private final int maxHealth;

    static {
        TEXTURE = new Image(
                Objects.requireNonNull(StandardBrick.class.getResourceAsStream(
                        ConfigLoader.getInstance().get("brick.decorator.glowing.texture"))),
                0, 0, false, false
        );
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de StandardBrick.");
        }
    }



    public GlowingBrickDecorator(AbstractBrick decoratedBrick) {
        super(decoratedBrick);
        this.score = ConfigLoader.getInstance().getInt("brick.decorator.glowing.score");
        this.health = ConfigLoader.getInstance().getInt("brick.decorator.glowing.health");
        this.maxHealth = this.health;
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        double opacity = Math.max(0.2, (double) health / maxHealth);
        gc.setGlobalAlpha(opacity);

        if (TEXTURE != null && !TEXTURE.isError()) {
            gc.drawImage(TEXTURE, x, y, width, height);
        } else {
            gc.setFill(Color.GRAY); // Color de respaldo si falla la textura
            gc.fillRect(x, y, width, height);
        }

        gc.setGlobalAlpha(1.0);
    }


    @Override
    protected void initializeShape() {}
    @Override
    protected void initializeColor() {}
}
