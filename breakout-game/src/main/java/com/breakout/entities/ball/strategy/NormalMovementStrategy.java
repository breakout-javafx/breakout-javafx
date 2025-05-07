package com.breakout.entities.ball.strategy;

import com.breakout.core.GameApp;
import com.breakout.entities.ball.Ball;
import com.breakout.entities.paddle.Paddle;
import com.breakout.entities.wall.CollisionStrategy;
import com.breakout.manager.GameStateManager;
import java.util.Map;
/*
public class NormalMovementStrategy implements BallMovementStrategy {
    private final Map<String, CollisionStrategy> walls;
    private final Paddle paddle;
    private final PaddleCollisionStrategy paddleCollision;

    public NormalMovementStrategy(Map<String, CollisionStrategy> walls, Paddle paddle) {
        this.walls = walls;
        this.paddle = paddle;
        this.paddleCollision = new PaddleCollisionStrategy();
    }

    @Override
    public void move(Ball ball) {
        if (!ball.isActive()) return;

        // Movimiento básico
        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());

        // Colisiones con bordes
        if (ball.getX() <= 0 || ball.getX() >= GameApp.WIDTH - ball.getRadius() * 2) {
            ball.setDx(-ball.getDx());
        }
        if (ball.getY() <= 0) {
            ball.setDy(-ball.getDy());
        }
        if (ball.getY() >= GameApp.HEIGHT) {
            ball.setActive(false); // Bola perdida
        }

        // Colisión con el paddle (rebote angular)
        if (ball.getBounds().intersects(paddle.getBounds())) {
            paddleCollision.onCollision(ball, paddle);
        }
    }
}*/
public class NormalMovementStrategy implements BallMovementStrategy {
    private static final double SPEED_MULTIPLIER = 1.02; // Pequeño aumento de velocidad en cada rebote
    // Variables de instancia
    private final Map<String, CollisionStrategy> walls;
    private final Paddle paddle;
    private final PaddleCollisionStrategy paddleCollision;

    // Constructor requerido
    public NormalMovementStrategy(
        Map<String, CollisionStrategy> walls, 
        Paddle paddle
    ) {
        this.walls = walls;
        this.paddle = paddle;
        this.paddleCollision = new PaddleCollisionStrategy();
    }
    
    @Override
    public void move(Ball ball) {
        if (!ball.isActive()) return;

        // Actualiza posición
        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());

        // Colisiones con bordes
        if (ball.getX() <= 0 || ball.getX() >= GameApp.WIDTH - ball.getRadius() * 2) {
            ball.setDx(-ball.getDx() * SPEED_MULTIPLIER); // Rebote con ligero aumento de velocidad
            ball.setX(Math.max(0, Math.min(GameApp.WIDTH - ball.getRadius() * 2, ball.getX())));
        }

        if (ball.getY() <= 0) {
            ball.setDy(-ball.getDy() * SPEED_MULTIPLIER);
            ball.setY(Math.max(0, ball.getY()));
        }

        // Colisión con el fondo (bola perdida)
        if (ball.getY() >= GameApp.HEIGHT) {
            ball.setActive(false);
        }
    }
}
