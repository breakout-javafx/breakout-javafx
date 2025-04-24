package com.breakout.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameApp extends Application {
    public static final Integer WIDTH = 800;
    public static final Integer HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT); // Tu pantalla de juego
        GraphicsContext gc = canvas.getGraphicsContext2D();

        GameLoop loop = new GameLoop(gc); // Lógica
        loop.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Breakout con Patrones");
        primaryStage.setScene(scene);
        primaryStage.show();

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
