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

import com.uncg.save.controllers.RootPaneController;
import com.uncg.save.models.SchemeModel;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public class MainApp extends Application {

    static final ClassLoader LOADER = MainApp.class.getClassLoader();

    public static DataFormat dataModelDataFormat
            = new DataFormat("dataModelFormat");
    public static DataFormat evidenceModelDataFormat
            = new DataFormat("evidenceModelFormat");
    public static DataFormat schemeModelDataFormat
            = new DataFormat("schemeModelFormat");
    public static DataFormat argumentModelDataFormat
            = new DataFormat("argumentModelFormat");
    public static DataFormat propositionModelDataFormat
            = new DataFormat("propositionModelFormat");
    public static DataFormat evidenceChunkDataFormat
            = new DataFormat("evidenceChunkFormat");
    public static DataFormat commentDataFormat
            = new DataFormat("commentFormat");
    private static SchemeList sl;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader
                = new FXMLLoader(getClass().getResource("/fxml/RootPane.fxml"));
        Parent root = loader.load();
        RootPaneController rootControl
                = loader.<RootPaneController>getController();

        /*
        Generate scheme structure
         */
        List<SchemeModel> schemeModelList = generateSchemeList();

        /*
        Pass scheme list to root controller
         */
        rootControl.setSchemeModelList(schemeModelList);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("AVizE");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);
        stage.show();
    }

    private List<SchemeModel> generateSchemeList() throws JAXBException {
        List<SchemeModel> schemeList = new ArrayList<>();
        try {
            SchemaFactory sf
                    = SchemaFactory
                    .newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema
                    = sf.newSchema(getClass().getResource("/xml/scheme.xsd"));
            InputStream xmlStream = getClass().getResourceAsStream(("/xml/schemeList.xml"));
            
            JAXBContext jaxbContext = JAXBContext.newInstance(SchemeList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(schema);
            sl = (SchemeList) jaxbUnmarshaller.unmarshal(xmlStream);
            
            schemeList = sl.getModels();
            return schemeList;
        } catch (SAXException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schemeList;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args){
        launch(args);
    }
}
