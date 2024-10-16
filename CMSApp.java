//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.main.ecommerceprototype.CMS;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CMSApp extends Application {
    public CMSApp() {
    }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CMSApp.class.getResource("cms.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 1920.0, 1080.0);
        scene.getStylesheets().add(this.getClass().getResource("stylesheets/CMSGlobal.css").toExternalForm());
        stage.setTitle("CMSApp");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(new String[0]);
    }
}
