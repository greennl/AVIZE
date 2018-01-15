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
import com.uncg.save.models.PropositionModel;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

/**
 * FXML controller for evidence chunks once they have been attached to a 
 * proposition
 * 
 */
public class PropositionEvidenceChunkController extends EvidenceChunkController
        implements Initializable {

    private PropositionModel prop;
    private PropositionBoxController propControl;

    public void setPropControl(PropositionBoxController control) {
        propControl = control;
    }

    public void setProp(PropositionModel prop) {
        this.prop = prop;
    }

    @Override
    public void removeEvidence(Pane evPane, EvidenceModel ev) {
        prop.removeSupport(ev);
        evidenceList.remove(ev);
        evidenceVBox.getChildren().remove(evPane);

        constControl.constructionAreaSizeCheck();

        propControl.updateEvidenceCount();

    }

}
