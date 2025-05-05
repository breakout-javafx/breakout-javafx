package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class StandardBrick extends AbstractBrick {
    private static final Image TEXTURE;

    static {
        TEXTURE = new Image(
                Objects.requireNonNull(StandardBrick.class.getResourceAsStream(
                        ConfigLoader.getInstance().get("brick.decorator.standard.texture"))),
                0, 0, false, false
        );
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de StandardBrick.");
        }
    }

    private final int maxHealth;

    public StandardBrick(double x, double y, double width, double height) {
        super(x, y,
                ConfigLoader.getInstance().getInt("brick.width"),
                ConfigLoader.getInstance().getInt("brick.height"));
        this.score = ConfigLoader.getInstance().getInt("brick.decorator.standard.score");
        this.health = ConfigLoader.getInstance().getInt("brick.decorator.standard.health");
        this.maxHealth = this.health;
    }

    @Override
    protected void initializeShape() {}

    @Override
    protected void initializeColor() {}

    @Override
    public void render(GraphicsContext gc) {
        double opacity = Math.max(0.2, (double) health / maxHealth);
        gc.setGlobalAlpha(opacity);

        if (TEXTURE != null && !TEXTURE.isError()) {
            gc.drawImage(TEXTURE, x, y, width, height);
        } else {
            gc.setFill(Color.GRAY); // color alternativo si falla la textura
            gc.fillRect(x, y, width, height);
        }

        gc.setGlobalAlpha(1.0);
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
