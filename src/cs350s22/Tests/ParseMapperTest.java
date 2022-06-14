package cs350s22.Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ParseMapperTest {
    File fileOut;
    File fileErr;
    FileWriter writerOut;
    FileWriter writerErr;
    PrintStream defaultOut;
    PrintStream defaultErr;

    ParseMapperTest() throws IOException {
        String testName = "Mapper";
        fileOut = new File(testName+"TestLog.txt");
        fileErr = new File(testName+"TestErrors.txt");
        writerOut = new FileWriter(fileOut,true);
        writerErr = new FileWriter(fileErr, true);
    }

    @BeforeEach
    void setUp() {
        PrintStream out = new PrintStream(new OutputStream()
        {
            @Override
            public void write(int b)
            {
                try {
                    writerOut.write(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        PrintStream err = new PrintStream(new OutputStream()
        {
            @Override
            public void write(int b)
            {
                try {
                    writerErr.write(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        defaultOut = System.out;
        defaultErr = System.err;
        System.setOut(out);
        System.setErr(err);
        System.out.println("Next Test");
        System.out.println("==========================================================");
    }

    @AfterEach
    void tearDown() {
        System.setOut(defaultOut);
        System.setErr(defaultErr);
    }

    @Test
    void parseMapperCommand() {
    }
}