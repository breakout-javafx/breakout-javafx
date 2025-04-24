package com.breakout.core;

import com.breakout.config.ConfigLoader;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameApp extends Application {
    public static int WIDTH;
    public static int HEIGHT;
    private static final String TITLE = ConfigLoader.getInstance().get("game.tittle");

    @Override
    public void start(Stage primaryStage) {
        // 1. Config base
        int baseWidth = ConfigLoader.getInstance().getInt("window.width");
        int baseHeight = ConfigLoader.getInstance().getInt("window.height");

        // 2. Escalado
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double scaleX = screenBounds.getWidth() / 1920.0;
        double scaleY = screenBounds.getHeight() / 1080.0;
        double scale = Math.min(scaleX, scaleY);

        WIDTH = (int) (baseWidth * scale);
        HEIGHT = (int) (baseHeight * scale);

        // 3. Canvas y escena
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GameLoop loop = new GameLoop(gc);
        loop.start();

        StackPane root = new StackPane(canvas);
        root.setPrefSize(WIDTH, HEIGHT); // 🔧 Esto asegura que el pane no crezca
        Scene scene = new Scene(root);

        // 4. Ventana
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen(); // Centra automáticamente
        primaryStage.show();

        // 5. Input
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) loop.setLeftPressed(true);
            if (e.getCode() == KeyCode.RIGHT) loop.setRightPressed(true);
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) loop.setLeftPressed(false);
            if (e.getCode() == KeyCode.RIGHT) loop.setRightPressed(false);
        });
    }
}
