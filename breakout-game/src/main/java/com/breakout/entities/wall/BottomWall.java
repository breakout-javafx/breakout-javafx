package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;
import com.breakout.manager.LifeManager;

public class BottomWall implements Wall {
    @Override
    public void onCollision(Ball ball) {
        ball.setDy(-Math.abs(ball.getDy())); // Rebota hacia arriba
        LifeManager.getInstance().decreaseLife();
    }
}
