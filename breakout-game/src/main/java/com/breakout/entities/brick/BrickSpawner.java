package com.breakout.entities.brick;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import com.breakout.entities.brick.decorator.GlowingBrickDecorator;
import com.breakout.entities.brick.decorator.StandardBrick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickSpawner {
    private static final int totalClusters = ConfigLoader.getInstance().getInt("brick.generation.totalclusters");
    private static final int clusterSize = ConfigLoader.getInstance().getInt("brick.generation.clustersize");
    private static final int minDistanceFromPaddle = ConfigLoader.getInstance().getInt("brick.generation.mindistancefrompaddle");

    // Nueva configuración para la variación de los bricks dentro de un cluster
    private static final double offsetXMax = ConfigLoader.getInstance().getInt("brick.generation.offsetX");
    private static final double offsetYMax = ConfigLoader.getInstance().getInt("brick.generation.offsetY");

    private static final double brickWidth = ConfigLoader.getInstance().getInt("brick.width");
    private static final double brickHeight = ConfigLoader.getInstance().getInt("brick.height");

    private static final int maxRetries = ConfigLoader.getInstance().getInt("brick.generation.maxretries");

    private Random random = new Random();

    public List<AbstractBrick> generateBricks() {
        List<AbstractBrick> bricks = new ArrayList<>();
        generateClusterRecursive(bricks, 0, totalClusters);
        return bricks;
    }

    private void generateClusterRecursive(List<AbstractBrick> bricks, int currentCluster, int maxClusters) {
        if (currentCluster >= maxClusters) return;  // Caso base, terminamos si hemos generado todos los clusters.

        // Generar una posición aleatoria para el centro del cluster
        double paddleY = GameApp.HEIGHT - minDistanceFromPaddle;
        double centerX = random.nextDouble() * (GameApp.WIDTH - brickWidth);
        double centerY = random.nextDouble() * (paddleY - brickHeight);  // Aseguramos que no se sobrepase el paddle.

        // Contadores
        int blocksGenerated = 0;
        int blocksFailed = 0;

        // Generar los bricks dentro del cluster
        for (int i = 0; i < clusterSize; i++) {
            boolean positionFound = false;
            int retryCount = 0;

            // Intentar encontrar una posición disponible para el bloque, ampliando el área de búsqueda
            while (!positionFound && retryCount < maxRetries) {
                double offsetX = random.nextDouble() * offsetXMax - (offsetXMax / 2);  // Variación en X dentro del cluster
                double offsetY = random.nextDouble() * offsetYMax - (offsetYMax / 2);  // Variación en Y dentro del cluster

                // Generar la posición real de cada brick en el cluster
                double brickX = centerX + offsetX;
                double brickY = centerY + offsetY;

                // Verificar si la nueva posición está disponible
                if (isPositionAvailable(brickX, brickY, bricks, brickWidth, brickHeight)) {
                    // Aplicar la decoración aleatoria
                    AbstractBrick brick = new StandardBrick(brickX, brickY, brickWidth, brickHeight);
                    brick = applyRandomDecorator(brick); // Aplicar el decorador aleatorio

                    bricks.add(brick);
                    blocksGenerated++;
                    positionFound = true;
                } else {
                    blocksFailed++;
                }

                retryCount++;
            }

            // Si no se pudo colocar un bloque en los intentos permitidos, salimos del ciclo.
            if (retryCount == maxRetries) {
                System.out.println("No se pudo colocar un bloque después de " + maxRetries + " intentos.");
            }
        }

        // Imprimir la cantidad de bloques generados y fallidos
        System.out.println("Cluster " + (currentCluster + 1) + ": Generados: " + blocksGenerated + ", Fallidos: " + blocksFailed);

        // Recursivamente generar el siguiente cluster
        generateClusterRecursive(bricks, currentCluster + 1, maxClusters);
    }



    private AbstractBrick applyRandomDecorator(AbstractBrick brick) {
        // 50% de probabilidad de aplicar un decorador
        if (random.nextBoolean()) {
            return new GlowingBrickDecorator(brick); // Puedes añadir más decoradores aquí
        }
        return brick;  // Si no, devolvemos el brick sin decorador.
    }

    // Verificar si una nueva posición está disponible (sin solapamientos)
    private boolean isPositionAvailable(double x, double y, List<AbstractBrick> existingBricks, double width, double height) {
        for (AbstractBrick brick : existingBricks) {
            // Verifica si las coordenadas del nuevo bloque están dentro del área de un bloque existente
            if (x < brick.getX() + brick.getWidth() && x + width > brick.getX() &&
                    y < brick.getY() + brick.getHeight() && y + height > brick.getY()) {
                return false;  // Hay solapamiento
            }
        }
        return true;  // No hay solapamiento
    }
}
