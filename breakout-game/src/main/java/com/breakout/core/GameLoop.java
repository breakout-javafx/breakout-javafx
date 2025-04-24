package com.breakout.core;

import com.breakout.entites.ball.Ball;
import com.breakout.entites.paddle.Paddle;
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
        this.ball = new Ball(GameApp.WIDTH / 2.0, GameApp.HEIGHT / 2.0); // Centrada
        this.paddle = new Paddle();
    }

    @Override
    public void handle(long now) {
        update();
        render();
    }

    private void update() {

        ball.update();

        // Movimiento del paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();

        // Rebote con el paddle
        if (ball.getY() + ball.getRadius() >= paddle.getY() &&
                ball.getX() + ball.getRadius() >= paddle.getX() &&  // Asegurar que la pelota no se pase por el borde
                ball.getX() - ball.getRadius() <= paddle.getX() + paddle.getWidth()) {
            ball.invertY();
        }
    }


    private void render() {
        gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);
        ball.render(gc);
        paddle.render(gc);
    }

    public void setLeftPressed(boolean b) { leftPressed = b; }
    public void setRightPressed(boolean b) { rightPressed = b; }
    public Paddle getPaddle() { return paddle; }
}
