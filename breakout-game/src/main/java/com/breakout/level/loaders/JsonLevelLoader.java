package com.breakout.level.loaders;

import com.breakout.core.GameLoop;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.decorator.GlowingBrickDecorator;
import com.breakout.entities.brick.decorator.MultiBallBrickDecorator;
import com.breakout.entities.brick.decorator.StandardBrick;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonLevelLoader {

    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    public static List<AbstractBrick> loadFromInputStream(InputStream is, GameLoop gameLoop, BallSpawner spawner) {
        List<AbstractBrick> bricks = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            int width = root.get("width").asInt();
            JsonNode layer = root.get("layers").get(0);
            JsonNode data = layer.get("data");

            for (int index = 0; index < data.size(); index++) {
                int gid = data.get(index).asInt();

                if (gid == 0) continue; // espacio vacío

                int x = index % width;
                int y = index / width;

                double posX = x * TILE_WIDTH;
                double posY = y * TILE_HEIGHT;

                AbstractBrick brick = createBrickFromGid(gid, posX, posY, gameLoop, spawner);
                if (brick != null) {
                    bricks.add(brick);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el mapa JSON desde Tiled", e);
        }

        return bricks;
    }

    private static AbstractBrick createBrickFromGid(int gid, double x, double y,
                                                    GameLoop gameLoop, BallSpawner spawner) {
        return switch (gid) {
            case 1 -> new StandardBrick(x, y, TILE_WIDTH, TILE_HEIGHT);
            case 9 -> new MultiBallBrickDecorator(
                    new StandardBrick(x, y, TILE_WIDTH, TILE_HEIGHT), gameLoop, spawner
            );
            // Puedes añadir más casos si agregas más tipos de ladrillo en tu tileset
            default -> null;
        };
    }


}
