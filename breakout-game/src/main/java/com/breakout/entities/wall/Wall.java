package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;

public interface Wall {
    void onCollision(Ball ball);
}
