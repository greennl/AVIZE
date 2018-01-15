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
import com.uncg.save.models.ArgumentModel;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class ChainPaneController extends ConclusionPaneController implements Initializable {

    protected ArgumentNode connector;
    protected ArgumentNode cqLabel;
    protected ArgumentSchemeLabel argSchemeLabel;

    protected int position;
    protected List<ArgumentModel> premiseArgList;

    protected boolean cqFlag = false;
    protected boolean counterFlag = false;
    protected boolean subFlag = false;
    protected boolean hasComment = false;
    protected boolean visible = true;

    /*
    * Functions as an interem conclusion for chained arguments
    *
    * Associated view node: ChainNode
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        premiseArgList = new ArrayList<>();
        conclusionArgList = new ArrayList<>();
        mainPane.getStyleClass().add("chain-pane");
        propositionRectangle.getStyleClass().add("chain-rectangle");

        mainPane.addEventFilter(DragEvent.DRAG_DROPPED, (DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(propositionModelDataFormat) || db.hasString()) {
                try {
                    onDragDropped(event);
                    event.consume();
                } catch (IOException ex) {
                    Logger.getLogger(PremisePaneController.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        });

        // intercept drag detected events so props are not draggable
        mainPane.addEventFilter(MouseEvent.DRAG_DETECTED,
                (MouseEvent event) -> {
                    event.consume();
                });

        mainPane.addEventFilter(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    try {
                        if (!hasProp) {
                            createEmptyProp();
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
            Logger.getLogger(ChainPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        setContextMenuEventFilter();
    }

    public void setConnector(ArgumentNode connection) {
        this.connector = connection;
    }

    public ArgumentNode getConnector() {
        return this.connector;
    }

    public void setCQLabel(ArgumentNode cqLabel) {
        this.cqLabel = cqLabel;
    }

    public ArgumentNode getCQLabel() {
        return this.cqLabel;
    }

    public void setArgSchemeLabel(ArgumentNode argLabel) {
        this.argSchemeLabel = (ArgumentSchemeLabel) argLabel;
    }

    public ArgumentNode getArgSchemeLabel() {
        return this.argSchemeLabel;
    }

    private void createEmptyProp() throws IOException {
        deleteProp();
        mainPane.getChildren().add(propositionRectangle);
        prop = new PropositionModel();
        addPropositionAsPremise(prop);
        Pane tPropBox = loadNewPropPane(prop);
        mainPane.getChildren().add(tPropBox);
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() throws IOException {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem detachProp = new MenuItem("Detach Proposition");
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem addCounterArg = new MenuItem("Add Counter Argument");
        MenuItem detatchChain = new MenuItem("Detach Argument Chain");

        setHandlerForToggle(toggleCert);
        setHandlerForDetachProp(detachProp);
        setHandlerForDeleteProp(deleteProp);
        setHandlerForCounterArg(addCounterArg);
        setHandlerForDetatchChain(detatchChain);
        contextMenu.getItems().addAll(
                toggleCert,
                detachProp,
                deleteProp,
                addCounterArg,
                detatchChain
        );
    }

    /*
    * Adds a counter argument attached to this pane
    */
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

    /*
    * Removes a proposition from this pane and places it in the constructionarea
    */
    private void setHandlerForDetachProp(MenuItem item) {
        item.setOnAction(action -> {
            if (prop != null) {
                extractProp();
                removeProp();
                propBoxC.deleteComment();
            }
            try {
                contextMenu.getItems().clear();
                setContextMenuItems();
            } catch (IOException ex) {
                Logger.getLogger(ConclusionPaneController.class.getName()).log(Level.SEVERE, null, ex);
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
                propBoxC.deleteComment();
            }
            action.consume();
        });
    }

    /*
    * Removes proposition from view and model
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

        for (ArgumentModel arg : premiseArgList) {
            arg.removePremise(prop);
        }
        propBoxC.deleteComment();
        prop = null;
        hasProp = false;
        mainPane.getChildren().remove(propBox);
    }

    /*
    * Detaches the subargument from the whole argument, starting with this pane
    * and extending to all its children
    */
    private void setHandlerForDetatchChain(MenuItem item) {
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

    public void addPremiseArgument(ArgumentModel arg) {
        premiseArgList.clear();
        premiseArgList.add(0, arg);
    }

    public ArgumentModel getPremiseArgument() {
        return premiseArgList.get(0);
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /*
    * Listens for dragdropped events on this pane and determines how to handle
    */
    private void onDragDropped(DragEvent event) throws IOException {
        Dragboard db = event.getDragboard();
        boolean dropped = false;
        if (db.hasContent(propositionModelDataFormat)) {
            PropositionModel tProp
                    = (PropositionModel) db.getContent(
                            propositionModelDataFormat);

            if (!tProp.getSupport().isEmpty()) {
                extractEvidence(
                        tProp,
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
            }
            addProposition(tProp);
            hasProp = true;
            dropped = true;
        } else if (db.hasString()) {
            String treeID = db.getString();
            if (!treeID.equals(argTree.getTreeID())) {
                argTree.createMultiArgChain(treeID, this);
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
        deleteProp();
        mainPane.getChildren().add(propositionRectangle);
        addPropositionAsPremise(prop);
        addPropositionAsConclusion(prop);
        propBox = loadNewPropPane(prop);
        mainPane.getChildren().add(propBox);
    }

    private void addPropositionAsPremise(PropositionModel prop) {
        for (ArgumentModel arg : premiseArgList) {
            arg.addPremise(prop, position);
        }
    }

    private void addPropositionAsConclusion(PropositionModel prop) {
        for (ArgumentModel arg : conclusionArgList) {
            arg.setConclusion(prop);
        }
    }

    /*
    * Loads new proposition within pane
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
        this.prop = prop;
        this.propBox = tPropBox;
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
        if (db.hasContent(propositionModelDataFormat)) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    public boolean getCQFlag() {
        return this.cqFlag;
    }

    public void setCQFlag() {
        this.cqFlag = !cqFlag;
    }

    public boolean getCounterFlag() {
        return this.counterFlag;
    }

    public void setCounterFlag() {
        this.counterFlag = !counterFlag;
    }
}
