package com.breakout.manager.datapool;

import com.breakout.entities.ball.Ball;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.manager.observable.GameObservable;
import com.breakout.manager.observer.GameObserver;

import java.util.ArrayList;
import java.util.List;

public class GameDataPool implements GameObservable {
    private static GameDataPool instance;

    private final List<Ball> balls = new ArrayList<>();
    private final List<AbstractBrick> bricks = new ArrayList<>();
    private int score;
    private boolean gameStarted;
    private boolean gameOver;

    public static GameDataPool getInstance() {
        if (instance == null) {
            instance = new GameDataPool();
        }
        return instance;
    }

    public void setScore(int score) {
        this.score = score;
        notifyObservers();
    }

    public int getScore() {
        return score;
    }

    public void addBall(Ball ball) {
        balls.add(ball);
        notifyObservers();
    }

    public void removeBall(Ball ball) {
        balls.remove(ball);
        notifyObservers();
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public void setBricks(List<AbstractBrick> bricks) {
        this.bricks.clear();
        this.bricks.addAll(bricks);
        notifyObservers();
    }

    public List<AbstractBrick> getBricks() {
        return bricks;
    }

    public void setGameStarted(boolean started) {
        this.gameStarted = started;
        notifyObservers();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameOver(boolean over) {
        this.gameOver = over;
        notifyObservers();
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
