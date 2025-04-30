package com.breakout.entities.level;

import com.breakout.entities.brick.AbstractBrick;
import com.breakout.entities.brick.decorator.GlowingBrickDecorator;
import com.breakout.entities.brick.decorator.MultiBallBrickDecorator;
import com.breakout.entities.brick.decorator.StandardBrick;
import com.breakout.entities.level.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    private static final String LEVELS_FILE = "levels.json";  // Ruta del archivo JSON

    public List<AbstractBrick> loadLevel(int levelIndex) throws IOException {
        // Leer el archivo JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<Level> levels = objectMapper.readValue(new File(LEVELS_FILE), objectMapper.getTypeFactory().constructCollectionType(List.class, Level.class));

        // Obtener el nivel solicitado
        Level level = levels.get(levelIndex);

        // Crear los bloques a partir del JSON
        List<AbstractBrick> bricks = new ArrayList<>();
        for (Level.Block block : level.getBlocks()) {
            AbstractBrick brick = createBrickFromJson(block);
            bricks.add(brick);
        }

        return bricks;
    }

    private AbstractBrick createBrickFromJson(Level.Block block) {
        // Crear un ladrillo base (sin decoradores)
        AbstractBrick brick = new StandardBrick(block.getX(), block.getY(), 64, 20);  // Usar el tamaño de bloque definido en config

        // Añadir decoradores según el tipo del bloque
        switch (block.getType()) {
            case "glowing":
                brick = new GlowingBrickDecorator(brick);
                break;
            case "multiBall":
                brick = new MultiBallBrickDecorator(brick, null);  // Necesitarías pasar el GameLoop si lo usas en el decorador
                break;
            // Otros tipos de bloques que puedas añadir
            default:
                break;
        }

        return brick;
    }
}
