package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;

public class LeftWall implements CollisionStrategy {
    @Override
    public void onCollision(Ball ball) {
        ball.setDx(Math.abs(ball.getDx())); // Rebota hacia la derecha
    }
}
