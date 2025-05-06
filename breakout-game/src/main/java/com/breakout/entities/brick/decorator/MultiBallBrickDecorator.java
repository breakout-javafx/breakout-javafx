package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameLoop;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.manager.GameStateManager;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public class MultiBallBrickDecorator extends BrickDecorator {
    private final GameLoop gameLoop;
    private final BallSpawner ballSpawner;
    private boolean triggered = false;
    private final int maxHealth;

    private static final Image TEXTURE;

    static {
        TEXTURE = new Image(
                Objects.requireNonNull(StandardBrick.class.getResourceAsStream(
                        ConfigLoader.getInstance().get("brick.decorator.multiball.texture"))),
                0, 0, false, false
        );
        if (TEXTURE.isError()) {
            System.err.println("No se pudo cargar la textura de StandardBrick.");
        }
    }


    public MultiBallBrickDecorator(AbstractBrick decoratedBrick, GameLoop gameLoop, BallSpawner ballSpawner) {
        super(decoratedBrick);
        this.gameLoop = gameLoop;
        this.ballSpawner = ballSpawner;
        this.score = ConfigLoader.getInstance().getInt("brick.decorator.multiball.score");
        this.health = ConfigLoader.getInstance().getInt("brick.decorator.multiball.health");
        this.maxHealth = this.health;
    }

    @Override
    public boolean isDestroyed() {
        boolean destroyed = this.health <= 0;
        if (destroyed && !triggered) {
            triggered = true;
            spawnExtraBalls();
        }
        return destroyed;
    }

    @Override
    public void hit() {
        this.health--;
    }

    private void spawnExtraBalls() {
        Random rand = new Random();
        double speed = ConfigLoader.getInstance().getDouble("ball.speed");
        int extraBalls = ConfigLoader.getInstance().getInt("ball.extraspawn");

        for (int i = 0; i < extraBalls; i++) {
            Ball newBall = ballSpawner.spawnBall(x + width / 2, y);
            newBall.setDx(rand.nextBoolean() ? speed : -speed);
            newBall.setDy(speed);
        
            // Agregar la nueva bola al GameStateManager
            GameStateManager.getInstance().addBall(newBall);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        double opacity = Math.max(0.2, (double) health / maxHealth);
        gc.setGlobalAlpha(opacity);

        if (TEXTURE != null && !TEXTURE.isError()) {
            gc.drawImage(TEXTURE, x, y, width, height);
        } else {
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, width, height);
        }

        gc.setGlobalAlpha(1.0);
    }

}
