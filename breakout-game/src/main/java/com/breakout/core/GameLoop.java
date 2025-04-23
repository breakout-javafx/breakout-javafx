package com.breakout.core;

import com.breakout.entites.Ball;
import com.breakout.entites.Paddle;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;
    private final Ball ball;
    private final Paddle paddle;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;
        this.ball = new Ball(400, 300);
        this.paddle = new Paddle();
    }

    @Override
    public void handle(long now) {
        update();
        render();
    }

    private void update() {
        ball.update();

        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();

        // rebote básico contra la paleta
        if (ball.getY() + ball.getRadius() >= paddle.getY() &&
                ball.getX() >= paddle.getX() &&
                ball.getX() <= paddle.getX() + paddle.getWidth()) {
            ball.invertY();
        }
    }

    private void render() {
        gc.clearRect(0, 0, 800, 600);
        ball.render(gc);
        paddle.render(gc);
    }

    public void setLeftPressed(boolean b) {
        leftPressed = b;
    }

    public void setRightPressed(boolean b) {
        rightPressed = b;
    }

    public Paddle getPaddle() {
        return paddle;
    }
}
