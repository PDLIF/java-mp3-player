package com.example.sampleproject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.random.RandomGenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GameWindowController {

    LinkedList<CardController> commonCards = new LinkedList<CardController>();
    LinkedList<CardController> attackCards = new LinkedList<CardController>();
    LinkedList<CardController> defendCards = new LinkedList<CardController>();
    boolean isAttack = true;


    @FXML
    private Button addCardButton;

    @FXML
    private GridPane deskAttackCardPane;

    @FXML
    private GridPane deskAnswerCardPane;
    
    @FXML
    private ScrollPane firstPlayerScroll;

    @FXML
    private ScrollPane secondPlayerScroll;

    @FXML
    private FlowPane firstPlayerPane;

    @FXML
    private FlowPane secondPlayerPane;

    @FXML
    void addCard(ActionEvent event) throws IOException, InterruptedException {

        


        String[] Masks = {"Черви","Бубны","Пики","Крести"};
        for (String mask : Masks){
            for (int nominal=6;nominal<15;nominal++){

                commonCards.add(createCard( Integer.toString(nominal),mask) );

            }
        }
        for (CardController card: commonCards
             ) {
            switch (card.getNominal()) {
                case "11":{
                    card.setNominal("Валет");
                    break;
                }
                case "12":{
                    card.setNominal("Дама");
                    break;
                }
                case "13":{
                    card.setNominal("Король");
                    break;
                }
                case "14":{
                    card.setNominal("Туз");
                    break;
                }



            }
        }
        addCardButton.setVisible(false);




    }
    CardController createCard(String nominal, String mask) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Card.fxml"));
        Pane newPane = (Pane)loader.load();
        CardController cardController = loader.getController();
        cardController.setCardParameters(nominal, mask, this, newPane);


        return  cardController;
    }

    public void giveCards() throws IOException {
        int cardsCount = 32;
        Random random = new Random();
        firstPlayerScroll = new ScrollPane();
        secondPlayerScroll = new ScrollPane();
        for (int i=1; i<7;i++) {


            CardController card = commonCards.get(random.nextInt(1,cardsCount));
            commonCards.remove(card);
            cardsCount--;
            firstPlayerPane.getChildren().add(card.cardPane);

            card = commonCards.get(random.nextInt(1,cardsCount));
            commonCards.remove(card);
            cardsCount--;
            secondPlayerPane.getChildren().add(card.cardPane);


        }
        firstPlayerScroll.setContent(firstPlayerPane);
        secondPlayerScroll.setContent(secondPlayerPane);
        secondPlayerPane.setDisable(true);
        secondPlayerPane.setOpacity(0.5);
    }

    // При нажатии карты, кладет ее на стол.
    public void addCardOnTable(CardController card) throws IOException {
    	FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Card.fxml"));
        
    	Pane newPane = (Pane)loader.load();
    	card.setActive(false);
    	CardController cardController = loader.getController();
    	cardController.setCardParameters(card.getNominal(), card.getMask(), this, newPane);

        if (isAttack){
            attackCards.add(card);
            deskAttackCardPane.add(newPane, deskAttackCardPane.getChildren().size(), 0);
            firstPlayerPane.setDisable(true);
            firstPlayerPane.setOpacity(0.5);
            secondPlayerPane.setDisable(false);
            secondPlayerPane.setOpacity(1);
        }
        else {
            LinkedList<String> usedNominals =  new LinkedList<String>();
            for (CardController c: attackCards
                 ) {
                if (!usedNominals.contains(c.getNominal())) {
                    usedNominals.add(c.getNominal());
                }
            }


            defendCards.add(card);
            deskAnswerCardPane.add(newPane, deskAnswerCardPane.getChildren().size(), 0);
            secondPlayerPane.setDisable(true);
            secondPlayerPane.setOpacity(0.5);
            firstPlayerPane.setDisable(false);
            firstPlayerPane.setOpacity(1);
        }
        isAttack=!isAttack;
    }



}
