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
        fileOut = new File("MapperTestLog.txt");
        fileErr = new File("MapperTestErrors.txt");
        writerOut = new FileWriter(fileOut);
        writerErr = new FileWriter(fileErr);
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