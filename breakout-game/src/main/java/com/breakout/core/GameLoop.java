package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
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
    }

    private void updateBalls() {
        int activeBallsBefore = countActiveBalls();
    
        // Eliminar bolas inactivas (versión compatible)
        Iterator<Ball> iterator = balls.iterator();
        int ballsRemoved = 0;
        while (iterator.hasNext()) {
            if (!iterator.next().isActive()) {
                iterator.remove();
                ballsRemoved++;
            }
        }

        if (ballsRemoved > 0) {
            System.out.println("[JUEGO] Bolas eliminadas: " + ballsRemoved);
        }
    
        // Actualizar bolas restantes
        for (Ball ball : balls) {
            ball.update();
        
            // Detección de colisiones con bordes
            if (ball.getX() <= 0) {
                new LeftWall().onCollision(ball);
                ball.setX(1); // Pequeño margen
            } else if (ball.getX() >= GameApp.WIDTH - ball.getRadius() * 2) {
                new RightWall().onCollision(ball);
                ball.setX(GameApp.WIDTH - ball.getRadius() * 2 - 1);
            }
        
            if (ball.getY() <= 0) {
                new TopWall().onCollision(ball);
                ball.setY(1);
            }
        
            // Detección especial para fondo
            if (ball.getY() > GameApp.HEIGHT) {
                new BottomWall().onCollision(ball);
                System.out.println("[FÍSICA] Bola en Y=" + ball.getY() + " (HEIGHT=" + GameApp.HEIGHT + ")");
            }
        }
    
        // Verificación final para pérdida de bola
        int activeBallsAfter = countActiveBalls();
        if (activeBallsBefore > 0 && activeBallsAfter == 0) {
            System.out.println("[JUEGO] Última bola perdida - Notificando");
            GameStateManager.getInstance().notifyLastBallLost();
        }
    }

    private void updatePaddle() {
        if (leftPressed) paddle.moveLeft();
        if (rightPressed) paddle.moveRight();
    }

    private void handleCollisions() {
        bricksRecentlyHit.clear();

        for (Ball ball : balls) {
            double startX = ball.getPrevX(), startY = ball.getPrevY();
            double endX = ball.getX(), endY = ball.getY();
            double dx = endX - startX, dy = endY - startY;
            double radius = ball.getRadius();

            Iterator<AbstractBrick> iterator = bricks.iterator();
            while (iterator.hasNext()) {
                AbstractBrick brick = iterator.next();

                if (bricksRecentlyHit.contains(brick)) continue;

                double bx = brick.getX() - radius;
                double by = brick.getY() - radius;
                double bw = brick.getWidth() + 2 * radius;
                double bh = brick.getHeight() + 2 * radius;

                double tEntryX = dx == 0 ? Double.NEGATIVE_INFINITY : (dx > 0 ? bx - startX : bx + bw - startX) / dx;
                double tExitX = dx == 0 ? Double.POSITIVE_INFINITY : (dx > 0 ? bx + bw - startX : bx - startX) / dx;

                double tEntryY = dy == 0 ? Double.NEGATIVE_INFINITY : (dy > 0 ? by - startY : by + bh - startY) / dy;
                double tExitY = dy == 0 ? Double.POSITIVE_INFINITY : (dy > 0 ? by + bh - startY : by - startY) / dy;

                double entryTime = Math.max(tEntryX, tEntryY);
                double exitTime = Math.min(tExitX, tExitY);

                if (entryTime < exitTime && entryTime >= 0 && entryTime <= 1) {
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

                    break;
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
        gc.setFill(Color.RED);
        gc.setFont(new Font(48));
        String text = "GAME OVER - Score: " + totalScore;
        gc.fillText(text, centerText(text, gc.getFont()), GameApp.HEIGHT / 2);

        gc.setFont(new Font(24));
        String restart = "Press SPACE to return to menu";
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
        gc.closePath();
        gc.fill();
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

    private boolean shouldSpawnNewBall() {
        return !GameStateManager.getInstance().isGameOver() && 
               LifeManager.getInstance().getLives() > 0;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    // Método auxiliar para contar bolas activas
    private int countActiveBalls() {
        int count = 0;
        for (Ball ball : balls) {
            if (ball.isActive()) count++;
        }
        return count;
    }
}