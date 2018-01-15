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

public class PremiseModel implements Serializable{
    
    private PropositionModel proposition;
    private double argCertainty;
    private List<CounterArgumentModel> counterArguments;
    private List<ArgumentModel> conclusionForArgumentList;
    private List<ArgumentModel> premiseForArgumentList;
    
    public PremiseModel(){
        proposition = null;
        this.argCertainty = 0.00;
        counterArguments = new ArrayList<>();
        conclusionForArgumentList = new ArrayList<>();
        premiseForArgumentList = new ArrayList<>();
    }
    
    public PremiseModel(PropositionModel prop){
        proposition = prop;
        this.argCertainty = 0.00;
        counterArguments = new ArrayList<>();
        conclusionForArgumentList = new ArrayList<>();
        premiseForArgumentList = new ArrayList<>();
    }

    /**
     * @return the proposition
     */
    public PropositionModel getProposition() {
        return proposition;
    }

    /**
     * @param proposition the proposition to set
     */
    public void setProposition(PropositionModel proposition) {
        this.proposition = proposition;
    }

    /**
     * @return the argCertainty
     */
    public double getArgCertainty() {
        return argCertainty;
    }

    /**
     * @param argCertainty the argCertainty to set
     */
    public void setCertainty(double argCertainty) {
        this.argCertainty = argCertainty;
    }

    /**
     * @return the counterArgument
     */
    public boolean hasCounterArgument() {
        return !counterArguments.isEmpty();
    }
    
    public void addCounterArgument(CounterArgumentModel counterArg) {
        counterArguments.add(counterArg);
    }
    
    public void removeCounterArgument(CounterArgumentModel arg) {
        counterArguments.remove(arg);
    }
    
    public List<CounterArgumentModel> getCounterArgumentList(){
        return counterArguments;
    }
    
    public void removeProposition(){
        proposition = null;
    }
    
    public void addAsPremiseForArgument(ArgumentModel arg){
        premiseForArgumentList.add(arg);
    }
    
    public void addAsConclusionForArgument(ArgumentModel arg){
        conclusionForArgumentList.add(arg);
    }
    
    public void removeAsPremiseForArgument(ArgumentModel arg){
        premiseForArgumentList.remove(arg);
    }
    
    public void removeAsConclusionForArgument(ArgumentModel arg){
        conclusionForArgumentList.remove(arg);
    }
}
