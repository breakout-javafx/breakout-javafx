package com.breakout.entities.ball.strategy;

import com.breakout.core.GameApp;
import com.breakout.entities.ball.Ball;

public class NormalMovementStrategy implements BallMovementStrategy {

    @Override
    public void move(Ball ball) {
        ball.setX(ball.getX() + ball.getDx());
        ball.setY(ball.getY() + ball.getDy());

        if (ball.getX() <= 0 || ball.getX() >= GameApp.WIDTH - ball.getRadius()) {
            ball.setDx(ball.getDx() * -1);
        }
        if (ball.getY() <= 0 || ball.getY() >= GameApp.HEIGHT - ball.getRadius()) {
            ball.setDy(ball.getDy() * -1);
        }
    }
}
