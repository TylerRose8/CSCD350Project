package cs350s22.Tests;

import cs350s22.Tests.TestBase;
import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.ParserHelper;

import java.io.IOException;

class ParseActuatorTest extends TestBase {
    ParseActuatorTest() throws IOException {
        super("Actuator");
    }
    public static void main(String[] args) {
    	final A_ParserHelper _parserHelper = new ParserHelper();
		_parserHelper.schedule("@RUN actuatorCommands.txt");
    }
}