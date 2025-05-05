package com.breakout.entities.wall;

import com.breakout.entities.ball.Ball;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;

public class BottomWall implements CollisionStrategy {
    @Override
    public void onCollision(Ball ball) {
        System.out.println("[DEBUG] Colisión con fondo - Bola marcada como inactiva");
        ball.setActive(false); // Marco para eliminación
    }
}
