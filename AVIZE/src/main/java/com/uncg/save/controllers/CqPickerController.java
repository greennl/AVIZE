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
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CqPickerController implements Initializable {

    @FXML
    private Pane mainPane;

    @FXML
    private VBox criticalQuestionsVBox;

    private ArgumentModel argumentModel;

    private ArgumentViewTree avt;
    
    private ArgumentNode ASL;

    private String testString;

    /*
    * Controls pop up window that appears after selecting "Add critical question"
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        testString = "working";
    }

    public void setArgModel(ArgumentModel am) {
        this.argumentModel = am;
    }

    public void setAVT(ArgumentViewTree AVT) {
        this.avt = AVT;
    }
    
    public void setASL(ArgumentNode ASL){
        this.ASL = ASL;
    }
    public String getTest() {
        return this.testString;
    }

    /**
     * Gets the CQs of an argument and adds a button for each to the window
     * @param item 
     */
    public void populateCQs(MenuItem item) {
        for (int i = 0; i < argumentModel.getPatchNumCQs(); i++) {
            int j = i;
            Button cqBtn = new Button(argumentModel.getPatchCriticalQuestion(i));
            cqBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {                        
                        avt.addCriticalQuestion(argumentModel,ASL,j);
                        //item.setDisable(true);
                        Stage stage = (Stage) cqBtn.getScene().getWindow();
                        stage.close();
                    } catch (IOException ex) {
                        Logger.getLogger(CqPickerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            cqBtn.setFont(new Font("System Regular", 18));
            cqBtn.setMinWidth(350);
            cqBtn.setMaxWidth(350);          
            cqBtn.setWrapText(true);
            criticalQuestionsVBox.getChildren().add(cqBtn);
        }
    }
}
