/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author nickfolse
 */
public class WiFiMapperGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        MainGUI mainGui = MainGUI.loadInstance();
        mainGui.setStage(primaryStage);

        StackPane root = new StackPane(mainGui.root());

        Scene scene = new Scene(root, 800, 450);

        loadIcons(primaryStage);

        primaryStage.setOnHidden((e) -> {
            PersistentPreferences.getInstance().persist();
        });

        primaryStage.setTitle("WiFi Mapper");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void loadIcons(Stage primaryStage) {
        ObservableList<Image> icons = primaryStage.getIcons();
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_16.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_20.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_24.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_32.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_48.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_64.png").toExternalForm()));
        icons.add(new Image(WiFiMapperGUI.class.getResource("/icons/wifi_128.png").toExternalForm()));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
