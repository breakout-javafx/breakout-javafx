package com.breakout.entities.ball.strategy;

import com.breakout.core.GameApp;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.wall.CollisionStrategy;

import java.util.Map;

public class NormalMovementStrategy implements BallMovementStrategy {

    private final Map<String, CollisionStrategy> walls;

    public NormalMovementStrategy(Map<String, CollisionStrategy> walls) {
        this.walls = walls;
    }

    @Override
    public void move(Ball ball) {
        if (!ball.isActive()) return;

        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());

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
