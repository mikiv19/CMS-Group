package com.main.ecommerceprototype.CMS;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FXMLCreator extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //creates root element VBox
        VBox root = new VBox();
        root.setSpacing(10);

        //creates and configures user interface elements
        TextField fileNameField = new TextField();
        Label nameLabel = new Label("Enter File Name:");
        Button createButton = new Button("Create FXML");

        //adds user interface elements to the root element
        root.getChildren().addAll(nameLabel, fileNameField, createButton);

        //sets action on the create file button
        createButton.setOnAction(event -> createFXML(fileNameField.getText()));

        //creates the scene with the root element
        Scene scene = new Scene(root, 300, 150);

        //sets the scene onto the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("FXML Creator");
        primaryStage.show();
    }

    private void createFXML(String fileName) {
        //choose the path and make it able to assign your own name to the fxml file.
        String specificFileName = "CMSApp/src/main/resources/cms/templates/" + fileName + ".fxml";

        //creating root element AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(500);
        root.setPrefHeight(350);

        //creates and configures user interfaces elements
        Button button = new Button("Create file");
        button.setLayoutX(100);
        button.setLayoutY(100);

        //adds user interface elements to the root element
        root.getChildren().add(button);

        //creates the scene with the root element
        Scene scene = new Scene(root);

        //saves the FXML file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(specificFileName))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<?import javafx.scene.layout.AnchorPane?>\n");
            writer.write("<?import javafx.scene.control.Button?>\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }}
