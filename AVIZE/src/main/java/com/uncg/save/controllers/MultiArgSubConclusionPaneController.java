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

import com.uncg.save.argumentviewtree.ArgumentNode;
import com.uncg.save.argumentviewtree.ArgumentViewTree;
import com.uncg.save.models.ArgumentModel;
import com.uncg.save.util.LayoutUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * FXML controller for the individual conclusion components of 
 * multiple-arguments-sharing-the-same-conclusion structures. Appear as little
 * black circles in the GUI. Allow arguments sharing the same conclusion to
 * still act as independent arguments.
 * 
 */
public class MultiArgSubConclusionPaneController implements Initializable {

    @FXML
    private Pane mainPane;
    @FXML
    private Circle anchorCircle;

    private ArgumentViewTree argTree;
    private ArgumentModel argument;
    private ArgumentNode argNode;

    private ContextMenu contextMenu;
    private Point2D contextCoords;

    private ArgumentCertaintyPaneController certaintyControl;
    private ConstructionAreaController constructionControl;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       // create context menu for adding new premises
        contextMenu = new ContextMenu();
        setContextMenuItems();
        setContextMenuEventFilter();
    }
    
    public void setArgNode(ArgumentNode argNode){
        this.argNode = argNode;
    }
    
   /**
     * set menu items for context menu
     */
    private void setContextMenuItems() {
        MenuItem toggleCert = new MenuItem("Certainty On/Off");
        MenuItem detatchChain = new MenuItem("Detach Argument");

        setHandlerForToggle(toggleCert);
        setHandlerForDetatchChain(detatchChain);
        contextMenu.getItems().addAll(
                toggleCert,
                detatchChain
        );
    }

    private void setHandlerForToggle(MenuItem item) {
        item.setOnAction(action -> {
            certaintyControl.toggleVisible();
            action.consume();
        });
    }

    private void setHandlerForDetatchChain(MenuItem item) {
        item.setOnAction(action -> {
            Point2D localCoords = LayoutUtils.getLocalCoords(
                    constructionControl.getMainPane(),
                    contextCoords.getX(),
                    contextCoords.getY()
            );
            argTree.detatchMultiArgument(this, localCoords);
            action.consume();
        });
    }

    /**
     * add context menu event handler for mouse clicks
     */
    private void setContextMenuEventFilter() {
        anchorCircle.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
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

    public void setArgument(ArgumentModel arg) {
        argument = arg;
    }

    public void setArgumentViewTree(ArgumentViewTree avt) {
        this.argTree = avt;
    }

    public void setParentControl(ConstructionAreaController control) {
        constructionControl = control;
    }
    
    public void setCertaintyControl(ArgumentCertaintyPaneController control) {
        this.certaintyControl = control;
    }
    
    public ArgumentCertaintyPaneController getCertaintyControl() {
        return certaintyControl;
    }

    public Pane getMainPane(){
        return mainPane;
    }
    
    public ArgumentModel getArgument() {
        return argument;
    }
}
