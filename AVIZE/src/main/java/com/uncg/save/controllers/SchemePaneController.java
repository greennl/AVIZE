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

import static com.uncg.save.MainApp.schemeModelDataFormat;
import com.uncg.save.models.SchemeModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML controller for view elements meant to represent individual argument
 * schemes
 *
 */
public class SchemePaneController implements Initializable {

    boolean schemeCollapsed = true;
    boolean exampleCollapsed = true;
    boolean cqCollapsed = true;
    int textSpace;
    double anchorPaneHeight;

    @FXML
    private AnchorPane superAnchorPane;
    @FXML
    private TitledPane superTitledPane;

    @FXML
    private AnchorPane childAnchorPane;

    @FXML
    private Label premiseLabel;

    @FXML
    private TitledPane exampleTitledPane;

    @FXML
    private VBox dataVBox;

    @FXML
    private Label exampleLabel;

    @FXML
    private TitledPane criticalQuestionsTitledPane;

    @FXML
    private Label criticalQuestionLabel;

    private SchemeListController slc;
    private SchemeModel scheme = new SchemeModel();
    private int exampleHeight;
    private int cqHeight;
    private boolean heightsCalc = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeLabels();
        superAnchorPane.setPrefHeight(40);
        superTitledPane.getStyleClass().add("titled-pane");
        superAnchorPane.getStyleClass().add("anchor-pane");
        childAnchorPane.getStyleClass().add("anchor-pane");
        childAnchorPane.getStyleClass().add("scheme-data-pane");
        dataVBox.getStyleClass().add("scheme-data-vbox");
        premiseLabel.getStyleClass().add("scheme-data-label");
        exampleLabel.getStyleClass().add("internal-data-label");
        criticalQuestionLabel.getStyleClass().add("internal-data-label");
        exampleTitledPane.getStyleClass().add("internal-titled-pane");
        criticalQuestionsTitledPane.getStyleClass().add("internal-titled-pane");
        criticalQuestionLabel.getStyleClass().add("label");
    }

    public Label getPremisesTextArea() {
        return this.premiseLabel;
    }

    public void setPremisesTextArea(String premises) {
        this.premiseLabel.setText(premises + "Conclusion:\n" + this.scheme.getConclusion() + "\n" + " ");
    }

    public Label getCriticalQuestionsTextArea() {
        return this.criticalQuestionLabel;
    }

    public void setCriticalQuestionsTextArea(String criticalQuestions) {
        this.criticalQuestionLabel.setText(criticalQuestions);
    }

    public void setScheme(SchemeModel sm) {
        this.scheme = sm;
    }

    public void setTitle() {
        superTitledPane.setText(this.scheme.getTitle());
    }

    public void setExampleTextArea(String examples) {
        this.exampleLabel.setText(examples);
    }

    public void setParentController(SchemeListController slc) {
        this.slc = slc;
    }

    private void initializeLabels() {
        exampleLabel.setWrapText(true);
        premiseLabel.setWrapText(true);
        criticalQuestionLabel.setWrapText(true);
        exampleLabel.setMinWidth(288);
        premiseLabel.setMinWidth(292);
        criticalQuestionLabel.setMinWidth(288);
        exampleLabel.setMaxWidth(288);
        premiseLabel.setMaxWidth(292);
        criticalQuestionLabel.setMaxWidth(288);
        exampleLabel.setFont(new Font("System Regular", 18));
        premiseLabel.setFont(new Font("System Regular", 18));
        criticalQuestionLabel.setFont(new Font("System Regular", 18));
    }

    @FXML
    private void dragDetected() {
        Dragboard db = superTitledPane.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.put(schemeModelDataFormat, this.scheme);
        db.setContent(content);
    }

    @FXML
    private void dragDone(DragEvent event) {
        event.consume();
    }

    @FXML
    private void expandScheme() {
        if (!heightsCalc) {
            exampleTitledPane.setExpanded(true);
            criticalQuestionsTitledPane.setExpanded(true);
            exampleHeight = (int) exampleLabel.getHeight();
            cqHeight = (int) criticalQuestionLabel.getHeight();
            heightsCalc = true;
        }
        exampleTitledPane.setExpanded(false);
        criticalQuestionsTitledPane.setExpanded(false);
        exampleCollapsed = true;
        cqCollapsed = true;
        if (schemeCollapsed) {
            superAnchorPane.setPrefHeight(premiseLabel.getHeight() + 176);
            schemeCollapsed = !schemeCollapsed;
            superTitledPane.setExpanded(true);
            slc.getMainScrollPane().layout();
        } else {
            superAnchorPane.setPrefHeight(40);
            schemeCollapsed = !schemeCollapsed;
            superTitledPane.setExpanded(false);
            slc.getMainScrollPane().layout();
        }
    }

    @FXML
    private void expandCQs() {
        if (cqCollapsed) {
            superAnchorPane.setPrefHeight(superAnchorPane.getHeight() + cqHeight);
            cqCollapsed = !cqCollapsed;
            criticalQuestionsTitledPane.setExpanded(true);
        } else {
            superAnchorPane.setPrefHeight(superAnchorPane.getHeight() - cqHeight);
            cqCollapsed = !cqCollapsed;
            criticalQuestionsTitledPane.setExpanded(false);
        }
    }

    @FXML
    private void expandExample() {
        if (exampleCollapsed) {
            superAnchorPane.setPrefHeight(superAnchorPane.getHeight() + exampleHeight);
            exampleCollapsed = !exampleCollapsed;
            exampleTitledPane.setExpanded(true);
        } else {
            superAnchorPane.setPrefHeight(superAnchorPane.getHeight() - exampleHeight);
            exampleCollapsed = !exampleCollapsed;
            exampleTitledPane.setExpanded(false);
        }
    }
}
