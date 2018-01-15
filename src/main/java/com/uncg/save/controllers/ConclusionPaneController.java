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
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.PropositionModel;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ConclusionPaneController implements Initializable {

    @FXML
    protected Pane mainPane;
    @FXML
    protected Rectangle propositionRectangle;

    protected Label schemeLabel;
    protected Pane propBox;
    protected PropositionBoxController propBoxC;
    protected ArgumentCertaintyPaneController certaintyControl;

    protected PropositionModel prop;
    protected List<ArgumentModel> conclusionArgList;
    protected ArgumentViewTree argTree;
    protected ArgumentNode argNode;

    protected ContextMenu contextMenu;
    protected Point2D contextCoords;
    protected boolean hasProp = false;
    private boolean hasComment = false;
    private boolean visible = true;

    protected ConstructionAreaController parentControl;

    /**
     * Controls the ultimate conclusion of an argument structure
     * 
     * Associated view node: ConclusionNode
     * @param url
     * @param rb 
     */
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
        }); // create context menu for adding new premises
        contextMenu = new ContextMenu();
        try {
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setArgNode(ArgumentNode node) {
        this.argNode = node;
    }

    //Sets the background color corresponding to certainty
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

    public void setParentControl(ConstructionAreaController control) {
        parentControl = control;
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() throws IOException {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem counterArg = new MenuItem("Add a Counter Argument");
        MenuItem deleteArg = new MenuItem("Delete Argument");

        setHandlerForToggle(toggleCert);
        setHandlerForDetach(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForCounterArg(counterArg);
        setHandlerForDeleteArg(deleteArg);

        contextMenu.getItems().addAll(
                toggleCert,
                detachProp,
                deleteProp,
                counterArg,
                deleteArg
        );
    }

    private void setHandlerForCounterArg(MenuItem item) {
        item.setOnAction(action -> {
            try {
                argTree.addCounterArgument(
                        conclusionArgList.get(0).getConclusion(),
                        argNode
                );
            } catch (IOException ex) {
                Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
            if (prop != null) {
                removeProp();
            }
            action.consume();
        });
    }

    /*
    * Removes a proposition from the view and model of this pane
    */
    private void removeProp() {
        try {
            contextMenu.getItems().clear();
            setContextMenuItems();
        } catch (IOException ex) {
            Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (ArgumentModel arg : conclusionArgList) {
            arg.removeConclusion();
        }
        prop = null;
        mainPane.getChildren().remove(propBox);
        mainPane.getChildren().add(schemeLabel);
        propBoxC.deleteComment();
        hasProp = false;
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

    public void setCertaintyController(
            ArgumentCertaintyPaneController control) {
        certaintyControl = control;
        certaintyControl.setConcControl(this);
    }

    public ArgumentCertaintyPaneController getCertaintyController() {
        return certaintyControl;
    }

    public PropositionModel getProposition() {
        return prop;
    }

    /**
     * Sets the associated argument label
     * @param lbl 
     */
    public void setSchemeLabel(Label lbl) {
        this.schemeLabel = lbl;
        schemeLabel.setStyle("-fx-text-fill: red;");
        mainPane.getChildren().add(schemeLabel);
    }

    public ArgumentModel getConclusionArgumentModel() {
        if (conclusionArgList.size() != 0) {
            return conclusionArgList.get(0);
        }
        return new ArgumentModel();
    }

    public void addConclusionArgumentModel(ArgumentModel arg) {
        conclusionArgList.clear();
        conclusionArgList.add(0, arg);
    }

    public void setArgumentViewTree(ArgumentViewTree argTree) {
        this.argTree = argTree;
    }

    public ArgumentViewTree getArgumentViewTree() {
        return this.argTree;
    }
    
    /**
     * Handles a dragdropped event according to the contents of event
     * @param event
     * @throws IOException 
     */
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
                argTree.createMultiArgBranch(treeID, this);
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

    public void addProposition(PropositionModel prop) throws IOException {
        deleteProp();
        mainPane.getChildren().add(propositionRectangle);
        addPropositionAsConclusion(prop);
        propBox = loadNewPropPane(prop);
        mainPane.getChildren().add(propBox);
        hasProp = true;
    }

    private void addPropositionAsConclusion(PropositionModel prop) {
        for (ArgumentModel arg : conclusionArgList) {
            arg.setConclusion(prop);
        }
    }

    /**
     * Loads a new proposition into the model and view based on prop
     * @param prop
     * @return
     * @throws IOException 
     */
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
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        this.propBox = tPropBox;
        this.prop = prop;
        propControl.setCanHaveEvidence(false);
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
        propControl.setCanHaveEvidence(false);
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
        if (db.hasContent(propositionModelDataFormat)) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        if (db.hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
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

    public Pane getMainPane() {
        return mainPane;
    }

    public List<ArgumentModel> getConclusionArgumentModelList() {
        return conclusionArgList;
    }

    public void removeArgument(ArgumentModel argument) {
        conclusionArgList.remove(argument);
    }

    /**
     * Functioned in place of .clear so certain children aren't always deleted
     */
    protected void deleteProp() {
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

    public void checkHasProp() {
        if (propBox != null) {
            hasProp = true;
        }
    }
}
