package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;

public interface CollisionStrategy {
    void onCollision(Ball ball);
}
