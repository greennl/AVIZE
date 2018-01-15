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
package com.uncg.save;

import com.uncg.save.models.DataModel;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;

public class DataModelMaker implements Serializable {

    private String id = "UNKNOWN";
    private String text = "UNKNOWN";
    private String source = "UNKNOWN";
    private String date = "UNKNOWN";
    private String genre = "UNKNOWN";
    private String reliability = "UNKNOWN";
    private String likelihoodString = "UNKNOWN";
    private String comment = "No Comment";

    /*
    TODO: REFACTOR THIS TO USE CUSTOM AND SPECIFIC CLASSES SO IT'S NOT JUST A 
    BUNCH OF STRING
     */
    public DataModelMaker() {
    }

    public DataModelMaker(
            String id,
            String text,
            String source,
            String date,
            String genre,
            String reliability,
            String likelihoodString,
            String comment) {
        this.id = id;
        this.text = text;
        this.source = source;
        this.date = date;
        this.genre = genre;
        this.reliability = reliability;
        this.likelihoodString = likelihoodString;
        this.comment = comment;
    }

    /*
    Getters for data fields
     */
    public String getID() {
        return id;
    }

    @XmlElement(name = "id", defaultValue = "UNKNOWN")
    public void setID(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    @XmlElement(name = "text", defaultValue = "UNKNOWN")
    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    @XmlElement(name = "source", defaultValue = "UNKNOWN")
    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    @XmlElement(name = "date", defaultValue = "UNKNOWN")
    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    @XmlElement(name = "genre", defaultValue = "UNKNOWN")
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReliability() {
        return reliability;
    }

    @XmlElement(name = "reliability", defaultValue = "UNKNOWN")
    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    public String getLikelihoodString() {
        return this.likelihoodString;
    }

    @XmlElement(name = "likelihoodString", defaultValue = "UNKNOWN")
    public void setLikelihoodString(String sl) {
        this.likelihoodString = sl;
    }

    public String getComment() {
        return this.comment;
    }

    @XmlElement(name = "comment", defaultValue = "No Comment")
    public void setComment(String sl) {
        this.comment = sl;
    }

    public DataModel getDataModel() {
        return new DataModel(
                id,
                text,
                source,
                date,
                genre,
                reliability,
                comment,
                likelihoodString);
    }
}
