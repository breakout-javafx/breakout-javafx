package com.breakout.entities.brick.decorator;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.core.GameLoop;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.decorator.BrickDecorator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public class MultiBallBrickDecorator extends BrickDecorator {
    private final GameLoop gameLoop;
    private boolean triggered = false;

    public MultiBallBrickDecorator(AbstractBrick decoratedBrick, GameLoop gameLoop) {
        super(decoratedBrick);
        this.gameLoop = gameLoop;
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
        Ball originBall = new Ball(x + width / 2, y);
        Random rand = new Random();

        double speed = ConfigLoader.getInstance().getDouble("ball.speed");
        int extraBalls = ConfigLoader.getInstance().getInt("ball.extraspawn");

        for (int i = 0; i < extraBalls; i++) {
            Ball newBall = new Ball(originBall.getX(), originBall.getY());

            // Ajusta la velocidad usando el valor de ball.speed
            newBall.setDx(rand.nextBoolean() ? speed : -speed);  // Velocidad aleatoria en X
            newBall.setDy(speed);  // Velocidad en Y

            gameLoop.addBall(newBall);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);  // Dibuja el ladrillo base

        // Añadir un distintivo visual, como un círculo en el centro del ladrillo
        gc.setFill(Color.BLUE);  // Color distintivo para los bricks multi-ball
        gc.fillOval(x + width / 4, y + height / 4, width / 2, height / 2);  // Círculo en el centro
    }
}
