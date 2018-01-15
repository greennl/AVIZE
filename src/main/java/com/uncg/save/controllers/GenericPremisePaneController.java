/* 
 * Copyright 2017 Nancy Green
 * This file is part of AVIZE.
 *
 * AVIZE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AVIZE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AVIZE.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.uncg.save.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.uncg.save.models.SchemeModel;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Special FXML controller for the Generic Argument Scheme.
 * 
 */
public class GenericPremisePaneController implements Initializable {

    @FXML
    private Label numberLabel;
    @FXML
    private Button incButton;
    @FXML
    private Button decButton;
    @FXML
    private Button addCQButton;
    @FXML
    private Button removeCQButton;
    @FXML
    private TextArea addCQTextArea;
    @FXML
    private VBox cqVBox;

    private Stage stage;
    private Integer premiseNumber = 1;
    private List<String> addCQs;
    private SchemeModel scheme;
    private String toBeDeleted = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        decButton.setDisable(true);
    }

    public void setInitialScheme(SchemeModel s) {
        scheme = s;
        addCQs = s.getCriticalQs();
        updateLabel();
    }

    @FXML
    public void incNumPrem() {
        premiseNumber++;
        decButton.setDisable(false);
        numberLabel.setText(premiseNumber.toString());
    }

    @FXML
    public void decNumPrem() {
        if (premiseNumber > 1) {
            premiseNumber--;
        }
        if (premiseNumber == 1) {
            decButton.setDisable(true);
        }
        numberLabel.setText(premiseNumber.toString());
    }

    @FXML
    public void enter() {
        stage.close();
    }

    public void setStage(Stage s) {
        stage = s;
    }

    public int getPremiseNumber() {
        return (int) premiseNumber;
    }

    @FXML
    public void addCQ() {
        boolean exists = false;
        for(int i = 0;i<addCQs.size();i++){
            if(addCQs.get((i)).equals(addCQTextArea.getText())){
                exists = true;
            }
        }
        if (!addCQTextArea.getText().equals("") && !exists) {
            addCQs.add(addCQTextArea.getText());
            addCQTextArea.clear();          
            updateLabel();
        }
        else if(exists){
            addCQTextArea.setText("That CQ already exists");
        }
    }

    @FXML
    public void removeCQ() {
        for (int i = 0; i < addCQs.size(); i++) {
            if (addCQs.get(i).equals(toBeDeleted)) {
                addCQs.remove(i);
                cqVBox.getChildren().remove(i);
            }
        }
        addCQTextArea.setText("");
        removeCQButton.setDisable(true);
    }

    public List<String> getCQs() {
        return addCQs;
    }

    private void highlightCQ(String s) {
        if (!toBeDeleted.equals(s)) {
            toBeDeleted = s;
            removeCQButton.setDisable(false);
        } else {
            toBeDeleted = "";
            removeCQButton.setDisable(true);
        }
        addCQTextArea.setText(toBeDeleted);
    }

    private void updateLabel() {
        cqVBox.getChildren().clear();
        for (int i = 0; i < addCQs.size(); i++) {
            Label lbl = new Label(addCQs.get(i));           
            lbl.addEventFilter(MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    highlightCQ(lbl.getText());
                }
            });
            lbl.setFont(new Font("System Regular", 18));
            lbl.setWrapText(true);
            
            cqVBox.getChildren().add(lbl);
        }
    }
}
