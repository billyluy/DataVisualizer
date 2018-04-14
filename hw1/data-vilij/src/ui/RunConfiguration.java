package ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;

import java.util.Arrays;

public class RunConfiguration extends Stage implements Dialog {

    private static RunConfiguration dialog;
    private TextField iterations1;
    private TextField intervals1;
    private CheckBox runs1;
    private TextField clust1;
    private static String algotype = new String();

    private String[] previousInput = new String[4];

    private RunConfiguration(){

    }

    public static RunConfiguration getDialog(String algorithmType, String[] prev1) {
        if (dialog == null)
            algotype = algorithmType;
            dialog = new RunConfiguration();
            dialog.previousInput = prev1;
        return dialog;
    }

    @Override
    public void show(String title, String message) {
        setTitle("RunConfiguration");    // set the title of the dialog
        showAndWait();                 // open the dialog and wait for the user to click the close button
    }

    @Override
    public void init(Stage owner) {
        initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        initOwner(owner);

        PropertyManager manager     = PropertyManager.getManager();
        Button closeButton = new Button(manager.getPropertyValue(PropertyTypes.CLOSE_LABEL.name()));
        VBox messagePane = new VBox();
        HBox labelBox1 = new HBox();
        HBox labelBox2 = new HBox();
        HBox labelBox3 = new HBox();

        Label maxIt = new Label("Max Iterations:");
        Label upInt = new Label("Update Intervals:");
        Label run = new Label("Continuous Run?");

        iterations1 = new TextField();
        intervals1 = new TextField();
        runs1 = new CheckBox();

        iterations1.setText(previousInput[0]);
        intervals1.setText(previousInput[1]);

        labelBox1.getChildren().addAll(maxIt, iterations1);
        labelBox2.getChildren().addAll(upInt, intervals1);
        labelBox3.getChildren().addAll(run, runs1);

        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().addAll(labelBox1, labelBox2);

        if(algotype == "Clustering"){
            HBox labelBox4 = new HBox();
            Label numClusters = new Label("Number of Clusters:");
            clust1 = new TextField();

            clust1.setText(previousInput[2]);

            labelBox4.getChildren().addAll(numClusters,clust1);
            messagePane.getChildren().addAll(labelBox4);
        }
        if(previousInput[3] == "0"){
            runs1.setSelected(false);
        }else{
            runs1.setSelected(true);
        }

        closeButton.setOnAction(e -> {
            if(checkvalInput()){
                previousInput[0] = iterations1.getText();
                previousInput[1] = intervals1.getText();
                if(algotype == "Clustering"){
                    previousInput[2] = clust1.getText();
                }
                if(runs1.isSelected()){
                    previousInput[3] = "1";
                }else{
                    previousInput[3] = "0";
                }
         //       System.out.println("prev in runcon" + Arrays.toString(previousInput));
                dialog.previousInput = previousInput;
                this.close();
            }
        });
        messagePane.getChildren().addAll(labelBox3, closeButton);
        messagePane.setSpacing(20);


        Scene messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }

    public boolean checkvalInput(){
        boolean changed = true;

        try{
            if(!intervals1.getText().isEmpty() && Integer.parseInt(intervals1.getText()) <= 0){
                intervals1.setText("1");
                changed = false;
            }
            if(!iterations1.getText().isEmpty() && Integer.parseInt(iterations1.getText()) <= 0) {
                iterations1.setText("1");
                changed = false;
            }
            if(intervals1.getText().isEmpty()) {
                intervals1.setText("1");
                changed = false;
            }
            if(iterations1.getText().isEmpty()){
                iterations1.setText("1");
                changed = false;
            }
            if(algotype == "Clustering"){
                if(!clust1.getText().isEmpty() && Integer.parseInt(clust1.getText()) <= 0){
                    clust1.setText("1");
                    changed = false;
                }
                if(clust1.getText().isEmpty()) {
                    clust1.setText("1");
                    changed = false;
                }
            }
        }
        catch(Exception e){
            changed = false;
            return changed;
        }
        return changed;
    }

    public void setPrev(String[] x){
        dialog.previousInput = x;
    }

    public String[] returnPrevInput(){
    //    System.out.println("returning" + Arrays.toString(previousInput));
        return previousInput;
    }
}
