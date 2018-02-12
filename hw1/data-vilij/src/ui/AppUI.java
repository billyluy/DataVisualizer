package ui;

import actions.AppActions;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;


import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static settings.AppPropertyTypes.SCREENSHOT_TOOLTIP;
import static settings.AppPropertyTypes.SCREENSHOT_ICON;
import static settings.AppPropertyTypes.TEXT_INPUT_TITLE;
import static settings.AppPropertyTypes.CHART_TITLE;
import static settings.AppPropertyTypes.DISPLAY_BUTTON_NAME;

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
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    private static final String SEPARATOR = "/";
    private String screenshotPath;

    public ScatterChart<Number, Number> getChart() { return chart; }

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
    }

    private void layout() {
        // TODO for homework 1

        PropertyManager manager = applicationTemplate.manager;

        HBox textPane = new HBox(500);

        Text text1 = new Text();
        text1.setText(manager.getPropertyValue(TEXT_INPUT_TITLE.name()));

        textPane.getChildren().add(text1);

        HBox layoutPane = new HBox();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        chart = new ScatterChart<>(xAxis, yAxis);

        chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));

        textArea = new TextArea();
        displayButton = new Button();
        displayButton.setText(manager.getPropertyValue(DISPLAY_BUTTON_NAME.name()));


        layoutPane.getChildren().addAll(textArea, chart);

        appPane.getChildren().addAll(textPane, layoutPane, displayButton);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1

        displayButton.setOnAction(e -> ((AppData) applicationTemplate.getDataComponent()).loadData(textArea.getText()));


        //had this initially but changed it since there is warning to replace with lambda
//        textArea.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if(!textArea.getText().isEmpty()){
//                    newButton.setDisable(false);
//                    saveButton.setDisable(false);
//                }else{
//                    newButton.setDisable(true);
//                    saveButton.setDisable(true);
//                }
//            }
//        });

        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(!textArea.getText().isEmpty()){
                newButton.setDisable(false);
                saveButton.setDisable(false);
            }else{
                newButton.setDisable(true);
                saveButton.setDisable(true);
            }
        });
    }
}
