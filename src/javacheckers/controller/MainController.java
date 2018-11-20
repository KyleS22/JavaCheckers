package javacheckers.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.util.Optional;

public class MainController {

    public String username;

    public void handleJoinGameButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/join_game_menu.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 300, 275));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Username");
            dialog.setHeaderText("Enter the username you would like to use:");

            // TODO: Set the cancel button to return to the main menu

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                username=result.get();
                // TODO: Validate the name
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
    }



    public void handleHostGameButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/host_game_menu.fxml"));
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

    public void handleQuitButtonAction(javafx.event.ActionEvent actionEvent) {

        final Node source = (Node) actionEvent.getSource();
        final Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

    }
}
