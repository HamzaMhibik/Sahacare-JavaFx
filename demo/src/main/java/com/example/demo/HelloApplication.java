package com.example.demo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/demo/view/HomePage.fxml"));
        Parent homePage = FXMLLoader.load(getClass().getResource("/com/example/demo/view/admin/Dashboard.fxml"));
        primaryStage.setWidth(1500);  // Largeur en pixels
        primaryStage.setHeight(800);
        //Set up the scene
        Scene scene = new Scene(homePage);

        // Configure the stage
        primaryStage.setTitle("SahaCare");
        primaryStage.setScene(scene);

        // Set the stage to fullscreen mode
        primaryStage.setFullScreen(false);
        primaryStage.setFullScreenExitHint(""); // Remove the default exit hint
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}