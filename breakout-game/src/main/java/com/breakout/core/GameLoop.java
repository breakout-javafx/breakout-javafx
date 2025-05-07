package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.ball.strategy.PaddleCollisionStrategy;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.paddle.Paddle;
import com.breakout.entities.wall.BottomWall;
import com.breakout.entities.wall.LeftWall;
import com.breakout.entities.wall.RightWall;
import com.breakout.entities.wall.TopWall;
import com.breakout.level.facade.LevelLoader;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {

    private static final double TARGET_FPS = Math.max(ConfigLoader.getInstance().getInt("game.fps"), 30);
    private static final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_FPS;

    private final GraphicsContext gc;
    private final List<Ball> balls = new ArrayList<>();
    private final List<AbstractBrick> bricksRecentlyHit = new ArrayList<>();
    private final BallSpawner ballSpawner;
    private final Paddle paddle;
    private List<AbstractBrick> bricks;
    private final String levelPath;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private long lastUpdateTime = 0;
    private int totalScore = 0;

    public GameLoop(GraphicsContext gc, String levelPath) {
        this.gc = gc;
        this.levelPath = levelPath;

        this.paddle = new Paddle(
                GameApp.WIDTH / 2.0 - ConfigLoader.getInstance().getInt("paddle.width") / 2.0,
                GameApp.HEIGHT - ConfigLoader.getInstance().getInt("paddle.height")
        );

        this.ballSpawner = new BallSpawner(paddle);

        double radius = ConfigLoader.getInstance().getDouble("ball.radius");
        Ball initialBall = ballSpawner.spawnBall(
                paddle.getX() + paddle.getWidth() / 2.0 - radius,
                paddle.getY() - 1.5 * radius
        );
        initialBall.setDy(Math.abs(initialBall.getDy()));
        balls.add(initialBall);

        this.bricks = LevelLoader.loadLevel(levelPath, this, ballSpawner);

        GameStateManager gsm = GameStateManager.getInstance();
        gsm.setGameLoop(this);
        gsm.setPaddle(paddle);
    }

    @Override
    public void handle(long now) {
        if (now - lastUpdateTime >= NANOS_PER_UPDATE) {
            update();
            // Comprueba si no hay bolas activas y quedan vidas para añadir una nueva
            if (balls.isEmpty() && LifeManager.getInstance().getLives() > 0 && GameStateManager.getInstance().isGameStarted() && !GameStateManager.getInstance().isGameOver()) {
                addNewBall();
            }
            lastUpdateTime = now;
        }
        render();
    }

    private void update() {
        GameStateManager gsm = GameStateManager.getInstance();
        if (!gsm.isGameStarted() || gsm.isGameOver()) return;

        updateBalls();
        updatePaddle();
        handleCollisions();

        // La lógica de perder vida se mueve aquí, después de actualizar todas las bolas
        if (balls.isEmpty() && gsm.isGameStarted() && !gsm.isGameOver()) {
            gsm.loseLife();
        }
    }

    private void updateBalls() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.update();

            if (!ball.isActive()) {
                iterator.remove();
            }
        }
        // Ya no notificamos la pérdida de vida aquí. Se hace en el método update().
    }

    private void updatePaddle() {
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();
    }

    private void handleCollisions() {
        bricksRecentlyHit.clear();

        for (Ball ball : new ArrayList<>(balls)) {
            if (ball.getBounds().intersects(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight())) {
                new PaddleCollisionStrategy().onCollision(ball, paddle);
            }

            double startX = ball.getPrevX(), startY = ball.getPrevY();
            double endX = ball.getX(), endY = ball.getY();
            double dx = endX - startX, dy = endY - startY;
            double radius = ball.getRadius();

            Iterator<AbstractBrick> iterator = bricks.iterator();
            AbstractBrick earliestBrick = null;
            double earliestEntryTime = Double.POSITIVE_INFINITY;
            boolean horizontal = false;

            while (iterator.hasNext()) {
                AbstractBrick brick = iterator.next();

                if (bricksRecentlyHit.contains(brick)) continue;

                double bx = brick.getX();
                double by = brick.getY();
                double bw = brick.getWidth();
                double bh = brick.getHeight();

                double tEntryX = (dx == 0) ? Double.NEGATIVE_INFINITY : (dx > 0 ? bx - (startX + radius) : bx + bw - (startX - radius)) / dx;
                double tExitX = (dx == 0) ? Double.POSITIVE_INFINITY : (dx > 0 ? bx + bw - (startX + radius) : bx - (startX - radius)) / dx;

                double tEntryY = (dy == 0) ? Double.NEGATIVE_INFINITY : (dy > 0 ? by - (startY + radius) : by + bh - (startY - radius)) / dy;
                double tExitY = (dy == 0) ? Double.POSITIVE_INFINITY : (dy > 0 ? by + bh - (startY + radius) : by - (startY - radius)) / dy;

                double entryTime = Math.max(tEntryX, tEntryY);
                double exitTime = Math.min(tExitX, tExitY);

                if (entryTime < exitTime && entryTime >= 0 && entryTime <= 1) {
                    if (entryTime < earliestEntryTime) {
                        earliestEntryTime = entryTime;

                        // Mejora esquinas
                        if (Math.abs(tEntryX - tEntryY) < 0.15) {
                            horizontal = Math.abs(dx) > Math.abs(dy);
                        } else {
                            horizontal = tEntryX > tEntryY;
                        }

                        earliestBrick = brick;
                    }
                }
            }

            if (earliestBrick != null) {
                if (horizontal) {
                    ball.setX(startX + dx * earliestEntryTime + (dx > 0 ? 0.01 : -0.01));
                    ball.setDx(-ball.getDx());
                } else {
                    ball.setY(startY + dy * earliestEntryTime + (dy > 0 ? 0.01 : -0.01));
                    ball.setDy(-ball.getDy());
                }

                earliestBrick.hit();
                bricksRecentlyHit.add(earliestBrick);

                if (earliestBrick.isDestroyed()) {
                    addScore(earliestBrick.getScore());
                    bricks.remove(earliestBrick);
                }
            }
        }
    }

    private void render() {
        if (GameApp.getBackgroundImage() != null) {
            gc.drawImage(GameApp.getBackgroundImage(), 0, 0, GameApp.WIDTH, GameApp.HEIGHT);
        } else {
            gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);
        }

        GameStateManager gsm = GameStateManager.getInstance();

        if (gsm.isGameOver()) {
            renderGameOver();
        } else if (!gsm.isGameStarted()) {
            renderStartMessage();
        } else {
            renderGameElements();
        }
    }

    public void renderGameOver() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(48));
        String text = "GAME OVER - Score: " + totalScore;
        gc.fillText(text, centerText(text, gc.getFont()), GameApp.HEIGHT / 2);

        gc.setFont(new Font(24));
        String restart = "Presiona ESPACIO para volver al menu";
        gc.fillText(restart, centerText(restart, gc.getFont()), GameApp.HEIGHT / 2 + 50);
    }

    private void renderStartMessage() {
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

    private void renderGameElements() {
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        for (AbstractBrick brick : bricks) brick.render(gc);
        for (Ball ball : balls) ball.render(gc);
        paddle.render(gc);

        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + totalScore, 20, 30);

        int lives = LifeManager.getInstance().getLives();
        for (int i = 0; i < lives; i++) {
            drawHeart(gc, 120 + i * 30, 15, 20);
        }
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

    private void addScore(int points) {
        totalScore += points;
    }

    private double centerText(String text, Font font) {
        return (GameApp.WIDTH - calculateTextWidth(text, font)) / 2;
    }

    private double calculateTextWidth(String text, Font font) {
        // Método sencillo: aproximación
        return text.length() * font.getSize() * 0.5;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
    }

    public void startGame () {
        GameStateManager.getInstance().startGame();
    }

    public void resetGame () {
        GameStateManager.getInstance().restartGame();
    }

    public void addNewBall() {
        double radius = ConfigLoader.getInstance().getDouble("ball.radius");
        Ball newBall = ballSpawner.spawnBall(
                paddle.getX() + paddle.getWidth() / 2.0 - radius,
                paddle.getY() - 1.5 * radius
        );
        newBall.setDy(Math.abs(newBall.getDy()));
        balls.add(newBall);
        System.out.println("[JUEGO] Nueva bola añadida. Total: " + balls.size());
    }

    public List<Ball> getBalls() {
        return balls;
    }
}