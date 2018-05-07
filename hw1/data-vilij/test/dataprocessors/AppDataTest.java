package dataprocessors;

import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class AppDataTest {

    /**
     *  test for saving valid path
     * @throws Exception
     */
    @Test
    public void savingDataTestValid() throws Exception{
        String line = "@a\tlabel3\t3,6";
        Path dataFilePath = Paths.get("file");

        FileWriter x = new FileWriter(dataFilePath.toString());
        x.write(line);
        x.close();

        BufferedReader reader = new BufferedReader(new FileReader(dataFilePath.toString()));

        assertEquals(line, reader.readLine());

    }

    /**
     * test for saving invalid file path
     * file not found exception
     * @throws Exception
     */
    @Test(expected = FileNotFoundException.class)
    public void savingDataTestInvalid() throws Exception{
        String line = "@a\tlabel3\t3,6";
        Path dataFilePath = Paths.get("");

        FileWriter x = new FileWriter(dataFilePath.toString());
        x.write(line);
        x.close();

        BufferedReader reader = new BufferedReader(new FileReader(dataFilePath.toString()));

        assertEquals(line, reader.readLine());

    }

}