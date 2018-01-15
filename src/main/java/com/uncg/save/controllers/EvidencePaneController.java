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

import com.uncg.save.models.EvidenceModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;

/**
 * Controller for the basic evidence pane. Meant to populate floating and
 * proposition evidence panes
 *
 */
public class EvidencePaneController implements Initializable {

    private boolean metaCollapsed = true;

    @FXML
    protected VBox superPane;
    @FXML
    protected TitledPane metadataTitledPane;

    protected ConstructionAreaController constControl;
    protected EvidenceChunkController chunkControl;

    @FXML
    protected Label textLabel;
    @FXML
    protected Label sourceLabel;
    @FXML
    protected Label dateLabel;
    @FXML
    protected Label genreLabel;
    @FXML
    protected Label reliabilityLabel;
    @FXML
    protected Label likelihoodLabel;
    @FXML
    protected Label commentLabel;

    protected EvidenceModel evidence;

    protected ContextMenu contextMenu;

    private MenuItem removeEvidence;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        superPane.getStyleClass().add("evidence-grid-pane");
        metadataTitledPane.getStyleClass().add("titled-pane");
        textLabel.getStyleClass().add("data-label");

        // create context menu for adding new premises
        contextMenu = new ContextMenu();
        setContextMenuItems();
        setContextMenuEventFilter();
    }

    /**
     * sets the view fields using the provided evidence
     *
     * @param evidence EvidenceModel
     */
    public void setEvidence(EvidenceModel evidence) {
        this.evidence = evidence;
        textLabel.setText(evidence.getText());
        sourceLabel.setText(evidence.getSource());
        dateLabel.setText((evidence.getDate()));
        genreLabel.setText(evidence.getGenre());
        reliabilityLabel.setText(evidence.getReliability());
        likelihoodLabel.setText(evidence.getLikelihood());
        commentLabel.setText(evidence.getComment());
    }

    /*
    Getters and setters for view data
     */
    public Label getTextLabel() {
        return textLabel;
    }

    public void setTextLabel(String dataText) {
        textLabel.setText(dataText);
    }

    public Label getSourceLabel() {
        return sourceLabel;
    }

    public void setSourceLabel(String dataSource) {
        sourceLabel.setText(dataSource);
    }

    public Label getDateLabel() {
        return dateLabel;
    }

    public void setDateLabel(String dataDate) {
        dateLabel.setText(dataDate);
    }

    public Label getGenreLabel() {
        return genreLabel;
    }

    public void setGenreLabel(String dataGenre) {
        genreLabel.setText(dataGenre);
    }

    public Label getReliabilityLabel() {
        return reliabilityLabel;
    }

    public void setReliabilityLabel(String dataReliability) {
        reliabilityLabel.setText(dataReliability);
    }

    public Label getLikelihoodLabel() {
        return likelihoodLabel;
    }

    public void setLikelihoodLabel(String dataLikelihood) {
        likelihoodLabel.setText(dataLikelihood);
    }

    public void setLikelihoodLabel(double dataLikelihood) {
        likelihoodLabel.setText(Double.toString(dataLikelihood));
    }

    public Label getCommentLabel() {
        return commentLabel;
    }

    public void setCommentLabel(String dataComment) {
        commentLabel.setText(dataComment);
    }

    /**
     * sets the chunk controller for this pane
     *
     * @param control EvidenceChunk - subclass depends on whether it is in a
     * proposition or floating in the construction area
     */
    public void setChunkControl(EvidenceChunkController control) {
        this.chunkControl = control;
    }

    /**
     * sets the controller for the construction area
     *
     * @param control ConstructionAreaController
     */
    public void setConstructionAreaController(
            ConstructionAreaController control) {
        constControl = control;
    }

    /**
     * Event handler for right clicks on the main grid pane
     */
    private void setContextMenuEventFilter() {
        superPane.addEventFilter(
                ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                (ContextMenuEvent event) -> {
                    showContextMenu(event);
                    event.consume();
                });

    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() {
        MenuItem deleteEvidence = new MenuItem("Delete Evidence");
        setHandlerForDelete(deleteEvidence);
        removeEvidence = new MenuItem("Remove From Chunk");
        setHandlerForRemove(removeEvidence);
 
        contextMenu.getItems().addAll(deleteEvidence,removeEvidence);
    }

    private void setHandlerForDelete(MenuItem item) {
        item.setOnAction(action -> {
            chunkControl.removeEvidence(superPane, evidence);
            action.consume();
        });
    }

    public void enableRemove(){
        removeEvidence.setDisable(false);
    }
    public void disableRemove(){
        removeEvidence.setDisable(true);
    }

    private void setHandlerForRemove(MenuItem item) {
        item.setOnAction(action -> {
            try {
                chunkControl.popEvidencePane(evidence);
                chunkControl.removeEvidence(superPane, evidence);
                action.consume();
            } catch (IOException ex) {
                Logger.getLogger(EvidencePaneController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
    }

    /**
     * shows the context menu using the X and Y of the event to determine
     * location
     *
     * @param event MouseEvent
     */
    private void showContextMenu(ContextMenuEvent event) {
        /*
        call to hide() ensures that bugs arent encountered if
        multiple context menus are opened back to back
         */
        contextMenu.hide();
        contextMenu.show(
                superPane, event.getScreenX(), event.getScreenY()
        );
        event.consume();
    }

    /**
     * hides the context menu
     */
    private void closeContextMenu(Event event) {
        contextMenu.hide();
        event.consume();
    }

    /**
     * OnClick method that expands/collapses the metadata
     */
    @FXML
    private void modifyMetaPane() {
        if (metaCollapsed) {
            expandMetaPane();
        } else {
            collapseMetaPane();
        }
    }

    public EvidenceModel getEvidence() {
        return this.evidence;
    }

    /**
     * expand metadata. Grows the parent grid container to accomidate the extra
     * information
     */
    private void expandMetaPane() {
        // expands the TitledPane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        metadataTitledPane.setExpanded(true);
        metaCollapsed = false;

        constControl.constructionAreaSizeCheck();
    }

    /**
     * collapse metadata. Shrinks the parent grid container to default size
     */
    private void collapseMetaPane() {
        // collapses the TitlePane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        metadataTitledPane.setExpanded(false);
        metaCollapsed = true;

        constControl.constructionAreaSizeCheck();
    }
}
