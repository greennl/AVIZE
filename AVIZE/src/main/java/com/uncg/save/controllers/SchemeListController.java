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

import com.uncg.save.models.SchemeModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML controller for the view element that contains the individual argument
 * scheme panes
 * 
 */
public class SchemeListController implements Initializable {

    @FXML
    private VBox schemeVBox;
    @FXML
    private ScrollPane mainScrollPane;
    
    private RootPaneController rootControl;
    
    private List<SchemeModel> schemeModelElements;
    private List<AnchorPane> schemeViewElements;
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double height = screenSize.getHeight();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        schemeVBox.getStyleClass().add("veritcal-box");
        mainScrollPane.getStyleClass().add("side-pane");
        mainScrollPane.setPrefHeight(height - 33);
        schemeViewElements = new ArrayList<>();
    }    
    
    public void setRootControl(RootPaneController control) {
        rootControl = control;
    }
    public ScrollPane getMainScrollPane(){
        return this.mainScrollPane;
    }
    public List<AnchorPane> getSchemeElements(){
        return schemeViewElements;
    }
    
    public void setSchemeModelElements(List<SchemeModel> schemeElements){
        this.schemeModelElements = schemeElements;
        try{
            populateSchemeView();
        } catch (IOException ex){
            Logger.getLogger(DataListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void populateSchemeView() throws IOException{
        for(SchemeModel scheme : schemeModelElements){
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/fxml/SchemePane.fxml"));
            AnchorPane schemePane = loader.load();     
            SchemePaneController schemePaneControl 
                    = loader.<SchemePaneController>getController();         
            schemePaneControl.setScheme(scheme);
            setSchemeModelViewElements(schemePaneControl, scheme);      
            schemePaneControl.setParentController(this);
            schemeViewElements.add(schemePane);
            schemeVBox.getChildren().add(schemePane);
        }
    }
    
    public void setSchemeModelViewElements(
            SchemePaneController control,
            SchemeModel model){
        control.setPremisesTextArea(premiseTextDisplayBuilder(model.getPremises()));
        control.setExampleTextArea(exampleTextDisplayBuilder(model.getExamples()));
        control.setCriticalQuestionsTextArea(cqTextDisplayBuilder(model.getCriticalQs()));     
        control.setTitle();
    }
    
    private String premiseTextDisplayBuilder(List<String> s){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < s.size()){
            int j = i + 1;
            sb.append("Premise " + j + ": ");
            sb.append("\n");
            sb.append(s.get(i));
            sb.append("\n");
            sb.append("\n");
            i++;
        }
        return sb.toString();
    }
    private String exampleTextDisplayBuilder(List<String> s){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < s.size()){
            if(i < s.size() - 1){
                int j = i + 1;
                sb.append("Premise " + j + ": ");
                sb.append("\n");
                sb.append(s.get(i));
                sb.append("\n");
                sb.append("\n");
                i++;
            }
            else{
                int j = i + 1;
                sb.append("Conclusion: ");
                sb.append("\n");
                sb.append(s.get(i));
                sb.append("\n");
                sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }
    
    private String cqTextDisplayBuilder(List<String> s){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < s.size()){
            int j = i + 1;
            sb.append(s.get(i));
            sb.append("\n");
            sb.append("\n");
            i++;
        }
        return sb.toString();
    }
}
