package dataprocessors;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    private Map<String, String>  dataLabels;
    private Map<String, Point2D> dataPoints;

    private ArrayList<Integer> errors = new ArrayList<Integer>();
    private Boolean duplicates = false;
    private ArrayList<String> nameOfDuplicate = new ArrayList<>();
    private ArrayList<String> keysNames = new ArrayList<>();

    private ArrayList<String> uniqueLabelNames = new ArrayList<String>();
    private int numOfInstances;
    private int numOfLabels;
    private String copyOfTSDString = new String();

    private String info = new String();
    private int nonNull;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        copyOfTSDString = tsdString;
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
              .map(line -> Arrays.asList(line.split("\t")))
              .forEach(list -> {
                  try {
                      String   name  = checkedname(list.get(0));
                      String   label = checkedlabel(list.get(1));
                      String[] pair  = list.get(2).split(",");
                      Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                      dataLabels.put(name, label);
                      dataPoints.put(name, point);
                      keysNames.add(name);
                  } catch (Exception e) {
                      errorMessage.setLength(0);
                      errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                      hadAnError.set(true);
                      errors.add(dataLabels.size() + 1);
                  }
              });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());
    }

    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());
        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);
            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
            });
            chart.getData().add(series);
        }
    }

    void clear() {
        dataPoints.clear();
        dataLabels.clear();

        duplicates = false;
        keysNames.clear();
        nameOfDuplicate.clear();
        errors.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }

    /**
     *
     * @param label
     * @return label
     * @throws InvalidDataNameException
     *
     *
     * checks to see if there is no data label. if none, throw exception
     */
    private String checkedlabel(String label) throws InvalidDataNameException {
        if(label.isEmpty())
            throw new InvalidDataNameException(label);
        return label;
    }

    public Map<String, Point2D> getMap(){
        return dataPoints;
    }

    public int getlineOfError(){
        if(errors.isEmpty()){
            return -1;
        }
        return errors.get(0);
    }

    public void checkForDuplicates(String s){

        for(int i = 0; i < keysNames.size(); i++){
            for(int j = i+1; j < keysNames.size() -1; j++){
                if(keysNames.get(i).equals(keysNames.get(j))){
                    duplicates = true;
                    nameOfDuplicate.add(keysNames.get(i));
                }
            }
        }
    }

    public Boolean getDuplicates(){
        return duplicates;
    }

    public ArrayList<String> getNameOfDuplicate(){
        return nameOfDuplicate;
    }

    /**
     * checking for unique label names & count num of instances
     */
    public int countingInstances(){
        numOfInstances = dataPoints.size();

        uniqueLabelNames.clear();

        //for each label if the array of unique label names does not already contain that name, add it to the array
        for (Map.Entry labelName: dataLabels.entrySet()) {
            if (!uniqueLabelNames.contains(labelName.getValue())) {
                uniqueLabelNames.add(labelName.getValue().toString());
            }
        }

        numOfLabels = uniqueLabelNames.size();

        String allUniqueLabels = new String();

        for(int i = 0; i < uniqueLabelNames.size(); i++){
            allUniqueLabels += uniqueLabelNames.get(i) + "\n";
        }
//        int removeOnceCount = 0;
//        Boolean containsNullEmptySpace = false;

        //if not null and empty, set the object to null else if empty and null and no more than 1 then label -1 and contains nulland space is true
//        for(int i = 0; i < uniqueLabelNames.size(); i++){
//            if(uniqueLabelNames.get(i).isEmpty() && !uniqueLabelNames.contains("null")){
//                uniqueLabelNames.set(i, "null");
//            }else if(uniqueLabelNames.get(i).isEmpty() && uniqueLabelNames.contains("null") && removeOnceCount < 1){
//                numOfLabels--;
//                removeOnceCount++;
//                containsNullEmptySpace = true;
//            }
//            if(!uniqueLabelNames.get(i).isEmpty()) {
//                allUniqueLabels += uniqueLabelNames.get(i) + "\n";
//            }
//        }

       // if contains null and empty space remove the empty space and break
//        if(containsNullEmptySpace){
//            for(int i = 0; i < uniqueLabelNames.size(); i++){
//                if(uniqueLabelNames.get(i).isEmpty()){
//                    uniqueLabelNames.remove(i);
//                    break;
//                }
//            }
//        }
        System.out.println("unique label count " + uniqueLabelNames.size());
        info = numOfInstances + " instances with " + numOfLabels + " labels. The labels are " + "\n" + allUniqueLabels;

        nonNull = 0;
        for(int i = 0; i < uniqueLabelNames.size(); i++){
            if(!uniqueLabelNames.get(i).equals("null")){
                nonNull++;
            }
        }
        System.out.println("---------" + nonNull + "------------");
//        numOfNonNullLabels();
        return nonNull;
    }

    /**
     *
     * @return the number of unique non null labels
     */
    public int numOfNonNullLabels(){
//        int numNonNullLabels = 0;
//        for(int i = 0; i < uniqueLabelNames.size(); i++){
//            if(!uniqueLabelNames.get(i).equals("null")){
//                numNonNullLabels++;
//            }
//        }
        System.out.println("*********" + nonNull + "*********");
        return nonNull;
    }

    /**
     *
     * @return 1 if there is a null label
     */
    public int nullLabel(){
        return 1;
    }

    /**
     *
     * @return String with num of instances, unique label names, etc
     */
    public String getInfo(){
        return info;
    }

    public Boolean dup(){
        String[] copy = copyOfTSDString.split("\n");
    //    System.out.println(copy.length);
        if(dataPoints.size() < copy.length){
            return true;
        }
        return false;
    }
}
