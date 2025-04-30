package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;
import com.breakout.manager.LifeManager;

public class BottomWall implements CollisionStrategy {
    @Override
    public void onCollision(Ball ball) {
        if (ball.isActive()) {
            ball.setActive(false); // Marco para eliminación
            LifeManager.getInstance().decreaseLife();
        }
    }
}
