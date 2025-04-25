package com.breakout.core;

import com.breakout.entites.ball.Ball;
import com.breakout.entites.paddle.Paddle;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {

    private static final double TARGET_FPS = 60.0; // FPS deseados
    private static final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;

    private final GraphicsContext gc;
    private final Ball ball;
    private final Paddle paddle;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Control del tiempo
    private long lastUpdateTime = 0;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;
        this.ball = new Ball(GameApp.WIDTH / 2.0, GameApp.HEIGHT / 2.0); // Centrada
        this.paddle = new Paddle();
    }

    @Override
    public void handle(long now) {
        // Control del tiempo de actualización para mantener la tasa de FPS
        if (now - lastUpdateTime >= NANOS_PER_UPDATE) {
            update();
            lastUpdateTime = now;
        }
        render();  // El renderizado sigue ocurriendo a cada fotograma
    }

    private void update() {
        // Actualiza la lógica del juego (movimiento, colisiones, etc.)
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
