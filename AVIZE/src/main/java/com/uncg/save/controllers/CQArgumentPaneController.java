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
import com.uncg.save.argumentviewtree.ArgumentSchemeLabel;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.LayoutUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class CQArgumentPaneController extends ConclusionPaneController implements Initializable {

    private int position;
    private ArgumentModel argument;
    private String text;
    private ArgumentNode connector;
    private ArgumentNode label;
    private ArgumentSchemeLabel parentSchemeLabel;

    private ArgumentModel parentArgument;

    /*
    * Controls the ultimate pane of an argument supporting a critical question
    *
    * Only exists as a conclusion to an argument, lone panes are CQPanes
    *
    * Associated view node: CQArgumentNode
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conclusionArgList = new ArrayList<>();
        mainPane.getStyleClass().add("premise-pane");
        propositionRectangle.getStyleClass().add("premise-rectangle");

        mainPane.addEventFilter(DragEvent.DRAG_DROPPED, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(propositionModelDataFormat) || db.hasString()) {
                onDragDropped(event);
            }
        });

        // intercept drag detected so props are not draggable
        mainPane.addEventFilter(MouseEvent.DRAG_DETECTED, (MouseEvent event) -> {
            event.consume();
        });

        // create context menu for adding new premises
        contextMenu = new ContextMenu();
        setContextMenuItems();
        setContextMenuEventFilter();
    }

    public void setArgNode(ArgumentNode argNode) {
        this.argNode = argNode;
    }
    
    /*
    * Sets the context menu items
    */
    private void setContextMenuItems() {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem addCounterArg = new MenuItem("Add Counter Argument");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem detachChain = new MenuItem("Detach Argument Chain");

        setHandlerForToggle(toggleCert);
        setHandlerForAddCounter(addCounterArg);
        setHandlerForDetachProp(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForDetachArg(detachChain);
        contextMenu.getItems().addAll(
                toggleCert,
                addCounterArg,
                detachProp,
                deleteProp,
                detachChain
        );
    }
    
    /*
    * Detaches the supporting argument
    */
    private void setHandlerForDetachArg(MenuItem item) {
        item.setOnAction(action -> {
            Point2D localCoords = LayoutUtils.getLocalCoords(
                    parentControl.getMainPane(),
                    contextCoords.getX(),
                    contextCoords.getY()
            );
            argTree.detachArgumentChain(this, localCoords);
            action.consume();
        });
    }

    private void setHandlerForToggle(MenuItem item) {
        item.setOnAction(action -> {
            certaintyControl.toggleVisible();
            action.consume();
        });
    }

    /*
    * Adds a counter argument attached to this pane
    */
    private void setHandlerForAddCounter(MenuItem item) {
        item.setOnAction(action -> {
            try {
                argTree.addCounterArgument(
                        argument.getConclusion(),
                        argNode
                );
            } catch (IOException ex) {
                Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /*
    * Removes the proposition from this pane and adds it to constructionarea
    */
    private void setHandlerForDetachProp(MenuItem item) {
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

    /*
    * Removes the proposition from the view and model
    */
    private void removeProp() {
        contextMenu.getItems().clear();
        setContextMenuItems();
        if (argument != null) {
            argument.removeConclusion();
        }
        prop = null;
        mainPane.getChildren().remove(propBox);
        hasProp = false;
        deleteCommentPane();
    }

    /**
     * add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter() {
        mainPane.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                (ContextMenuEvent event) -> {
                    showContextMenu(event);
                    event.consume();
                }
        );
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
    private void closeContextMenu(Event event) {
        contextMenu.hide();
        event.consume();
    }

    public PropositionModel getProposition() {
        return prop;
    }

    public void setParentControl(ConstructionAreaController control) {
        parentControl = control;
    }

    public Pane getMainPane() {
        return mainPane;
    }

    public ArgumentModel getArgument() {
        return argument;
    }

    public void setArgumentModel(ArgumentModel arg) {
        argument = arg;
    }

    /*
    * Sets the propositionmodel to the proposition added to this pane
    */
    public void setPropositionModel(PropositionModel pm) {
        Pane tPropBox;
        try {
            addProposition(pm);
        } catch (IOException ex) {
            Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setArgumentViewTree(ArgumentViewTree argTree) {
        this.argTree = argTree;
    }

    /*
    * Listens for dragdropped events on this pane and determines how to handle
    */
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if (db.hasContent(propositionModelDataFormat)) {
            PropositionModel prop
                    = (PropositionModel) db.getContent(propositionModelDataFormat);
            try {
                dropProp(event);
                if (!prop.getSupport().isEmpty()) {
                    extractEvidence(
                            prop,
                            new Point2D(event.getSceneX(), event.getSceneY())
                    );
                }
                dropped = true;
            } catch (IOException ex) {
                Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void dropProp(DragEvent event) throws IOException {
        Dragboard db = event.getDragboard();
        PropositionModel prop
                = (PropositionModel) db.getContent(propositionModelDataFormat);
        mainPane.getChildren().clear();
        mainPane.getChildren().add(propositionRectangle);
        addProposition(prop);
    }

    @Override
    public void addProposition(PropositionModel prop) throws IOException {
        this.prop = prop;
        System.out.println("im here");
        addPropositionAsConclusion(prop);
        Pane tPropBox = loadNewPropPane(prop);
        mainPane.getChildren().add(tPropBox);
        hasProp = true;
    }

    private void addPropositionAsPremise(PropositionModel prop) {
        if (argument != null) {
            argument.addPremise(prop, position);
        }
    }

    /*
    * Loads a new proposition within the pane
    */
    private Pane loadNewPropPane(PropositionModel prop) throws IOException {
        contextMenu.getItems().clear();
        setContextMenuItems();
        if (propBoxC != null) {
            propBoxC.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/PropositionBox.fxml"));
        Pane tPropBox = loader.load();
        this.propBox = tPropBox;
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        propControl.setCanHaveEvidence(false);
        propControl.setConstructionAreaControl(parentControl);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        propControl.setContextMenu(contextMenu);
        propBoxC = propControl;
        return tPropBox;
    }

    @FXML
    private void dragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasContent(propositionModelDataFormat) || db.hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String s) {
        this.text = s;
    }

    public void checkHasProp() {
        if (propBox != null) {
            hasProp = true;
        }
    }

    /*
    * Adds the proposition to the conclusion of the argumentmodel
    */
    private void addPropositionAsConclusion(PropositionModel prop) {
        if (argument != null) {
            System.out.println(argument);
            System.out.println(prop);
            argument.setConclusion(prop);
        }
    }
    
    public void setParentArgument(ArgumentModel parentArg){
        parentArgument = parentArg;
    }
    
    public ArgumentModel getParentArgument(){
        return parentArgument;
    }
    
    
    public void addArgumentToParentCQArgList(ArgumentModel arg){
        parentArgument.addCQArgument(arg);
    }
    
    public void removeArgumentFromParentCQArgList(ArgumentModel arg){
        parentArgument.removeCQArgument(arg);
    }
    @Override
    public void moveComment(double x, double y) {
            propBoxC.moveComment(x, y);
    }

    @Override
    public void deleteCommentPane() {
        propBoxC.deleteComment();
    }

}
