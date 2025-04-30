package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;

public class TopWall implements CollisionStrategy {
    @Override
    public void onCollision(Ball ball) {
        ball.setDy(Math.abs(ball.getDy())); // Rebota hacia abajo
    }
}
