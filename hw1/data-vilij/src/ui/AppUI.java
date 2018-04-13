package ui;

import actions.AppActions;
import dataprocessors.AppData;

import dataprocessors.TSDProcessor;
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
import java.util.ArrayList;

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
        // TODO for homework 1
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
        // TODO for homework 1
        textArea.clear();
     //   chart.getData().clear();
    }

    private void layout() {
        // TODO for homework 1

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
//        displayButton = new Button();
//        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON_NAME.name()));


        layoutPane.getChildren().addAll(textArea, chart);

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

        typeAlgorithm1.setVisible(false);
        typeAlgorithm2.setVisible(false);


        doneButton = new ToggleButton(applicationTemplate.manager.getPropertyValue(DONE_BUTTON_NAME.name()));
        editButton = new ToggleButton(applicationTemplate.manager.getPropertyValue(EDIT_BUTTON_NAME.name()));
        ToggleGroup group = new ToggleGroup();
        doneButton.setToggleGroup(group);
        editButton.setToggleGroup(group);

        algoTitle = new Text(applicationTemplate.manager.getPropertyValue(ALGO_NAME.name()));
        algoTitle.setVisible(false);

        appPane.getChildren().addAll(doneButton, editButton, text2, algoTitle, typeAlgorithm1, typeAlgorithm2, algorithmsBox);

        doneButton.setVisible(false);
        editButton.setVisible(false);


    }

    private void setWorkspaceActions() {

 //       displayButton.setOnAction(e -> ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText()));

        String oldText = textArea.getText();


        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            if(((AppActions) applicationTemplate.getActionComponent()).getCheck()) {
                String a = "";
                //       ArrayList<String> notDisplayed = ((AppActions)applicationTemplate.getActionComponent()).getLinesLeft();
                ArrayList<String> notDisplayed = ((AppData) applicationTemplate.getDataComponent()).getLinesLeft();

                int deletedAmount = 10 - newValue.split("\n").length;
//            System.out.println("amount = " + deletedAmount);
//            System.out.println("size of notdisplayed = " + notDisplayed.size());
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

//            if(!textArea.getText().isEmpty()){
//                newButton.setDisable(false);
//            //    saveButton.setDisable(false);
//            }else{
//                newButton.setDisable(true);
//                saveButton.setDisable(true);
//            }

            if(!textArea.getText().isEmpty()){
                newButton.setDisable(false);
                //    saveButton.setDisable(false);
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
            }else{
                scrnshotButton.setDisable(false);
            }
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

        typeAlgorithm1.setOnMouseClicked(event -> {
            algorithmsBox.getChildren().clear();
            typeAlgorithm1.setVisible(false);
            typeAlgorithm2.setVisible(false);
            algoTitle.setVisible(false);

            HBox algorithmWSettingsBox = new HBox();
            RadioButton randomClassificationButton = new RadioButton(applicationTemplate.manager.getPropertyValue(RANDO_CLASSIF.name()));
            randomClassificationButton.setSelected(false);
            Text classification = new Text(applicationTemplate.manager.getPropertyValue(CLASSIFICATION_TITLE.name()));
            Button settingsButton = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
            algorithmWSettingsBox.getChildren().addAll(randomClassificationButton, settingsButton);

        //   algorithmsBox.getChildren().addAll(classification, randomClassificationButton);
            algorithmsBox.getChildren().addAll(classification, algorithmWSettingsBox);
            algorithmsBox.setVisible(true);

        });

        typeAlgorithm2.setOnMouseClicked(event -> {
            algorithmsBox.getChildren().clear();
            typeAlgorithm1.setVisible(false);
            typeAlgorithm2.setVisible(false);
            algoTitle.setVisible(false);

            HBox algorithmWSettingsBox = new HBox();
            RadioButton randomClusteringButton = new RadioButton(applicationTemplate.manager.getPropertyValue(RANDO_CLUST.name()));
            Text clustering = new Text(applicationTemplate.manager.getPropertyValue(CLUSTERING_TITLE.name()));
            randomClusteringButton.setSelected(false);
            Button settingsButton = new Button(applicationTemplate.manager.getPropertyValue(SETTINGS_TITLE.name()));
            algorithmWSettingsBox.getChildren().addAll(randomClusteringButton, settingsButton);

        //    algorithmsBox.getChildren().addAll(clustering, randomClusteringButton);
            algorithmsBox.getChildren().addAll(clustering, algorithmWSettingsBox);
            algorithmsBox.setVisible(true);

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

    /**
     * checks to see if numOfLabels == 2 then both algorithms are valid else only clustering algorithm is valid
     */
    public void validTypeOfAlgorithm(){
        if(((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() == 2){
            ToggleGroup selectingAlgorithmType = new ToggleGroup();
            typeAlgorithm1.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm2.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm1.setVisible(true);
            typeAlgorithm2.setVisible(true);
        }else if(((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() == 1 || ((AppData)applicationTemplate.getDataComponent()).returnNumofLabels() > 2 || ((AppData)applicationTemplate.getDataComponent()).returnNullLabel() == 1){
            ToggleGroup selectingAlgorithmType = new ToggleGroup();
            typeAlgorithm2.setToggleGroup(selectingAlgorithmType);
            typeAlgorithm2.setVisible(true);
            typeAlgorithm1.setVisible(false);
        }
    }

    /**
     *  resets the text, textArea, and radio buttons
     */
    public void clearInfoandButton(){
//        textArea.setVisible(false);
//        displayButton.setVisible(false);
//        readOnly.setVisible(false);
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
}
