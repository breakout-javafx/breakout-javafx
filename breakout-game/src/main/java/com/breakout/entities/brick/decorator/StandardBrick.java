package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class StandardBrick extends AbstractBrick {
    private Color color;

    private static final Image TEXTURE;

    static {
        // Carga la imagen desde dentro del JAR (resources)
        TEXTURE = new Image(Objects.requireNonNull(StandardBrick.class.getResourceAsStream("/textures/standard_brick.png")));
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de StandardBrick.");
        }
    }

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
    protected void initializeColor() {}

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(TEXTURE, x, y, width, height);
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
