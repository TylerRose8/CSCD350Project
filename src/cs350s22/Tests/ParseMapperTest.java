package cs350s22.Tests;

import org.junit.jupiter.api.Test;

import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.ParserHelper;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
class ParseMapperTest extends TestBase {
    ParseMapperTest() throws IOException {
        super("Mapper");
    }

    @Test
    void parseMapperCommand() {
    	final A_ParserHelper _parserHelper = new ParserHelper();
		_parserHelper.schedule("@RUN mapperCommands.txt");
		_parserHelper.schedule("@exit");
    }
}