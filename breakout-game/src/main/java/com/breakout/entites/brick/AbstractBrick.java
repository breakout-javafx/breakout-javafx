package com.breakout.entites.brick;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

/*
Ejemplo de uso de Brick con patrón Template + Decorator:

// 1. Crear un ladrillo concreto basado en la plantilla
AbstractBrick baseBrick = new StandardBrick(100, 200, 60, 20); // x, y, ancho, alto

// 2. Decorar el ladrillo con un efecto de brillo (glow)
AbstractBrick glowingBrick = new GlowingBrickDecorator(baseBrick);

// 3. Renderizar el ladrillo decorado en el canvas
glowingBrick.render(graphicsContext);

// Este enfoque permite combinar diferentes decoradores sobre una misma estructura base.
// Puedes seguir añadiendo más decoradores si lo necesitas, por ejemplo:
// AbstractBrick fancyBrick = new ShadowDecorator(new GlowingBrickDecorator(baseBrick));
*/


public abstract class AbstractBrick {
    protected double x, y;
    protected double width, height;

    public AbstractBrick(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        createBrick(); // Método template
    }

    // Template method
    protected final void createBrick() {
        initializeShape();
        initializeColor();
    }

    protected abstract void initializeShape();
    protected abstract void initializeColor();

    public abstract void render(GraphicsContext gc);

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    // Agregado: Método getBounds() para obtener el área del ladrillo
    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }
}
