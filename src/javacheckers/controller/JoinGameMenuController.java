package javacheckers.controller;

import javacheckers.networking.BroadcastReceiver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinGameMenuController {

    private String username;
    private BroadcastReceiver receiver;

    @FXML
    private ListView availableHosts;
    private ObservableList<String> hostList;

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

    public void startBroadcastReceiver(){
        this.receiver = new BroadcastReceiver(this);
    }


    public void initialize(){
        this.startBroadcastReceiver();
        hostList = FXCollections.observableArrayList();
        hostList.addAll(getUsernamesFromActiveHosts());

        availableHosts.setItems(hostList);

    }

    private List<String> getUsernamesFromActiveHosts(){

        List<String> usernames = new ArrayList<>();

        for(Map<String, Object> m : receiver.getActiveHosts()){
            usernames.add((String) m.get("username"));
        }

        return usernames;
    }

    public void updateHostList(String username){
        hostList.add(username);
    }

    public void removeHost(String username){
        hostList.remove(username);
    }
    // TODO: Update the UI with a list of the usernames received by receiver
}
