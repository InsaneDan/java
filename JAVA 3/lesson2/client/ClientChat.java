package lesson2.client;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lesson2.client.controllers.AuthController;
import lesson2.client.controllers.ViewController;
import lesson2.client.models.ClientChatState;
import lesson2.client.models.Network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ClientChat extends Application {

    private ClientChatState state = ClientChatState.AUTHENTICATION;
    private Stage primaryStage;
    private Stage authDialogStage;
    private Network network;
    private ViewController viewController;

    public void updateUsers(List<String> users) {
        viewController.usersList.setItems(FXCollections.observableList(users));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientChat.class.getResource("view.fxml"));

        Parent root = loader.load();
        viewController = loader.getController();

        primaryStage.setTitle("Messenger Default Title");
        primaryStage.setScene(new Scene(root, 600, 400));
        viewController.getTextField().requestFocus();

        network = new Network(this);
        if (!network.connect()) {
            showNetworkError("", "Failed to connect to server", primaryStage);
        }

        viewController.setNetwork(network);
        viewController.setStage(primaryStage);

        network.waitMessages(viewController);

        primaryStage.setOnCloseRequest(event -> {
            network.close();
        });

        openAuthDialog();
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientChat.class.getResource("authDialog.fxml"));
        AnchorPane parent = loader.load();

        authDialogStage = new Stage();
        authDialogStage.initModality(Modality.WINDOW_MODAL);
        authDialogStage.initOwner(primaryStage);

        AuthController authController = loader.getController();
        authController.setNetwork(network);

        authDialogStage.setScene(new Scene(parent));
        authDialogStage.show();
    }

    public static void showNetworkError(String errorDetails, String errorTitle, Stage dialogStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.setTitle("Network Error");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ClientChatState getState() {
        return state;
    }

    public void activeChatDialog(String nickname) {
        primaryStage.setTitle(nickname);
        state = ClientChatState.CHAT;
        authDialogStage.close();

        // загружаем нужное количество сообщений, затем показываем primaryStage
        viewController.restoreChatHistory(100);

        primaryStage.show();
        viewController.getTextField().requestFocus();
    }

    public void changeNicknameTitle(String nickname) {
        primaryStage.setTitle(nickname);
    }
}