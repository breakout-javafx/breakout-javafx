package com.breakout.ui;

import com.breakout.core.GameApp;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class LevelMenu {

    private final Stage stage;

    public LevelMenu(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 30; -fx-alignment: center;");

        try {
            URL levelsUrl = getClass().getClassLoader().getResource("levels");
            if (levelsUrl == null) {
                System.err.println("❌ No se encontró la carpeta levels en resources.");
                return;
            }

            Path path = Paths.get(levelsUrl.toURI());

            Files.list(path)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(file -> {
                    String fileName = file.getFileName().toString();
                    Button b = new Button(fileName);
                    b.setOnAction(e -> GameApp.startGame("levels/" + fileName));
                    root.getChildren().add(b);
                });

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root, GameApp.WIDTH, GameApp.HEIGHT));
        stage.setTitle("Selecciona un nivel");
        stage.show();
    }
}
