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
import com.uncg.save.models.DataModel;
import com.uncg.save.models.EvidenceModel;
import java.util.List;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

/**
 * FXML controller class for evidence chunks not attached to a proposition
 * pane, that are "floating" in the construction area
 * 
 */
public class FloatingEvidenceChunkController extends EvidenceChunkController 
        implements Initializable {
    
    @FXML
    private void dragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasContent(dataModelDataFormat)) {
            addEvidence(new EvidenceModel(
                    (DataModel) db.getContent(dataModelDataFormat))
            );
            constControl.constructionAreaSizeCheck();
            success = true;
        } else if (db.hasContent(evidenceChunkDataFormat)
                && !event.getGestureSource().equals(evidenceVBox)) {
            List<EvidenceModel> payload
                    = (List<EvidenceModel>) db.getContent(
                            evidenceChunkDataFormat);
            for (EvidenceModel evidence : payload) {
                addEvidence(evidence);
            }
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    private void dragDetected(Event event) {
        Dragboard db = evidenceVBox.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        content.put(evidenceChunkDataFormat, this.evidenceList);
        db.setContent(content);
        event.consume();
    }

    @FXML
    private void dragDone(DragEvent event) {
        /**
         * if drag event was a MOVE event, delete the source of the event
         */
        if (event.getTransferMode() == TransferMode.MOVE) {
            constControl.removePane(evidenceVBox);
        }
        event.consume();
    }

    @Override
    public void removeEvidence(Pane evPane, EvidenceModel ev) {
        evidenceList.remove(ev);
        evidenceVBox.getChildren().remove(evPane);

        if (evidenceList.isEmpty()) {
            constControl.removePane(evidenceVBox);
        }
        constControl.constructionAreaSizeCheck();

    }

}
