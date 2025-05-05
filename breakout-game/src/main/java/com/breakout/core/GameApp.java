package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.ui.LevelMenu;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class GameApp extends Application {

    public static int WIDTH;
    public static int HEIGHT;
    private static final String TITLE = ConfigLoader.getInstance().get("game.tittle");

    private static Stage primaryStage;
    private static GameLoop currentLoop;

    // ✅ NUEVO: textura de fondo
    private static Image backgroundImage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Escalar y configurar ventana
        initializeWindow(primaryStage);

        // Cargar textura de fondo desde config
        loadBackgroundTexture();

        // Mostrar menú de selección de nivel
        new LevelMenu(primaryStage).show();
    }

    private void initializeWindow(Stage stage) {
        int baseWidth = ConfigLoader.getInstance().getInt("window.width");
        int baseHeight = ConfigLoader.getInstance().getInt("window.height");

        scaleWindow(baseWidth, baseHeight);

        stage.setTitle(TITLE);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.centerOnScreen();
        stage.toFront();
    }

    private void scaleWindow(int baseWidth, int baseHeight) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double scaleX = screenBounds.getWidth() / 1920.0;
        double scaleY = screenBounds.getHeight() / 1080.0;
        double scale = Math.min(scaleX, scaleY);

        WIDTH = (int) (baseWidth * scale);
        HEIGHT = (int) (baseHeight * scale);
    }

    /**
     * Método llamado desde LevelMenu al seleccionar un nivel.
     */
    public static void startGame(String levelPath) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        currentLoop = new GameLoop(gc, levelPath);

        StackPane root = new StackPane(canvas);
        root.setPrefSize(WIDTH, HEIGHT);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        configureInput(scene);

        currentLoop.start();
        currentLoop.startGame();
    }

    private static void configureInput(Scene scene) {
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) currentLoop.setLeftPressed(true);
            if (e.getCode() == KeyCode.RIGHT) currentLoop.setRightPressed(true);

            if (e.getCode() == KeyCode.SPACE) {
                if (currentLoop.isGameOver()) {
                    new LevelMenu(primaryStage).show();
                } else {
                    currentLoop.startGame();
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) currentLoop.setLeftPressed(false);
            if (e.getCode() == KeyCode.RIGHT) currentLoop.setRightPressed(false);
        });
    }

    private void loadBackgroundTexture() {
        String path = ConfigLoader.getInstance().get("background.texture");
        System.out.println("🧩 Cargando fondo desde: " + path);
    
        try {
            var url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("❌ No se encontró el recurso de fondo en el JAR: " + path);
                return;
            }
            backgroundImage = new Image(url.toExternalForm()); // ✅ JAR-safe
            System.out.println("✅ Fondo cargado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error cargando la imagen de fondo.");
        }
    }
    
    

    // ✅ NUEVO: getter para acceder desde GameLoop
    public static Image getBackgroundImage() {
        return backgroundImage;
    }
}
