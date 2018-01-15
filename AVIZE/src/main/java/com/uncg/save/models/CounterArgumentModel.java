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
package com.uncg.save.models;

import java.util.ArrayList;
import java.util.List;

public class CounterArgumentModel extends ArgumentModel {

    private List<PremiseModel> parentModelList;

    public CounterArgumentModel() {
        super();
        this.parentModelList = new ArrayList<>();
    }

    public CounterArgumentModel(ArgumentModel argModel) {
        this.parentModelList = new ArrayList<>();
        this.scheme = argModel.scheme;

        this.conclusion = argModel.getConclusion();
        premises = argModel.premises;

        criticalQuestions = argModel.criticalQuestions;
        cq = argModel.cq;
    }

    public void addToParentModelList(PremiseModel model) {
        if (!parentModelList.contains(model)) {
            parentModelList.add(model);
        }
    }

    public void removeFromParentModelList(PremiseModel model) {
        parentModelList.remove(model);
    }

    public void setParentModelList(List<PremiseModel> newList) {
        System.out.println("setting parent models to:");
        System.out.println(newList);
        parentModelList.clear();
        System.out.println("parent models cleared");
        parentModelList.addAll(newList);
        System.out.println("models added");
    }

    public List<PremiseModel> getParentModelList() {
        return parentModelList;
    }
}
