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


import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class CQLabel extends ArgumentNode{

    private Label CQLabel;
    private ArgumentViewTree argTree;
    private ContextMenu contextMenu;
    private Point2D contextCoords;
    private Pane canvas;
    
   public CQLabel(String cq, Point2D target, ArgumentViewTree avt, Pane canvas, ArgumentNode connector) {
        CQLabel = new Label();
        CQLabel.setMaxWidth(PREMISE_WIDTH);
        CQLabel.setWrapText(true);
        CQLabel.setText(cq);
        this.canvas = canvas;
        argTree = avt;
        CQLabel.setLayoutX(target.getX() + 5);
        CQLabel.setLayoutY(target.getY() - 80);

    }

    @Override
    public Node getView() {
        return CQLabel;
    }

    @Override
    public void setArgTree(ArgumentViewTree argTree) {
        this.argTree = argTree;
    }

    public Point2D getCoordinates() {
        Point2D layout = new Point2D(
                (int) (CQLabel.getLayoutX()),
                (int) (CQLabel.getLayoutY())
        );
        return layout;
    }

    @Override
    public void moveComment(double x, double y) {
    }

    @Override
    public void deleteCommentPane() {
    }
}
