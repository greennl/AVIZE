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

import static com.uncg.save.MainApp.dataModelDataFormat;
import static com.uncg.save.MainApp.evidenceChunkDataFormat;
import static com.uncg.save.MainApp.propositionModelDataFormat;
import com.uncg.save.models.DataModel;
import com.uncg.save.models.EvidenceModel;
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
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

/**
 * FXML controller class for the view element that represents a proposition.
 * Meant to either float in the construction area or can be placed in a premise
 * or conclusion
 *
 */
public class PropositionBoxController implements Initializable {

    @FXML
    GridPane mainGridPane;
    @FXML
    TextArea text;
    @FXML
    Accordion evidenceAccordion;
    @FXML
    TitledPane evidenceTitledPane;
    @FXML
    private PropositionEvidenceChunkController evidenceChunkController;
    @FXML
    Button maximizeButton;

    private Pane parentContainer;
    private ConstructionAreaController constControl;

    private boolean evidenceCollapsed = true;
    private boolean canHaveEvidence;

    private PropositionModel prop;
    private Pane commentPane;
    private Line line;
    protected boolean hasComment = false;
    protected boolean visible = false;
    private boolean redText = false;
    private boolean maximized = false;
    private int paneHeight = 145;
    private int textAreaHeight = 100;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text.getStyleClass().add("text-area1");
        mainGridPane.getStyleClass().add("grid-pane-prop");
        ContextMenu contextMenu = new ContextMenu();
        try {
            setContextMenuItems(contextMenu);
        } catch (IOException ex) {
            Logger.getLogger(PropositionBoxController.class.getName()).log(Level.SEVERE, null, ex);
        }
        text.setContextMenu(contextMenu);
        text.setEditable(true);
        mainGridPane.setPrefHeight(125);
        mainGridPane.addEventFilter(MouseEvent.DRAG_DETECTED,
                new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                dragDetected(event);
                event.consume();
            }
        });

        mainGridPane.addEventFilter(
                DragEvent.DRAG_DROPPED, (DragEvent event) -> {
                    dragDrop(event);
                }
        );

        maximizeButton.setVisible(false);
        evidenceChunkController.setPropControl(this);
    }

    public void setParentContainer(Pane container) {
        parentContainer = container;
    }

    public String getPropText() {
        return prop.getProposition();
    }

    public String getCommentText() {
        return prop.getComment();
    }

    public void setCanHaveEvidence(boolean bool) {
        this.canHaveEvidence = bool;
    }

    public void setRedText() {
        redText = true;
    }

    /**
     * set menu items for context menu
     */
    private void setContextMenuItems(ContextMenu contextMenu) throws IOException {
        MenuItem deleteProp = new MenuItem("Delete Proposition");
        MenuItem toggleComment = new MenuItem("Show/Hide Comment");

        setHandlerForDeleteProp(deleteProp);
        setHandlerForComment(toggleComment);

        contextMenu.getItems().addAll(deleteProp, toggleComment);
    }

    private void setHandlerForComment(MenuItem item) throws IOException {
        item.setOnAction(action -> {
            if (!hasComment) {
                try {
                    double x = 0;
                    double y = 0;
                    this.commentPane = loadComment();
                    System.out.println(constControl.toString());
                    System.out.println(this.mainGridPane.toString());
                    if (constControl.getMainPane().getChildren().contains(this.mainGridPane)) {
                        x = mainGridPane.getLayoutX();
                        y = mainGridPane.getLayoutY();
                    } else {
                        for (int i = 0; i < constControl.getMainPane().getChildren().size(); i++) {
                            if (constControl.getMainPane().getChildren().get(i) instanceof Pane) {
                                Pane testPane = (Pane) constControl.getMainPane().getChildren().get(i);
                                if (testPane.getChildren().contains(this.mainGridPane)) {
                                    x = testPane.getLayoutX();
                                    y = testPane.getLayoutY();
                                }
                            }
                        }
                    }
                    commentPane.setTranslateX(x + 325);
                    commentPane.setTranslateY(y + 100);
                    line = new Line(x + 318, y + 100, x + 325, y + 100);
                } catch (IOException ex) {
                    Logger.getLogger(PremisePaneController.class.getName()).log(Level.SEVERE, null, ex);
                }
                visible = true;
                hasComment = true;
                constControl.getMainPane().getChildren().add(commentPane);
                constControl.getMainPane().getChildren().add(line);
            } else if (visible) {
                commentPane.setVisible(false);
                line.setVisible(false);
                visible = false;
            } else if (!visible) {
                commentPane.setVisible(true);
                line.setVisible(true);
                visible = true;
            }
        });
    }

    private Pane loadComment() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/CommentPane.fxml"));
        this.commentPane = loader.load();
        CommentPaneController commentControl = loader.<CommentPaneController>getController();
        commentControl.setParent(this);
        commentControl.setComment(prop.getComment());
        commentPane.toFront();
        return commentPane;
    }

    public void setComment(String comment) {
        prop.setComment(comment);
    }

    public Pane getComment() {
        return this.commentPane;
    }

    public boolean getHasComment() {
        return this.hasComment;
    }

    public void moveComment(double x, double y) {
        if (commentPane != null) {
            this.commentPane.setTranslateX(commentPane.getTranslateX() + x);
            this.commentPane.setTranslateY(commentPane.getTranslateY() + y);
            this.line.setTranslateX(line.getTranslateX() + x);
            this.line.setTranslateY(line.getTranslateY() + y);
        }
    }

    @FXML
    private void updateText() {
        text.setStyle("-fx-text-fill: white;");
        prop.setProposition(text.getText());
        //if (getNeededHeight() > 105) {
        //  maximizeButton.setVisible(true);
        //} else {
        maximizeButton.setVisible(false);
        //}
        //if (maximized) {
        //  maximizeBox();
        //}
    }

    @FXML
    private void clearTextOnDown() {
        if (redText) {
            redText = false;
            text.clear();
        }
    }

    private void setHandlerForDeleteProp(MenuItem delete) {
        delete.setOnAction(action -> {
            parentContainer.getChildren().remove(mainGridPane);
            deleteComment();
            action.consume();
        });
    }

    private void dragDrop(DragEvent event) {
        boolean success = false;
        Dragboard db = event.getDragboard();
        if (this.canHaveEvidence) {
            if (db.hasContent(evidenceChunkDataFormat)) {
                List<EvidenceModel> evidenceList
                        = (List<EvidenceModel>) db.getContent(
                                evidenceChunkDataFormat
                        );
                prop.addSupport(evidenceList);
                for (EvidenceModel evidence : evidenceList) {
                    evidenceChunkController.addEvidence(evidence);
                }
                success = true;
            } else if (db.hasContent(dataModelDataFormat)) {
                DataModel data = (DataModel) db.getContent(dataModelDataFormat);
                EvidenceModel evidence = new EvidenceModel(data);

                prop.addSupport(evidence);

                evidenceChunkController.addEvidence(evidence);
            }
            updateEvidenceCount();
            event.setDropCompleted(success);
            event.consume();
        }
    }

    /**
     * event handler method for drag detection on prop elements passes a
     * reference the the PropositionModel object represented by the dragged view
     */
    @FXML
    private void dragDetected(Event event) {
        Dragboard db = mainGridPane.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(propositionModelDataFormat, this.prop);
        db.setContent(content);
        event.consume();
    }

    /**
     * Event handler for completion of drag event. Deletes the view associated
     * with this controller to simulate movement
     *
     * @param event
     */
    @FXML
    private void dragDone(DragEvent event) {
        if (event.isAccepted()) {
            if (constControl.getMainPane().getChildren().contains(commentPane)) {
                constControl.getMainPane().getChildren().remove(commentPane);
                constControl.getMainPane().getChildren().remove(line);
            }
            constControl.removePane(mainGridPane);
        }
        event.consume();
    }

    public void deleteComment() {
        if (constControl.getMainPane().getChildren().contains(commentPane)) {
            constControl.getMainPane().getChildren().remove(commentPane);
            constControl.getMainPane().getChildren().remove(line);
        }
        hasComment = false;
        visible = false;
    }

    /**
     * Sets the proposition model that the view associated with this controller
     * is based on
     *
     * @param prop PropositionModel
     */
    public void setPropModel(PropositionModel prop) throws IOException {
        this.prop = prop;
        evidenceChunkController.setProp(prop);
        text.setText(prop.getProposition());
        //if (getNeededHeight() > 105) {
        //  maximizeButton.setVisible(true);
        //} else {
        maximizeButton.setVisible(false);
        //}
        if (!canHaveEvidence) {
            prop.getSupport().clear();
            mainGridPane.getChildren().remove(evidenceAccordion);
        } else {
            for (EvidenceModel evidence : prop.getSupport()) {
                evidenceChunkController.addEvidence(evidence);
            }
            updateEvidenceCount();
        }
    }

    /**
     * Sets the controller for the parent of the view this controller is
     * associated with
     *
     * @param control ConstructionAreaController
     */
    public void setConstructionAreaControl(ConstructionAreaController control) {
        this.constControl = control;
        evidenceChunkController.setConstructionControl(constControl);
    }

    /**
     * OnClick method that expands/collapses the metadata
     */
    @FXML
    public void modifyEvidencePane(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (evidenceCollapsed) {
                expandEvidencePane();
            } else {
                collapseEvidencePane();
            }
        }
        event.consume();
    }

    /**
     * expand metadata. Grows the parent grid container to accomidate the extra
     * information
     */
    private void expandEvidencePane() {
        mainGridPane.setPrefHeight(125 + (140 * prop.getSupport().size()));
        // expands the TitledPane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        evidenceTitledPane.setExpanded(true);
        evidenceCollapsed = false;
        //move parent to front of its siblings to garauntee visibility
        mainGridPane.getParent().toFront();
        constControl.constructionAreaSizeCheck();
    }

    /**
     * collapse metadata. Shrinks the parent grid container to default size
     */
    private void collapseEvidencePane() {
        mainGridPane.setPrefHeight(125);
        // collapses the TitlePane if it is clicked anywhere. Necessary for
        // reliable growing/shrinking behavior
        evidenceTitledPane.setExpanded(false);
        evidenceCollapsed = true;
        constControl.constructionAreaSizeCheck();
    }

    public void updateEvidenceCount() {
        evidenceTitledPane.setText(
                "Evidence: "
                + evidenceChunkController.evidenceListSize()
        );
    }

    public void setContextMenu(ContextMenu menu) throws IOException {
        text.setContextMenu(menu);
        MenuItem toggleComment = new MenuItem("Show/Hide Comment");
        setHandlerForComment(toggleComment);
        text.getContextMenu().getItems().add(toggleComment);
    }

    public void setViewText(String s) {
        text.setStyle("-fx-text-fill: white;");
        text.setText(s);
        /*
        if (getNeededHeight() > 105) {
            maximizeButton.setVisible(true);
        } else {*/
        maximizeButton.setVisible(false);
        //}
        System.out.println("im here yetoert");
    }

    public void setInitialText(String s) {
        text.setStyle("-fx-text-fill: red;");
        text.setText(s);
        //if (getNeededHeight() > 105) {
        //  maximizeButton.setVisible(true);
        //} else {
        maximizeButton.setVisible(false);
        //}
        System.out.println("im here!!!!");
    }

    public void translateComment(double x, double y) {
        this.commentPane.setTranslateX(commentPane.getTranslateX() + x);
        this.commentPane.setTranslateY(commentPane.getTranslateY() + y);
        line.setEndX(line.getEndX() + x);
        line.setEndY(line.getEndY() + y);
    }

    public void setColor(double r, double b) {
        //  text.setStyle
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
    @FXML
    private void maxProp() {
        System.out.println("IM HERE");
        if (!maximized) {
            maximizeBox();
            maximized = true;
        } else {
            minimizeBox();
            maximized = false;
        }
    }

    private void maximizeBox() {
        mainGridPane.setPrefHeight(getNeededHeight() + 35);
        mainGridPane.getRowConstraints().get(0).setPrefHeight(getNeededHeight());
        text.setPrefHeight(getNeededHeight());
    }

    private int getNeededHeight() {
        int i = 105;
        int j = text.getText().length();
        int growth = 0;
        if (j > 96) {
            j = j - 96;
            growth = j / 32;
            growth++;
            growth = growth * 26;
            i = i + growth;
            return i;
        } else {
            return 105;
        }
    }

    private void minimizeBox() {
        System.out.println("every other click");
        mainGridPane.setPrefHeight(paneHeight);
        mainGridPane.getRowConstraints().get(0).setPrefHeight(105);
        text.setPrefHeight(textAreaHeight);

    }
     */
}
