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

public class EvidenceModel implements Serializable {

    private String id;
    private String text;
    private String source;
    private String date;
    private String genre;
    private String reliability;
    private String likelihood;
    private String comment;

    public EvidenceModel() {
        this.id = "not provided";
        this.text = "not provided";
        this.source = "not provided";
        this.date = "not provided";
        this.genre = "not provided";
        this.reliability = "not provided";
        this.likelihood = "0.0";
        this.comment = "not provided";
    }

    /*
    TODO: REFACTOR THIS TO USE CUSTOM AND SPECIFIC CLASSES SO IT'S NOT JUST A 
    BUNCH OF STRING
     */
    public EvidenceModel(DataModel data) {

        this.id = data.getDataID();
        this.text = data.getDataText();
        this.source = data.getDataSource();
        this.date = data.getDataDate();
        this.genre = data.getDataGenre();
        this.reliability = data.getDataReliability();
        this.likelihood = data.getDataLikelihood();
        this.comment = data.getDataComment();
    }

    /*
    Getters for evidence fields
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReliability() {
        return reliability;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    public String getLikelihood() {
        return likelihood;
    }

    public void setLikelihood(String likelihood) {
        this.likelihood = likelihood;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
