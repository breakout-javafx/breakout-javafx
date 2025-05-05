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

        this.bricks = LevelLoader.loadLevel(levelPath, this, ballSpawner);
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
        int activeBallsBefore = (int) balls.stream().filter(Ball::isActive).count();
    
        // Eliminar bolas inactivas primero
        balls.removeIf(ball -> !ball.isActive());
    
        // Actualizar bolas activas
        for (Ball ball : balls) {
            ball.update();
        
            // Detección de colisiones con bordes (excepto fondo)
            if (ball.getX() <= 0) {
                new LeftWall().onCollision(ball);
                ball.setX(1); // Ajuste para evitar atascamiento
            } else if (ball.getX() >= GameApp.WIDTH - ball.getRadius() * 2) {
                new RightWall().onCollision(ball);
                ball.setX(GameApp.WIDTH - ball.getRadius() * 2 - 1);
            }
        
            if (ball.getY() <= 0) {
                new TopWall().onCollision(ball);
                ball.setY(1);
            }
        }
    
        // Verificación especial para fondo (después de actualizar posiciones)
        balls.removeIf(ball -> {
            if (ball.getY() > GameApp.HEIGHT) {
                new BottomWall().onCollision(ball);
                return true;
            }
            return false;
        });
    
        // Manejo de pérdida de vida
        int activeBallsAfter = (int) balls.stream().filter(Ball::isActive).count();
        if (activeBallsBefore > 0 && activeBallsAfter == 0) {
            System.out.println("[JUEGO] Última bola perdida - Notificando GameStateManager");
            GameStateManager.getInstance().notifyBallLost();
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
        // Primero, dibujamos el fondo si hay una imagen o limpiamos la pantalla si no.
        if (GameApp.getBackgroundImage() != null) {
            gc.drawImage(GameApp.getBackgroundImage(), 0, 0, GameApp.WIDTH, GameApp.HEIGHT);
        } else {
            gc.clearRect(0, 0, GameApp.WIDTH, GameApp.HEIGHT);
        }

        GameStateManager gsm = GameStateManager.getInstance();

        if (GameStateManager.getInstance().isGameOver()) {
            System.out.println("[GameLoop] Renderizado Game Over");
            renderGameOver();
            return;
        }

        // Si el juego está terminado (game over), mostramos la pantalla de Game Over
        if (gsm.isGameOver()) {
            System.out.println("Renderizando pantalla de Game Over");
            renderGameOver();
            return;
        } else if (!gsm.isGameStarted()) {  // Si el juego no ha comenzado, mostramos el mensaje de inicio
            renderStartMessage();
        } else {  // Si el juego está en progreso, renderizamos los elementos del juego
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
        String restartText = "Press SPACE to return to menu";
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
        if (ball != null) {
            balls.add(ball);
            System.out.println("[DEBUG] Bola añadida. Total bolas: " + balls.size());
        }
    }

    /*
    private void handleBallLost() {
        if (balls.isEmpty()) {
            GameStateManager.getInstance().loseLife();
        }
    }
    */

    public void resetGame() {
        GameStateManager gsm = GameStateManager.getInstance();
        gsm.restartGame(); // Delegar en GameStateManager
    
        // Mantén solo la lógica gráfica/local:
        balls.clear();
    
        // Cargar el nivel, usando un path adecuado
        bricks = LevelLoader.loadLevel(levelPath, this, ballSpawner);

        // Resetear el puntaje y la vida
        totalScore = 0;
        LifeManager.getInstance().reset();

        // Restablecer el estado del juego
        gameStarted = false;
        gameOver = false;

        // Crear una nueva pelota y añadirla
        Ball newBall = ballSpawner.spawnBall(
                paddle.getX() + paddle.getWidth() / 2,
                paddle.getY() - ConfigLoader.getInstance().getDouble("ball.radius")
        );
        balls.add(newBall);

        // Resetear la posición de la pala
        paddle.resetPosition();
    }


    private double calculateTextWidth(String text, Font font) {
        Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }

    public BallSpawner getBallSpawner() {
        return ballSpawner;
    }

    public int getActiveBallCount () {
        return balls.size();
    }

    private void adjustBallPosition (Ball ball, String boundary) {
        // Ajuste fino para evitar que la bola se quede atascada
        switch (boundary) {
            case "left":
                ball.setX(1); // Pequeño margen
                break;
            case "right":
                ball.setX(GameApp.WIDTH - ball.getRadius() * 2 - 1);
                break;
            case "top":
                ball.setY(1);
                break;
        }
    }
}