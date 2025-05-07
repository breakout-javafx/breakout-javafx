package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;

public class BottomWall implements CollisionStrategy {
    @Override
    public void onCollision(Ball ball) {
        // Solo marcamos la bola como inactiva
        ball.setActive(false);
        System.out.println("[COLISIÓN] Bola marcada como inactiva por BottomWall");
    }
}
