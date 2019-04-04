package javacheckers.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;

public class MainController {

    // The user's chosen name
    public String username;

    /**
     * Response to pressing the join game menu button.  Opens the join game menu and prompts the user for a name.
     * @param actionEvent -> The click event triggering the button
     */
    public void handleJoinGameButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            // Load the join game menu
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/join_game_menu.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 300, 275));


            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            // Have the user enter their preferred user name
            showUsernameDialog(stage, actionEvent);

            JoinGameMenuController controller = fxmlLoader.getController();
            controller.setUserName(this.username);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Response to clicking the host game menu button.  Opens the menu for hosting a new game and prompts the user
     * for a user name.
     * @param actionEvent -> The click event triggering the button
     */
    public void handleHostGameButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            // Load the host game menu
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/game.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1, 800, 800));

            stage.show();

            final Node source = (Node) actionEvent.getSource();
            final Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();

            // Have the user enter their preferred user name
            showUsernameDialog(stage, actionEvent);

            GameController controller = fxmlLoader.getController();
            controller.startHost();
            controller.startClient("localhost");
            controller.setUsername(username);

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
     * Response to clicking the quit button.  Exits the game.
     * @param actionEvent -> The click event triggering the button
     */
    public void handleQuitButtonAction(javafx.event.ActionEvent actionEvent) {

        final Node source = (Node) actionEvent.getSource();
        final Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

    }

    /**
     * Close the current window and open the main menu window.
     * @param actionEvent -> The click event
     * @throws IOException
     */
    private void returnToMainMenu(ActionEvent actionEvent) throws IOException {
        FXMLLoader menuFxmlLoader = new FXMLLoader(getClass().getResource("../view/main_menu.fxml"));
        Parent menuRoot = menuFxmlLoader.load();
        Stage menuStage = new Stage();
        menuStage.setScene(new Scene(menuRoot, 300, 275));
        menuStage.show();

        final Node menuSource = (Node) actionEvent.getSource();
        final Stage menuOldStage = (Stage) menuSource.getScene().getWindow();
        menuOldStage.close();
    }

    /**
     * Display a pop-up diaogue that prompts the user for a name. It will return to the main menu if cancelled or closed
     * without a valid name.
     * @param nextStage -> The stage to open after the user has finished with the dialogue
     * @param actionEvent -> The event that triggered the button
     */
    private void showUsernameDialog(Stage nextStage, ActionEvent actionEvent){
        Dialog<String> dialog = new Dialog();
        ButtonType oKButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(oKButtonType, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Username");

        gridPane.add(nameField, 0, 0);

        dialog.getDialogPane().setContent(gridPane);
        dialog.setTitle("Enter a username");

        // Request focus on the username field by default.
        Platform.runLater(() -> nameField.requestFocus());

        // Get the OK button
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(oKButtonType);

        // Set the OK button to validate the user name before allowing the user to continue
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event -> {

                    ValidationResult validation = validateUserName(nameField.getText());
                    if(!validation.isSuccess()){
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, validation.getMessage(), ButtonType.OK);
                        alert.showAndWait();

                        event.consume();
                    }
                }
        );

        // Get the resulting name
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == oKButtonType) {
                return nameField.getText();
            }else if (dialogButton == ButtonType.CANCEL){
                return null;
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            username = result.get();

            nextStage.show();

        }else{
            try {
                returnToMainMenu(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Check that the given user name is valid.
     * @param name -> The user name to validate
     * @return A ValidationResult that contains info on whether the name is valid and a message explaining why the
     * validation failed or suceeded.
     */
    private ValidationResult validateUserName(String name){

        // No empty names
        if(name == null){
            return new ValidationResult(false, "Name cannot be empty.");
        }

        // Name must have some characters.  (This number was chosen randomly)
        if(name.length() < 4){
            return new ValidationResult(false,"Name must contain at least 4 characters.");
        }

        // No spaces
        if(name.contains(" ")){
            return new ValidationResult(false, "Name must not contain spaces.");
        }


        return new ValidationResult(true, "Good Name!");
    }

    /**
     * The result of a validation
     */
    private class ValidationResult {

        // Whether the validation was a success
        private boolean success;

        // A message to explain the success or failure
        private String message;


        /**
         * Create a new validation result
         * @param success -> True if the validation was a success, false otherwise
         * @param message -> A descriptive message that explains the success or failure
         */
        public ValidationResult(boolean success, String message){
            this.success = success;
            this.message = message;
        }

        /**
         * Get the message
         * @return  A string message describing the success or failure
         */
        public String getMessage() {
            return message;
        }

        /**
         * Set the message
         * @param message -> The message for the success or failure of validation
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Was the validation successful?
         * @return True if the validation was successful, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Set the success of the validation
         * @param success -> True if the validation was a success, false otherwise
         */
        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
