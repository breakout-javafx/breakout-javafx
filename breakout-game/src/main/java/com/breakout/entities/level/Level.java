package com.breakout.entities.level;

import java.util.List;

public class Level {
    private List<Block> blocks;  // Lista de bloques que componen el nivel

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public static class Block {
        private double x;
        private double y;
        private String type;  // Tipo de bloque (e.g., "glowing", "multiBall", etc.)

        public Block(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
