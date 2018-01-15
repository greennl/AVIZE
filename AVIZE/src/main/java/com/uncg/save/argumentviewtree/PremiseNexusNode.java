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
import javafx.scene.shape.Rectangle;

/**
 * Class creates a node to be added to an ArgumentViewTree. Contains the
 * specifications for a rectangle which serves as the horizontal line connecting
 * the premise and conclusion sections of an argument.
 *
 * TODO: this rectangle can be used to control spacing/repositioning of
 * arguments when we start to worry about view collisions
 */
public class PremiseNexusNode extends ArgumentNode {

    private Rectangle rect;

    /**
     * Constructs a new ConclusionConnectionNode that details the specifications
     * for a rectangle that is the actual JavaFX Node used to draw the tree
     *
     * @param target Point2D detailing the coordinates the rectangle will be
     * drawn at. Calculations are done to offset this value and accommodate the
     * dimensions of the rectangle to make sure everything is centered.
     */
    public PremiseNexusNode(int numPremises, Point2D target) {
        super();
        double rectWidth
                = ((numPremises-1) * (PREMISE_WIDTH + PADDING)) + 5;
        double rectHeight = 5;
        this.rect = new Rectangle(
                target.getX() - rectWidth / 2,
                target.getY(),
                rectWidth,
                rectHeight
        );
    }

    @Override
    public Node getView() {
        return rect;
    }

    @Override
    public void setArgTree(ArgumentViewTree argTree) {
    }

    public void growWidth(double length) {
        rect.setWidth(rect.getWidth() + length);
    }

    public void resizeToDefaultWidth() {
        rect.setWidth(
                (children.size() - 1)
                * (PREMISE_WIDTH + PADDING)
                + 5
        );
    }

    @Override
    public void moveComment(double x, double y) {
    }
    @Override
    public void deleteCommentPane(){
    }
    
    @Override
    public int getWidth(){
        return (int)rect.getWidth();
    }
}
