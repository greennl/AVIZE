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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Class creates a node to be added to an ArgumentViewTree. Contains the
 * specifications for a rectangle which serves as a connector between a
 * conclusion pane and the rest of an argument structure.
 */
public class ConclusionConnectionNode extends ArgumentNode {

    /*
    TODO: need to add a label associated with this node so we can show what
    scheme this portion of the tree represents
     */
    private VBox vBox;
    private Polygon tri;
    private Rectangle rect;

    /**
     * Constructs a new ConclusionConnectionNode that details the specifications
     * for a rectangle that is the actual JavaFX Node used to draw the tree
     *
     * @param target Point2D detailing the coordinates the rectangle will be
     * drawn at. Calculations are done to offset this value and accommodate the
     * dimensions of the rectangle to make sure everything is centered.
     */
    public ConclusionConnectionNode(Point2D target) {
        super();
        double vBoxWidth = 18;
        double vBoxHeigth = 100;
        double triSide = 18;
        double triHeight = Math.sqrt(
                (Math.pow(triSide, 2)) - (Math.pow((triSide / 2), 2))
        );
        double rectWidth = 5;
        double rectHeight = vBoxHeigth - triHeight;

        tri = new Polygon();
        tri.getPoints().addAll(new Double[]{
            target.getX(), target.getY(),
            (target.getX() + triSide / 2), target.getY() + triHeight,
            (target.getX() - triSide / 2), target.getY() + triHeight
        });
        rect = new Rectangle(rectWidth, rectHeight);

        vBox = new VBox(tri, rect);
        vBox.setPrefSize(vBoxWidth, vBoxHeigth);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setLayoutX(target.getX() - (vBoxWidth / 2));
        vBox.setLayoutY(target.getY());
    }

    /**
     * Method returns the rectangle view
     *
     * @return Node. Rectangle that will be drawn on the actual tree view
     * structure
     */
    @Override
    public Node getView() {
        return vBox;
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
