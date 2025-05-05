package com.breakout.ui;

import com.breakout.core.GameApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class LevelMenu {

    private final Stage stage;
    private String selectedLevel;
    private String selectedPaddle;
    private String selectedBall;

    private final ImageView previewPaddle = new ImageView();
    private final ImageView previewBall = new ImageView();
    private final Button startButton = new Button("Iniciar Juego");

    public LevelMenu(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label title = new Label("SELECCIONA TU PARTIDA");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 28px;");

        // NIVELES
        Label levelLabel = new Label("Selecciona Nivel");
        levelLabel.setTextFill(Color.WHITE);
        HBox levelButtons = new HBox(10);
        levelButtons.setAlignment(Pos.CENTER);
        loadLevelButtons(levelButtons);

        // PADDLES
        Label paddleLabel = new Label("Selecciona Paddle");
        paddleLabel.setTextFill(Color.WHITE);
        HBox paddleImages = new HBox(10);
        paddleImages.setAlignment(Pos.CENTER);
        loadImagesInto("textures/paddle", paddleImages, 100, 25, true);

        // BALLS
        Label ballLabel = new Label("Selecciona Bola");
        ballLabel.setTextFill(Color.WHITE);
        HBox ballImages = new HBox(10);
        ballImages.setAlignment(Pos.CENTER);
        loadImagesInto("textures/ball", ballImages, 32, 32, false);

        // VISTA PREVIA
        HBox previewBox = new HBox(20);
        previewBox.setAlignment(Pos.CENTER);
        previewPaddle.setFitWidth(100);
        previewPaddle.setFitHeight(25);
        previewBall.setFitWidth(32);
        previewBall.setFitHeight(32);
        previewBox.getChildren().addAll(previewPaddle, previewBall);

        // BOTÓN INICIO
        startButton.setDisable(true);
        startButton.setOnAction(e -> {
            System.out.println("🟢 INICIANDO JUEGO:");
            System.out.println("Nivel: " + selectedLevel);
            System.out.println("Paddle: " + selectedPaddle);
            System.out.println("Ball: " + selectedBall);
        
            System.setProperty("selected.paddle", selectedPaddle);
            System.setProperty("selected.ball", selectedBall);
        
            GameApp.startGame(selectedLevel);
        });
        

        root.getChildren().addAll(
                title,
                levelLabel, levelButtons,
                paddleLabel, paddleImages,
                ballLabel, ballImages,
                new Label("Vista previa"), previewBox,
                startButton
        );

        Scene scene = new Scene(root, GameApp.WIDTH, GameApp.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Menú de Juego");
        stage.show();
    }

    private void loadLevelButtons(HBox target) {
        try {
            URL levelsUrl = getClass().getClassLoader().getResource("levels");
            if (levelsUrl == null) {
                System.err.println("❌ No se encontró la carpeta 'levels' en resources.");
                return;
            }

            Path path = Paths.get(levelsUrl.toURI());
            Files.list(path)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(file -> {
                    String fileName = file.getFileName().toString();
                    Button b = new Button(fileName);
                    b.setOnAction(e -> {
                        selectedLevel = "levels/" + fileName;
                        checkSelections();
                    });
                    target.getChildren().add(b);
                });

        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void loadImagesInto(String folder, HBox target, int width, int height, boolean isPaddle) {
        try {
            URL folderUrl = getClass().getClassLoader().getResource(folder);
            if (folderUrl == null) {
                System.err.println("❌ No se encontró carpeta de imágenes: " + folder);
                return;
            }

            Path folderPath = Paths.get(folderUrl.toURI());
            Files.list(folderPath)
                .filter(path -> path.toString().endsWith(".png"))
                .forEach(path -> {
                    String relativePath = folder + "/" + path.getFileName().toString();
                    Image image = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(relativePath)));
                    ImageView imgView = new ImageView(image);
                    imgView.setFitWidth(width);
                    imgView.setFitHeight(height);
                    imgView.setStyle("-fx-cursor: hand;");

                    imgView.setOnMouseClicked(e -> {
                        if (isPaddle) {
                            selectedPaddle = relativePath;
                            previewPaddle.setImage(image);
                        } else {
                            selectedBall = relativePath;
                            previewBall.setImage(image);
                        }
                        checkSelections();
                    });

                    target.getChildren().add(imgView);
                });

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void checkSelections() {
        boolean ready = selectedLevel != null && selectedPaddle != null && selectedBall != null;
        startButton.setDisable(!ready);
    }
}
