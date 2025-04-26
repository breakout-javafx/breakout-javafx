package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.BrickSpawner;
import com.breakout.entities.paddle.Paddle;
import com.breakout.manager.LifeManager;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    private static final double TARGET_FPS = Math.max(ConfigLoader.getInstance().getInt("game.fps"), 30);
    private static final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;

    private final GraphicsContext gc;
    private final Ball ball;
    private final Paddle paddle;
    private List<AbstractBrick> bricks;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameStarted = false;

    private long lastUpdateTime = 0;
    private int totalScore = 0;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;

        this.ball = new Ball(GameApp.WIDTH / 2.0, GameApp.HEIGHT / 2.0);

        this.paddle = new Paddle(GameApp.WIDTH / 2.0 - (ConfigLoader.getInstance().getInt("paddle.width") / 2.0),
                GameApp.HEIGHT - ConfigLoader.getInstance().getInt("paddle.height"));

        BrickSpawner brickSpawner = new BrickSpawner();
        this.bricks = brickSpawner.generateBricks();
    }

    @Override
    public void handle(long now) {
        if (now - lastUpdateTime >= NANOS_PER_UPDATE) {
            update();
            lastUpdateTime = now;
        }
        render();
    }

    private void update() {
        updateBall();
        updatePaddle();
        handleCollisions();
    }

    private void updateBall() {
        if (gameStarted) {
            ball.update();
        } else {
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getRadius());
            ball.setY(paddle.getY() - ball.getRadius() * 2);
        }
    }

    private void updatePaddle() {
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();
    }

    private void handleCollisions() {
        if (ball.getY() + ball.getRadius() >= paddle.getY() &&
                ball.getX() + ball.getRadius() >= paddle.getX() &&
                ball.getX() - ball.getRadius() <= paddle.getX() + paddle.getWidth()) {
            ball.invertY();
        }

        Iterator<AbstractBrick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            AbstractBrick brick = iterator.next();
            if (ball.getBounds().intersects(brick.getBounds())) {
                brick.hit();

                if (brick.isDestroyed()) {
                    addScore(brick.getScore());
                    iterator.remove();
                }

                ball.invertY();
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        for (AbstractBrick brick : bricks) {
            brick.render(gc);
        }

        paddle.render(gc);
        ball.render(gc);

        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + totalScore, 20, 30);

        int lives = LifeManager.getInstance().getLives();
        for (int i = 0; i < lives; i++) {
            drawHeart(gc, 120 + i * 30, 15, 20);
        }

        if (!gameStarted) {
            String message = "Presiona ESPACIO para comenzar";

            double fontSize = gc.getFont().getSize();
            double textWidth = fontSize * message.length() * 0.5;
            double textHeight = fontSize;

            double rectX = GameApp.WIDTH / 2.0 - textWidth / 2 - 10;
            double rectY = GameApp.HEIGHT / 2.0 - textHeight / 2 - 10;
            double rectWidth = textWidth + 20;
            double rectHeight = textHeight + 20;

            gc.setFill(Color.DIMGRAY);
            gc.fillRect(rectX, rectY, rectWidth, rectHeight);

            gc.setFill(Color.WHITE);
            gc.fillText(message, GameApp.WIDTH / 2.0 - textWidth / 2, GameApp.HEIGHT / 2.0 + textHeight / 4);
        }
    }

    private void addScore(int points) {
        totalScore += points;
    }

    private void drawHeart(GraphicsContext gc, double x, double y, double size) {
        gc.setFill(Color.RED);
        gc.beginPath();
        gc.moveTo(x, y + size / 4);
        gc.bezierCurveTo(x, y, x - size / 2, y, x - size / 2, y + size / 4);
        gc.bezierCurveTo(x - size / 2, y + size / 2, x, y + size, x, y + size * 1.25);
        gc.bezierCurveTo(x, y + size, x + size / 2, y + size / 2, x + size / 2, y + size / 4);
        gc.bezierCurveTo(x + size / 2, y, x, y, x, y + size / 4);
        gc.fill();
        gc.closePath();
    }

    public void setLeftPressed(boolean b) { leftPressed = b; }
    public void setRightPressed(boolean b) { rightPressed = b; }

    public Paddle getPaddle() { return paddle; }

    public void startGame() {
        this.gameStarted = true;
    }
}