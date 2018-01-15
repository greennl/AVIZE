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
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.CounterArgumentModel;
import com.uncg.save.models.PremiseModel;
import com.uncg.save.models.PropositionModel;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CounterPropositionPaneController extends ConclusionPaneController implements Initializable {

    private int position;
    private CounterArgumentModel argument;
    private String text;
    private ArgumentNode connector;
    private boolean subFlag = false;

    /**
     * Controls single proposition answers to counter arguments
     * 
     * Controls only single propositions, CounterArgumentPaneController manages
     * extended arguments
     * 
     * Associated view node: CounterPropositionNode
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
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
        
        mainPane.addEventFilter(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    try {
                        if (!hasProp) {
                            deleteProp();
                            mainPane.getChildren().add(propositionRectangle);
                            prop = new PropositionModel();
                            addPropositionAsConclusion(prop);
                            propBox = loadNewPropPane(prop);
                            mainPane.getChildren().add(propBox);
                            hasProp = true;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                event.consume();
            }
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
        MenuItem addCounterArg = new MenuItem("Add Counter Argument");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem deleteCounterArgument = new MenuItem("Delete Counter Argument");
        
        setHandlerForToggle(toggleCert);
        setHandlerForAddCounter(addCounterArg);
        setHandlerForDetach(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForDeleteCounterArgument(deleteCounterArgument);
        
        contextMenu.getItems().addAll(
                toggleCert,
                addCounterArg,
                detachProp, 
                deleteProp, 
                deleteCounterArgument
        );
    }

    private void setHandlerForToggle(MenuItem item) {
        item.setOnAction(action -> {
            certaintyControl.toggleVisible();
            action.consume();
        });
    }
    
    /*
    * Adds a counter argument to the pane
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
    
    //Colors the pane based on the certainty
    public void setViewColor(double d) {
        propositionRectangle.setFill(Color.rgb(redFunct(d), 0, blueFunct(d)));
    }

    private int redFunct(double d) {
        d = (1 - d) * 255;
        int i = (int) d;
        return i;
    }

    private int blueFunct(double d) {
        d = d * 255;
        int i = (int) d;
        return i;
    }
    
    /*
    * Detaches argument from the pane
    */
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

    /*
    * Removes proposition from view and model of this pane
    */
    private void removeProp() {
        contextMenu.getItems().clear();
        setContextMenuItems();
        if (argument != null) {
            argument.removeConclusion();
        }
        prop = null;
        hasProp = false;
        propBoxC.deleteComment();
        mainPane.getChildren().remove(propBox);
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

    public void setSchemeLabel(Label lbl) {
        this.schemeLabel = lbl;
        mainPane.getChildren().add(lbl);
    }

    public void setParentControl(ConstructionAreaController control) {
        parentControl = control;
    }

    public Pane getMainPane() {
        return mainPane;
    }

    public CounterArgumentModel getArgument() {
        return argument;
    }

    public void setArgumentModel(CounterArgumentModel arg) {
        argument = arg;
    }

    /*
    public PropositionModel getPropositionModel() {
        return propositionModel;
    }
     */
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setArgumentViewTree(ArgumentViewTree argTree) {
        this.argTree = argTree;
    }

    /**
     * Listens for dragdropped events and handles them
     * @param event 
     */
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if (db.hasContent(propositionModelDataFormat)) {
            try {
                dropProp(event);
                dropped = true;
            } catch (IOException ex) {
                Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasString()
                && !db.getString().equals(argTree.getTreeID())) {
            try {
                dropTree(event);
                dropped = true;
            } catch (IOException ex) {
                Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        event.setDropCompleted(dropped);
        event.consume();
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

        addPropositionAsConclusion(prop);
        Pane tPropBox = loadNewPropPane(prop);
        mainPane.getChildren().add(tPropBox);
    }

    private void addPropositionAsConclusion(PropositionModel prop) {
        if (argument != null) {
            argument.setConclusion(prop);
        }
    }

    public PropositionModel getProposition() {
        return this.prop;
    }

    /**
     * Creates a proposition within this pane
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
        this.propBox = tPropBox;
        this.prop = prop;
        propControl.setCanHaveEvidence(true);
        propControl.setConstructionAreaControl(parentControl);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        propControl.setContextMenu(contextMenu);
        this.propBoxC = propControl;
        hasProp = true;
        return tPropBox;
    }

    private void dropTree(DragEvent event) throws IOException {
        Dragboard db = event.getDragboard();
        String treeID = db.getString();
        argTree.mergeTree(treeID, this);
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

    public void shiftToSub() {
        argTree.translateNode(argNode, 102, 0);
        argTree.translateNode(argNode, position, position);
    }
}
