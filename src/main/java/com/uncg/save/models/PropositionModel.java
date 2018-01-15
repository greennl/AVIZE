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

public class PropositionModel implements Serializable{
    
    private List<EvidenceModel> support;
    private String proposition;
    private String comment;

    public PropositionModel(){
        support = new ArrayList<>();
        proposition = "";
        comment = "";
    }    

    /*
    Getters and setters
    */
    public List<EvidenceModel> getSupport() {
        return support;
    }
    public void addSupport(List<EvidenceModel> support) {
        this.support.addAll(support);
    }
    public void addSupport(EvidenceModel support) {
        this.support.add(support);
    }
    public void removeSupport(EvidenceModel support) {
        this.support.remove(support);
    }
    public String getProposition() {
        return proposition;
    }
    public void setProposition(String proposition) {
        this.proposition = proposition;
    }
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }    
}
