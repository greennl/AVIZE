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

import com.uncg.save.models.EvidenceModel;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Abstract FXML Controller class that defines the base behavior of evidence
 * chunks. Evidence chunks are view structures that hold evidence panes
 *
 */
public abstract class EvidenceChunkController implements Initializable {

    @FXML
    protected VBox evidenceVBox;

    protected ConstructionAreaController constControl;
    protected List<EvidenceModel> evidenceList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        evidenceVBox.getStyleClass().add("evidence-vertical-box");
        evidenceList = new ArrayList<>();
    }

    public abstract void removeEvidence(Pane evPane, EvidenceModel ev);

    /**
     * Returns the number of elements currently stored in this evidence chunk
     *
     * @return int
     */
    public int evidenceListSize() {
        return evidenceList.size();
    }

    /**
     * Sets the construction controller for this evidence chunk
     *
     * @param parentControl
     */
    public void setConstructionControl(
            ConstructionAreaController parentControl) {
        this.constControl = parentControl;
    }

    /**
     * adds new evidence to this chunk
     *
     * @param evidence
     */
    public void addEvidence(EvidenceModel evidence) {
        try {
            Pane evidencePane = loadNewEvidencePane(evidence);
            evidenceVBox.getChildren().add(evidencePane);
            evidenceList.add(evidence);
        } catch (IOException ex) {
            Logger.getLogger(EvidenceChunkController.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private Pane loadNewEvidencePane(EvidenceModel evidence)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/EvidencePane.fxml"));
        Pane evidencePane = loader.load();
        EvidencePaneController evidenceControl
                = loader.<EvidencePaneController>getController();
        evidenceControl.setConstructionAreaController(constControl);
        evidenceControl.setChunkControl(this);
        evidenceControl.setEvidence(evidence);
        return evidencePane;
    }

    public void popEvidencePane(EvidenceModel evidence) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/fxml/FloatingEvidenceChunk.fxml"));
        Pane evidencePane = loader.load();
        EvidenceChunkController chunkControl
                = loader.<EvidenceChunkController>getController();
        chunkControl.setConstructionControl(constControl);
        chunkControl.addEvidence(evidence);

        evidencePane.getStyleClass().add("pane");

        Bounds bounds = evidenceVBox.getBoundsInLocal();
        Bounds screenBounds = evidenceVBox.localToScreen(bounds);
        int x = (int) screenBounds.getMinX();
        int y = (int) screenBounds.getMinY();
        evidencePane.setTranslateX(x + 50);
        evidencePane.setTranslateY(y + 50);

        constControl.getMainPane().getChildren().add(evidencePane);
    }
}
