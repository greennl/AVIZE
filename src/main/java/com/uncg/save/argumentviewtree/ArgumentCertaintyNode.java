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

import com.uncg.save.controllers.ArgumentCertaintyPaneController;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ArgumentCertaintyNode extends ArgumentNode {

    private Pane argCertaintyContainer;
    private ArgumentCertaintyPaneController certControl;

    private final double paneLength = 50;

    public ArgumentCertaintyNode(
            Pane certPane,
            ArgumentCertaintyPaneController control,
            Point2D target) {
        argCertaintyContainer = certPane;
        certControl = control;

        argCertaintyContainer.setLayoutX(target.getX() - paneLength);
        argCertaintyContainer.setLayoutY(target.getY());
    }

    @Override
    public Node getView() {
        return argCertaintyContainer;
    }

    @Override
    public void setArgTree(ArgumentViewTree argTree) {
    }

    @Override
    public void moveComment(double x, double y) {
    }

    @Override
    public void deleteCommentPane() {
    }
}
