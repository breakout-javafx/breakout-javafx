package com.breakout.level.facade;

import com.breakout.core.GameLoop;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.level.loaders.JsonLevelLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LevelLoader {

    public static List<AbstractBrick> loadLevel(String path, GameLoop gameLoop, BallSpawner ballSpawner) {
        if (path.endsWith(".json")) {
            return loadFromJsonResource(path, gameLoop, ballSpawner);
        } else {
            throw new IllegalArgumentException("Formato de nivel no soportado: " + path);
        }
    }

    private static List<AbstractBrick> loadFromJsonResource(String resourcePath, GameLoop gameLoop, BallSpawner spawner) {
        try (InputStream is = LevelLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("No se pudo encontrar el recurso: " + resourcePath);
            } else {
                System.out.println("Cargando nivel JSON desde " + resourcePath);
            }

            return JsonLevelLoader.loadFromInputStream(is, gameLoop, spawner);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el nivel desde " + resourcePath, e);
        }
    }
}