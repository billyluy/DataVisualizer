package dataprocessors;

import javafx.geometry.Point2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class TSDProcessorTest {

    /**
     * test for parsing a valid line
     * @throws Exception
     */
    @Test
    public void parsingLinePass() throws Exception{
        String lineData = "@a\tlabel3\t3,6";

        TSDProcessor processor = new TSDProcessor();
        processor.processString(lineData);

        assertEquals(new Point2D(3,6), processor.returnDataPoints().get("@a"));
        assertEquals("label3", processor.returnDataLabel().get("@a"));
    }

    /**
     *  test for parsing invalid line
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void parsingLineFail() throws Exception{
        String lineData = "";

        TSDProcessor processor = new TSDProcessor();
        processor.processString(lineData);
    }

    /*
        Lower Boundary Test

        Data Point is the lowest double value for the X and Y coordinates
     */

    @Test
    public void parsingLineLowerBoundaryTest() throws Exception{
        String lineData = "@a\tlabel3\t" + Double.MIN_VALUE + "," + Double.MIN_VALUE;

        TSDProcessor processor = new TSDProcessor();
        processor.processString(lineData);

        assertEquals(new Point2D(Double.MIN_VALUE,Double.MIN_VALUE), processor.returnDataPoints().get("@a"));
        assertEquals("label3", processor.returnDataLabel().get("@a"));
    }

    /*
        Upper Boundary Test

        Data Point is the max double value for the X and Y coordinates
     */
    @Test
    public void parsingLineUpperBoundaryTest() throws Exception{
        String lineData = "@a\tlabel3\t" + Double.MAX_VALUE + "," + Double.MAX_VALUE;

        TSDProcessor processor = new TSDProcessor();
        processor.processString(lineData);

        assertEquals(new Point2D(Double.MAX_VALUE,Double.MAX_VALUE), processor.returnDataPoints().get("@a"));
        assertEquals("label3", processor.returnDataLabel().get("@a"));
    }

}