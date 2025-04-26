package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.BrickSpawner;
import com.breakout.entities.paddle.Paddle;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    // FPS deseados, ahora desde el archivo de configuración
    private static final double TARGET_FPS = Math.max(ConfigLoader.getInstance().getInt("game.fps"), 30); // Asegura que el FPS no sea menor a 30
    private static final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;

    private final GraphicsContext gc;
    private final Ball ball;
    private final Paddle paddle;
    private List<AbstractBrick> bricks;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameStarted = false;

    // Control del tiempo
    private long lastUpdateTime = 0;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;

        // Crear la pelota y colocarla en el centro de la pantalla
        this.ball = new Ball(GameApp.WIDTH / 2.0, GameApp.HEIGHT / 2.0);

        // Crear el paddle primero
        this.paddle = new Paddle(GameApp.WIDTH / 2.0 - (ConfigLoader.getInstance().getInt("paddle.width") / 2.0),
                GameApp.HEIGHT - ConfigLoader.getInstance().getInt("paddle.height"));

        // Generar los bricks utilizando el BrickSpawner
        BrickSpawner brickSpawner = new BrickSpawner();
        this.bricks = brickSpawner.generateBricks();
    }

    @Override
    public void handle(long now) {
        // Control del tiempo de actualización para mantener la tasa de FPS
        if (now - lastUpdateTime >= NANOS_PER_UPDATE) {
            update();
            lastUpdateTime = now;
        }
        render();
    }

    private void update() {
        // Actualiza la lógica del juego (movimiento, colisiones, etc.)
        updateBall();
        updatePaddle();
        handleCollisions();
    }

    private void updateBall() {
        if (gameStarted) {
            ball.update();
        } else {
            // Coloca la pelota justo encima del paddle si no ha comenzado el juego
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getRadius());
            ball.setY(paddle.getY() - ball.getRadius() * 2);
        }
    }

    private void updatePaddle() {
        // Movimiento del paddle
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();
    }

    private void handleCollisions() {
        // Rebote con el paddle
        if (ball.getY() + ball.getRadius() >= paddle.getY() &&
                ball.getX() + ball.getRadius() >= paddle.getX() &&  // Asegurar que la pelota no se pase por el borde
                ball.getX() - ball.getRadius() <= paddle.getX() + paddle.getWidth()) {
            ball.invertY();
        }

        // Colisión con los bricks usando un Iterator para evitar ConcurrentModificationException
        Iterator<AbstractBrick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            AbstractBrick brick = iterator.next();
            if (ball.getBounds().intersects(brick.getBounds())) {
                // Acción de colisión con el brick
                ball.invertY();  // Por ejemplo, invertir la dirección de la pelota
                iterator.remove();  // Eliminar el brick de forma segura
            }
        }
    }

    private void render() {
        // Limpiar el canvas
        gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        // Dibujar el borde alrededor del área de juego
        gc.strokeRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT); // Borde alrededor del canvas

        // Dibujar los bricks
        for (AbstractBrick brick : bricks) {
            brick.render(gc);
        }

        // Dibujar el paddle y la pelota
        paddle.render(gc);
        ball.render(gc);

        // Mostrar texto si el juego aún no ha comenzado
        if (!gameStarted) {
            gc.setFill(Color.WHITE);
            gc.fillText("Presiona ESPACIO para comenzar", GameApp.WIDTH / 2.0 - 100, GameApp.HEIGHT / 2.0);
        }
    }


    // Métodos para manejar las teclas presionadas
    public void setLeftPressed(boolean b) { leftPressed = b; }
    public void setRightPressed(boolean b) { rightPressed = b; }

    public Paddle getPaddle() { return paddle; }

    public void startGame() {
        this.gameStarted = true;
    }

}
