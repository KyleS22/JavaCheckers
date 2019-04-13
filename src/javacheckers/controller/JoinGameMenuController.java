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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JoinGameMenuController {

    private String username;    // The current user's username
    private BroadcastReceiver receiver; // The broadcast receiver to use to receive active hosts

    @FXML
    private ListView availableHosts;    // A list view for displaying active hosts
    private ObservableList<String> hostList;    // An observable list for storing active host names

    /**
     * Called when the user selects the back button
     * @param actionEvent The action taken
     */
    public void handleBackButtonAction(javafx.event.ActionEvent actionEvent){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javacheckers/view/main_menu.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 300, 275));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            this.receiver.shutdown();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the user selects the join button
     * @param actionEvent The action taken
     */
    public void handleJoinButtonAction(javafx.event.ActionEvent actionEvent){

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javacheckers/view/game.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 800, 800));
            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            GameController controller = fxmlLoader.getController();
            controller.setUsername(this.username);

            String hostUser = (String) availableHosts.getSelectionModel().getSelectedItem();

            String hostIP = getIPFromUsername(hostUser);

            if(hostIP == null){
                showInvalidHostDialog();
            }

            this.receiver.shutdown();

            controller.startClient(hostIP);

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

    /**
     * Displays an error box if the selected host is not valid for some reason
     */
    public void showInvalidHostDialog(){


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Host");
        alert.setHeaderText(null);
        alert.setContentText("The host you selected is no longer available.");

    }


    /**
     * Get the matching IP address for the selected username
     * @param username The username to get the IP for
     * @return The IP address of the host matching the username
     */
    private String getIPFromUsername(String username){
        for(Map<String, Object> m : this.receiver.getActiveHosts()){
            if(m.get("username").equals(username)){
                return (String) m.get("IP");
            }
        }

        return null;
    }

    /**
     * Set this user's username
     * @param name The name to use
     */
    public void setUserName(String name){
        username = name;
    }

    /**
     * Start the broadcast receiver to start detecting active hosts
     */
    public void startBroadcastReceiver(){
        this.receiver = new BroadcastReceiver(this);
    }


    /**
     * Initialize the list view
     */
    public void initialize(){
        this.startBroadcastReceiver();
        hostList = FXCollections.observableArrayList();
        hostList.addAll(getUsernamesFromActiveHosts());

        availableHosts.setItems(hostList);

    }

    /**
     * Get a list of usernames from the list of active hosts found by the broadcast receiver
     * @return A list of username strings for the currently active hosts
     */
    private List<String> getUsernamesFromActiveHosts(){

        List<String> usernames = new ArrayList<>();

        for(Map<String, Object> m : receiver.getActiveHosts()){
            usernames.add((String) m.get("username"));
        }

        return usernames;
    }

    /**
     * Update the list of host usernames being displayed with a new username
     * @param username The username to add to the list
     */
    public void updateHostList(String username){
        hostList.add(username);
    }

    /**
     * Remove a host username from the currently displayed list of hosts
     * @param username The username to remove
     */
    public void removeHost(String username){
        hostList.remove(username);
    }
}
