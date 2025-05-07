package com.breakout.entities.ball;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.strategy.BallMovementStrategy;
import com.breakout.entities.ball.strategy.NormalMovementStrategy;
import com.breakout.entities.paddle.Paddle;
import com.breakout.entities.wall.*;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BallSpawner {
    private final Paddle paddle;
    private final Map<String, CollisionStrategy> walls;

    public BallSpawner(Paddle paddle) {
        this.paddle = paddle;
        this.walls = createWallsMap(); // Método para crear las paredes
    }

    private Map<String, CollisionStrategy> createWallsMap() {
        Map<String, CollisionStrategy> walls = new HashMap<>();
        walls.put("top", new TopWall());
        walls.put("bottom", new BottomWall());
        walls.put("left", new LeftWall());
        walls.put("right", new RightWall());
        return walls;
    }

    public Ball spawnBall(double x, double y) {
        Ball ball = new Ball(x, y);
        // Pasa los parámetros necesarios al constructor
        ball.setMovementStrategy(new NormalMovementStrategy(walls, paddle));

        String texturePath = System.getProperty("selected.ball");
        if (texturePath  != null) {
            try {
                // Cargar desde el classpath
                InputStream is = getClass().getClassLoader().getResourceAsStream(texturePath);
                if (is != null) {
                    Image texture = new Image(is);
                    ball.setTexture(texture);
                } else {
                    System.err.println("Archivo no encontrado : " + texturePath);
                }
            } catch (Exception e) {
                System.err.println("Error al cargar las texturas");
            }
        }

        return ball;
    }
}