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

import com.uncg.save.models.PremiseModel;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

public class ArgumentCertaintyPaneController implements Initializable {

    @FXML
    private Pane mainContainer;
    @FXML
    private TextField certaintyTextField;

    private PremiseModel controllingModel;
    private double certainty;
    private ConclusionPaneController concControl;
    private PremisePaneController premControl;

    /**
     * Controls the certainty panes attached to conclusion, chain,
     * counter argument, and critical question panes.
     * 
     * Associated view node: ArgumentCertaintyNode
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        certainty = 0.00;

        certaintyTextField.setTextFormatter(new CertaintyFormatter());
        certaintyTextField
                .focusedProperty()
                .addListener((isFocus, oldValue, newValue) -> {
                    if (!newValue) {
                        Double parsed
                                = Double
                                        .parseDouble(certaintyTextField.getText());
                        if (parsed < 0.00 || parsed > 1.00) {
                            certaintyTextField
                                    .setText(Double.toString(certainty));
                        } else {
                            certainty = parsed;
                             controllingModel.setCertainty(parsed);
                        }
                        if (concControl != null) {
                            concControl.setViewColor(certainty);
                        }else{
                            premControl.setViewColor(certainty);
                        }
                    }
                });

        mainContainer.setVisible(false);
    }

    public void setConcControl(ConclusionPaneController pControl) {
        this.concControl = pControl;
    }

    public ConclusionPaneController getConcControl() {
        return this.concControl;
    }
    
    public void setPremControl(PremisePaneController pControl){
        this.premControl = pControl;
    }
    
    public PremisePaneController getPremControl(){
        return this.premControl;
    }

    public void setControllingModel(PremiseModel model) {
        this.controllingModel = model;
    }

    public void toggleVisible() {
        mainContainer.setVisible(!mainContainer.isVisible());
    }
    public void killVisibility(){
        mainContainer.setVisible(false);
    }

    /**
     * Private inner class meant to be attached to the text field to limit valid
     * inputs
     */
    private static class CertaintyFormatter extends TextFormatter<Double> {

        private static final double DEFAULT_VALUE = 0.00;
        private static final DecimalFormat DEC_FORM
                = new DecimalFormat("#0.00");

        public CertaintyFormatter() {
            super(
                    new StringConverter<Double>() {
                @Override
                public String toString(Double value) {
                    return DEC_FORM.format(value);
                }

                @Override
                public Double fromString(String string) {
                    try {
                        return DEC_FORM.parse(string).doubleValue();
                    } catch (ParseException ex) {
                        return Double.NaN;
                    }
                }
            },
                    DEFAULT_VALUE,
                    change -> {
                        try {
                            String text = change.getText();
                            if (!change.isContentChange()) {
                                return change;
                            }
                            DEC_FORM.parse(change.getControlNewText());
                            return change;
                        } catch (ParseException ex) {
                            return null;
                        }
                    }
            );
        }
    }
}
