package cs350s22.Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TestBase {
    File fileOut;
    File fileErr;
    FileWriter writerOut;
    FileWriter writerErr;
    PrintStream defaultOut;
    PrintStream defaultErr;

    TestBase() throws IOException {
        String testName = "Generic";
        fileOut = new File(testName+"TestLog.txt");
        fileErr = new File(testName+"TestErrors.txt");
        writerOut = new FileWriter(fileOut,true);
        writerErr = new FileWriter(fileErr, true);
        writerOut.write("Running tests for: " + testName);
        writerOut.write("\n==========================================================");
    }

    TestBase(String testName) throws IOException {;
        fileOut = new File(testName+"TestLog.txt");
        fileErr = new File(testName+"TestErrors.txt");
        writerOut = new FileWriter(fileOut,true);
        writerErr = new FileWriter(fileErr, true);
        writerOut.write("Running tests for: " + testName);
        writerOut.write("\n==========================================================");
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
                    writerOut.flush();
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
                    writerOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        defaultOut = System.out;
        defaultErr = System.err;
        System.setOut(out);
        System.setErr(err);
        System.out.println("\nNext Test");
        System.out.println("==========================================================");
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(defaultOut);
        System.setErr(defaultErr);
        writerOut.close();
        writerErr.close();
    }
}