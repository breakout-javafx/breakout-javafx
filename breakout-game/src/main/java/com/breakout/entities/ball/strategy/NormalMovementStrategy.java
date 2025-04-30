package com.breakout.entities.ball.strategy;

import com.breakout.core.GameApp;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.wall.*;

import java.util.HashMap;
import java.util.Map;

public class NormalMovementStrategy implements BallMovementStrategy {

    private final Map<String, Wall> walls;

    public NormalMovementStrategy() {
        walls = new HashMap<>();
        walls.put("top", new TopWall());
        walls.put("bottom", new BottomWall());
        walls.put("left", new LeftWall());
        walls.put("right", new RightWall());
    }

    @Override
    public void move(Ball ball) {
        if (!ball.isActive()) return; // No muevo bolas inactivas

        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());

        // Detectar colisiones con paredes
        if (ball.getX() <= 0) {
            walls.get("left").onCollision(ball);
        } else if (ball.getX() >= GameApp.WIDTH - ball.getRadius()) {
            walls.get("right").onCollision(ball);
        }

        if (ball.getY() <= 0) {
            walls.get("top").onCollision(ball);
        } else if (ball.getY() >= GameApp.HEIGHT - ball.getRadius()) {
            walls.get("bottom").onCollision(ball);
        }
    }
}
