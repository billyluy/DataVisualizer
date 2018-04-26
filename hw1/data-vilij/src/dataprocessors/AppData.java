package dataprocessors;

import javafx.scene.chart.XYChart;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    private Path dataPath;
    Boolean validity = false;
    private int labelNum;
    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        TSDProcessor processor = new TSDProcessor();

        try{
            processor.clear();
            if(dataFilePath != null) {
                Scanner sc = new Scanner(dataFilePath);
                int lineCount = Integer.parseInt(applicationTemplate.manager.getPropertyValue(LINE_COUNTER.name()));
                int totalLines = 0;
                String y = new String();

                String t = new String();
                linesLeft.clear();

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

                for(int i = 0; i < linesLeft.size(); i++){
                    t += linesLeft.get(i);
                }

                String s = y + t;
                processor.processString(s);
                processor.checkForDuplicates(s);

                if(processor.getDuplicates() == true){
                    applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE_TITLE.name()), manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE.name()) + processor.getNameOfDuplicate().get(0));
                }
                else{
                    if (totalLines > 10) {
                        String x = manager.getPropertyValue(LINE_FILLED.name()) + totalLines + manager.getPropertyValue(LINES_DISPLAY.name());
                        applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(TOTAL_LINES_ERROR.name(), x);
                    }
                    ((AppUI) applicationTemplate.getUIComponent()).setTextArea(y);

                    labelNum = processor.countingInstances();
//                    System.out.println("label num " + labelNum);
//                    dataPath = dataFilePath;
                    ((AppUI) applicationTemplate.getUIComponent()).setTextInfo(manager.getPropertyValue(SOURCE_NAME.name()) + dataFilePath + "\n" + processor.getInfo());
                    ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
                    ((AppUI) applicationTemplate.getUIComponent()).textAreaVisibility();
                    ((AppUI) applicationTemplate.getUIComponent()).loadDisableButtons();
                    dataFilePath = null;

                    ((AppUI) applicationTemplate.getUIComponent()).clearSelectingAlgorithmsBox();
                    ((AppUI) applicationTemplate.getUIComponent()).getAlgoTitle().setVisible(true);
                }
            }

        } catch (Exception e) {
            if(processor.getlineOfError() > 0){
                String msg = manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE.name()) + processor.getlineOfError();
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE_TITLE.name()), manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE.name()) + msg);
            }else if(processor.getDuplicates() == true){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE_TITLE.name()), manager.getPropertyValue(UNABLE_TO_LOAD_DUPLICATE.name()) + processor.getNameOfDuplicate().get(0));

            } else {
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()), manager.getPropertyValue(LOAD_ERROR_MSG.name()));
            }
        }
    }

    public ArrayList<String> getLinesLeft(){
        return linesLeft;
    }

    public void loadData(String dataString) {
        PropertyManager manager = applicationTemplate.manager;
        try {
            ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
            processor.clear();
            processor.processString(dataString);
            if(!processor.dup()){
                validity = true;
                ((AppUI) applicationTemplate.getUIComponent()).setTextInfo("\n" + processor.getInfo());
            }else{
                validity = false;
               throw new TSDProcessor.InvalidDataNameException(dataString);

            }


            //    this.displayData();
        } catch (Exception e) {
            //invalid input pop-up dialog box
            validity = false;
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(DISPLAY_ERROR_TITLE.name()), manager.getPropertyValue(DISPLAY_ERROR_MSG.name()));
            ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        }

//        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
//        this.displayData();

    }

    @Override
    public void saveData(Path dataFilePath) {
        PropertyManager manager = applicationTemplate.manager;
        String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea();
        try(FileWriter x = new FileWriter(dataFilePath.toString())){
            String a =  new String();
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

//    public void displayData() {
//        PropertyManager manager = applicationTemplate.manager;
//
//        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
//        if(!((AppUI) applicationTemplate.getUIComponent()).getChart().getData().isEmpty()){
//            averageYValues();
//        }
//
//        for (XYChart.Series<Number, Number> series : ((AppUI) applicationTemplate.getUIComponent()).getChart().getData()) {
//            for (Object data1: series.getData()) {
//                Tooltip tip1 = new Tooltip();
//                tip1.setText(series.getName());
//                Tooltip.install(((XYChart.Data)data1).getNode(), tip1);
//
//                ((XYChart.Data)data1).getNode().setOnMouseEntered(event -> ((XYChart.Data) data1).getNode().getStyleClass().add(manager.getPropertyValue(HOVER_NAME.name())));
//                ((XYChart.Data)data1).getNode().setOnMouseExited(event -> ((XYChart.Data) data1).getNode().getStyleClass().remove(manager.getPropertyValue(HOVER_NAME.name())));
//            }
//        }
//
//    }

    public void displayData(List<Integer> outputList) {
        PropertyManager manager = applicationTemplate.manager;


        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());


        double[] xVal = processor.returnXCoords();

        double yMin = (0 - (outputList.get(0) * xVal[0]) - outputList.get(2))/outputList.get(1);
        double yMax = (0 - (outputList.get(0) * xVal[1]) - outputList.get(2))/outputList.get(1);;
        System.out.println("yMin = " + yMin);
        System.out.println("yMax = " + yMax);
        System.out.println("*********************************");

        XYChart.Series<Number, Number> lineseries = new XYChart.Series<>();
        lineseries.setName(applicationTemplate.manager.getPropertyValue(AVERAGE_Y_VALUE.name()));

        lineseries.getData().add(new XYChart.Data<>(xVal[0], yMin));
        lineseries.getData().add(new XYChart.Data<>(xVal[1], yMax));

        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(lineseries);

        for (Object data1: lineseries.getData()) {
            ((XYChart.Data)data1).getNode().setVisible(false);
        }

        lineseries.getNode().setId(applicationTemplate.manager.getPropertyValue(LINE_SERIES.name()));

    }

    public void averageYValues(){
      //  Map<String, javafx.geometry.Point2D> mapOfPoints = processor.getMap();
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

        XYChart.Series<Number, Number> lineseries = new XYChart.Series<>();
        lineseries.setName(applicationTemplate.manager.getPropertyValue(AVERAGE_Y_VALUE.name()));

        lineseries.getData().add(new XYChart.Data<>(minXvalue, sum/processor.getMap().size()));
        lineseries.getData().add(new XYChart.Data<>(maxXValue, sum/processor.getMap().size()));



        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(lineseries);
        for (Object data1: lineseries.getData()) {
            ((XYChart.Data)data1).getNode().setVisible(false);
        }

        lineseries.getNode().setId(applicationTemplate.manager.getPropertyValue(LINE_SERIES.name()));
    }


    //returns num of non null labels
    public int returnNumofLabels(){
//        int nonNullCount = processor.numOfNonNullLabels();
//        System.out.println("nonnull count" + nonNullCount);
//        return nonNullCount;

        return labelNum;
    }

    public int returnNullLabel(){
        int nullCount = processor.nullLabel();
        return nullCount;
    }

    public void getInfoForNew(){
        PropertyManager manager = applicationTemplate.manager;
        labelNum = processor.countingInstances();
        ((AppUI) applicationTemplate.getUIComponent()).setTextInfo("\n" + processor.getInfo());
        ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
        ((AppUI) applicationTemplate.getUIComponent()).textAreaVisibility();
    }

    //data is valid format
    public Boolean getValidity(){
        return validity;
    }


    public void clearlinesleft(){
        linesLeft.clear();
    }
}
