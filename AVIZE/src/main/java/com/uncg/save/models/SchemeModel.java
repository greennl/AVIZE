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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchemeModel implements Serializable{
    private List<String> premises = new ArrayList();
    private List<String> criticalQs = new ArrayList();
    private List<String> examples = new ArrayList();
    private final String conclusion;
    private final String title;
    
    public SchemeModel(){
        this.conclusion = "to be set";
        this.title = "to be set";
    }
    
    public SchemeModel(
            List<String> premises, 
            List<String> criticalQs, 
            List<String> examples,
            String conclusion, 
            String title){
        this.premises = premises;
        this.criticalQs = criticalQs;
        this.examples = examples;
        this.conclusion = conclusion;
        this.title = title;
    }
    
    public List<String> getPremises(){
        return this.premises;
    }
    public List<String> getCriticalQs(){
        return this.criticalQs;
    }
    public void setCriticalQs(List<String> cq){
        this.criticalQs = cq;
    }
    public List<String> getExamples(){
        return this.examples;
    }
    public String getConclusion(){
        return conclusion;
    }
    public String getTitle(){
        return title;
    }
    public void clearPremise(){
        premises.clear();
    }
    public void addPremise(String s){
        premises.add(s);
    }
}