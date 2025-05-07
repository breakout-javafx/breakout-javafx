package com.breakout.entities.ball.strategy;

import com.breakout.entities.ball.Ball;
import com.breakout.entities.paddle.Paddle;

public class PaddleCollisionStrategy {
    private static final double MAX_BOUNCE_ANGLE = Math.toRadians(75); // Ángulo más amplio
    
    public void onCollision(Ball ball, Paddle paddle) {
        // Calcula el punto de impacto relativo al centro del paddle
        double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
        double hitPosition = (ball.getX() + ball.getRadius()) - paddleCenter;
        double normalized = hitPosition / (paddle.getWidth() / 2);
        normalized = Math.max(-1, Math.min(1, normalized)); // Asegura valor entre -1 y 1
        
        // Calcula el ángulo de rebote
        double bounceAngle = normalized * MAX_BOUNCE_ANGLE;
        
        // Calcula la velocidad resultante
        double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
        double newDx = speed * Math.sin(bounceAngle);
        double newDy = -Math.abs(speed * Math.cos(bounceAngle)); // Siempre hacia arriba
        
        // Aplica la nueva velocidad
        ball.setDx(newDx);
        ball.setDy(newDy);
        
        // Ajusta posición para evitar stuck
        ball.setY(paddle.getY() - ball.getRadius() * 2);
    }
}
