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
package com.uncg.save.argumentviewtree;

import com.uncg.save.controllers.CqPickerController;
import com.uncg.save.models.ArgumentModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ArgumentSchemeLabel extends ArgumentNode {

    private Label argSchemeLabel;
    private ArgumentViewTree argTree;
    private ContextMenu contextMenu;
    private Point2D contextCoords;
    private ArgumentModel scheme;
    private Pane canvas;
    private MenuItem addCQ;

    public ArgumentSchemeLabel(ArgumentModel scheme, Point2D target, ArgumentViewTree avt, Pane canvas) {
        argSchemeLabel = new Label();
        argSchemeLabel.setText(scheme.getTitle());
        this.scheme = scheme;
        this.canvas = canvas;
        argTree = avt;
        argSchemeLabel.setLayoutX(target.getX() + 5);
        argSchemeLabel.setLayoutY(target.getY() + 120 / 2);
        argSchemeLabel.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
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
    public Node getView() {
        return argSchemeLabel;
    }

    @Override
    public void setArgTree(ArgumentViewTree argTree) {
        this.argTree = argTree;
    }

    private void showContextMenu(ContextMenuEvent event) {
        /*
        call to hide() ensures that bugs arent encountered if
        multiple context menus are opened back to back
         */
        contextMenu.hide();
        contextMenu.show(
                argSchemeLabel, event.getScreenX(), event.getScreenY()
        );
        contextCoords = new Point2D(event.getScreenX(), event.getScreenY());
        event.consume();
    }

    private void setContextMenuItems() {
        addCQ = new MenuItem("Add Critical Question");
        setHandlerForAddCQ(addCQ);
        contextMenu.getItems().addAll(
                addCQ
        );
    }

    private void setHandlerForAddCQ(MenuItem item) {
        item.setOnAction(action -> {
            try {
                displayCQPicker(item);
                action.consume();
            } catch (IOException ex) {
                Logger.getLogger(ArgumentSchemeLabel.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void enableAddCQ() {
        this.addCQ.setDisable(false);
    }

    public Point2D getCoordinates() {
        Point2D layout = new Point2D(
                (int) (argSchemeLabel.getLayoutX()),
                (int) (argSchemeLabel.getLayoutY())
        );
        return layout;
    }

    public void displayCQPicker(MenuItem item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CqPicker.fxml"));
        Parent cqPickerBox = loader.load();
        CqPickerController cqPickerController = loader.<CqPickerController>getController();
        cqPickerController.setArgModel(scheme);
        cqPickerController.setAVT(argTree);
        cqPickerController.setASL(this);
        cqPickerController.populateCQs(item);
        Scene scene = new Scene(cqPickerBox);
        Stage stage = new Stage();
        stage.initOwner(canvas.getScene().getWindow());
        stage.setTitle("Choose Critical Question");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @Override
    public void moveComment(double x, double y) {
    }

    @Override
    public void deleteCommentPane() {
    }
}
