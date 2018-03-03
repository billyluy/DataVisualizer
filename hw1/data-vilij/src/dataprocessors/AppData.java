package dataprocessors;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.util.Map;

import static settings.AppPropertyTypes.DISPLAY_ERROR_MSG;
import static settings.AppPropertyTypes.DISPLAY_ERROR_TITLE;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
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
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {

        averageYValues();

        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());

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
        lineseries.setName("Average Y Value");

        lineseries.getData().add(new XYChart.Data<>(minXvalue, sum/processor.getMap().size()));
        lineseries.getData().add(new XYChart.Data<>(maxXValue, sum/processor.getMap().size()));



        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().add(lineseries);
        for (Object data1: lineseries.getData()) {
            ((XYChart.Data)data1).getNode().setVisible(false);
        }

        lineseries.getNode().setId("lineseries");

        System.out.println(sum/processor.getMap().size());
    }

}
