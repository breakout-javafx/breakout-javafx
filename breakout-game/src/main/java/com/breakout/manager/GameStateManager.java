package com.breakout.manager;

import com.breakout.core.GameLoop;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.level.facade.LevelLoader;
import com.breakout.manager.LifeManager;
import com.breakout.entities.paddle.Paddle;
import com.breakout.manager.datapool.GameDataPool;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {
    private static GameStateManager instance;

    private GameLoop gameLoop;
    private BallSpawner ballSpawner;
    private LifeManager lifeManager;
    private LevelLoader levelLoader;
    private Paddle paddle;

    private boolean gameStarted = false;
    private boolean gameOver = false;
    private List<Ball> balls = new ArrayList<>();
    private List<AbstractBrick> bricks = new ArrayList<>();
    private int score = 0;
    private boolean processingLifeLoss = false; // Nueva variable de estado
    private boolean processingBallLoss = false;

    private GameStateManager() {}

    public static GameStateManager getInstance () {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void setGameLoop(GameLoop loop) {
        this.gameLoop = loop;
    }

    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
        this.ballSpawner = new BallSpawner(paddle); // Instanciamos BallSpawner con el Paddle
    }

    public void setLifeManager(LifeManager manager) {
        this.lifeManager = manager;
    }

    public void setLevelLoader(LevelLoader loader) {
        this.levelLoader = loader;
    }

    public void addBall (Ball ball) {
        balls.add(ball);
        // Actualizamos el data pool
        GameDataPool.getInstance().addBall(ball);
    }

    public void setBricks(List<AbstractBrick> bricks) {
        this.bricks = bricks;
        // Actualizamos el data pool
        GameDataPool.getInstance().setBricks(bricks);
    }

    public void startGame() {
        if (!gameStarted && !gameOver) {
            gameStarted = true;
            // Actualizamos el data pool
            GameDataPool.getInstance().setGameStarted(true);
        }
    }

    public void restartGame() {
        score = 0;
        gameStarted = false;
        gameOver = false;
        balls.clear();
        GameDataPool.getInstance().setScore(0);
        GameDataPool.getInstance().setGameStarted(false);
        GameDataPool.getInstance().setGameOver(false);

        LifeManager.getInstance().reset();

        bricks = levelLoader.loadLevel("levels/level1.json", gameLoop, ballSpawner);
        GameDataPool.getInstance().setBricks(bricks);

        Ball ball = ballSpawner.spawnBall(
            gameLoop.getPaddle().getX() + gameLoop.getPaddle().getWidth() / 2,
            gameLoop.getPaddle().getY() - Ball.RADIUS
        );

        gameLoop.addBall(ball);
        GameDataPool.getInstance().addBall(ball);

        gameLoop.getPaddle().resetPosition();
    }

    public void addScore(int points) {
        score += points;
        // Actualizamos el data pool
        GameDataPool.getInstance().setScore(score);
    }

    public void loseLife() {
        if (gameOver) return;

        System.out.println("[GSM] Procesando eliminación de vida");
    
        LifeManager.getInstance().decreaseLife();

        if (LifeManager.getInstance().getLives() <= 0) {
            gameOver();
        } else {
            // Solo spawnear nueva bola si no hay bolas activas
            if (gameLoop != null && gameLoop.getActiveBallCount() == 0) {
                spawnNewBall();
            }
        }
    }

    private void spawnNewBall() {
        if (gameLoop == null || gameOver) return;
    
        try {
            // Pequeña pausa para dar feedback visual
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    
        Ball newBall = ballSpawner.spawnBall(
            gameLoop.getPaddle().getX() + gameLoop.getPaddle().getWidth() / 2,
            gameLoop.getPaddle().getY() - Ball.RADIUS
        );
    
        // Resetear velocidad para asegurar movimiento hacia arriba
        newBall.setDy(Math.abs(newBall.getDy()));
        gameLoop.addBall(newBall);
    
        System.out.println("[GSM] Nueva bola creada en (" + newBall.getX() + "," + newBall.getY() + ")");
    }

    public void gameOver() {
        if (!this.gameOver) {
            System.out.println("[GameStateManager] Activando Game Over");
            this.gameOver = true;
            GameDataPool.getInstance().setGameOver(true);
            
            // Forzar renderizado final
            if (gameLoop != null) {
                gameLoop.renderGameOver();
                gameLoop.stop();
            }
        }
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Integer getScore() {
        return score;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<AbstractBrick> getBricks() {
        return bricks;
    }

    public void notifyBallLost () {
        if (gameOver) {
            System.out.println("[JUEGO] Ignorando pérdida de bola - Juego ya terminado");
            return;
        }
        
        System.out.println("[JUEGO] Procesando pérdida de última bola");
        LifeManager.getInstance().decreaseLife();
        
        if (LifeManager.getInstance().getLives() <= 0) {
            gameOver();
        } else {
            // Pequeña pausa para feedback visual
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            spawnNewBall();
        }
    }
}