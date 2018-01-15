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
import com.uncg.save.DataList;
import com.uncg.save.models.DataModel;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 * FXML controller for the title bar and menu bar of the application
 * 
 */
public class TitleAndMenuBarController implements Initializable {

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu loadMenu;

    private RootPaneController parentControl;
    private boolean dataLoaded = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setParentController(RootPaneController control) {
        parentControl = control;
    }

    @FXML
    private void closeProgram() {
        Stage primaryStage = (Stage) menuBar.getScene().getWindow();
        primaryStage.close();
    }

    @FXML
    private void loadXMLAppend(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        configureFileChooser(chooser);
        File loadedXML = chooser.showOpenDialog(menuBar.getScene().getWindow());
        if (loadedXML != null) {
            loadData(loadedXML);
        }

        event.consume();
    }

    private void setHandlerForReplaceData(MenuItem item) {
        item.setOnAction(action -> {
            loadXMLReplace(action);
        });
    }

    private void configureFileChooser(FileChooser chooser) {
        chooser.setTitle("Load data");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML", "*.xml")
        );
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    }

    private void loadData(File file) {
        List<DataModel> dataModelList = new ArrayList<>();
        try {
            DataList dl;
            SchemaFactory sf
                    = SchemaFactory
                            .newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema
                    = sf.newSchema(getClass().getResource("/xml/data.xsd"));

            JAXBContext jaxbContext = JAXBContext.newInstance(DataList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(schema);

            dl = (DataList) jaxbUnmarshaller.unmarshal(file);
            dataModelList = dl.getModels();
            if (!dataLoaded) {
                MenuItem replaceXML = new MenuItem("Replace Data");
                setHandlerForReplaceData(replaceXML);
                loadMenu.getItems().add(1, replaceXML);
                parentControl.toggleData();
            }
            dataLoaded = true;
        } catch (SAXException | JAXBException ex) {
            Logger.getLogger(TitleAndMenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }

        parentControl.setDataModelList(dataModelList);
    }

    private void loadXMLReplace(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        configureFileChooser(chooser);
        File loadedXML = chooser.showOpenDialog(menuBar.getScene().getWindow());
        if (loadedXML != null) {
            replaceData(loadedXML);
        }
        event.consume();
    }

    private void replaceData(File file) {
        List<DataModel> dataModelList = new ArrayList<>();
        try {
            DataList dl;
            SchemaFactory sf
                    = SchemaFactory
                            .newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema
                    = sf.newSchema(getClass().getResource("/xml/data.xsd"));

            JAXBContext jaxbContext = JAXBContext.newInstance(DataList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(schema);

            dl = (DataList) jaxbUnmarshaller.unmarshal(file);
            dataModelList = dl.getModels();
        } catch (SAXException | JAXBException ex) {
            Logger.getLogger(TitleAndMenuBarController.class.getName()).log(Level.SEVERE, null, ex);
        }

        parentControl.replaceDataModelList(dataModelList);
    }
}
