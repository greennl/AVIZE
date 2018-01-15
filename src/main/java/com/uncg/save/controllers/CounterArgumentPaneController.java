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
import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.models.CounterArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.util.LayoutUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

public class CounterArgumentPaneController extends ConclusionPaneController implements Initializable {

    private int position;
    private CounterArgumentModel argument;
    private String text;
    private ArgumentNode connector;
    private boolean subFlag = false;

    /**
     * Controls the ultimate pane of an argument supporting a counter argument
     * 
     * Only applies to arguments, single panes controlled by CounterPropositionPaneControllers
     * 
     * Associated view node: CounterArgumentNode
     * @param url
     * @param rb 
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

    public void setConnection(ArgumentNode connector) {
        this.connector = connector;
    }

    public ArgumentNode getConnection() {
        return this.connector;
    }

    public void setArgNode(ArgumentNode argNode) {
        this.argNode = argNode;
    }

    public void setParentModelList(List<PremiseModel> modelList) {
        argument.setParentModelList(modelList);
    }

    public List<PremiseModel> getParentModelList() {
        return argument.getParentModelList();
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem addCounterArg = new MenuItem("Add Counter Argument");
        MenuItem detachChain = new MenuItem("Detach Argument Chain");
        MenuItem deleteCounterArgument = new MenuItem("Delete Counter Argument");
        
        setHandlerForToggle(toggleCert);
        setHandlerForDetach(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForCounterArg(addCounterArg);
        setHandlerForDetachChain(detachChain);
        setHandlerForDeleteCounterArgument(deleteCounterArgument);
        contextMenu.getItems().addAll(
                toggleCert, 
                detachProp, 
                deleteProp, 
                addCounterArg,
                detachChain,
                deleteCounterArgument
        );
    }

    private void setHandlerForToggle(MenuItem item) {
        item.setOnAction(action -> {
            certaintyControl.toggleVisible();
            action.consume();
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
            System.out.println(prop);
            if (prop != null) {
                System.out.println("found prop");
                removeProp();
            }
            action.consume();
        });
    }

    /*
    * Removes proposition from view and model
    */
    private void removeProp() {
        if (argument != null) {
            argument.removeConclusion();
        }
        prop = null;
        mainPane.getChildren().remove(propBox);
        contextMenu.getItems().clear();
        setContextMenuItems();
        deleteCommentPane();
    }
    
    /*
    * Allows a counter argument to be added to the pane
    */
    private void setHandlerForCounterArg(MenuItem item) {
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

    private void setHandlerForDetachChain(MenuItem item) {
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
    
    /*
    * Deletes the counter argument
    */
    private void setHandlerForDeleteCounterArgument(MenuItem item) {
        item.setOnAction(action -> {
            argTree.deleteCounterArgument(
                    this, 
                    argument.getParentModelList(), 
                    argument
            );
        });
    }

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

    public CounterArgumentModel getArgument() {
        return argument;
    }

    public void setArgumentModel(CounterArgumentModel arg) {
        argument = arg;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Handles a dragdropped event based on the content of event
     * @param event 
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

    /*
    * Extracts evidence on creation
    */
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
        deleteProp();
        mainPane.getChildren().add(propositionRectangle);
        addPropositionAsConclusion(prop);
        propBox = loadNewPropPane(prop);
        mainPane.getChildren().add(propBox);
        hasProp = true;
    }

    private void addPropositionAsConclusion(PropositionModel prop) {
        argument.setConclusion(prop);        
        this.prop = prop;
    }

    /**
     * Loads a new proposition within the pane
     * @param prop
     * @return
     * @throws IOException 
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
        System.out.println(parentControl);
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
        if (db.hasContent(propositionModelDataFormat)) {
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

    public void shiftToSub() {
        argTree.translateNode(argNode, 102, 0);
        argTree.translateNode(argNode, position, position);
    }
    
    public void addCounterArgumentToParentModels(CounterArgumentModel arg){
        for(PremiseModel model : argument.getParentModelList()) {
            model.addCounterArgument(arg);
        }
    }
    
    public void removeCounterArgumentFromParentModels(CounterArgumentModel arg){
        for(PremiseModel model : argument.getParentModelList()) {
            model.removeCounterArgument(arg);
        }
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
