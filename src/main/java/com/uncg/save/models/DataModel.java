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

public class DataModel implements Serializable {

    private final String dataID;
    private final String dataText;
    private final String dataSource;
    private final String dataDate;
    private final String dataGenre;
    private final String dataReliability;
    private final String dataLikelihood;
    private final String dataComment;

    /*
    TODO: REFACTOR THIS TO USE CUSTOM AND SPECIFIC CLASSES SO IT'S NOT JUST A 
    BUNCH OF STRING
     */
    public DataModel(
            String id,
            String text,
            String source,
            String date,
            String genre,
            String reliability,
            String comment,
            String likelihoodString) {
        this.dataID = id;
        this.dataText = text;
        this.dataSource = source;
        this.dataDate = date;
        this.dataGenre = genre;
        this.dataReliability = reliability;
        this.dataLikelihood = likelihoodString;
        this.dataComment = comment;
    }

    /*
    Getters for data fields
     */
    public String getDataID() {
        return dataID;
    }

    public String getDataText() {
        return dataText;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getDataDate() {
        return dataDate;
    }

    public String getDataGenre() {
        return dataGenre;
    }

    public String getDataReliability() {
        return dataReliability;
    }

    public String getDataComment() {
        return dataComment;
    }

    public String getDataLikelihood() {
        return dataLikelihood;
    }

}
