package ui;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Classifier;
import algorithms.Clusterer;
import allAlgo.RandomClassifier;
import allAlgo.KMeansClusterer;
import allAlgo.RandomClusterer;
import components.ExitDialog;
import data.DataSet;
import dataprocessors.AppData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ListChangeListener;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;

import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import java.io.File;
import java.util.Arrays;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number>    chart;          // the chart where data will be displayed
    //    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox                     readOnly;

    private static final String SEPARATOR = "/";
    private String screenshotPath;

    private Text text2;
    private RadioButton typeAlgorithm1;
    private RadioButton typeAlgorithm2;
    private ToggleButton doneButton;
    private ToggleButton editButton;
    private VBox algorithmsBox = new VBox();
    private Text algoTitle;

    private ToggleGroup algoSelectToggleGroup;
    private Button runButton;

    private HBox algorithmWSettingsBox;
    private HBox algorithmWSettingsBox2;

    private boolean setConfig = false;
    private boolean threadRunning = false;


    private ArrayList<String[]> allPrevInputClassif = new ArrayList<String[]>();
    private ArrayList<String[]> allPrevInputClust = new ArrayList<String[]>();

    private String[] prevData = {"1", "1", "2", "0"};
    private String[] allAlgo = new String[3];

    public LineChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;

        String iconsPath = SEPARATOR + String.join(SEPARATOR,
                manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));

        screenshotPath = String.join(SEPARATOR, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        super.setToolBar(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        scrnshotButton = setToolbarButton(screenshotPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
        toolBar.getItems().add(scrnshotButton);
    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
        scrnshotButton.setOnAction(e -> {
            try {
                ((AppActions)applicationTemplate.getActionComponent()).handleScreenshotRequest();
            } catch (IOException e1) {
                if(chart.getData().isEmpty()){
                    scrnshotButton.setDisable(true);
                }
            }
        });
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        textArea.clear();
        //   chart.getData().clear();
    }

    private void layout() {
        reflectionAlgo();

        ExitDialog exit1 = ExitDialog.getDialog();
        exit1.init(primaryStage);

        newButton.setDisable(false);


        PropertyManager manager = applicationTemplate.manager;

        HBox textPane = new HBox(500);

        Text text1 = new Text();
        text1.setText(manager.getPropertyValue(TEXT_AREA.name()));

        textPane.getChildren().add(text1);


        HBox layoutPane = new HBox();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();


        chart = new LineChart<>(xAxis, yAxis);

        chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));


        textArea = new TextArea();
        chart.setMaxHeight(400);

        VBox textInfoBox = new VBox();


        textInfoBox.getChildren().add(textArea);
//        displayButton = new Button();
//        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON_NAME.name()));

        layoutPane.getChildren().addAll(textInfoBox, chart);
        //  layoutPane.getChildren().addAll(textArea, chart);

        readOnly = new CheckBox(applicationTemplate.manager.getPropertyValue(READ_ONLY_TITLE.name()));
        readOnly.setSelected(false);

        text2 = new Text();

//        appPane.getChildren().addAll(textPane, layoutPane, displayButton, readOnly);

        appPane.getChildren().addAll(textPane, layoutPane, readOnly);


        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);

        appPane.getStylesheets().add(applicationTemplate.manager.getPropertyValue(STYLE_SHEET_PATH.name()));

        hasNewText = false;
        textArea.setVisible(false);
//        displayButton.setVisible(false);
        readOnly.setVisible(false);
        text1.setVisible(false);

        typeAlgorithm1 = new RadioButton(applicationTemplate.manager.getPropertyValue(ALGO_TYPE_1.name()));
        typeAlgorithm2 = new RadioButton(applicationTemplate.manager.getPropertyValue(ALGO_TYPE_2.name()));


        typeAlgorithm1.setSelected(false);
        typeAlgorithm2.setSelected(false);
        typeAlgorithm1.setVisible(false);
        typeAlgorithm2.setVisible(false);


        doneButton = new ToggleButton(applicationTemplate.manager.getPropertyValue(DONE_BUTTON_NAME.name()));
        editButton = new ToggleButton(applicationTemplate.manager.getPropertyValue(EDIT_BUTTON_NAME.name()));
        ToggleGroup group = new ToggleGroup();

        doneButton.setToggleGroup(group);
        editButton.setToggleGroup(group);


        algoTitle = new Text(applicationTemplate.manager.getPropertyValue(ALGO_NAME.name()));
        algoTitle.setVisible(false);

        textInfoBox.getChildren().addAll(doneButton, editButton, text2, algoTitle, typeAlgorithm1, typeAlgorithm2, algorithmsBox);

        doneButton.setVisible(false);
        editButton.setVisible(false);

        allPrevInputClassif.clear();
        allPrevInputClust.clear();

        String[] p = {"1", "1", "2", "0"};
        allPrevInputClassif.add(prevData);
        allPrevInputClassif.add(p);

        String[] c1 = {"1", "1", "2", "0"};
        String[] c2 = {"1", "1", "2", "0"};

        allPrevInputClust.add(c1);
        allPrevInputClust.add(c2);

    }

    private void setWorkspaceActions() {

        //       displayButton.setOnAction(e -> ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText()));

        String oldText = textArea.getText();


        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            if(((AppActions) applicationTemplate.getActionComponent()).getCheck()) {
                String a = "";
                ArrayList<String> notDisplayed = ((AppData) applicationTemplate.getDataComponent()).getLinesLeft();

                int deletedAmount = 10 - newValue.split("\n").length;
                if (deletedAmount > 0 && notDisplayed.size() > 0) {
                    while (deletedAmount > 0) {
                        if (notDisplayed.size() > 0) {
                            a += notDisplayed.remove(0);
                        }
                        deletedAmount--;
                    }


                }
                textArea.setText(textArea.getText() + a);
            }

            if(!oldText.equals(textArea.getText()) && !hasNewText){
                hasNewText = true;
            }


            if(!textArea.getText().isEmpty()){
                newButton.setDisable(false);
            }else{
                newButton.setDisable(true);
                saveButton.setDisable(true);
            }

            if(hasNewText){
                saveButton.setDisable(false);
                hasNewText = false;
            }

        });

        chart.getData().addListener((ListChangeListener<XYChart.Series<Number, Number>>) c -> {
            if(chart.getData().isEmpty()){
                scrnshotButton.setDisable(true);
            }
//            else{
//                scrnshotButton.setDisable(false);
//            }
        });

        readOnly.setOnMouseClicked(event -> checkingBox());

        //user is done editing -> textArea is disabled -> clears previous info -> loads data -> checks to see if user input is valid -> get info of instances and labels
        //->checks for algorithms validity -> displays info
        doneButton.setOnAction(e -> {
            textArea.setDisable(true);
            clearInfoandButton();
            ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText());

            if(((AppData) applicationTemplate.getDataComponent()).getValidity()){
                ((AppData)applicationTemplate.getDataComponent()).getInfoForNew();
                validTypeOfAlgorithm();
                typeAlgorithm1.setSelected(false);
                typeAlgorithm2.setSelected(false);
                algoTitle.setVisible(true);
                text2.setVisible(true);
            }
        });

        //if edit button is clicked, text area is typable and clears all info
        editButton.setOnMouseClicked(event -> {
            textArea.setDisable(false);
            clearInfoandButton();
            algorithmsBox.setVisible(false);
        });

    }

    /**
     * if the readOnly box is checked then textArea is disabled and no edits can be done
     */
    public void checkingBox(){
        if(readOnly.isSelected()) {
            textArea.setDisable(true);
        }
        else{
            textArea.setDisable(false);
        }
    }

    /**
     *
     * @return text in the textArea
     */
    public String getTextArea(){
        return textArea.getText();
    }

    /**
     *
     * @param text
     * sets TextArea
     */
    public void setTextArea(String text){
        textArea.setText(text);
    }

    /**
     *
     * @return saveButton
     */
    public Button getSaveButton(){
        return saveButton;
    }

    /**
     * textArea shows up and is disabled -> checks for which buttons to show based on algorithm validity -> text2 info is visible
     */
    public void textAreaVisibility(){
        textArea.setVisible(true);
//        displayButton.setVisible(true);
//        readOnly.setVisible(true);
        readOnly.setSelected(true);
        checkingBox();
        validTypeOfAlgorithm();
        text2.setVisible(true);
    }

    public void validTypeOfAlgorithm(){
        //     System.out.println("passeed " + ((AppData)applicationTemplate.getDataComponent()).returnNumofLabels());

        if(((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() == 2){
            ToggleGroup selectingAlgorithmType = new ToggleGroup();
            typeAlgorithm1.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm2.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm1.setVisible(true);
            typeAlgorithm2.setVisible(true);

            selectingAlgorithmType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov,
                                    Toggle old_toggle, Toggle new_toggle) {
                    if (selectingAlgorithmType.getSelectedToggle() != null) {

                        algorithmsBox.getChildren().clear();
//                        typeAlgorithm1.setVisible(false);
//                        typeAlgorithm2.setVisible(false);
                        algoTitle.setVisible(false);
                        runButton = new Button(applicationTemplate.manager.getPropertyValue(RUN_BUTTON_TITLE.name()));

                        algorithmWSettingsBox = new HBox();
                        algorithmWSettingsBox2 = new HBox();
                        algoSelectToggleGroup = new ToggleGroup();

                        if(selectingAlgorithmType.getSelectedToggle() == typeAlgorithm1) {
                            Text classification = new Text(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_TITLE.name()));

                            displayAlgorithmforReflection("Classification");
                            algorithmsBox.getChildren().addAll(classification, algorithmWSettingsBox, algorithmWSettingsBox2);
                        }else{

                            Text clustering = new Text(applicationTemplate.manager.getPropertyValue(CLUSTERING_TITLE.name()));
                            displayAlgorithmforReflection("Clustering");
                            algorithmsBox.getChildren().addAll(clustering, algorithmWSettingsBox, algorithmWSettingsBox2);
                        }
                        algorithmsBox.getChildren().add(runButton);
                        algorithmsBox.setVisible(true);
                        runButton.setVisible(false);
                        runButton.setDisable(true);

                        algoSelectToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                            public void changed(ObservableValue<? extends Toggle> ov,
                                                Toggle old_toggle, Toggle new_toggle) {
                                if (algoSelectToggleGroup.getSelectedToggle() != null){
                                    runButton.setVisible(true);
                                    runButton.setDisable(true);
                                }
                            }
                        });
                    }
                }
            });
        }else if(((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() == 1 || ((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() > 2 || ((AppData)applicationTemplate.getDataComponent()).returnNullLabel() == 1){
            ToggleGroup selectingAlgorithmType = new ToggleGroup();
            typeAlgorithm2.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm2.setVisible(true);
            typeAlgorithm1.setVisible(false);


            selectingAlgorithmType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov,
                                    Toggle old_toggle, Toggle new_toggle) {
                    if (selectingAlgorithmType.getSelectedToggle() != null) {

                        algorithmsBox.getChildren().clear();
//                        typeAlgorithm1.setVisible(false);
//                        typeAlgorithm2.setVisible(false);
                        algoTitle.setVisible(false);

                        algorithmWSettingsBox = new HBox();
                        algorithmWSettingsBox2 = new HBox();
                        runButton = new Button(applicationTemplate.manager.getPropertyValue(RUN_BUTTON_TITLE.name()));

                        algoSelectToggleGroup = new ToggleGroup();

                        Text clustering = new Text(applicationTemplate.manager.getPropertyValue(CLUSTERING_TITLE.name()));

                        displayAlgorithmforReflection("Clustering");

                        algorithmsBox.getChildren().addAll(clustering, algorithmWSettingsBox, algorithmWSettingsBox2);

                        algorithmsBox.getChildren().add(runButton);
                        algorithmsBox.setVisible(true);
                        runButton.setVisible(false);
                        runButton.setDisable(true);

                        algoSelectToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                            public void changed(ObservableValue<? extends Toggle> ov,
                                                Toggle old_toggle, Toggle new_toggle) {
                                if (algoSelectToggleGroup.getSelectedToggle() != null){
                                    runButton.setVisible(true);
                                    runButton.setDisable(true);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void displayClusteringPart(String algo1){
//        RadioButton randomClusteringButton = new RadioButton(applicationTemplate.manager.getPropertyValue(RANDO_CLUST.name()));
//        RadioButton someOtherClusterButton = new RadioButton(applicationTemplate.manager.getPropertyValue(SOME_RAND_CLUST.name()));
//        randomClusteringButton.setToggleGroup(algoSelectToggleGroup);
//        someOtherClusterButton.setToggleGroup(algoSelectToggleGroup);
//
//        Text clustering = new Text(applicationTemplate.manager.getPropertyValue(CLUSTERING_TITLE.name()));
//        randomClusteringButton.setSelected(false);
//        someOtherClusterButton.setSelected(false);
//
//        Button settingsButton = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
//        Button settingsButton2 = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
//
//        settingsButton.setOnMouseClicked(event -> {
//            popUpWindow("Clustering", 0);
//            runButton.setDisable(false);
//            DataSet l = new DataSet();
//            try {
//                runButtonAction(runButton, l, "Clustering", 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        settingsButton2.setOnMouseClicked(event -> {
//            popUpWindow("Clustering", 1);
//            runButton.setDisable(false);
//            DataSet l = new DataSet();
//            try {
//                runButtonAction(runButton, l, "Clustering", 1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//            algorithmWSettingsBox.getChildren().addAll(randomClusteringButton, settingsButton);
//            algorithmWSettingsBox2.getChildren().addAll(someOtherClusterButton, settingsButton2);

        if(algo1.equals("Random")){
            RadioButton randomClusteringButton = new RadioButton(applicationTemplate.manager.getPropertyValue(RANDO_CLUST.name()));
            randomClusteringButton.setToggleGroup(algoSelectToggleGroup);
            randomClusteringButton.setSelected(false);
            Button settingsButton = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
            settingsButton.setOnMouseClicked(event -> {
                popUpWindow("Clustering", 0);
                runButton.setDisable(false);
                DataSet l = new DataSet();
                try {
                    runButtonAction(runButton, l, "Clustering", 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            algorithmWSettingsBox.getChildren().addAll(randomClusteringButton, settingsButton);
        }else{
            RadioButton someOtherClusterButton = new RadioButton(applicationTemplate.manager.getPropertyValue(SOME_RAND_CLUST.name()));
            someOtherClusterButton.setToggleGroup(algoSelectToggleGroup);

            someOtherClusterButton.setSelected(false);

            Button settingsButton2 = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));


            settingsButton2.setOnMouseClicked(event -> {
                popUpWindow("Clustering", 1);
                runButton.setDisable(false);
                DataSet l = new DataSet();
                try {
                    runButtonAction(runButton, l, "Clustering", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
                algorithmWSettingsBox2.getChildren().addAll(someOtherClusterButton, settingsButton2);
        }

    }

    public void displayClassificationPart(){
        RadioButton randomClassificationButton = new RadioButton(applicationTemplate.manager.getPropertyValue(RANDO_CLASSIF.name()));
        RadioButton someotherClassButton = new RadioButton(applicationTemplate.manager.getPropertyValue(SOME_RAND_CLASS.name()));

        randomClassificationButton.setToggleGroup(algoSelectToggleGroup);
        someotherClassButton.setToggleGroup(algoSelectToggleGroup);

        someotherClassButton.setSelected(false);
        randomClassificationButton.setSelected(false);


        Text classification = new Text(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_TITLE.name()));
        Button settingsButton = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
    //    Button settingsButton2 = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));

        settingsButton.setOnMouseClicked(event -> {
            popUpWindow("Classification", 0);
            runButton.setDisable(false);
            runButton.setText("Run");

            DataSet l = new DataSet();
            try {
                runButtonAction(runButton, l, "Classification", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

//        settingsButton2.setOnMouseClicked(event -> {
//            popUpWindow("Classification", 1);
//            runButton.setDisable(true);
//
//        });

        algorithmWSettingsBox.getChildren().addAll(randomClassificationButton, settingsButton);
//        algorithmWSettingsBox2.getChildren().addAll(someotherClassButton, settingsButton2);
    }

    /**
     *  resets the text, textArea, and radio buttons
     */
    public void clearInfoandButton(){
        text2.setText("");
        readOnly.setSelected(false);
        typeAlgorithm1.setVisible(false);
        typeAlgorithm2.setVisible(false);
        textArea.setVisible(true);
        textArea.setDisable(false);

        algoTitle.setVisible(false);

    }

    /**
     *
     * @param infoText
     *
     * assigns # of instance, unique labels, and names to text2
     */
    public void setTextInfo(String infoText){
        text2.setText(infoText);
        text2.setVisible(true);
    }

    /**
     * toggle buttons become visible for when the "new button" is clicked
     */
    public void setToggleButton(){
        doneButton.setVisible(true);
        editButton.setVisible(true);
        editButton.setSelected(true);
    }

    /**
     * hides the toggle buttons when data is loaded so user cannot edit the data
     */
    public void loadDisableButtons(){
        doneButton.setVisible(false);
        editButton.setVisible(false);
        typeAlgorithm1.setSelected(false);
        typeAlgorithm2.setSelected(false);
    }

    /**
     * makes selecting algorithms box invisible and clears all the radio buttons in the vbox
     */
    public void clearSelectingAlgorithmsBox(){
        algorithmsBox.setVisible(false);
        algorithmsBox.getChildren().clear();
        algoTitle.setVisible(false);
    }

    /**
     *
     * @return title for Algorithm Type
     */
    public Text getAlgoTitle(){
        return algoTitle;
    }

    public void popUpWindow(String type, int num){
        setConfig = false;

        if(type.equals("Classification")){
            RunConfiguration runConfig = RunConfiguration.getDialog(type, allPrevInputClassif.get(num));
            runConfig.init(primaryStage);
            allPrevInputClassif.set(num, runConfig.returnPrevInput());

            runConfig.showAndWait();
        }else{
            RunConfiguration runConfig = RunConfiguration.getDialog(type, allPrevInputClust.get(num));
            runConfig.init(primaryStage);
            allPrevInputClust.set(num, runConfig.returnPrevInput());

            runConfig.showAndWait();
        }


        setConfig = true;
    }

    public void makeAlgorithmandRunButtonVisibleAgain(){
        typeAlgorithm1.setVisible(true);
        typeAlgorithm2.setVisible(true);
        runButton.setDisable(false);
        scrnshotButton.setDisable(false);
        doneButton.setDisable(false);
        editButton.setDisable(false);
    }

    public void makeClassificationInvisible(){
        typeAlgorithm1.setVisible(false);
    }

    public void resetPrev(){
        String[] b = {"1", "1", "2", "0"};
        String[] t = {"1", "1", "2", "0"};
        String[] s = {"1", "1", "2", "0"};
        String[] p = {"1", "1", "2", "0"};
        allPrevInputClassif.clear();
        allPrevInputClassif.add(b);
        allPrevInputClassif.add(t);

        allPrevInputClust.clear();
        allPrevInputClust.add(s);
        allPrevInputClust.add(p);
    }

    public void disableButton(){
        runButton.setDisable(true);
    }

    public void changeTextofRunButton(String x){
        runButton.setText(x);
    }

    public void runButtonAction(Button run, DataSet x, String type, int num) throws Exception{
        if(type.equals("Classification") && num == 0) {
            int maxInt = Integer.parseInt(allPrevInputClassif.get(num)[0]);
            int updateInt = Integer.parseInt(allPrevInputClassif.get(num)[1]);

            boolean continuous = false;
            if(allPrevInputClassif.get(num)[3].equals("0")){
                continuous = false;
            }else{
                continuous = true;
            }
            boolean finalContinuous = continuous;

            Class ranClassifier = Class.forName(allAlgo[1]);
            Constructor construct1 = ranClassifier.getConstructors()[0];
            Algorithm alg = (Algorithm) construct1.newInstance(x, maxInt, updateInt, finalContinuous, applicationTemplate);

    //        RandomClassifier ranClassifier = new RandomClassifier(x, maxInt, updateInt, finalContinuous, applicationTemplate);

            threadRunning = false;;

            runButton.setOnAction(event -> {
                chart.setVisible(true);
                if(threadRunning){
                    synchronized (alg){
                        runButton.setDisable(true);
                        scrnshotButton.setDisable(true);
                        alg.notify();
                    }
                }else {
                    Thread thread1 = new Thread(alg);
                    thread1.start();
                    runButton.setDisable(true);
                    typeAlgorithm1.setVisible(false);
                    typeAlgorithm2.setVisible(false);
                    scrnshotButton.setDisable(true);
                    doneButton.setDisable(true);
                    editButton.setDisable(true);
                }
            });

        }else if(type.equals("Clustering") && num == 0){
            int maxInt = Integer.parseInt(allPrevInputClust.get(num)[0]);
            int updateInt = Integer.parseInt(allPrevInputClust.get(num)[1]);
            int numClusters = Integer.parseInt((allPrevInputClust.get(num)[2]));

            boolean continuous = false;
            if(allPrevInputClust.get(num)[3].equals("0")){
                continuous = false;
            }else{
                continuous = true;
            }
            boolean finalContinuous = continuous;

            DataSet q = new DataSet();

            ((AppData) applicationTemplate.getDataComponent()).process(textArea.getText());
            q.setLabels(((AppData) applicationTemplate.getDataComponent()).getLabels());
            q.setLocations(((AppData) applicationTemplate.getDataComponent()).getLocationPoint());

            Class randomClust = Class.forName(allAlgo[2]);
            Constructor construct1 = randomClust.getConstructors()[0];
            Algorithm alg = (Algorithm) construct1.newInstance(q, maxInt, updateInt, finalContinuous, numClusters, applicationTemplate);

//            RandomClusterer randomClust = new RandomClusterer(q, maxInt, updateInt, finalContinuous, numClusters, applicationTemplate);
            threadRunning = false;

            runButton.setOnAction(event -> {
                if(threadRunning){
                    synchronized (alg){
                        runButton.setDisable(true);
                        scrnshotButton.setDisable(true);
                        alg.notify();
                    }
                }else {
                    chart.setVisible(true);
                    Thread thread1 = new Thread(alg);
                    thread1.start();
                    runButton.setDisable(true);
                    typeAlgorithm1.setVisible(false);
                    typeAlgorithm2.setVisible(false);
                    scrnshotButton.setDisable(true);
                    doneButton.setDisable(true);
                    editButton.setDisable(true);
                }
            });
        }else if(type.equals("Clustering") && num == 1){
            int maxInt = Integer.parseInt(allPrevInputClust.get(num)[0]);
            int updateInt = Integer.parseInt(allPrevInputClust.get(num)[1]);
            int numClusters = Integer.parseInt((allPrevInputClust.get(num)[2]));

            boolean continuous = false;
            if(allPrevInputClust.get(num)[3].equals("0")){
                continuous = false;
            }else{
                continuous = true;
            }
            boolean finalContinuous = continuous;


            DataSet q = new DataSet();

            ((AppData) applicationTemplate.getDataComponent()).process(textArea.getText());
            q.setLabels(((AppData) applicationTemplate.getDataComponent()).getLabels());
            q.setLocations(((AppData) applicationTemplate.getDataComponent()).getLocationPoint());

            Class kMeans = Class.forName(allAlgo[0]);
            Constructor construct1 = kMeans.getConstructors()[0];
            Algorithm alg = (Algorithm) construct1.newInstance(q, maxInt, updateInt, numClusters, finalContinuous, applicationTemplate);

 //           KMeansClusterer kMeans = new KMeansClusterer(q, maxInt, updateInt, numClusters, finalContinuous, applicationTemplate);
            threadRunning = false;

            runButton.setOnAction(event -> {
                if(threadRunning){
                    synchronized (alg){
                        runButton.setDisable(true);
                        scrnshotButton.setDisable(true);
                        alg.notify();
                    }
                }else {
                    chart.setVisible(true);
                    Thread thread1 = new Thread(alg);
                    thread1.start();
                    runButton.setDisable(true);
                    typeAlgorithm1.setVisible(false);
                    typeAlgorithm2.setVisible(false);
                    scrnshotButton.setDisable(true);
                    doneButton.setDisable(true);
                    editButton.setDisable(true);
                }
            });
        }
    }

    public void setThreadRunningBoolean(Boolean b){
        threadRunning = b;
    }

    public boolean getThreadRunning(){
        return threadRunning;
    }

    public boolean isHasNewText(){
        return hasNewText;
    }

    public void reflectionAlgo(){
        File folder = new File("hw1/data-vilij/src/allAlgo");
        File[] listOfFiles = folder.listFiles();

        int i = 0;
        for (File file : listOfFiles) {
            allAlgo[i] =  "allAlgo."  + file.getName().replaceAll(".java", "");
            i++;
        }
    }

    public void displayAlgorithmforReflection(String type){
        for(int i = 0; i < allAlgo.length; i++){
            try {
//                System.out.println(allAlgo[i].toString());
//                System.out.println(Class.forName(allAlgo[i]).getSuperclass());
                if(Class.forName(allAlgo[i]).getSuperclass() == Classifier.class && type.equals("Classification")){
                    displayClassificationPart();
                }else if(Class.forName(allAlgo[i]).getSuperclass() == Clusterer.class && type.equals("Clustering")){
 //                   System.out.println("here");
//                    System.out.println(allAlgo[i].contains("KMeans"));
//                    System.out.println(allAlgo[i].contains("Random"));
                  if(allAlgo[i].contains("KMeans")) {
                      displayClusteringPart("KMeans");
                  }else{
                      displayClusteringPart("Random");
                  }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
