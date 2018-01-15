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
import static com.uncg.save.MainApp.argumentModelDataFormat;
import static com.uncg.save.MainApp.commentDataFormat;
import static com.uncg.save.MainApp.dataModelDataFormat;
import static com.uncg.save.MainApp.evidenceChunkDataFormat;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import static com.uncg.save.MainApp.schemeModelDataFormat;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.models.DataModel;
import com.uncg.save.models.EvidenceModel;
import com.uncg.save.models.PropositionModel;
import com.uncg.save.models.SchemeModel;
import com.uncg.save.util.LayoutUtils;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConstructionAreaController implements Initializable {

    @FXML
    Pane mainPane;

    private HashMap<String, ArgumentViewTree> argumentTrees;

    private ContextMenu contextMenu;

    private Point2D targetPropBoxCoords;

    private RootPaneController rpc;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize lists
        argumentTrees = new HashMap<>();

        mainPane.getStyleClass().add("pane");

        // create context menu for adding new premises
        contextMenu = new ContextMenu();
        setContextMenuItems();
        setContextMenuEventHandler();
    }

    public Pane getMainPane() {
        return this.mainPane;
    }

    public void setRootPaneController(RootPaneController rpc) {
        this.rpc = rpc;
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems() {
        MenuItem createNewPremise = new MenuItem("New Proposition");
        setHandlerForCreatePremise(createNewPremise);
        contextMenu.getItems().add(createNewPremise);
    }

    /**
     * sets the handler for creating a new proposition box when this menu item
     * is selected
     *
     * @param item MenuItem
     */
    private void setHandlerForCreatePremise(MenuItem item) {
        item.setOnAction(action -> {
            try {
                createNewPropBox();
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
            action.consume();
        });
    }

    /**
     * creates a new proposition box and empty PropositionModel
     *
     * @param prop PropositionModel
     * @throws IOException
     */
    private void createNewPropBox() throws IOException {
        PropositionModel newProp = new PropositionModel();
        Pane propBox = loadNewPropPane(newProp);

        LayoutUtils.setChildLayout(propBox, targetPropBoxCoords);
        mainPane.getChildren().add(propBox);
    }

    /**
     * add context menu event handler for mouse clicks
     */
    private void setContextMenuEventHandler() {
        mainPane.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
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
        setPropCoords(event.getScreenX(), event.getScreenY());
    }

    /**
     * sets coordinates when context menu is opened. Used in creation of
     * proposition boxes
     *
     * @param x double
     * @param y double
     */
    private void setPropCoords(double x, double y) {
        targetPropBoxCoords = LayoutUtils.getLocalCoords(mainPane, x, y);
    }

    /**
     * hides the context menu
     */
    private void closeContextMenu() {
        contextMenu.hide();
    }

    /**
     * event handler for drag over events
     *
     * @param event
     */
    @FXML
    private void dragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * event handler for drop events from draggables
     *
     * @param event
     */
    @FXML
    private void dragDropped(DragEvent event) {
        boolean success = false;
        Dragboard db = event.getDragboard();
        if (db.hasContent(dataModelDataFormat)) {
            /*
            Use new Data to create evidence
             */
            try {
                dropData(db, event);
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasContent(evidenceChunkDataFormat)) {
            /*
            Move evidence chunks in construction area
             */
            try {
                dropEvidence(
                        (List<EvidenceModel>) db
                                .getContent(evidenceChunkDataFormat),
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasContent(schemeModelDataFormat)) {
            /*
            Use new scheme to generate Argument
             */
            try {
                dropScheme(db, event);
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasContent(argumentModelDataFormat)) {
            try {
                dropArgument(db, event);
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasContent(propositionModelDataFormat)) {
            try {
                /*
                Move proposition in construction area
                 */
                dropProp(
                        (PropositionModel) db
                                .getContent(propositionModelDataFormat),
                        new Point2D(event.getSceneX(), event.getSceneY())
                );
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (db.hasContent(commentDataFormat)) {

        } else if (db.hasString()) {
            try {
                /*
                Drag argument tree
                 */
                dropArgument(db, event);
                success = true;
            } catch (IOException ex) {
                Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        constructionAreaSizeCheck();
        event.setDropCompleted(success);
        event.consume();
    }

    public void constructionAreaSizeCheck() {
        int edgeBuffer = 75;

        // check if nodes need to be shifted because they're falling off the 
        // left side
        double moveBuffer = furthestNodeMinX();
        System.out.println(moveBuffer);
        if (moveBuffer < 0) {
            shiftAllChildren(moveBuffer);
        }

        // grow construction area if nodes do not fit
        mainPane.layout();
        mainPane.setPrefSize(
                furthestNodeMaxX() + edgeBuffer,
                furthestNodeMaxY() + edgeBuffer
        );
    }

    private void shiftAllChildren(double move) {
        move = move * -1;
        for (Node node : mainPane.getChildren()) {
            node.setLayoutX(node.getLayoutX() + move);
        }
        mainPane.setPrefWidth(mainPane.getWidth() + move);

    }

    //Finds the leftmost node on the area
    private double furthestNodeMinX() {
        double minX = Double.MAX_VALUE;
        for (Node node : mainPane.getChildren()) {
            if (node.getBoundsInParent().getMinX() < minX) {
                minX = node.getBoundsInParent().getMinX();
            }
        }
        return minX;
    }

    //Finds the rightmost node on the area
    private double furthestNodeMaxX() {
        double maxX = Double.MIN_VALUE;
        for (Node node : mainPane.getChildren()) {
            if (node.getBoundsInParent().getMaxX() > maxX) {
                maxX = node.getBoundsInParent().getMaxX();
            }
        }
        return maxX;
    }

    //Finds the topmost node on the area
    private double furthestNodeMaxY() {
        double maxY = Double.MIN_VALUE;
        for (Node node : mainPane.getChildren()) {
            if (node.getBoundsInParent().getMaxY() > maxY) {
                maxY = node.getBoundsInParent().getMaxY();
            }
        }
        return maxY;
    }

    /**
     * Creates a new evidence model and view using data from dragboard
     *
     * @param db Dragboard
     * @param event DragEvent
     * @throws IOException
     */
    private void dropData(Dragboard db, DragEvent event) throws IOException {
        DataModel data = (DataModel) db.getContent(dataModelDataFormat);
        EvidenceModel evidence = new EvidenceModel(data);

        Pane evidenceChunk = loadNewEvidenceChunkPane(evidence);

        Point2D localCoords
                = LayoutUtils.getLocalCoords(
                        mainPane, event.getSceneX(), event.getSceneY()
                );
        evidenceChunk.getStyleClass().add("pane");
        LayoutUtils.setChildLayout(evidenceChunk, localCoords);

        mainPane.getChildren().add(evidenceChunk);
        event.consume();
    }

    private void dropScheme(Dragboard db, DragEvent event) throws IOException {
        SchemeModel scheme = (SchemeModel) db.getContent(schemeModelDataFormat);
        if (scheme.getTitle().equals("Generic")) {
            //ASK FOR NUMBER OF PREMISES FROM USER
            scheme.clearPremise();
            getGeneric(scheme);
        }
        ArgumentModel argument = new ArgumentModel(scheme);

        ArgumentViewTree argTree = new ArgumentViewTree(mainPane, this);
        String treeID = Integer.toString(argTree.hashCode());
        argTree.setTreeID(treeID);
        argumentTrees.put(treeID, argTree);

        argTree.addRootArgument(argument, event.getSceneX(), event.getSceneY());
    }

    //Loads the generic scheme maker and writes the specified scheme to the area
    private void getGeneric(SchemeModel scheme) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GenericPremisePane.fxml"));
        Parent genericPremSetterBox = loader.load();
        GenericPremisePaneController gppC = loader.<GenericPremisePaneController>getController();
        gppC.setInitialScheme(scheme);
        Scene scene = new Scene(genericPremSetterBox);
        Stage stage = new Stage();
        stage.initOwner(mainPane.getScene().getWindow());
        stage.setTitle("Set Number Premises");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        gppC.setStage(stage);
        stage.showAndWait();
        
        int i = gppC.getPremiseNumber();
        for (int j = i; j != 0; j--) {
            scheme.addPremise("Premise");
        }
        scheme.setCriticalQs(gppC.getCQs());
    }

    /**
     * Creates new evidence view from an existing model from dragboard to
     * simulate movement
     *
     * @param db Dragboard
     * @param event DragEvent
     * @throws IOException
     */
    private void dropEvidence(
            List<EvidenceModel> evidence, Point2D coords) throws IOException {
        Pane evidencePane = loadNewEvidenceChunkPane(evidence);
        Point2D localCoords
                = LayoutUtils.getLocalCoords(
                        mainPane, coords.getX(), coords.getY()
                );
        evidencePane.getStyleClass().add("pane");
        LayoutUtils.setChildLayout(evidencePane, localCoords);

        mainPane.getChildren().add(evidencePane);
    }

    /**
     * Creates a new argument from an existing structure of argument trees to
     * simulate movement
     * 
     * @param db
     * @param event
     * @throws IOException 
     */
    private void dropArgument(
            Dragboard db, DragEvent event) throws IOException {
        String draggedTreeID = db.getString();
        ArgumentViewTree targetTree = null;
        for (String treeID : argumentTrees.keySet()) {
            if (treeID.equals(draggedTreeID)) {
                targetTree = argumentTrees.get(draggedTreeID);
            }
        }
        Point2D localCoords
                = LayoutUtils.getLocalCoords(
                        mainPane, event.getSceneX(), event.getSceneY()
                );
        targetTree.translateTree(localCoords.getX(), localCoords.getY());
    }

    /**
     * Creates a new proposition box from an existing model from dragboard to
     * simulate movement
     *
     * @param db Dragboard
     * @param event DragEvent
     * @throws IOException
     */
    private void dropProp(
            PropositionModel prop, Point2D coords) throws IOException {
        Pane propBox = loadNewPropPane(prop);
        Point2D localCoords
                = LayoutUtils.getLocalCoords(
                        mainPane, coords.getX(), coords.getY()
                );
        LayoutUtils.setChildLayout(propBox, localCoords);

        mainPane.getChildren().add(propBox);
    }

    /**
     * Loads a new chunk evidence view and sets fields according to evidence
     * provided
     *
     * @param evidence EvidenceModel
     * @return Pane
     * @throws IOException
     */
    private Pane loadNewEvidenceChunkPane(
            EvidenceModel evidence) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/FloatingEvidenceChunk.fxml"));
        Pane evidencePane = loader.load();
        EvidenceChunkController chunkControl
                = loader.<EvidenceChunkController>getController();
        chunkControl.setConstructionControl(this);
        chunkControl.addEvidence(evidence);

        return evidencePane;
    }

    /**
     * Loads a new evidence chunk view and sets fields according to list of
     * evidence provided
     *
     * @param evidence EvidenceModel
     * @return Pane
     * @throws IOException
     */
    private Pane loadNewEvidenceChunkPane(
            List<EvidenceModel> evidenceList) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/FloatingEvidenceChunk.fxml"));
        Pane evidencePane = loader.load();
        FloatingEvidenceChunkController chunkControl
                = loader.<FloatingEvidenceChunkController>getController();
        chunkControl.setConstructionControl(this);
        for (EvidenceModel evidence : evidenceList) {
            chunkControl.addEvidence(evidence);
        }

        return evidencePane;
    }

    /**
     * Loads a new proposition box and sets fields according to proposition
     * provided
     *
     * @param prop Proposition Model
     * @return Pane
     * @throws IOException
     */
    private Pane loadNewPropPane(PropositionModel prop) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/PropositionBox.fxml"));
        Pane propBox = loader.load();
        PropositionBoxController propControl
                = loader.<PropositionBoxController>getController();
        propControl.setCanHaveEvidence(true);
        propControl.setConstructionAreaControl(this);
        propControl.setPropModel(prop);
        propControl.setParentContainer(mainPane);
        return propBox;
    }

    public void removePane(Pane pane) {
        mainPane.getChildren().remove(pane);
        constructionAreaSizeCheck();
    }

    public ArgumentViewTree getArgTree(String key) {
        return argumentTrees.get(key);
    }

    public void removeArgumentTree(String key) {
        argumentTrees.remove(key);
        constructionAreaSizeCheck();
    }

    public void registerNewArgTree(ArgumentViewTree tree) {
        String treeID = Integer.toString(tree.hashCode());
        tree.setTreeID(treeID);
        argumentTrees.put(treeID, tree);
    }

    public void createProp(PropositionModel prop, Point2D coords) {
        try {
            dropProp(prop, coords);
        } catch (IOException ex) {
            Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createEvidenceChunk(
            List<EvidenceModel> evidenceList, Point2D coords) {
        try {
            dropEvidence(evidenceList, coords);
        } catch (IOException ex) {
            Logger.getLogger(ConstructionAreaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArgumentViewTree getTargetTree(String s) {
        ArgumentViewTree targetTree = null;
        for (String treeID : argumentTrees.keySet()) {
            if (treeID.equals(s)) {
                targetTree = argumentTrees.get(s);
            }
        }
        return targetTree;
    }
}
