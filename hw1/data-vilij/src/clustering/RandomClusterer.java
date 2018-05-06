package clustering;

import algorithms.Clusterer;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {

    private static final Random RAND = new Random();

    private final int maxIterations;
    private final int updateInterval;
    private DataSet dataset;
    private final AtomicBoolean tocontinue;

    private final int numCluster;
    private ApplicationTemplate applicationTemplate;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClusterer (DataSet dataset, int maxIterations, int updateInterval, boolean continuous, int numberOfClusters, ApplicationTemplate applicationTemplate) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(continuous);
        this.numCluster = numberOfClusters;
        this.applicationTemplate = applicationTemplate;
    }


    @Override
    public void run() {
        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(true);
        if(tocontinue()){
            for(int i = 1; i <= maxIterations && tocontinue(); i++) {
                assignLabels();

                if(i % updateInterval == 0) {
                    Platform.runLater(() -> {
                        ((AppData) applicationTemplate.getDataComponent()).displayDatawnewLabel(dataset.getLabels());

                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            for(int i = 1; i <= maxIterations && !tocontinue(); i++){
                assignLabels();
                System.out.println("i = " + i);

                if(i % updateInterval == 0){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
                        ((AppData) applicationTemplate.getDataComponent()).displayDatawnewLabel(dataset.getLabels());
                        ((AppUI) applicationTemplate.getUIComponent()).changeTextofRunButton("Continue");
                        ((AppUI) applicationTemplate.getUIComponent()).makeAlgorithmandRunButtonVisibleAgain();

                    });

                    synchronized (this){
                        try {
                            if(i > maxIterations){
                                break;
                            }
                            this.wait();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            Platform.runLater(() -> ((AppUI) applicationTemplate.getUIComponent()).changeTextofRunButton("Run"));
        }
        System.out.println("------------thread is done running------------");
        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(false);
        ((AppUI) applicationTemplate.getUIComponent()).makeAlgorithmandRunButtonVisibleAgain();
    }

    private void assignLabels(){
        dataset.getLocations().forEach((instanceName, location) -> {
            int randomLabelNum = RAND.nextInt(numberOfClusters) + 1;
            String labelx = "Label" + randomLabelNum;
            System.out.println("rand num " + randomLabelNum);
            dataset.getLabels().put(instanceName, labelx);
        });
        System.out.println("done");
    }
}
