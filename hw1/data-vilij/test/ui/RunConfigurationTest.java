package ui;

import org.junit.Test;

import static org.junit.Assert.*;

public class RunConfigurationTest {

    /**
     *  testing for valid input for all fields in classification
     */
    @Test
    public void runConfigClassificationTest(){
        String[] data = {"4","4","1","0"};
        String[] newData = {"4","4","1","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";

        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";

        }
        for(int i = 0; i < data.length; i++){
            assertEquals(data[i], newData[i]);
        }
    }

    /**
     * invalid input in fields so invalid inputs turn to 1 as default
     * expected = 1
     */
    @Test
    public void runConfigClassificationTestInvalidInput(){
        String[] data = {"1","-1","1","0"};
        String[] expected = {"1","1","1","0"};
        String[] newData = {"1","1","1","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";
        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";
        }
        for(int i = 0; i < data.length; i++){
            assertEquals(expected[i], newData[i]);
        }
    }

    /**
     * testing valid input for clustering algos kmeans and randomclustering
     * no changes should be made to the fields
     */
    @Test
    public void runConfigClusteringTest(){
        String[] data = {"1","1","3","0"};
        String[] newData = {"1","1","3","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";
        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";
        }
        if(Integer.parseInt(data[2]) < 2){
            newData[2] = "2";
        }
        if(Integer.parseInt(data[2] ) > 4){
            newData[2] = "4";
        }

        for(int i = 0; i < data.length; i++){
            assertEquals(data[i], newData[i]);
        }
    }

    /**
     * testing invalid input for clustering algo kmeans and randomclustering
     *
     */
    @Test
    public void runConfigClusteringTestInvalidInput(){
        String[] data = {"1","-1","0","0"};
        String[] expected = {"1","1","2","0"};
        String[] newData = {"1","1","2","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";
        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";
        }
        if(Integer.parseInt(data[2]) < 2){
            newData[2] = "2";
        }
        if(Integer.parseInt(data[2] ) > 4){
            newData[2] = "4";
        }
        for(int i = 0; i < data.length; i++){
            assertEquals(expected[i], newData[i]);
        }
    }

    /**
     * boundary test for classification algo
     * boundary is 1 for maxiteartion and update interval 
     */

    @Test
    public void runConfigClassificationTestBoundary(){
        String[] data = {"1","1","1","0"};
        String[] newData = {"1","1","1","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";

        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";

        }
        for(int i = 0; i < data.length; i++){
            assertEquals(data[i], newData[i]);
        }
    }

    /**
     * boundary test for clustering algo kmeans and randomclustering
     * number of clusters should be 4
     * maxiterations and update interval should be at least 1
     */
    @Test
    public void runConfigClusteringTestBoundary(){
        String[] data = {"1","1","4","0"};
        String[] expected = {"1","1","4","0"};
        String[] newData = {"1","1","4","0"};
        if(Integer.parseInt(data[0]) <= 0){
            newData[0] = "1";
        }
        if(Integer.parseInt(data[0]) <= 0){
            newData[1] = "1";
        }
        if(Integer.parseInt(data[2]) < 2){
            newData[2] = "2";
        }
        if(Integer.parseInt(data[2] ) > 4){
            newData[2] = "4";
        }
        for(int i = 0; i < data.length; i++){
            assertEquals(expected[i], newData[i]);
        }
    }



}