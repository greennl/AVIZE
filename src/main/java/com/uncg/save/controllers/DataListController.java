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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DataListController implements Initializable {

    @FXML
    private VBox dataVBox;
    @FXML
    private ScrollPane mainScrollPane;

    private RootPaneController rootControl;

    private List<DataModel> dataModelElements;
    private List<Pane> dataViewElements;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double height = screenSize.getHeight();
    
    /**
     * Initializes the controller class.
     * 
     * Controls the list of evidence on the left side of the screen
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mainScrollPane.getStyleClass().add("side-pane");
        dataViewElements = new ArrayList<>();
        mainScrollPane.setPrefHeight(height - 33);
    }

    public void setRootControl(RootPaneController control) {
        rootControl = control;
    }

    /**
     *
     * @return list of data elements
     */
    public List<Pane> getDataElements() {
        return dataViewElements;
    }

    /**
     * Obtains a reference to the list of DataModel elements and generates views
     * for them
     *
     * @param dataElements
     */
    public void setDataModelElements(List<DataModel> dataElements) {
        if (dataModelElements != null) {
            for (int i = 0; i < dataElements.size(); i++) {
                this.dataModelElements.add(dataElements.get(i));
            }
        } else {
            dataModelElements = dataElements;
        }
        try {
            populateDataViews(dataElements);
        } catch (IOException ex) {
            Logger.getLogger(DataListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void replaceDataModelElements(List<DataModel> dataElements) {
        this.dataModelElements = dataElements;
        try {
            dataVBox.getChildren().clear();
            populateDataViews(dataElements);
        } catch (IOException ex) {
            Logger.getLogger(DataListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates views all data elements
     *
     * @throws IOException
     */
    private void populateDataViews(List<DataModel> dataElements) throws IOException {
        for (DataModel data : dataElements) {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/fxml/DataPane.fxml"));
            Pane dataPane = loader.load();
            DataPaneController dataPaneControl
                    = loader.<DataPaneController>getController();
            dataPane.getStyleClass().add("grid-pane");
            dataPaneControl.setData(data);
            dataViewElements.add(dataPane);
            dataVBox.getChildren().add(dataPane);
        }
    }
}
