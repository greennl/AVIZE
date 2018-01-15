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
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

 /**
  * FXML controller for premise view components of an argument structure. 
  * 
  */
public class PremisePaneController implements Initializable {

    @FXML
    private Pane mainPane;
    @FXML
    private Rectangle propositionRectangle;

    private Label schemeLabel;
    private Pane propBox;
    private PropositionBoxController propBoxC;
    private PropositionModel prop;
    private int position;
    private ArgumentModel argument;
    private ArgumentViewTree argTree;
    private ArgumentNode argNode;
    private String text;
    private Point2D contextCoords;
    private ConstructionAreaController parentControl;
    private ArgumentCertaintyPaneController certaintyControl;
    private boolean hasProp = false;
    private boolean hasComment = false;
    private boolean visible = true;

    private ContextMenu contextMenu;

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
                            addPropositionAsPremise(prop);
                            propBox = loadNewPropPane(prop, true);
                            mainPane.getChildren().add(propBox);
                            propBoxC.setRedText();
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
        try {
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setArgNode(ArgumentNode argNode) {
        this.argNode = argNode;
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() throws IOException {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem counterArg = new MenuItem("Add Counter Argument");

        setHandlerForToggle(toggleCert);
        setHandlerForDetach(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForCounterArg(counterArg);
        contextMenu.getItems().addAll(toggleCert, detachProp, deleteProp, counterArg);
    }

    private void setHandlerForToggle(MenuItem item) {
        item.setOnAction(action -> {
            certaintyControl.toggleVisible();
            action.consume();
        });
    }

    public void certaintyVisibilityOff() {
        certaintyControl.killVisibility();
    }

    public void setViewColor(double d) {
        propositionRectangle.setFill(Color.rgb(redFunct(d), 0, blueFunct(d)));
        propBoxC.setColor(d, d);
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
        Bounds localBounds = mainPane.getBoundsInLocal();
        Bounds screenBounds = mainPane.localToScreen(localBounds);
        Point2D targetCoords = new Point2D(
                screenBounds.getMinX() + 50,
                screenBounds.getMinY() + 50
        );
        parentControl.createProp(prop, targetCoords);
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
        try {
            contextMenu.getItems().clear();
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (argument != null) {
            argument.removePremise(position);
        }
        prop = null;
        mainPane.getChildren().remove(propBox);
        mainPane.getChildren().add(schemeLabel);
        hasProp = false;
        propBoxC.deleteComment();
    }

    private void setHandlerForCounterArg(MenuItem item) {
        item.setOnAction(action -> {
            try {
                argTree.addCounterArgument(
                        argument.getPremise(position),
                        argNode
                );
            } catch (IOException ex) {
                Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private void setHandlerForComment(MenuItem item) throws IOException {
        item.setOnAction(action -> {
        });
    }

    public PropositionModel getProposition() {
        return prop;
    }

    public void setSchemeLabel(Label lbl) {
        this.schemeLabel = lbl;
        schemeLabel.setStyle("-fx-text-fill: red;");
        mainPane.getChildren().add(lbl);
    }

    public void setParentControl(ConstructionAreaController control) {
        parentControl = control;
    }

    public void setCertaintyController(ArgumentCertaintyPaneController control) {
        this.certaintyControl = control;
        certaintyControl.setPremControl(this);
    }
    
    public ArgumentCertaintyPaneController getCertaintyController() {
        return certaintyControl;
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

    public void setPropositionModel(PropositionModel pm) {
        Pane tPropBox;
        try {
            propBox = loadNewPropPane(pm);
            mainPane.getChildren().add(propBox);
        } catch (IOException ex) {
            Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        deleteProp();
        mainPane.getChildren().add(propositionRectangle);
        hasProp = true;
        addProposition(prop);
    }

    private void dropTree(DragEvent event) throws IOException {
        Dragboard db = event.getDragboard();
        String treeID = db.getString();
        argTree.mergeTree(treeID, this);
    }

    private void addProposition(PropositionModel prop) throws IOException {
        this.prop = prop;

        addPropositionAsPremise(prop);
        propBox = loadNewPropPane(prop);
        mainPane.getChildren().add(propBox);
    }

    private void addPropositionAsPremise(PropositionModel prop) {
        if (argument != null) {
            argument.addPremise(prop, position);
        }
    }

    private Pane loadNewPropPane(PropositionModel prop) throws IOException {
        try {
            contextMenu.getItems().clear();
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (propBoxC != null) {
            propBoxC.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/PropositionBox.fxml"));
        Pane tPropBox = loader.load();
        this.propBox = tPropBox;
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        propControl.setCanHaveEvidence(true);
        propControl.setConstructionAreaControl(parentControl);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        propControl.setContextMenu(contextMenu);
        this.propBoxC = propControl;
        hasProp = true;
        return tPropBox;
    }

    private Pane loadNewPropPane(PropositionModel prop, boolean click) throws IOException {//OVERLOADED SO TEXT CAN BE PUT IN ON CLICK SPAWN
        try {
            contextMenu.getItems().clear();
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (propBoxC != null) {
            propBoxC.deleteComment();
        }
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/PropositionBox.fxml"));
        Pane tPropBox = loader.load();
        this.propBox = tPropBox;
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        propControl.setCanHaveEvidence(true);
        propControl.setConstructionAreaControl(parentControl);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        propControl.setContextMenu(contextMenu);
        if (click) {
            propControl.setInitialText(schemeLabel.getText());
        }
        this.propBoxC = propControl;
        hasProp = true;
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

    private void deleteProp() {
        if (mainPane.getChildren().contains(propositionRectangle)) {
            mainPane.getChildren().remove(propositionRectangle);
        }
        if (mainPane.getChildren().contains(propBox)) {
            mainPane.getChildren().remove(propBox);
        }
        if (mainPane.getChildren().contains(schemeLabel)) {
            mainPane.getChildren().remove(schemeLabel);
        }
    }

    public void moveComment(double x, double y) {
        propBoxC.moveComment(x, y);
    }

    public void deleteCommentPane() {
        propBoxC.deleteComment();
    }
}
