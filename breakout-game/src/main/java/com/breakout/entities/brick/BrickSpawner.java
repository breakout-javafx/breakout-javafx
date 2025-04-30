package com.breakout.entities.brick;

import com.breakout.config.ConfigLoader;
import com.breakout.core.GameApp;
import com.breakout.core.GameLoop;
import com.breakout.entities.ball.BallSpawner;
import com.breakout.entities.brick.decorator.GlowingBrickDecorator;
import com.breakout.entities.brick.decorator.MultiBallBrickDecorator;
import com.breakout.entities.brick.decorator.StandardBrick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickSpawner {
    private static final int totalClusters = ConfigLoader.getInstance().getInt("brick.generation.totalclusters");
    private static final int clusterSize = ConfigLoader.getInstance().getInt("brick.generation.clustersize");
    private static final int minDistanceFromPaddle = ConfigLoader.getInstance().getInt("brick.generation.mindistancefrompaddle");

    private static final double offsetXMax = ConfigLoader.getInstance().getInt("brick.generation.offsetX");
    private static final double offsetYMax = ConfigLoader.getInstance().getInt("brick.generation.offsetY");

    private static final double brickWidth = ConfigLoader.getInstance().getInt("brick.width");
    private static final double brickHeight = ConfigLoader.getInstance().getInt("brick.height");

    private static final int maxRetries = ConfigLoader.getInstance().getInt("brick.generation.maxretries");

    private final Random random = new Random();

    public List<AbstractBrick> generateBricks(GameLoop gameLoop, BallSpawner ballSpawner) {
        List<AbstractBrick> bricks = new ArrayList<>();
        generateClusterRecursive(bricks, 0, totalClusters, gameLoop, ballSpawner);
        return bricks;
    }

    private void generateClusterRecursive(List<AbstractBrick> bricks, int currentCluster, int maxClusters,
                                          GameLoop gameLoop, BallSpawner ballSpawner) {
        if (currentCluster >= maxClusters) return;

        double paddleY = GameApp.HEIGHT - minDistanceFromPaddle;
        double centerX = random.nextDouble() * (GameApp.WIDTH - brickWidth);
        double centerY = random.nextDouble() * (paddleY - brickHeight);

        int blocksGenerated = 0;
        int blocksFailed = 0;

        for (int i = 0; i < clusterSize; i++) {
            boolean positionFound = false;
            int retryCount = 0;

            while (!positionFound && retryCount < maxRetries) {
                double offsetX = random.nextDouble() * offsetXMax - (offsetXMax / 2);
                double offsetY = random.nextDouble() * offsetYMax - (offsetYMax / 2);

                double brickX = centerX + offsetX;
                double brickY = centerY + offsetY;

                if (isPositionAvailable(brickX, brickY, bricks, brickWidth, brickHeight)) {
                    AbstractBrick brick = new StandardBrick(brickX, brickY, brickWidth, brickHeight);
                    brick = applyRandomDecorator(brick, gameLoop, ballSpawner);

                    bricks.add(brick);
                    blocksGenerated++;
                    positionFound = true;
                } else {
                    blocksFailed++;
                }

                retryCount++;
            }

            if (retryCount == maxRetries) {
                System.out.println("No se pudo colocar un bloque después de " + maxRetries + " intentos.");
            }
        }

        System.out.println("Cluster " + (currentCluster + 1) + ": Generados: " + blocksGenerated + ", Fallidos: " + blocksFailed);

        generateClusterRecursive(bricks, currentCluster + 1, maxClusters, gameLoop, ballSpawner);
    }

    private AbstractBrick applyRandomDecorator(AbstractBrick brick, GameLoop gameLoop, BallSpawner ballSpawner) {
        if (random.nextBoolean()) {
            if (random.nextBoolean()) {
                return new GlowingBrickDecorator(brick);
            } else {
                return new MultiBallBrickDecorator(brick, gameLoop, ballSpawner);
            }
        }
        return brick;
    }

    private boolean isPositionAvailable(double x, double y, List<AbstractBrick> existingBricks, double width, double height) {
        for (AbstractBrick brick : existingBricks) {
            if (x < brick.getX() + brick.getWidth() && x + width > brick.getX() &&
                y < brick.getY() + brick.getHeight() && y + height > brick.getY()) {
                return false;
            }
        }
        return true;
    }
}
