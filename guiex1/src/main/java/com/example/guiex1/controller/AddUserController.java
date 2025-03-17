package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.security.Provider;

public class AddUserController {

    private UtilizatorService service;
    Stage stage;

    byte[] image;



    @FXML
    private TextField first_name_field;
    @FXML
    private TextField last_name_field;
    @FXML
    private TextField passField;
    @FXML
    private TextField username_field;

    public void setService(UtilizatorService service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    @FXML
    public void initialize(){
    }

    public void handleImage(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selectează o imagine");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {

            image = selectedFile.toURI().toString().getBytes();
        }
    }

    public void handleAdd(ActionEvent actionEvent) {
        String name = first_name_field.getText();
        String last_name = last_name_field.getText();
        String username = username_field.getText();
        String password = passField.getText();

        Utilizator user = new Utilizator(name, last_name , username, password , image);
        Utilizator u = service.searchName(name, last_name);

        if(u == null){
            service.addUtilizator(user);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Register");
            alert.setHeaderText("Cererea a fost trimisă!");
            alert.setContentText("Ti ai fct un cont cu succes!!");

            alert.showAndWait();
        }
        else{
            MessageAlert.showErrorMessage(null,"Utilizatorul nu este valid sau exista deja!");
        }
    }
}
