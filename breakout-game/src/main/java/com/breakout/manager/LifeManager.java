package com.breakout.manager;

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
            System.out.println("Vida perdida. Vidas restantes: " + lives);
        }

        if (lives == 0) {
            onLivesDepleted();
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
}
