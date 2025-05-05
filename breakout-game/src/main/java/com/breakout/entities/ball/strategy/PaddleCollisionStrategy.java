package com.breakout.entities.ball.strategy;

import com.breakout.entities.ball.Ball;
import com.breakout.entities.paddle.Paddle;

public class PaddleCollisionStrategy {

    // Ángulo máximo en radianes para el rebote (30 grados)
    private static final double MAX_BOUNCE_ANGLE = Math.toRadians(30);

    public void onCollision(Ball ball, Paddle paddle) {
        double paddleCenterX = paddle.getX() + paddle.getWidth() / 2.0;
        double ballCenterX = ball.getX() + ball.getRadius();

        // Distancia desde el centro del paddle al punto de impacto
        double distanceFromCenter = ballCenterX - paddleCenterX;

        // Normalizar entre -1 y 1
        double normalized = distanceFromCenter / (paddle.getWidth() / 2.0);
        normalized = Math.max(-1, Math.min(1, normalized)); // Clamping

        // Calcular ángulo
        double bounceAngle = normalized * MAX_BOUNCE_ANGLE;

        // Magnitud de la velocidad actual
        double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());

        // Calcular nuevas velocidades
        double newDx = speed * Math.sin(bounceAngle);
        double newDy = -speed * Math.cos(bounceAngle); // Hacia arriba

        ball.setDx(newDx);
        ball.setDy(newDy);
    }
}
