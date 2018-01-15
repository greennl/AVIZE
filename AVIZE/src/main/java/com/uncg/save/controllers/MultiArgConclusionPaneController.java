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

import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

/**
 * FXML controller for conclusion of 
 * multiple-arguments-sharing-the-same-conclusion structure when it is the root 
 * of an argument
 * 
 */
public class MultiArgConclusionPaneController extends ConclusionPaneController
        implements Initializable {

    private ArgumentNode argNode;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conclusionArgList = new ArrayList<>();
        mainPane.getStyleClass().add("conclusion-pane");
        propositionRectangle.getStyleClass().add("conclusion-rectangle");
        mainPane.addEventFilter(DragEvent.DRAG_DROPPED, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(propositionModelDataFormat) || db.hasString()) {
                try {
                    onDragDropped(event);
                    event.consume();
                } catch (IOException ex) {
                    Logger.getLogger(PremisePaneController.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        });

        mainPane.addEventFilter(MouseEvent.DRAG_DETECTED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.setDragDetect(true);
                dragDetected();
                event.consume();
            }
        });

        mainPane.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                (ContextMenuEvent event) -> {
                    showContextMenu(event);
                    event.consume();
                }
        );

        // create context menu for adding new premises
        contextMenu = new ContextMenu();
        setContextMenuItems();
    }

    @Override
    public void setArgNode(ArgumentNode argNode) {
        this.argNode = argNode;
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() {
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem counterArg = new MenuItem("Add a Counter Argument");
        MenuItem deleteArg = new MenuItem("Delete Argument");
        setHandlerForDetach(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForCounterArg(counterArg);
        setHandlerForDeleteArg(deleteArg);

        contextMenu.getItems().addAll(
                detachProp,
                deleteProp,
                counterArg,
                deleteArg
        );

    }

    private void setHandlerForCounterArg(MenuItem item) {
        item.setOnAction(action -> {
            try {
                List<PremiseModel> conclusionModels = new ArrayList<>();
                for(ArgumentModel arg : conclusionArgList){
                    conclusionModels.add(arg.getConclusion());
                }
                argTree.addCounterArgument(
                        conclusionModels,
                        argNode
                );
            } catch (IOException ex) {
                Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setHandlerForDetach(MenuItem item) {
        item.setOnAction(action -> {
            if (prop != null) {
                extractProp();
                removeProp();
            }
            action.consume();
        });
    }

    private void extractProp() {
        parentControl.createProp(prop, contextCoords);
    }

    private void setHandlerForDeleteProp(MenuItem delete) {
        delete.setOnAction(action -> {
            if (prop != null) {
                removeProp();
            }
            action.consume();
        });
    }

    private void removeProp() {
        for (ArgumentModel arg : conclusionArgList) {
            arg.removeConclusion();
        }
        prop = null;
        mainPane.getChildren().remove(propBox);
    }

    /**
     * sets the handler for deleting an argument tree
     *
     * @param item MenuItem
     */
    private void setHandlerForDeleteArg(MenuItem item) {
        item.setOnAction(action -> {
            argTree.deleteArgument();
            action.consume();
        });
    }

    private void onDragDropped(DragEvent event) throws IOException {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if (db.hasContent(propositionModelDataFormat)) {
            PropositionModel tProp
                    = (PropositionModel) db
                            .getContent(propositionModelDataFormat);

            if (!tProp.getSupport().isEmpty()) {
                extractEvidence(
                        tProp,
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
            }
            addProposition(tProp);
            dropped = true;
        } else if (db.hasString()) {
            String treeID = db.getString();
            if (!treeID.equals(argTree.getTreeID())) {
                argTree.addSupportToArgument(treeID, this);
                dropped = true;
            }
        }
        event.setDropCompleted(dropped);
        event.consume();
    }

    private void extractEvidence(PropositionModel prop, Point2D targetCoords) {
        parentControl.createEvidenceChunk(
                prop.getSupport(),
                targetCoords
        );
    }

    @Override
    public void addProposition(PropositionModel prop) throws IOException {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(propositionRectangle);
        addPropositionAsConclusion(prop);
        Pane tPropBox = loadNewPropPane(prop);
        mainPane.getChildren().add(tPropBox);
    }

    private void addPropositionAsConclusion(PropositionModel prop) {
        for (ArgumentModel arg : conclusionArgList) {
            arg.setConclusion(prop);
        }
    }

    private Pane loadNewPropPane(PropositionModel prop) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/PropositionBox.fxml"));
        Pane tPropBox = loader.load();
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        this.propBox = tPropBox;
        this.prop = prop;
        propControl.setCanHaveEvidence(false);
        propControl.setConstructionAreaControl(parentControl);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        propBoxC = propControl;
        return tPropBox;
    }

    private void dragDetected() {
        Dragboard db = mainPane.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.putString(argTree.getTreeID());
        db.setContent(content);
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
                mainPane, event.getScreenX(), event.getScreenY()
        );
        contextCoords = new Point2D(event.getScreenX(), event.getScreenY());
        event.consume();
    }

    /**
     * hides the context menu
     */
    private void closeContextMenu() {
        contextMenu.hide();
    }

    @Override
    public void addConclusionArgumentModel(ArgumentModel arg) {
        conclusionArgList.add(arg);
    }

    public void setPropositionModel(PropositionModel pm) {
        try {
            //Pane tPropBox = loadNewPropPane(pm);
            //mainPane.getChildren().add(tPropBox);
            addProposition(pm);
        } catch (IOException ex) {
            Logger.getLogger(PremisePaneController.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }
}
