package com.main.ecommerceprototype.CMS;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableArrayBase;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Callback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMSController implements Initializable {
    DirectoryChooser directoryChooser = null;
    FileChooser fileChooser = null;
    URL templateFolderPath = CMSApp.class.getResource("templates");
    Map<String,File> workingFiles = new HashMap<>();
    File workingFolder = null;
    File[] workingFileList = null;
    URL resourcePath = CMSApp.class.getResource("");
    File workingFile = null;
    List<String> fxidList= new ArrayList<>();
    private Node rootNode;

    private Document document;

    // might add the selectionmodel up here
    @FXML
    TabPane tabPane;
    @FXML
    TextField controllerNameInput, prefHeightTextField, prefWidthTextField, minWidthTextField, minHeightTextField, maxWidthTextField, maxHeightTextField, fxidTextField, styleTextField, textTextField, addChildNodeNameTextField;
    @FXML
    ColorPicker styleColorPicker;
    @FXML
    ChoiceBox<String> styleChoiceBox;
    @FXML TextField searchFieldearchId;
    @FXML
    TreeView<Element> HierarchyTreeView;
    @FXML
    TitledPane propertiesTitledPane, overviewTitledPane, layoutTitledPane, codeTitledPane, sizeTitledPane, identityTitledPane, javaFXCSSTitledPane, nodeTitledPane, textTitledPane, internalTitledPane, positionTitledPane, transformTitledPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // sets up the directoryChooser for exporting template files
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        // sets up fileChooser for selecting fxml files to edit
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        // sets the working folder
        try {
            workingFolder = new File(new File(resourcePath.toURI()), "fxmlWorkingFolder");
            workingFolder.mkdir();
            // clear the content of this folder
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // add tab listener
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                workingFileList = workingFolder.listFiles();
                for (File file : workingFileList) { // searches for the file that matches the tab and sets the workingFile
                    if (file.getName().equals(newTab.getText())) {
                        workingFile = file;
                        getListOfFxidInFxmlFile();
                        parseFXMLFile();
                        createHierarchyOfNodes();
                    }
                }
            } else {
                workingFile = null;
            }
        });
        hideEditingPanels();
        // add treeview listener
        HierarchyTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                if (newItem.getValue() instanceof Element) {
                    createEditingPanels(newItem.getValue());
                }
            } else {
                // clear editing panel content
                hideEditingPanels();
            }
        });
        // add stylechoicebox listener

        ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1 != null) {
                    String styleString = getSelectedNode().getAttribute("style");
                    String searchString = "(-fx-background-color:)(.[^\"]+)(;)";
                    String fxBackgroundColor = null;
                    Matcher matcher = Pattern.compile(searchString).matcher(styleString); // uses regex
                    while (matcher.find()) { // find() method applies the global regex flag
                        fxBackgroundColor = matcher.group();
                    }
                    if (t1.equals("NONE")){
                        //remove styling of background
                        if (fxBackgroundColor != null) {
                            styleString = styleString.replace(fxBackgroundColor,"");
                        }
                        getSelectedNode().setAttribute("style", styleString);
                        styleTextField.setText(styleString);
                    } else if (t1.equals("-fx-background-color")){
                        //add styling if it does not already exist
                        double red = styleColorPicker.getValue().getRed() * 255;
                        double green = styleColorPicker.getValue().getGreen() * 255;
                        double blue = styleColorPicker.getValue().getBlue() * 255;
                        double opacity = styleColorPicker.getValue().getOpacity();
                        String rgba = "rgba(" + red + "," + green + "," + blue + "," + opacity + ")";
                        if (!getSelectedNode().getAttribute("style").contains("-fx-background-color")) {
                            styleString += "-fx-background-color: " + rgba + ";";
                        } else {
                            styleString.replace(fxBackgroundColor, "-fx-background-color: " + rgba + ";");
                        }
                        getSelectedNode().setAttribute("style", styleString);
                        styleTextField.setText(styleString);
                    }
                }
            }
        };
        styleChoiceBox.getSelectionModel().selectedItemProperty().addListener(listener);


        // Initialize searchField-variable here
        searchFieldearchId = new TextField();
    }

    // returns a list of files in the template folder
    public List<File> getTemplates() {
        List<File> templateList = new ArrayList<>();
        try {
            File templateFolder = new File(templateFolderPath.toURI()); // gets the template folder might be moved to initialize method
            if (templateFolder.exists() && templateFolder.isDirectory()){ // checks if the folder is a folder and exist
                templateList = Arrays.asList(templateFolder.listFiles()); // lists all the files in the folder
                return templateList;
            }
        } catch (URISyntaxException e) {
            System.out.println("failed creating templateFolder file object: " + e);
        }
        return templateList;
    }

    //exports a file from one location to another
    private void exportFile(File origin, File destination) {
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(origin)); // reads from the origin file
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(destination)); // writes to the destination file
            int character; // the current character in int format
            while ((character = bReader.read()) != -1) { // gives -1 when finished
                bWriter.write(character); // writes the current character to the file
            }
            bReader.close();
            bWriter.close();
        } catch (IOException e) {
            System.out.println(e + ": export failed");
        }
    }

    //exports all template files from template folder, to the specified folder
    public void exportAllTemplates() {
        List<File> templatesList = getTemplates(); // gets a list of all templates
        File destinationFolder = directoryChooser.showDialog(null); // gets the destination folder,
        // make an if to check if there is a folder choosen, it still creates the files if you close the folderChooser
        for (File file : templatesList) { // each file gets copied to the destination folder
            exportFile(file, new File(destinationFolder, file.getName()));
        }
    }

        // moves the choosen file from specified location to the working folder needs to be a fxml file
        // remember the file path for the choosen file with the name of that file in a map
        // create a tab showing the specified file
    public void importFxmlFile() throws IOException { // if adding a fxml file with n fx include, the include must be imported first, also it fails and still at it to the map so you cant add the page agian
        File importedFxmlFile = fileChooser.showOpenDialog(null);
        // might make variable for the name since I use it so much
        String fileExtension = importedFxmlFile.getName().substring(importedFxmlFile.getName().lastIndexOf("."));
        if (importedFxmlFile.isFile() && fileExtension.equals(".fxml") && !workingFiles.containsKey(importedFxmlFile.getName())/* && does the file already exist in the working directory or the map*/ ) {
            workingFiles.put(importedFxmlFile.getName(), importedFxmlFile);
            File tempFile = new File(workingFolder, importedFxmlFile.getName());
            exportFile(importedFxmlFile, tempFile);
            Parent root = new FXMLLoader().load(tempFile.toURI().toURL());
            SubScene subScene = new SubScene(root, 1920, 1080);
            subScene.setDisable(true);
            ScrollPane scrollPane = new ScrollPane(subScene);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            Tab newTab = new Tab(importedFxmlFile.getName(), scrollPane);
            newTab.setClosable(true);
            newTab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    // delete file from working dir
                    for (File file : workingFileList) {// needs to point to last workingFile because its getting overwritten by the tab change listener, before this executes
                        if (file.getName().equals(newTab.getText())) {
                            file.delete();
                        }
                    }
                    // remove entry in map
                    workingFiles.remove(newTab.getText());
                }
            });
            tabPane.getTabs().add(newTab);
        }
    }
    public void getListOfFxidInFxmlFile() {
        fxidList.clear();
        String searchString = "(?<=fx:id=\")(.[^\"]+)(?=\")"; // gets the inside of fx:id=" and "
        String fxmlContent = getFxmlContentAsString();

        Matcher matcher = Pattern.compile(searchString).matcher(fxmlContent); // uses regex
        while (matcher.find()) { // find() method applies the global regex flag
            fxidList.add(matcher.group());
        }
    }
    
    //Delete file using search id
    @FXML
    private void handleDeleteButtonAction() {
        String searchId = searchFieldearchId.getText();

        File fileToDelete = searchForFileById(searchId);

        if (fileToDelete != null) {
            fileToDelete.delete();
            System.out.println("File deleted successfully");
        } else {
            System.out.println("File not found");
        }
    }
        // method to search about a particular file by id
    private File searchForFileById (String searchId) {
        try {
            File templatefolder = new File(templateFolderPath.toURI());
            if (templatefolder.exists() && templatefolder.isDirectory()) {
                for (File file : templatefolder.listFiles()) {
                    if (file.getName().startsWith(searchId)) {
                        return file;
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
          }
        return null;
    }
    public String getControllerInFxmlFile() {
        String searchString = "(?<=fx:controller=\")(.[^\"]+)(?=\")"; // gets the inside of fx:id=" and "
        String fxmlContent = getFxmlContentAsString();
        String controller = null;
        Matcher matcher = Pattern.compile(searchString).matcher(fxmlContent); // uses regex
        while (matcher.find()) { // find() method applies the global regex flag
            controller = matcher.group();
        }
        return controller; // will return null if no matches found
    }

        // method for getting all the file content inside a string and return it
    public String getFxmlContentAsString() {
        String fileString = "";
        try (BufferedReader bReader = new BufferedReader(new FileReader(workingFile))) { // reads from the working file, closes automatically
            String line;
            while ((line = bReader.readLine()) != null) { // gives -1 when finished
                fileString += line;
                fileString += "\n";
            }
        } catch (IOException e) {
            System.out.println(e + "has occurred when reading working file");
        }
        return fileString;
    }

    public void overwriteFxmlFileWithString(String content) {
        try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(workingFile))) {
            bWriter.write(content);
        } catch (IOException e) {
            System.out.println(e + "has occurred when writing changes to working file");
        }
    }

    public void changeControllerOnFxmlFile() {
        // getting fxml file content as string
        String fileString = getFxmlContentAsString();
        // if there is written something in the inputfield then we need to create one
        String controllerName = getControllerInFxmlFile();
        if (controllerName != null) {
            // rewrite to the working file
            fileString = fileString.replaceFirst(controllerName,controllerNameInput.getText());
            overwriteFxmlFileWithString(fileString);
        } else if (!controllerNameInput.getText().isEmpty()) { // is null meaning there is not a controller, so we need to add one if the input field have an input
            // missing implementation!
            System.out.println("does not contain a controller field, changes was not made");
        }
    }


    public void createHierarchyOfNodes() {
        Element rootElement = document.getDocumentElement();
        TreeItem<Element> root = new TreeItem<>(rootElement);
        root.setExpanded(true);
        createNodeItem(rootElement, root);
        HierarchyTreeView.setCellFactory(new Callback<TreeView<Element>, TreeCell<Element>>() {
            @Override
            public TreeCell<Element> call(TreeView<Element> nodeTreeView) {
                return new ElementTreeCell();
            }
        });
        HierarchyTreeView.setRoot(root);
    }

    public void createNodeItem(Element element, TreeItem<Element> parent) {
        NodeList children = element.getChildNodes();
        for (int i=0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element child = (Element) children.item(i);
                TreeItem<Element> childItem = new TreeItem<>(child);
                childItem.setExpanded(true);
                parent.getChildren().add(childItem);
                createNodeItem(child,childItem);
            }
        }
    }

    public Element getSelectedNode() {
        Element node = null;
        if (HierarchyTreeView.getSelectionModel().getSelectedItem() != null) {
            Element selectedNode = HierarchyTreeView.getSelectionModel().getSelectedItem().getValue();
            node = selectedNode;
        }
        return node;
    }

    public void saveChangesToOriginalFile() {
        // might need to place the applyChanges function here
        exportFile(workingFile, workingFiles.get(workingFile.getName()));
    }

    //




    public void parseFXMLFile() {
        try {
            if (workingFile.isFile() && workingFile.exists()) {
                DocumentBuilderFactory dBFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dBFactory.newDocumentBuilder();
                document = dBuilder.parse(workingFile);
                document.getDocumentElement().normalize();
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void saveChanges() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileWriter(workingFile));
            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
        updateChanges();
    }

    public void updateChanges() {
        try {
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                Tab tab = tabPane.getTabs().get(i);
                if (tab.getText().equals(workingFile.getName())) {
                    Parent root = new FXMLLoader().load(workingFile.toURI().toURL());
                    SubScene subScene = new SubScene(root, 1920, 1080);
                    tab.setContent(new ScrollPane(subScene));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // test functions:
    public void hideEditingPanels(){
        List<TitledPane> editingPanels = new ArrayList<>();
        editingPanels.add(sizeTitledPane);
        editingPanels.add(identityTitledPane);
        editingPanels.add(javaFXCSSTitledPane);
        editingPanels.add(nodeTitledPane);
        editingPanels.add(textTitledPane);
        editingPanels.add(internalTitledPane);
        editingPanels.add(positionTitledPane);
        editingPanels.add(transformTitledPane);
        for (TitledPane titledPane : editingPanels) {
            titledPane.setVisible(false);
            titledPane.setDisable(true);
            titledPane.setExpanded(false);
        }
    }

    public void createEditingPanels(Element element){
        // need to clear what was in the editingPanels before running anything else in here
        hideEditingPanels();
        switch (element.getTagName()) {
            case "Pane", "Label", "VBox", "HBox", "Hyperlink", "Button" -> {
                propertiesTitledPane.setText("Properties :: " + element.getTagName());
                //createEditingPanelNode(element);
                if (element.getTagName().equals("Label") || element.getTagName().equals("Hyperlink") || element.getTagName().equals("Button")){
                    createEditingPanelText(element);
                }
                createEditingPanelJavaFXCSS(element);
                layoutTitledPane.setText("Layout :: " + element.getTagName());
                createEditingPanelSize(element);
                codeTitledPane.setText("Code :: " + element.getTagName());
                createEditingPanelIdentity(element);
            }
            default -> {
                System.out.println("The chosen element is not supported");
                propertiesTitledPane.setText("Properties");
                layoutTitledPane.setText("Layout");
                codeTitledPane.setText("Code");
            }
        }
    }

    public void createEditingPanelNode(Element element) {

    }

    public void createEditingPanelText(Element element) {
        textTitledPane.setVisible(true);
        textTitledPane.setDisable(false);
        textTitledPane.setExpanded(true);
        textTextField.setText(element.getAttribute("text"));
    }

    public void changeTextOnElement() {
        if (!textTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("text", textTextField.getText());
        } else {
            getSelectedNode().removeAttribute("text");
        }
        saveChanges();
    }
    public void createEditingPanelJavaFXCSS(Element element) {
        javaFXCSSTitledPane.setVisible(true);
        javaFXCSSTitledPane.setDisable(false);
        javaFXCSSTitledPane.setExpanded(true);
        styleTextField.setText(element.getAttribute("style"));
        styleChoiceBox.getItems().clear();
        styleChoiceBox.getItems().add("NONE");
        styleChoiceBox.getItems().add("-fx-background-color");
        if (element.getAttribute("style").contains("-fx-background-color")) {
            styleChoiceBox.setValue("-fx-background-color");
            String styleString = element.getAttribute("style");
            String searchString = "(?:rgba\\()(.+),(.+),(.+),(.+)(?:\\);)";
            double red = 0;
            double green = 0;
            double blue = 0;
            double opacity = 0;
            Matcher matcher = Pattern.compile(searchString).matcher(styleString); // uses regex
            while (matcher.find()) { // find() method applies the global regex flag
                red = Double.valueOf(matcher.group(1)) / 255;
                green = Double.valueOf(matcher.group(2)) / 255;
                blue = Double.valueOf(matcher.group(3)) / 255;
                opacity = Double.valueOf(matcher.group(4));
            }
            styleColorPicker.setValue(new Color(red, green, blue, opacity));
            styleTextField.setText(element.getAttribute("style"));
        } else {
            styleChoiceBox.setValue("NONE");
        }
    }

    public void changeJavaFXCSSOnElement() {
        if (!styleTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("style", styleTextField.getText());
            if (styleTextField.getText().contains("-fx-background-color")) {
                styleChoiceBox.setValue("-fx-background-color");
                String styleString = styleTextField.getText();
                String searchString = "(?:rgba\\()(.+),(.+),(.+),(.+)(?:\\);)";
                double red = 0;
                double green = 0;
                double blue = 0;
                double opacity = 0;
                Matcher matcher = Pattern.compile(searchString).matcher(styleString); // uses regex
                while (matcher.find()) { // find() method applies the global regex flag
                    red = Double.valueOf(matcher.group(1)) / 255;
                    green = Double.valueOf(matcher.group(2)) / 255;
                    blue = Double.valueOf(matcher.group(3)) / 255;
                    opacity = Double.valueOf(matcher.group(4));
                }
                styleColorPicker.setValue(new Color(red, green, blue, opacity));
            }
        } else {
            styleChoiceBox.setValue("NONE");
            getSelectedNode().removeAttribute("style");
        }
        saveChanges();
    }
    public void changeJavaFXCSSOnElementColorPicker() {
        String styleString = styleTextField.getText();
        String searchString = "(-fx-background-color:)(.[^\"]+)(;)";
        String fxBackgroundColor = null;
        Matcher matcher = Pattern.compile(searchString).matcher(styleString); // uses regex
        while (matcher.find()) { // find() method applies the global regex flag
            fxBackgroundColor = matcher.group();
        }
        double red = styleColorPicker.getValue().getRed() * 255;
        double green = styleColorPicker.getValue().getGreen() * 255;
        double blue = styleColorPicker.getValue().getBlue() * 255;
        double opacity = styleColorPicker.getValue().getOpacity();
        String rgba = "rgba(" + red + "," + green + "," + blue + "," + opacity + ")";
        if (!styleTextField.getText().contains("-fx-background-color")) {
            styleString += "-fx-background-color: " + rgba + ";";
        } else {
            styleString = styleString.replace(fxBackgroundColor, "-fx-background-color: " + rgba + ";");
        }
        getSelectedNode().setAttribute("style", styleString);
        styleTextField.setText(styleString);
        saveChanges();
    }

    public void createEditingPanelSize(Element element) {
        sizeTitledPane.setVisible(true);
        sizeTitledPane.setDisable(false);
        sizeTitledPane.setExpanded(true);
        minWidthTextField.setText(element.getAttribute("minWidth"));
        minHeightTextField.setText(element.getAttribute("minHeight"));
        prefWidthTextField.setText(element.getAttribute("prefWidth"));
        prefHeightTextField.setText(element.getAttribute("prefHeight"));
        maxWidthTextField.setText(element.getAttribute("maxWidth"));
        maxHeightTextField.setText(element.getAttribute("maxHeight"));
    }

    public void createEditingPanelIdentity(Element element) {
        identityTitledPane.setVisible(true);
        identityTitledPane.setDisable(false);
        identityTitledPane.setExpanded(true);
        fxidTextField.setText(element.getAttribute("fx:id"));
    }

    public void changeIdentityOnElement(){
        if (!fxidTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("fx:id", fxidTextField.getText());
        } else {
            getSelectedNode().removeAttribute("fx:id");
        }
        saveChanges();
    }

    public void changeSizeOnElement(){
        if (!minWidthTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("minWidth", minWidthTextField.getText());
        } else {
            getSelectedNode().removeAttribute("minWidth");
        }
        if (!minHeightTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("minHeight", minHeightTextField.getText());
        } else {
            getSelectedNode().removeAttribute("minHeight");
        }
        if (!prefWidthTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("prefWidth", prefWidthTextField.getText());
        } else {
            getSelectedNode().removeAttribute("prefWidth");
        }
        if (!prefHeightTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("prefHeight", prefHeightTextField.getText());
        } else {
            getSelectedNode().removeAttribute("prefHeight");
        }
        if (!maxWidthTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("maxWidth", maxWidthTextField.getText());
        } else {
            getSelectedNode().removeAttribute("maxWidth");
        }
        if (!maxHeightTextField.getText().isEmpty()) {
            getSelectedNode().setAttribute("maxHeight", maxHeightTextField.getText());
        } else {
            getSelectedNode().removeAttribute("maxHeight");
        }
        saveChanges();
    }
    public void removeSelectedNode(){
        getSelectedNode().getParentNode().removeChild(getSelectedNode());
        saveChanges();
        createHierarchyOfNodes();
    }
    public void addChildToSelectedNode(){
        Element child = document.createElement(addChildNodeNameTextField.getText());
        getSelectedNode().appendChild(child);
        saveChanges();
        createHierarchyOfNodes();
    }
}