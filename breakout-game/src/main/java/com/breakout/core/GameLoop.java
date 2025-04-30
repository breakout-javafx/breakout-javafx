package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.BrickSpawner;
import com.breakout.entities.paddle.Paddle;
import com.breakout.manager.LifeManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    private static final double TARGET_FPS = Math.max(ConfigLoader.getInstance().getInt("game.fps"), 30);
    private static final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;

    private final GraphicsContext gc;
    private final List<Ball> balls = new ArrayList<>();
    private final Paddle paddle;
    private List<AbstractBrick> bricks;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private long lastUpdateTime = 0;
    private int totalScore = 0;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;

        // Crear el paddle
        this.paddle = new Paddle(GameApp.WIDTH / 2.0 - (ConfigLoader.getInstance().getInt("paddle.width") / 2.0),
                GameApp.HEIGHT - ConfigLoader.getInstance().getInt("paddle.height"));

        Ball initialBall = new Ball(GameApp.WIDTH / 2.0, paddle.getY() - ConfigLoader.getInstance().getInt("ball.radius"));
        balls.add(initialBall);

        // Generar los ladrillos
        BrickSpawner brickSpawner = new BrickSpawner();
        this.bricks = brickSpawner.generateBricks(this);
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
        if (!gameStarted || gameOver) return; // No actualizar si no ha empezado el juego o si es game over

        updateBalls();
        updatePaddle();
        handleCollisions();
    }

    private void updateBalls() {
        Iterator<Ball> iterator = balls.iterator();
        boolean ballLost = false;

        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.update();
            
            if (!ball.isActive()) {
                iterator.remove();
                ballLost = true;
            }
        }

        if (ballLost) {
            handleBallLost();
        }
    }

    private void updatePaddle() {
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();
    }

    private void handleCollisions() {
        // Colisiones con el paddle
        for (Ball ball : balls) {
            if (ball.getY() + ball.getRadius() >= paddle.getY() &&
                    ball.getX() + ball.getRadius() >= paddle.getX() &&
                    ball.getX() - ball.getRadius() <= paddle.getX() + paddle.getWidth()) {
                ball.invertY();
            }
        }

        // Colisiones con los ladrillos
        Iterator<AbstractBrick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            AbstractBrick brick = iterator.next();
            for (Ball ball : balls) {
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
    }

    private void render() {
        gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        if (!gameOver && gameStarted) {
            renderGameElements();
        }
        if (gameOver) {
            renderGameOver();
        } else if (!gameStarted) {
            renderStartMessage();
        }

    }

    public void renderGameOver () {
        gc.setFill(Color.RED);
        gc.setFont(new Font(48));
        String text = "GAME OVER - Score: " + totalScore;
        double textWidth = calculateTextWidth(text, gc.getFont());
        gc.fillText(text, (GameApp.WIDTH - textWidth)/2, GameApp.HEIGHT/2);
        
        gc.setFont(new Font(24));
        String restartText = "Press SPACE to restart";
        double restartWidth = calculateTextWidth(restartText, gc.getFont());
        gc.fillText(restartText, (GameApp.WIDTH - restartWidth)/2, GameApp.HEIGHT/2 + 50);
    }

    public void renderGameElements () {
        // Dibujar el borde
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        // Dibujar los ladrillos
        for (AbstractBrick brick : bricks) {
            brick.render(gc);
        }

        // Dibujar todas las bolas
        for (Ball ball : balls) {
            ball.render(gc);
        }

        // Dibujar el paddle
        paddle.render(gc);

        // Mostrar la puntuación
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + totalScore, 20, 30);

        // Mostrar vidas
        int lives = LifeManager.getInstance().getLives();
        for (int i = 0; i < lives; i++) {
            drawHeart(gc, 120 + i * 30, 15, 20);
        }
    }

    public void renderStartMessage () {
        // Mensaje inicial
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
        if (gameOver) {
            // Si es game over reinicia el juego
            resetGame();
            gameStarted = true;
        } else if (!gameStarted) {
            // Si el juego no ha comenzado, iniciarlo
            gameStarted = true;

            // Asegurar que al menos hay una bola
            if (balls.isEmpty()) {
                spawnNewBall();
            }
        }
    }

    public void addBall(Ball ball) {
        balls.add(ball);
    }

    private void handleBallLost() {
        if (balls.isEmpty()) {
            if (LifeManager.getInstance().getLives() > 0) {
                // Reiniciar con una nueva bola
                spawnNewBall();
                gameStarted = false; // Pausar el juego
            } else {
                gameOver();
            }
        }
    }

    private void spawnNewBall() {
        Ball newBall = new Ball(paddle.getX() + paddle.getWidth()/2, paddle.getY() - ConfigLoader.getInstance().getDouble("ball.radius"));
        balls.add(newBall);
        gameStarted = false;
    }

    private void gameOver() {
        gameOver = true;  
    }

    private void showGameOverMessage() {
        Platform.runLater(() -> {
            gc.setFill(Color.RED);
            gc.setFont(new Font(48));
            gc.fillText("GAME OVER", 
                GameApp.WIDTH/2 - 120, 
                GameApp.HEIGHT/2);
        });
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void resetGame() {
        // Reiniciar todos los estados
        balls.clear();
        bricks = new BrickSpawner().generateBricks(this);
        totalScore = 0;
        LifeManager.getInstance().reset();

        gameStarted = false;
        gameOver = false;

        Ball newBall = new Ball (paddle.getX() + paddle.getWidth() / 2, paddle.getY() - ConfigLoader.getInstance().getDouble("ball.radius"));
        balls.add(newBall);

        // Reiniciar la posición del paddle
        paddle.resetPosition();
        System.out.println("Juego Reiniciado"); // Debug
    }

    private double calculateTextWidth(String text, Font font) {
        Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }
}
