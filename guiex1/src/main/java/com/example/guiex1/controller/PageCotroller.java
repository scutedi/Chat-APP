package com.example.guiex1.controller;

import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.example.guiex1.utils.Hashing.hashPassword;

public class PageCotroller implements Observer<Event> {

    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    Stage dialogStage;
    Utilizator utilizator1;
    Utilizator utilizator2;

    @FXML
    Label Name;
    @FXML
    Label NumarPrieteni;
    @FXML
    ImageView image;


    public void setService(UtilizatorService service, Stage stage, Utilizator u1 , Utilizator u2) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator1 = u1;
        this.utilizator2 = u2;
        Name.setText(this.utilizator1.getFirstName() + " " + this.utilizator1.getLastName());
        service.addObserver(this);
        initModel();


    }

    private void initModel() {

        byte[] blobBytes = this.utilizator1.getCale_imagine();
        String text = new String(blobBytes);
        Image initialImage = new Image(text);
        image.setImage(initialImage);


        if(this.utilizator1 == this.utilizator2) {
            image.setOnMouseClicked(event -> {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Selectează o imagine");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );
                File selectedFile = fileChooser.showOpenDialog(dialogStage);

                if (selectedFile != null) {

                    Image newImage = new Image(selectedFile.toURI().toString());
                    byte[] textBytes = selectedFile.toURI().toString().getBytes();
                    Utilizator utilizator1 = new Utilizator(this.utilizator1.getFirstName(), this.utilizator1.getLastName(), this.utilizator1.getUsername(), this.utilizator1.getPassword(), textBytes);
                    utilizator1.setId(this.utilizator1.getId());
                    service.updateUtilizator(utilizator1);
                    image.setImage(newImage);
                }
            });
        }

        int nr = service.nrPrieteniAcceptati(this.utilizator1);
        NumarPrieteni.setText(String.valueOf(nr));
    }

    @Override
    public void update(Event event) {
        this.utilizator1 = service.searchUser(this.utilizator1.getId());
        initModel();
    }
}
