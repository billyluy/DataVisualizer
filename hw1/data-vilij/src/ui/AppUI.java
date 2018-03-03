package ui;

import actions.AppActions;
import dataprocessors.AppData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
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
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private CheckBox                     readOnly;

    private static final String SEPARATOR = "/";
    private String screenshotPath;

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
                };
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
        displayButton = new Button();
        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON_NAME.name()));


        layoutPane.getChildren().addAll(textArea, chart);

        readOnly = new CheckBox(applicationTemplate.manager.getPropertyValue(READ_ONLY_TITLE.name()));
        readOnly.setSelected(false);

        appPane.getChildren().addAll(textPane, layoutPane, displayButton, readOnly);

        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);

        appPane.getStylesheets().add("gui.css/Chart.css");

        hasNewText = false;

    }

    private void setWorkspaceActions() {

        displayButton.setOnAction(e -> ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText()));

        String oldText = textArea.getText();
        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!oldText.equals(textArea.getText()) && !hasNewText){
                hasNewText = true;
            }

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

        chart.getData().addListener(new ListChangeListener<XYChart.Series<Number, Number>>() {
            @Override
            public void onChanged(Change<? extends XYChart.Series<Number, Number>> c) {
                if(chart.getData().isEmpty()){
                //    System.out.println("empty");
                    scrnshotButton.setDisable(true);
                }else{
                //    System.out.println("filled");
                    scrnshotButton.setDisable(false);
                }
            }
        });

        readOnly.setOnMouseClicked(event -> checkingBox());

    }

    public void checkingBox(){
        if(readOnly.isSelected()) {
            textArea.setDisable(true);
        }
        else{
            textArea.setDisable(false);
        }
    }

    public String getTextArea(){
        return textArea.getText();
    }

    public Button getSaveButton(){
        return saveButton;
    }
}
