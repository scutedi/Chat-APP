package com.example.guiex1.controller;

//import com.example.guiex1.ChatBetweenTwoUsers;
import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.Page;
import com.example.guiex1.repository.dbrepo.Pageable;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PrietenieController implements Observer<Event> {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    Stage dialogStage;
    Utilizator utilizator;

    static Message message ;
    private int currentPage = 0;
    private int pageSize = 6;
    private int totalNumberOfElements = 0;


    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableColumn<Utilizator,String> FirstName;
    @FXML
    TableColumn<Utilizator,String> LastName;
    @FXML
    TableColumn<Utilizator,String> UserName1;
    @FXML
    TableColumn<Utilizator,String> Date;
    @FXML
    private Label pageNumber;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;

    @FXML
    Label username;
    @FXML
    TableColumn<Utilizator,String> UserName;
    @FXML
    TextField share;

    public void setService(UtilizatorService service, Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator = u;
        username.setText(this.utilizator.getFirstName() + " " + this.utilizator.getLastName());
        service.addObserver(this);
        initFriendshipsModelPage();
    }

    @Override
    public void update(Event utilizatorEntityChangeEvent) {
        if(Objects.equals(AddPrietenController.numeCerere, utilizator.getUsername())) {
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Cerere noua", "Ai primit o cerere noua!");
        }
        this.utilizator = service.searchUser(this.utilizator.getId());
        initFriendshipsModelPage();
    }

    private void initModel() {

        model.setAll(service.prieteniAcceptati(utilizator));
    }

    @FXML
    public void initialize() {
        FirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        LastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        UserName1.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        Date.setCellValueFactory(cellData-> {
                    Utilizator user = cellData.getValue();
                    Tuple<Long,Long> idF = new Tuple<>(utilizator.getId(),user.getId());
                    Prietenie friend = service.searchFriendship(idF);
                    return new SimpleStringProperty(friend.getDate().toString());
                }
        );
        tableView.setItems(model);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addDoubleClickEvent(tableView);
    }

    public void handleRemove(ActionEvent actionEvent){
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            Tuple<Long,Long> idF = new Tuple<>(this.utilizator.getId(), user.getId());
            Prietenie deleted= service.deleteFriendship(idF);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Delete Friendship","Userul a fost sters de la prieteni");
        }
        else MessageAlert.showErrorMessage(null, "NU ati selectat nici un prieten");
    }

    public void handleAdd(ActionEvent actionEvent){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/add-prieten-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddPrietenController AddprietenController = loader.getController();
            AddprietenController.setService(service, dialogStage, this.utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handlePage(ActionEvent actionEvent){
        try {
            // create a new stage for the popup dialog.

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/page-vew.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            PageCotroller pageController = loader.getController();
            pageController.setService(service, dialogStage, this.utilizator , this.utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleFriendPage(ActionEvent actionEvent){
        try {
            // create a new stage for the popup dialog.

            List<Utilizator> selectedItems = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/page-vew.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            PageCotroller pageController = loader.getController();
            pageController.setService(service, dialogStage, selectedItems.get(0), this.utilizator);

            dialogStage.show();


        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRequests(ActionEvent actionEvent){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/prieten-request-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            PrietenieRequestController prietenieRequestController = loader.getController();
            prietenieRequestController.setService(service, dialogStage, this.utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    private void addDoubleClickEvent(TableView<Utilizator> tableView) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Dublu click
                try {
                    Utilizator utilizator2 = tableView.getSelectionModel().getSelectedItem();
                    // create a new stage for the popup dialog.
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("../views/chat-view.fxml"));

                    AnchorPane root = (AnchorPane) loader.load();

                    // Create the dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Chat");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    //dialogStage.initOwner(primaryStage);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    ChatController chatController = loader.getController();
                    chatController.setChatService(service, dialogStage, this.utilizator , utilizator2);

                    dialogStage.show();

                } catch ( IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void actionShare(ActionEvent actionEvent) {
        List<Utilizator> selectedItems = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
        /*List<Utilizator> list = new ArrayList<>(selectedItems);*/

        String text = share.getText().trim();
        Message message = new Message(utilizator , selectedItems, text , LocalDateTime.now() , null);

        ChatController.message = message;
        service.addMessage(message);

    }

    private void initFriendshipsModelPage()
    {
        Page<Prietenie> page = service.getAllFriendshipsPage(utilizator, new Pageable(currentPage, pageSize));

        int maxPage = 1;
        if(currentPage > maxPage) {
            currentPage = maxPage;
            page = service.getAllFriendshipsPage(utilizator, new Pageable(currentPage, pageSize));
        }

        model.setAll(service.getPrieteni(utilizator, page));

        totalNumberOfElements = page.getTotalElementCount();

        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*pageSize >= totalNumberOfElements);

        pageNumber.setText("Page " + (currentPage+1) +"/" + (maxPage+1));
    }
    public void onPrevious2(ActionEvent actionEvent) {
        currentPage--;
        initFriendshipsModelPage();
    }

    public void onNext2(ActionEvent actionEvent) {
        currentPage++;
        initFriendshipsModelPage();
    }

}
