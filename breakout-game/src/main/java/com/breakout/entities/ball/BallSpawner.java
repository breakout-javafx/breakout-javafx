package com.breakout.entities.ball;

import com.breakout.entities.ball.strategy.BallMovementStrategy;
import com.breakout.entities.ball.strategy.NormalMovementStrategy;
import com.breakout.entities.paddle.Paddle;
import com.breakout.entities.wall.*;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BallSpawner {

    private final BallMovementStrategy strategy;

    public BallSpawner(Paddle paddle) {
        this.strategy = createDefaultStrategy(paddle);
    }

    private BallMovementStrategy createDefaultStrategy(Paddle paddle) {
        Map<String, CollisionStrategy> walls = new HashMap<>();
        walls.put("top", new TopWall());
        walls.put("bottom", new BottomWall());
        walls.put("left", new LeftWall());
        walls.put("right", new RightWall());
        return new NormalMovementStrategy(walls, paddle); 
    }

    public Ball spawnBall(double x, double y) {
        Ball ball = new Ball(x, y);
        ball.setMovementStrategy(strategy);

        String texturePath = System.getProperty("selected.ball");
        if (texturePath != null) {
            Image texture = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(texturePath)));
            ball.setTexture(texture);
        }

        return ball;
    }

    public BallMovementStrategy getStrategy() {
        return strategy;
    }
}
