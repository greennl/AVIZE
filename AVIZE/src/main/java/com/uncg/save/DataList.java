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
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "dataList" )
public class DataList {
    List<DataModelMaker> data;
    
    @XmlElement( name = "data" )
    public List<DataModelMaker> getData(){
        return this.data;
    }
    public void setData(List<DataModelMaker> data){
        this.data = data;
    }
    
    public List<DataModel> getModels(){
        List<DataModel> dataModelList = new ArrayList();
        for(int i = 0; i < data.size(); i++){
            dataModelList.add(data.get(i).getDataModel());
        }
        return dataModelList;
    }
}
