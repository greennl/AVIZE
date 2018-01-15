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

import static com.uncg.save.MainApp.dataModelDataFormat;
import com.uncg.save.models.DataModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

public class DataPaneController implements Initializable {
    

    @FXML
    private VBox metadataVBox;
    @FXML
    private TitledPane metadataTitledPane;
    
    @FXML
    private VBox superVBox;
    @FXML
    private Label dataTextLabel;
    @FXML
    private Label dataSourceLabel;
    @FXML 
    private Label dataDateLabel;
    @FXML
    private Label dataGenreLabel;
    @FXML
    private Label dataReliabilityLabel;
    @FXML
    private Label dataLikelihoodLabel;
    @FXML
    private Label dataCommentLabel;

    private DataModel data;
    private boolean minimized = true;
    
    /**
     * Initializes the controller class.
     * 
     * Functions as a holder of an imported piece of evidence
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataTextLabel.setPrefWidth(335);
        dataTextLabel.setWrapText(true);
        metadataTitledPane.setExpanded(false);
        metadataTitledPane.getStyleClass().add("titled-pane");
        metadataVBox.getStyleClass().add("veritcal-box");
        dataTextLabel.getStyleClass().add("data-label");
    }    

    /*
    Getters and setters for view data
    */
    public Label getDataTextLabel() {
        return dataTextLabel;
    }
    public void setDataTextLabel(String dataText) {
        dataTextLabel.setText(dataText);
    }
    public Label getDataSourceLabel() {
        return dataSourceLabel;
    }
    public void setDataSourceLabel(String dataSource) {
        dataSourceLabel.setText(dataSource);
    }
    public void setDataDateLabel(String dataDate) {
        dataSourceLabel.setText(dataDate);
    }
    public Label getDataGenreLabel() {
        return dataGenreLabel;
    }
    public void setDataGenreLabel(String dataGenre) {
        dataGenreLabel.setText(dataGenre);
    }
    public Label getDataReliabilityLabel() {
        return dataReliabilityLabel;
    }
    public void setDataReliabilityLabel(String dataReliability) {
        dataReliabilityLabel.setText(dataReliability);
    }
    public Label getDataLikelihoodLabel() {
        return dataLikelihoodLabel;
    }
    public void setDataLikelihoodLabel(String dataLikelihood) {
        dataLikelihoodLabel.setText(dataLikelihood);
    }
    public void setDataLikelihoodLabel(double dataLikelihood) {
        dataLikelihoodLabel.setText(Double.toString(dataLikelihood));
    }
    public Label getDataCommentLabel() {
        return dataCommentLabel;
    }
    public void setDataCommentLabel(String dataComment) {
        dataCommentLabel.setText(dataComment);
    }
    
    /**
     * sets the view fields using the provided data
     * @param data DataModel
     */
    public void setData(DataModel data){
        this.data = data;
        dataTextLabel.setText(data.getDataText());
        dataSourceLabel.setText(data.getDataSource());
        dataDateLabel.setText((data.getDataDate()));
        dataGenreLabel.setText(data.getDataGenre());
        dataReliabilityLabel.setText(data.getDataReliability());
        dataLikelihoodLabel.setText(data.getDataLikelihood());
        dataCommentLabel.setText(data.getDataComment());
    }
    
        @FXML
    private void modifyMetaPane() {
        if (minimized) {
            expandMetaPane();
        } else {
            collapseMetaPane();
        }
    }

    
    private void expandMetaPane() {
        // expands the TitledPane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        metadataTitledPane.setExpanded(true);
        minimized = false;
    }

    /**
     * collapse metadata. Shrinks the parent grid container to default size
     */
    private void collapseMetaPane() {
        // collapses the TitlePane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        metadataTitledPane.setExpanded(false);
        minimized = true;
    }
    @FXML
    private void dragDetected(){
        Dragboard db = superVBox.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.put(dataModelDataFormat, this.data);
        db.setContent(content);
    }
    
    @FXML
    private void dragDone(DragEvent event){
        event.consume();
    }   
}
