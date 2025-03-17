package com.example.guiex1.controller;

//import com.example.guiex1.Message;
import com.example.guiex1.Enum.Status;
import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
        import com.example.guiex1.utils.events.Event;
        import com.example.guiex1.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<Event> {

    UtilizatorService service;
    Utilizator utilizator1;
    Utilizator utilizator2;
    ObservableList<Message> model = FXCollections.observableArrayList();

    Message selectedMessage;

    static Message message;
    Stage dialogStage;
    @FXML
    TextField send;
    @FXML
    VBox chatBox;
    @FXML
    ScrollPane scrollPane;
    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> Username;
    @FXML
    Label from;
    @FXML
    Label to;

    Boolean first = true;

    public void setChatService(UtilizatorService service , Stage stage , Utilizator utilizator1 , Utilizator utilizator2) {
        this.service = service;
        this.dialogStage = stage;
        this.utilizator1 = utilizator1;
        this.utilizator2 = utilizator2;
        service.addObserver(this);
        initModel();
    }


    private void initModel() {

        from.setText(utilizator1.getUsername());
        to.setText(utilizator2.getUsername());
        model.addAll(service.getAllMessages(utilizator1, utilizator2));

        updateChatBox();
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }


    @Override
    public void update(Event utilizatorEntityChangeEvent) {

        for (int i = 0; i < message.getTo().size(); i++)
            if (!model.contains(message) && (message.getTo().get(i).equals(this.utilizator2) && message.getFrom().equals(this.utilizator1) ||
                    (message.getTo().get(i).equals(this.utilizator1) && message.getFrom().equals(this.utilizator2)))) {
                System.out.println(message.getTo().get(i));
                model.add(message);
            }
        updateChatBox();
        Platform.runLater(() -> {
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }

    public void handleSendMssage(ActionEvent actionEvent) {

        String text = send.getText().trim();

        if (!text.isEmpty() && utilizator2 != null) {
            if (selectedMessage != null) {
                message = new Message(utilizator1, List.of(utilizator2), text, LocalDateTime.now(), selectedMessage);
                selectedMessage = null;
            } else {
                message = new Message(utilizator1, List.of(utilizator2), text , LocalDateTime.now(), null);
            }

            service.addMessage(message);
            Platform.runLater(() -> {
                scrollPane.layout();
                scrollPane.setVvalue(1.0);
            });
            send.clear();
        }

    }

    private void updateChatBox() {
        if(verify() || first) {
            System.out.println();
            chatBox.getChildren().clear();
            for (Message message1 : model) {

                Label messageLabel = new Label(message1.toString());
                messageLabel.setWrapText(true);
                messageLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
                messageLabel.setMaxWidth(600);


                HBox messageBox = new HBox(messageLabel);
                messageBox.setPadding(new Insets(5));

                messageBox.setOnMouseClicked(event -> {
                    selectedMessage = message1;
                    for (Node node : chatBox.getChildren()) {
                        if (node instanceof HBox) {
                            node.setStyle("-fx-background-color: #ffffff; -fx-padding: 5; -fx-border-radius: 50; -fx-background-radius: 5;");
                        }
                    }

                    messageBox.setStyle("-fx-background-color : #469cb9 ; -fx-padding: 10px ; -fx-background-radius: 15px; ");
                });

                if (message1.getFrom().getUsername().equals(utilizator1.getUsername())) {
                    messageBox.setAlignment(Pos.BASELINE_RIGHT);
                    messageLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
                } else {
                    messageBox.setAlignment(Pos.BASELINE_LEFT);
                }

                chatBox.getChildren().add(messageBox);
                first = false;
            }
        }
    }


    private boolean verify(){
        int ok1 = 0;
        int ok2 = 0;
        for (Message message : model) {
            if (message.getFrom().getUsername().equals(utilizator1.getUsername()))
                ok1++;
            if (message.getTo().get(0).getUsername().equals(utilizator2.getUsername()))
                ok2++;
        }
        if(ok1 >= 1 && ok2 >= 1) {
            return true;
        }
        return false;
    }

    /*public void actionShare(ActionEvent actionEvent) {
        ObservableList<Utilizator> selectedItems = tableView.getSelectionModel().getSelectedItems();


    }*/

//    private void clickedMessage(){
//            messageBox.setOnMouseClicked(event -> {
//            for (Node node : chatBox.getChildren()) {
//                if (node instanceof HBox) {
//                    node.setStyle("-fx-background-color: #ffffff; -fx-padding: 5; -fx-border-radius: 5; -fx-background-radius: 5;");
//                }
//            }
//
//            // Schimbăm culoarea doar pentru HBox-ul apăsat
//            messageBox.setStyle("-fx-padding: 5px; -fx-border-color: #78E144FF; -fx-border-width: 1; -fx-background-color: #78E144FF; -fx-border-radius: 50px;");
//            //messageBox.setStyle("-fx-pref-height: 30px; -fx-background-color: lightblue; -fx-border-radius: 15px; -fx-padding: 5px;");
//            System.out.println("Ai selectat: " + message);
//        });
//    }
}
