package com.example.guiex1.controller;

import com.example.guiex1.Enum.Status;
import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddPrietenController implements Observer<Event> {
    @FXML
    private TextField textFirstName;
    @FXML
    private TextField textLastName;
    @FXML
    private TextField textUserName;
    @FXML
    private TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> tableColumnFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnLastName;
    @FXML
    TableColumn<Utilizator,String> tableColumnUserName;


    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private UtilizatorService service;
    Stage dialogStage;
    Utilizator utilizator;
    static String numeCerere;

    public void setService(UtilizatorService service,  Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
        this.service.addObserver(this);
        initModel();
        tableView.setOnMouseClicked(event -> {
            Utilizator selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) { // Verificare pentru a evita NullPointerException
                textFirstName.setText(selectedItem.getFirstName());
                textLastName.setText(selectedItem.getLastName());
                textUserName.setText(selectedItem.getUsername());
            }
        });
    }

    private void initModel() {

        model.setAll(service.filtruPrietenieNeadaugati(utilizator));
    }

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnUserName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableView.setItems(model);
    }

    @Override
    public void update(Event utilizatorEntityChangeEvent) {
        this.utilizator=service.searchUser(utilizator.getId());
        initModel();
    }

    @FXML
    public void handleAddF(ActionEvent actionEvent) {
        
        String usernameText= textUserName.getText();
        Utilizator friend = service.searchUserName(usernameText);
        try {
            if (friend == null) {
                MessageAlert.showErrorMessage(null, "Nu exista asa cineva");
            } else {
                Long id1 = utilizator.getId();
                Long id2 = friend.getId();
                numeCerere = friend.getUsername();
                service.addFriendship(new Prietenie(id1, id2));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmare");
                alert.setHeaderText("Cererea a fost trimisă!");
                alert.setContentText("Cererea ta a fost procesată cu succes.");

                alert.showAndWait();
            }
        }catch (ValidationException e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}
