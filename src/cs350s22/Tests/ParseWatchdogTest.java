package cs350s22.Tests;

import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.ParserHelper;
import org.junit.Test;
//import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ParseWatchdogTest extends TestBase {
    ParseWatchdogTest() throws IOException {
        super("watchdog");
    }

    @Test
    void parseMapperCommand() {
    	final A_ParserHelper _parserHelper = new ParserHelper();
		_parserHelper.schedule("@RUN mapperCommands.txt");
		_parserHelper.schedule("@exit");
    }
}