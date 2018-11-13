package javacheckers.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

public class MainController {


    public void handleJoinGameButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/join_game_menu.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
