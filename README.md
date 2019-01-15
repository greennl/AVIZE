# AVIZE
AVIZE (Argument Visualization and Evaluation) is an educational argument diagramming tool for the domain of international politics. AVIZE provides a set of argument schemes as cognitive building blocks for constructing argument diagrams in this domain, which  often involves reasoning about the beliefs, goals, appraisals, actions, and plans of actors such as countries, governments, politicians, etc. However, the tool can be used in other domains by supplying your own data to use in arguments and your own set of argument schemes. For more information, see Nancy L. Green, Michael Branon and Luke Roosje, "Argument schemes and visualization software for critical thinking about international politics," Argument and Computation 2019 10(1):41-53. Available free at https://content.iospress.com/articles/argument-and-computation/aac181003

## Running Avize
To run AVIZE with default argument schemes simply download the latest release and run the included JAR file.  
#### Custom ArgumentSchemes
In the current version, if you wish to utilize your own custom schemes the AVIZE/src/main/resources/xml/schemeList.xml file must be edited/replaced. Edit the aforementioned file and rebuild the project with Maven.The XML schema for argument scheme files is located in AVIZE/src/main/resources/xml/scheme.xsd.
#### Custom Data
Custom data can be loaded from the application anytime after start up. Simply create an XML file with the data you wish to use and load it after starting AVIZE. Default data examples can be found in AVIZE/src/main/resources/dataXMLs/. The XML schema for data files is located in AVIZE/src/main/resources/xml/data.xsd.

## Usage
Please refer to the quickstart guide in the documentation folder for details on how to use AVIZE.
