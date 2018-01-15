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
import java.util.HashMap;
import java.util.List;

public class ArgumentModel implements Serializable {

    protected SchemeModel scheme;

    protected PremiseModel conclusion;
    protected PremiseModel[] premises;
    protected HashMap<Integer, String> criticalQuestions;
    protected boolean cq;
    
    protected List<ArgumentModel> cqArguments;

    public ArgumentModel() {
        this.scheme = new SchemeModel();
        this.cqArguments = new ArrayList<>();

        this.conclusion = new PremiseModel();
        this.conclusion.addAsConclusionForArgument(this);

        this.premises = new PremiseModel[1];
        for (int i = 0; i < premises.length; i++) {
            PremiseModel premise = new PremiseModel();
            premise.addAsPremiseForArgument(this);
            premises[i] = premise;

        }

        this.criticalQuestions = new HashMap<>();
        cq = false;
    }

    public ArgumentModel(SchemeModel scheme) {
        this.scheme = scheme;
        this.cqArguments = new ArrayList<>();

        this.conclusion = new PremiseModel();
        this.conclusion.addAsConclusionForArgument(this);

        premises = new PremiseModel[scheme.getPremises().size()];
        for (int i = 0; i < premises.length; i++) {
            PremiseModel premise = new PremiseModel();
            premise.addAsPremiseForArgument(this);
            premises[i] = premise;

        }

        criticalQuestions = new HashMap<>();
        cq = false;
    }

    public ArgumentModel(CounterArgumentModel counterArgument) {
        this.scheme = counterArgument.scheme;
        this.cqArguments = new ArrayList<>();

        this.conclusion = counterArgument.getConclusion();
        premises = counterArgument.premises;

        criticalQuestions = counterArgument.criticalQuestions;
        cq = counterArgument.cq;
    }

    public boolean hasCQ() {
        return cq;
    }

    public void setCQ(boolean value) {
        cq = value;
    }

    public String getTitle() {
        return scheme.getTitle();
    }

    public boolean containsPremise(int position) {
        return premises[position].getProposition() != null;
    }

    public PremiseModel getPremise(int position) {
        return premises[position];
    }

    public String getCriticalQuestion(int key) {
        return criticalQuestions.get(key);
    }

    public PremiseModel getConclusion() {
        return conclusion;
    }

    public void setConclusion(PropositionModel conclusion) {
        this.conclusion.setProposition(conclusion);
    }

    public void removeConclusion() {
        conclusion.removeProposition();
    }

    public void addPremise(PropositionModel prop, int position) {
        premises[position].setProposition(prop);
    }

    public void removePremise(int position) {
        if (containsPremise(position)) {
            premises[position].removeProposition();
        }
    }

    public void removePremise(PropositionModel prop) {
        for (int i = 0; i < premises.length; i++) {
            if (premises[i] != null && premises[i].equals(prop)) {
                premises[i] = null;
            }
        }
    }

    public int getPatchNumCQs() {
        return this.scheme.getCriticalQs().size();
    }

    public String getPatchCriticalQuestion(int i) {
        return this.scheme.getCriticalQs().get(i);
    }

    public String getSchemeConclusion() {
        return scheme.getConclusion();
    }

    public String getSchemePremise(int premiseNumber) {
        return scheme.getPremises().get(premiseNumber);
    }

    public int getSchemeNumPremises() {
        return scheme.getPremises().size();
    }

    public String getSchemeTitle() {
        return scheme.getTitle();
    }
    
    public void addCQArgument(ArgumentModel arg){
        cqArguments.add(arg);
    }
    
    public void removeCQArgument(ArgumentModel arg){
        cqArguments.remove(arg);
    }
}
