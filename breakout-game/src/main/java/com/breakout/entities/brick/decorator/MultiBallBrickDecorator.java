package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameLoop;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;
import java.util.Random;

public class MultiBallBrickDecorator extends BrickDecorator {
    private final GameLoop gameLoop;
    private final BallSpawner ballSpawner;
    private boolean triggered = false;

    private static final Image TEXTURE;

    static {
        TEXTURE = new Image(Objects.requireNonNull(MultiBallBrickDecorator.class.
                getResourceAsStream(ConfigLoader.getInstance().get("ball.decorator.multiball.texture"))));
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de MultiBallBrickDecorator.");
        }
    }

    public MultiBallBrickDecorator(AbstractBrick decoratedBrick, GameLoop gameLoop, BallSpawner ballSpawner) {
        super(decoratedBrick);
        this.gameLoop = gameLoop;
        this.ballSpawner = ballSpawner;
        this.score = ConfigLoader.getInstance().getInt("ball.decorator.multiball.score");
        this.health = ConfigLoader.getInstance().getInt("ball.decorator.multiball.health");
    }

    @Override
    public boolean isDestroyed() {
        boolean destroyed = super.isDestroyed();
        if (destroyed && !triggered) {
            triggered = true;
            spawnExtraBalls();
        }
        return destroyed;
    }

    private void spawnExtraBalls() {
        Random rand = new Random();
        double speed = ConfigLoader.getInstance().getDouble("ball.speed");
        int extraBalls = ConfigLoader.getInstance().getInt("ball.extraspawn");

        for (int i = 0; i < extraBalls; i++) {
            Ball newBall = ballSpawner.spawnBall(x + width / 2, y);
            newBall.setDx(rand.nextBoolean() ? speed : -speed);
            newBall.setDy(speed);
            gameLoop.addBall(newBall);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc); // Render del ladrillo base
        gc.drawImage(TEXTURE, x, y, width, height); // Textura extra decorativa
    }
}
