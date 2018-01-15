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

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;

/**
 * ArgumentNode class specifies a common base class for use in argument view
 * trees. Each ArgumentNode contains a javafx Node (Rectangle, FXML defined
 * views, etc) as data, as well as a list of children to represent the tree
 * structure of an argument view.
 * 
 */
public abstract class ArgumentNode {

    static final int PREMISE_WIDTH = 325;
    static final int PREMISE_HEIGHT = 145;
    static final int PADDING = 30;

    private ArgumentNode parent;
    public final List<ArgumentNode> children;
    
    public boolean cqFlag = false;
    private int width;

    public ArgumentNode() {
        this.children = new ArrayList<>();
    }
    
    public List<ArgumentNode> getChildren() {
        return children;
    }

    public ArgumentNode getParent() {
        return parent;
    }
    
    public void setParent(ArgumentNode parent) {
        this.parent = parent;
    }
    
    public void addAsChild(ArgumentNode node) {
        children.add(node);
    }
    
    public void removeChild(ArgumentNode node) {
        children.remove(node);
    }

    public void shrinkOnDetatch(){
    }
    public void setWidth(int i){
        width = i;
    }
    public int getWidth(){
        return this.width;
    }

    public abstract Node getView();
    public abstract void setArgTree(ArgumentViewTree argTree);
    public abstract void moveComment(double x, double y);
    public abstract void deleteCommentPane();
}
