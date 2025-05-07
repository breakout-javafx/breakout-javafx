package com.breakout.core;

import com.breakout.config.ConfigLoader;
import com.breakout.entities.ball.Ball;
import com.breakout.level.facade.LevelLoader;
import com.breakout.manager.GameStateManager;
import com.breakout.manager.LifeManager;
import com.breakout.ui.LevelMenu;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static int WIDTH;
    public static int HEIGHT;
    private static final String TITLE = ConfigLoader.getInstance().get("game.title");
    private static final GameStateManager gsm = GameStateManager.getInstance();

    private static Stage primaryStage;
    private static GameLoop currentLoop;
    private static Image backgroundImage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        initializeWindow(primaryStage);
        StackPane root = new StackPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        
        primaryStage.setScene(scene);
        loadBackgroundTexture();

        gsm.setLifeManager(LifeManager.getInstance());
        gsm.setLevelLoader(new LevelLoader());

        configureInput(scene);
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

    public static void startGame(String levelPath) {
        // Asegura que no haya GameLoop previo
    if (currentLoop != null) {
        currentLoop.stop();
        currentLoop = null;
    }
    
    // Crea una instancia completamente nueva
    Canvas canvas = new Canvas(GameApp.WIDTH, GameApp.HEIGHT);
    currentLoop = new GameLoop(canvas.getGraphicsContext2D(), levelPath);
    
    // Configuración de la escena
    StackPane root = new StackPane(canvas);
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    
    // Reinicia estados
    GameStateManager.getInstance().restartGame();
    configureInput(scene);
    
    currentLoop.start();
    }

    private static void configureInput(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("La escena no puede ser null");
        }

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                currentLoop.setLeftPressed(true);
            }
            if (e.getCode() == KeyCode.RIGHT) {
                currentLoop.setRightPressed(true);
            }

            if (e.getCode() == KeyCode.SPACE) {
                if (gsm.isGameOver()) {
                    gsm.restartGame(); // Reinicio el GameStateManager
                    if (currentLoop != null) {
                        currentLoop.stop();
                        currentLoop = null;
                    }
                    new LevelMenu(primaryStage).show();
                    
                } else {
                    gsm.startGame();
                    if (currentLoop != null) {
                        currentLoop.startGame();
                    }
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                currentLoop.setLeftPressed(false);
            }
            if (e.getCode() == KeyCode.RIGHT) {
                currentLoop.setRightPressed(false);
            }
        });
    }

    private void loadBackgroundTexture() {
        String path = ConfigLoader.getInstance().get("background.texture");
        System.out.println("Cargando fondo desde: " + path);

        try {
            var url = getClass().getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("No se encontró el recurso de fondo en el JAR: " + path);
                return;
            }
            backgroundImage = new Image(url.toExternalForm());
            System.out.println("Fondo cargado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando la imagen de fondo.");
        }
    }

    public static Image getBackgroundImage() {
        return backgroundImage;
    }
}
