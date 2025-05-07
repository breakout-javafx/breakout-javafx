package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameLoop; // Importa GameLoop
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.Random;

public class MultiBallBrickDecorator extends BrickDecorator {
    private boolean triggered = false;
    private final int maxHealth;
    private final Random rand = new Random();
    private final double speed = ConfigLoader.getInstance().getDouble("ball.speed");
    private final int extraBallsToSpawn = ConfigLoader.getInstance().getInt("ball.extraspawn");
    private final GameLoop gameLoop;  // Agregamos una referencia a GameLoop
    private final BallSpawner ballSpawner; // Agregamos una referencia al BallSpawner

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
        this.gameLoop = gameLoop; // Inicializamos la referencia a GameLoop
        this.ballSpawner = ballSpawner; // Inicializamos la referencia al BallSpawner
        this.score = ConfigLoader.getInstance().getInt("brick.decorator.multiball.score");
        this.health = ConfigLoader.getInstance().getInt("brick.decorator.multiball.health");
        this.maxHealth = this.health;
    }

    @Override
    public boolean isDestroyed() {
        boolean destroyed = this.health <= 0;
        return destroyed; // La lógica de spawn se mueve al GameLoop
    }

    @Override
    public void hit() {
        this.health--;
    }

    // Nuevo método llamado desde GameLoop
    public void spawnExtraBalls() {
        if (!triggered) {
            triggered = true;
            for (int i = 0; i < extraBallsToSpawn; i++) {
                Ball newBall = ballSpawner.spawnBall(x + width / 2, y);
                newBall.setDx(rand.nextBoolean() ? speed : -speed);
                newBall.setDy(speed);

                // Añade la nueva bola directamente al GameLoop
                gameLoop.addBall(newBall);
                System.out.println("[MULTIBALL DECORATOR] Spawneando bola extra.");
            }
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
