package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.paddle.Paddle;
import com.breakout.level.facade.LevelLoader;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;
import javafx.animation.AnimationTimer;
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
    
    private final List<AbstractBrick> bricksRecentlyHit = new ArrayList<>();

    private final GraphicsContext gc;
    private final List<Ball> balls = new ArrayList<>();
    private final Paddle paddle;
    private List<AbstractBrick> bricks;

    private final BallSpawner ballSpawner;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private long lastUpdateTime = 0;
    private int totalScore = 0;

    public GameLoop(GraphicsContext gc) {
        this.gc = gc;

        this.paddle = new Paddle(
                GameApp.WIDTH / 2.0 - (ConfigLoader.getInstance().getInt("paddle.width") / 2.0),
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

        this.bricks = LevelLoader.loadLevel("levels/level1.json", this, ballSpawner);
        GameStateManager.getInstance().setGameLoop(this);
        GameStateManager.getInstance().setPaddle(paddle);
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
        GameStateManager gsm = GameStateManager.getInstance();

        if (!gsm.isGameStarted() || gsm.isGameOver()) {
            return;
        }

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
        bricksRecentlyHit.clear();
    
        for (Ball ball : balls) {
            double startX = ball.getPrevX();
            double startY = ball.getPrevY();
            double endX = ball.getX();
            double endY = ball.getY();
            double radius = ball.getRadius();
    
            double dx = endX - startX;
            double dy = endY - startY;
    
            Iterator<AbstractBrick> iterator = bricks.iterator();
            while (iterator.hasNext()) {
                AbstractBrick brick = iterator.next();
    
                if (bricksRecentlyHit.contains(brick)) continue;
    
                // Inflar el brick con el radio de la bola
                double bx = brick.getX() - radius;
                double by = brick.getY() - radius;
                double bw = brick.getWidth() + 2 * radius;
                double bh = brick.getHeight() + 2 * radius;
    
                double tEntryX, tEntryY;
                double tExitX, tExitY;
    
                if (dx == 0) {
                    tEntryX = Double.NEGATIVE_INFINITY;
                    tExitX = Double.POSITIVE_INFINITY;
                } else {
                    tEntryX = (dx > 0 ? bx - startX : bx + bw - startX) / dx;
                    tExitX = (dx > 0 ? bx + bw - startX : bx - startX) / dx;
                }
    
                if (dy == 0) {
                    tEntryY = Double.NEGATIVE_INFINITY;
                    tExitY = Double.POSITIVE_INFINITY;
                } else {
                    tEntryY = (dy > 0 ? by - startY : by + bh - startY) / dy;
                    tExitY = (dy > 0 ? by + bh - startY : by - startY) / dy;
                }
    
                double entryTime = Math.max(tEntryX, tEntryY);
                double exitTime = Math.min(tExitX, tExitY);
    
                // Colisión válida solo si entra entre 0 y 1 y sale después
                if (entryTime < exitTime && entryTime >= 0 && entryTime <= 1) {
                    // Decide el eje de colisión
                    if (tEntryX > tEntryY) {
                        ball.setDx(-ball.getDx());
                    } else {
                        ball.setDy(-ball.getDy());
                    }
    
                    brick.hit();
                    bricksRecentlyHit.add(brick);
    
                    if (brick.isDestroyed()) {
                        addScore(brick.getScore());
                        iterator.remove();
                    }
    
                    // No procesamos más bricks en este frame para esta bola
                    break;
                }
            }
        }
    }

    private void render() {
        gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

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
        gc.setFill(Color.RED);
        gc.setFont(new Font(48));
        String text = "GAME OVER - Score: " + totalScore;
        double textWidth = calculateTextWidth(text, gc.getFont());
        gc.fillText(text, (GameApp.WIDTH - textWidth) / 2, GameApp.HEIGHT / 2);

        gc.setFont(new Font(24));
        String restartText = "Press SPACE to restart";
        double restartWidth = calculateTextWidth(restartText, gc.getFont());
        gc.fillText(restartText, (GameApp.WIDTH - restartWidth) / 2, GameApp.HEIGHT / 2 + 50);
    }

    public void renderGameElements() {
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);

        for (AbstractBrick brick : bricks) {
            brick.render(gc);
        }

        for (Ball ball : balls) {
            ball.render(gc);
        }

        paddle.render(gc);

        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + totalScore, 20, 30);

        int lives = LifeManager.getInstance().getLives();
        for (int i = 0; i < lives; i++) {
            drawHeart(gc, 120 + i * 30, 15, 20);
        }
    }

    public void renderStartMessage() {
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
        GameStateManager gsm = GameStateManager.getInstance();
        if (gsm.isGameOver()) {
            resetGame();
        }
    }

    public void addBall(Ball ball) {
        balls.add(ball);
    }

    private void handleBallLost() {
        if (balls.isEmpty()) {
            GameStateManager.getInstance().loseLife();
        }
    }

    public void resetGame() {
        GameStateManager gsm = GameStateManager.getInstance();
        gsm.restartGame(); // Delegar en GameStateManager
    
        // Mantén solo la lógica gráfica/local:
        balls.clear();
        bricks = LevelLoader.loadLevel("levels/level1.json", this, ballSpawner);
        Ball newBall = ballSpawner.spawnBall(paddle.getX() + paddle.getWidth() / 2,
        paddle.getY() - ConfigLoader.getInstance().getDouble("ball.radius"));
        balls.add(newBall);
        paddle.resetPosition();
        totalScore = 0;
    }

    private double calculateTextWidth(String text, Font font) {
        Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }

    public BallSpawner getBallSpawner() {
        return ballSpawner;
    }
}