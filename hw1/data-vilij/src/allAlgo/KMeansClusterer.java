package allAlgo;

import algorithms.Clusterer;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Ritwik Banerjee
 */
public class KMeansClusterer extends Clusterer {

    private DataSet dataset;
    private List<Point2D> centroids;

    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;


    private ApplicationTemplate applicationTemplate;
    private boolean initialchoice;


    public KMeansClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean continuous, ApplicationTemplate applicationTemplate) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(continuous);
        this.applicationTemplate = applicationTemplate;
        this.initialchoice = continuous;
    }

    @Override
    public int getMaxIterations() { return maxIterations; }

    @Override
    public int getUpdateInterval() { return updateInterval; }

    @Override
    public boolean tocontinue() { return tocontinue.get(); }

    @Override
    public void run() {

        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(true);
        initializeCentroids();
        int iteration = 1;

        while(iteration++ <= maxIterations && tocontinue()){
            assignLabels();
            recomputeCentroids();

        //    System.out.println(initialchoice);
            if(initialchoice && (iteration % updateInterval == 0 || !tocontinue())){
                Platform.runLater(() -> {
                    ((AppData) applicationTemplate.getDataComponent()).displayDatawnewLabel(dataset.getLabels());
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(!initialchoice && (iteration % updateInterval == 0 || !tocontinue())){
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
                        if(iteration > maxIterations){
                            break;
                        }
                        this.wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(!initialchoice && !tocontinue()){
                Platform.runLater(() -> ((AppUI) applicationTemplate.getUIComponent()).changeTextofRunButton("Run"));
            }
        }
        ((AppUI) applicationTemplate.getUIComponent()).setThreadRunningBoolean(false);
        ((AppUI) applicationTemplate.getUIComponent()).makeAlgorithmandRunButtonVisibleAgain();
    }

    private void initializeCentroids() {
        Set<String>  chosen        = new HashSet<>();
        List<String> instanceNames = new ArrayList<>(dataset.getLabels().keySet());
        Random       r             = new Random();
        while (chosen.size() < numberOfClusters) {
            int i = r.nextInt(instanceNames.size());
            while (chosen.contains(instanceNames.get(i)))
                ++i;
            chosen.add(instanceNames.get(i));
        }
        centroids = chosen.stream().map(name -> dataset.getLocations().get(name)).collect(Collectors.toList());
        tocontinue.set(true);
    }

    private void assignLabels() {
        dataset.getLocations().forEach((instanceName, location) -> {
            double minDistance      = Double.MAX_VALUE;
            int    minDistanceIndex = -1;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = computeDistance(centroids.get(i), location);
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndex = i;
                }
            }
            dataset.getLabels().put(instanceName, Integer.toString(minDistanceIndex));
        });
    }

    private void recomputeCentroids() {
        tocontinue.set(false);
        IntStream.range(0, numberOfClusters).forEach(i -> {
            AtomicInteger clusterSize = new AtomicInteger();
            Point2D sum = dataset.getLabels()
                    .entrySet()
                    .stream()
                    .filter(entry -> i == Integer.parseInt(entry.getValue()))
                    .map(entry -> dataset.getLocations().get(entry.getKey()))
                    .reduce(new Point2D(0, 0), (p, q) -> {
                        clusterSize.incrementAndGet();
                        return new Point2D(p.getX() + q.getX(), p.getY() + q.getY());
                    });
            Point2D newCentroid = new Point2D(sum.getX() / clusterSize.get(), sum.getY() / clusterSize.get());
            if (!newCentroid.equals(centroids.get(i))) {
                centroids.set(i, newCentroid);
                tocontinue.set(true);
            }
        });
    }

    private static double computeDistance(Point2D p, Point2D q) {
        return Math.sqrt(Math.pow(p.getX() - q.getX(), 2) + Math.pow(p.getY() - q.getY(), 2));
    }

}