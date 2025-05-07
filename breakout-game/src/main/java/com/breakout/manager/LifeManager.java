package com.breakout.manager;

import com.breakout.config.ConfigLoader;

public class LifeManager {

    private static LifeManager instance;
    private int lives;

    private LifeManager() {
        this.lives = 3; // Número inicial de vidas
    }

    public static LifeManager getInstance() {
        if (instance == null) {
            instance = new LifeManager();
        }
        return instance;
    }

    // Resta una vida
    public void decreaseLife() {
        if (lives > 0) {
            lives--;
            System.out.println("[LifeManager] Vida restada. Vidas: " + lives);
            
            if (lives <= 0) {
                System.out.println("[LifeManager] Notificando Game Over");
                GameStateManager.getInstance().gameOver(); // Notificación directa
            }
        }
    }

    // Suma una vida
    public void increaseLife() {
        lives++;
        System.out.println("Vida ganada. Vidas totales: " + lives);
    }

    public int getLives() {
        return lives;
    }

    private void onLivesDepleted() {
        System.out.println("¡Juego terminado! Has perdido todas tus vidas.");
        // Aquí más adelante puedes implementar el fin de la partida o la transición a pantalla de Game Over
    }

    public void reset() {
        this.lives = ConfigLoader.getInstance().getInt("initial.lives");
    }
}
