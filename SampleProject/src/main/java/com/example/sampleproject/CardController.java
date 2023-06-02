package com.example.sampleproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class CardController {
	Pane cardPane;

    private boolean active = true;

	GameWindowController gameWindow;

    @FXML
    private Text nominal;

    @FXML
    private Text mask;

    @FXML
    private ImageView imgBuffer;

    @FXML
    private Pane imgPane;

   // @FXML
   // private Text smth;

    public void setCardParameters(String nominal, String mask, GameWindowController gameWindow, Pane cardPane) throws FileNotFoundException {
    	this.nominal.setText(nominal);
    	this.mask.setText(mask);
    	this.gameWindow = gameWindow;
    	this.cardPane = cardPane;
    	
    	File img = new File("src\\main\\resources\\com\\example\\sampleproject\\EntrancePicture.jpg");
    	InputStream isImage = (InputStream) new FileInputStream(img);
    	imgBuffer = new ImageView(new Image(isImage));
    	imgBuffer.setFitWidth(100);
    	imgBuffer.setPreserveRatio(true);
    	//imgBuffer.setSmooth(true);
    	
    	//cardPane.getChildren().add(imgBuffer);
    	
    	//imgPane = new Pane();
    	imgPane.getChildren().setAll(imgBuffer);
    	
    }
    
    public String getNominal() {
    	return this.nominal.getText();
    }
    public void setNominal(String nominal) {
         this.nominal.setText(nominal);
    }
    
    public String getMask() {
    	return this.mask.getText();
    }

    public Pane getPlane(){
        return cardPane;
    }
    @FXML
    void replaceCardToTable(MouseEvent event) throws IOException {
        if (this.isActive()) {
            gameWindow.addCardOnTable(this);
            cardPane.setVisible(false);
            //FlowPane firstPlayerPane = (FlowPane) cardPane.getParent();
            //firstPlayerPane.getChildren().remove(cardPane);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
