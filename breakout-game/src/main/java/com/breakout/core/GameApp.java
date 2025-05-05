package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.level.facade.LevelLoader;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

public class GameApp extends Application {
    public static int WIDTH;
    public static int HEIGHT;
    private static final String TITLE = ConfigLoader.getInstance().get("game.tittle");
    GameStateManager gsm = GameStateManager.getInstance();

    //TODO ENCARGARSE DE QUE SE USE EL SEETING DE REDIO DE BOLA AL GENERAR LA BOLA
    //LÓGICA DE ELIMINAR BOLAS AL TOCAR LA BASE DE LA PANTALLA
    //LÓGICA DE TERMINAR PARTIDA SI NOS QUEDAMOS SIN BOLAS

    @Override
    public void start(Stage primaryStage) {
        // Configuración base
        initializeWindow(primaryStage);

        // Crear y mostrar el canvas y escena
        Canvas canvas = createCanvas(primaryStage);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Crear el GameLoop
        GameLoop loop = new GameLoop(gc);
        loop.start();

        GameStateManager.getInstance().setGameLoop(loop);

        // Establezco el LifeManager y el LevelLoader
        GameStateManager gameStateManager = GameStateManager.getInstance();
        gameStateManager.setLifeManager(LifeManager.getInstance());
        gameStateManager.setLevelLoader(new LevelLoader());

        // Configurar los eventos de entrada
        configureInput(primaryStage.getScene(), loop);
    }

    private void initializeWindow(Stage primaryStage) {
        // Obtener las dimensiones de la pantalla
        int baseWidth = ConfigLoader.getInstance().getInt("window.width");
        int baseHeight = ConfigLoader.getInstance().getInt("window.height");

        // Escalar la ventana
        scaleWindow(baseWidth, baseHeight);

        // Configuración de la ventana
        primaryStage.setTitle(TITLE);
        primaryStage.setFullScreen(true);  // Pantalla completa
        primaryStage.setFullScreenExitHint(""); // Opcional
        primaryStage.centerOnScreen();
        primaryStage.toFront();

    }

    private void scaleWindow(int baseWidth, int baseHeight) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double scaleX = screenBounds.getWidth() / 1920.0;
        double scaleY = screenBounds.getHeight() / 1080.0;
        double scale = Math.min(scaleX, scaleY);

        WIDTH = (int) (baseWidth * scale);
        HEIGHT = (int) (baseHeight * scale);
    }

    private Canvas createCanvas(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        root.setPrefSize(WIDTH, HEIGHT);
        Scene scene = new Scene(root);

        // Mostrar la ventana
        primaryStage.setScene(scene);
        primaryStage.show();

        return canvas;
    }

    private void configureInput(Scene scene, GameLoop loop) {
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        scene.setOnKeyPressed(e -> {
            GameStateManager gsm = GameStateManager.getInstance();
            if (e.getCode() == KeyCode.LEFT) loop.setLeftPressed(true);
            if (e.getCode() == KeyCode.RIGHT) loop.setRightPressed(true);
            if (e.getCode() == KeyCode.SPACE) {
                if (gsm.isGameOver()) {
                    gsm.restartGame();
                    loop.resetGame();
                } else {
                    gsm.startGame();
                    loop.start();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) loop.setLeftPressed(false);
            if (e.getCode() == KeyCode.RIGHT) loop.setRightPressed(false);
        });
    }
}
