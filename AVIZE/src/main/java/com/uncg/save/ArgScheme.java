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

import com.uncg.save.models.SchemeModel;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "argScheme", namespace="com.uncg.save")
public class ArgScheme {

    private String title;
    private Premises premise;
    private Examples example;
    private CQs cq;
    private String conclusion;

    public ArgScheme() {
    }

    public ArgScheme(String title, Premises dat, Examples example, String conc, CQs dat2) {
        super();
        this.title = title;
        this.premise = dat;
        this.example = example;
        this.conclusion = conc;
        this.cq = dat2;
    }

    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }     
    public Premises getPremises(){
        return this.premise;
    }
    public void setPremises(Premises premises){ 
        this.premise = premises;
    }
    public Examples getExamples(){
        return this.example;
    }
    public void setExamples(Examples examples){
        this.example = examples;
    }
    public String getConclusion() {
        return conclusion;
    }
    public void setConclusion(String c) {
        this.conclusion = c;
    }
    public CQs getCQs() {
        return cq;
    }
    public void setCQs(CQs datas2) {
        this.cq = datas2;
    }
    
    public SchemeModel getSchemeModel(){
        if(this.cq.getCQ().isEmpty()){
            this.cq.getCQ().add("NO CQs");
        }
        return new SchemeModel(
                this.premise.getPremise(),
                this.cq.getCQ(),
                this.example.getExample(),
                this.conclusion,
                this.title);
      
    }

}
