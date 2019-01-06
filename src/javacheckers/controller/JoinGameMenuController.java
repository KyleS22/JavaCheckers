package javacheckers.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JoinGameMenuController {

    private String username;

    public void handleBackButtonAction(javafx.event.ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/main_menu.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 300, 275));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void handleJoinButtonAction(javafx.event.ActionEvent actionEvent){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/game.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 800, 800));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            GameController controller = fxmlLoader.getController();
            controller.setUsername(this.username);

            controller.startClient("localhost");

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.close();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }


    }

    public void setUserName(String name){
        username = name;
    }

}
