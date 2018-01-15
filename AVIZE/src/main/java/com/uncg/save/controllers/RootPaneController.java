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

import com.uncg.save.models.DataModel;
import com.uncg.save.models.SchemeModel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

/**
 * FXML controller for the main underlying pane of the application.
 * 
 */
public class RootPaneController implements Initializable {

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private TitleAndMenuBarController titleAndMenuBarController;
    @FXML
    private DataListController dataListController;
    @FXML
    private SchemeListController schemeListController;
    @FXML
    private ConstructionAreaController constructionAreaController;
    @FXML
    private AnchorPane dataAnchorPane;
    @FXML
    private AnchorPane schemesAnchorPane;
    @FXML
    private Button hideDataButton;
    @FXML
    private Button hideSchemesButton;

    private List<DataModel> dataModelList;

    private List<SchemeModel> schemeModelList;

    private boolean dataButtonHidden = true;
    private boolean dataUp = false;
    private boolean schemesUp = true;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private double screenWidth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hideDataButton.setDisable(true);
        
        javafx.stage.Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D rectangle2D  = screen.getVisualBounds();
        screenWidth = rectangle2D.getWidth();
        
        
        dataAnchorPane.setPrefWidth(0);
        schemesAnchorPane.setPrefWidth(350);
        hideDataButton.setMaxWidth(0);
        hideDataButton.setMinWidth(0);
        hideSchemesButton.setMaxWidth(25);
        hideSchemesButton.setMinWidth(25);
        mainScrollPane.setMaxWidth(screenWidth - dataAnchorPane.getPrefWidth() - schemesAnchorPane.getPrefWidth() - 25);
        mainScrollPane.setMinWidth(screenWidth - dataAnchorPane.getPrefWidth() - schemesAnchorPane.getPrefWidth() - 25);
        constructionAreaController.getMainPane().setMinWidth(mainScrollPane.getMaxWidth());
        titleAndMenuBarController.setParentController(this);
    }

    public void setDataModelList(List<DataModel> modelList) {
        this.dataModelList = modelList;
        dataListController.setDataModelElements(dataModelList);
    }
    public void replaceDataModelList(List<DataModel> modelList) {
        this.dataModelList = modelList;
        dataListController.replaceDataModelElements(dataModelList);
    }

    public void setSchemeModelList(List<SchemeModel> modelList) {
        this.schemeModelList = modelList;
        schemeListController.setSchemeModelElements(schemeModelList);
    }

    private void enableDataButton(){
        hideDataButton.setMaxWidth(25);
        hideDataButton.setMinWidth(25);
        mainScrollPane.setMaxWidth(mainScrollPane.getMaxWidth() - 25);
        mainScrollPane.setMinWidth(mainScrollPane.getMinWidth() - 25);
        hideDataButton.setDisable(false);     
        dataButtonHidden = false;
    }
    @FXML
    public void toggleData() {
        if(dataButtonHidden){enableDataButton();}
        int width = 350;
        if (!dataUp) {
            showData(width);
            dataUp = !dataUp;
        } else {
            hideData(width);
            dataUp = !dataUp;
        }
    }

    private void showData(int width) {
        dataAnchorPane.setMinWidth(width);
        dataAnchorPane.setMaxWidth(width);
        mainScrollPane.setMaxWidth(mainScrollPane.getMaxWidth() - width);
        mainScrollPane.setMinWidth(mainScrollPane.getMinWidth() - width);
        hideDataButton.setText("←←←←←←←←←←");
        constructionAreaController.getMainPane().setMinWidth(mainScrollPane.getMinWidth());
        constructionAreaController.getMainPane().setPrefWidth(constructionAreaController.getMainPane().getWidth() - width);
    }

    private void hideData(int width) {
        dataAnchorPane.setMinWidth(0);
        dataAnchorPane.setMaxWidth(0);
        mainScrollPane.setMaxWidth(mainScrollPane.getMaxWidth() + width);
        mainScrollPane.setMinWidth(mainScrollPane.getMinWidth() + width);
        hideDataButton.setText("→→→→→→→→→→");
        constructionAreaController.getMainPane().setMinWidth(mainScrollPane.getMinWidth());
        constructionAreaController.getMainPane().setPrefWidth(constructionAreaController.getMainPane().getWidth() + width);
    }

    @FXML
    public void toggleSchemes() {
        int width = 350;
        if (!schemesUp) {
            showSchemes(width);
            schemesUp = !schemesUp;
        } else {
            hideSchemes(width);
            schemesUp = !schemesUp;
        }
    }

    private void showSchemes(int width) {
        schemesAnchorPane.setMinWidth(width);
        schemesAnchorPane.setMaxWidth(width);
        mainScrollPane.setMaxWidth(mainScrollPane.getMaxWidth() - width);
        mainScrollPane.setMinWidth(mainScrollPane.getMinWidth() - width);
        hideSchemesButton.setText("→→→→→→→→→→");
        constructionAreaController.getMainPane().setMinWidth(mainScrollPane.getMinWidth());
        constructionAreaController.getMainPane().setPrefWidth(constructionAreaController.getMainPane().getWidth() - width);

    }
    
    private void hideSchemes(int width) {
        schemesAnchorPane.setMinWidth(0);
        schemesAnchorPane.setMaxWidth(0);
        mainScrollPane.setMaxWidth(mainScrollPane.getMaxWidth() + width);
        mainScrollPane.setMinWidth(mainScrollPane.getMinWidth() + width);
        hideSchemesButton.setText("←←←←←←←←←←");
        constructionAreaController.getMainPane().setMinWidth(mainScrollPane.getMinWidth());
        constructionAreaController.getMainPane().setPrefWidth(constructionAreaController.getMainPane().getWidth() + width);
    }
}
