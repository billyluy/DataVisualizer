package dataprocessors;

import actions.AppActions;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import static settings.AppPropertyTypes.*;
import static settings.AppPropertyTypes.TOTAL_LINES_ERROR;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public ArrayList<String> linesLeft = new ArrayList<String>();

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        try{
            if(dataFilePath != null) {
                Scanner sc = new Scanner(dataFilePath);
                int lineCount = Integer.parseInt(applicationTemplate.manager.getPropertyValue(LINE_COUNTER.name()));
                int totalLines = 0;
                String y = "";


                while (sc.hasNextLine()) {
                    if (lineCount < Integer.parseInt(applicationTemplate.manager.getPropertyValue(MAX_LINES.name()))) {
                        y += sc.nextLine() + "\n";
                        lineCount++;
                        totalLines++;
                    }else {
                        linesLeft.add(sc.nextLine() + "\n");
                        totalLines++;
                    }
                }
                ((AppUI) applicationTemplate.getUIComponent()).setTextArea(y);
                if (totalLines > 10) {
                    String x = manager.getPropertyValue(LINE_FILLED.name()) + totalLines + manager.getPropertyValue(LINES_DISPLAY.name());
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(TOTAL_LINES_ERROR.name(), x);
                }
            }

        } catch (IOException e) {
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()), manager.getPropertyValue(LOAD_ERROR_MSG.name()));
        }
    }

    public ArrayList<String> getLinesLeft(){
        return linesLeft;
    }

    public void loadData(String dataString) {
        // TODO for homework 1
        try {
            ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
            processor.clear();
            processor.processString(dataString);

            this.displayData();
        } catch (Exception e) {
            //invalid input pop-up dialog box
            PropertyManager manager = applicationTemplate.manager;
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(DISPLAY_ERROR_TITLE.name()), manager.getPropertyValue(DISPLAY_ERROR_MSG.name()));
            ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        }

//        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
//        this.displayData();
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
       PropertyManager manager = applicationTemplate.manager;
       String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea();
       try(FileWriter x = new FileWriter(dataFilePath.toString())){
            String a =  "";
            for(int i = 0; i < ((AppData) applicationTemplate.getDataComponent()).getLinesLeft().size(); i++){
                a += ((AppData) applicationTemplate.getDataComponent()).getLinesLeft().get(i);
            }
           x.write(text + a);
       }
       catch(Exception e){
           applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
       }
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {


        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        if(!((AppUI) applicationTemplate.getUIComponent()).getChart().getData().isEmpty()){
            averageYValues();
        }

        for (XYChart.Series<Number, Number> series : ((AppUI) applicationTemplate.getUIComponent()).getChart().getData()) {
            for (Object data1: series.getData()) {
                Tooltip tip1 = new Tooltip();
                tip1.setText(series.getName());
                Tooltip.install(((XYChart.Data)data1).getNode(), tip1);

                ((XYChart.Data)data1).getNode().setOnMouseEntered(event -> ((XYChart.Data) data1).getNode().getStyleClass().add("onHover"));
                ((XYChart.Data)data1).getNode().setOnMouseExited(event -> ((XYChart.Data) data1).getNode().getStyleClass().remove("onHover"));
            }
        }

    }

    public void averageYValues(){
        Map<String, javafx.geometry.Point2D> mapOfPoints = processor.getMap();
        double sum = 0;
        double minXvalue = Double.MAX_VALUE;
        double maxXValue = Double.MIN_VALUE;
        for(Map.Entry<String, javafx.geometry.Point2D> entry : processor.getMap().entrySet()) {
            sum += entry.getValue().getY();
            if(entry.getValue().getX() > maxXValue){
                maxXValue = entry.getValue().getX();
            }
            if(entry.getValue().getX() < minXvalue){
                minXvalue = entry.getValue().getX();
            }
        }

        XYChart.Series lineseries = new XYChart.Series();
        lineseries.setName(applicationTemplate.manager.getPropertyValue(AVERAGE_Y_VALUE.name()));

        lineseries.getData().add(new XYChart.Data<>(minXvalue, sum/processor.getMap().size()));
        lineseries.getData().add(new XYChart.Data<>(maxXValue, sum/processor.getMap().size()));



        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(lineseries);
        for (Object data1: lineseries.getData()) {
            ((XYChart.Data)data1).getNode().setVisible(false);
        }

        lineseries.getNode().setId(applicationTemplate.manager.getPropertyValue(LINE_SERIES.name()));
    }

}
