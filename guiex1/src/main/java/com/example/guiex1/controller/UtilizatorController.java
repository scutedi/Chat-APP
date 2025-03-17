package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.Hashing;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.example.guiex1.utils.Hashing.checkPassword;
import static com.example.guiex1.utils.Hashing.hashPassword;

public class UtilizatorController implements Observer<Event> {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();


    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TextField UserName;
    @FXML
    PasswordField Password;


    public void setUtilizatorService(UtilizatorService service) {
        this.service = service;
        Password.setPromptText("Introdu parola");
        service.addObserver(this);
        initModel();


    }

    @FXML
    public void initialize() {
        /*tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableView.setItems(model);*/
    }

    private void initModel() {
        /*Iterable<Utilizator> messages = service.getAll();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);*/
    }

    @Override
    public void update(Event utilizatorEntityChangeEvent) {
        initModel();
    }

    public void handleAddUtilizator(ActionEvent actionEvent) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/add-user-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Register");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddUserController addUserController = loader.getController();
            addUserController.setService(service, dialogStage);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteUtilizator(ActionEvent actionEvent) {
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            Utilizator deleted= service.deleteUtilizator(user.getId());
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Delete user","Userul a fost sters");
        }
        else MessageAlert.showErrorMessage(null, "NU ati selectat nici un utilizator");
    }

    public void handleUpdateUtilizator(ActionEvent actionEvent) {
        // TODO
        Utilizator selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showMessageTaskEditDialog(selected);
        } else
            MessageAlert.showErrorMessage(null, "NU ati selectat nici un student");

    }

    public void showMessageTaskEditDialog(Utilizator user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/edit-user-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, user);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLoginUtilizator(ActionEvent actionEvent) throws Exception {

        String userName = UserName.getText();
        String password = Password.getText();
        String checkPassword = hashPassword(password);
        Utilizator user = service.searchUserName(userName);
        if(password == null || userName == null || user == null) {
            MessageAlert.showErrorMessage(null, "Ti ai gresit parola sau username ul!!");
        }
        else if (Objects.equals(user.getPassword(), checkPassword))
        {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/prieten-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Prieteni");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            PrietenieController friendController = loader.getController();
            friendController.setService(service, dialogStage, user);

            dialogStage.show();
        }

    }


    public void handleUsers(ActionEvent actionEvent){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/password-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Password");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UsersController usersController = loader.getController();
            usersController.setUsersService(service , dialogStage);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
