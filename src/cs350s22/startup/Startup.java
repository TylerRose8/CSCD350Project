/**
 * @Author Tyler Rose
 * @Author Spencer West
 * @Author Ryan Golden
 */
package cs350s22.startup;

import cs350s22.component.logger.LoggerActuator;
import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.ParserHelper;
import cs350s22.support.Filespec;

//=================================================================================================================================================================================
public class Startup {
    private final A_ParserHelper _parserHelper = new ParserHelper();

    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public Startup() {
        System.out.println("Welcome to your Startup class");
        LoggerActuator.initialize(Filespec.make("out.txt"));
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void main(final String[] arguments) throws Exception {
        Startup startup = new Startup();

        // this command must come first. The filenames do not matter here
        startup.parse("@CONFIGURE LOG \"a.txt\" DOT SEQUENCE \"b.txt\" NETWORK \"c.txt\" XML \"d.txt\"");
        // run your tests like this
        startup.parse("@exit");
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void parse(final String parse) throws Exception {
        System.out.println("PARSE> " + parse);

        //Parser parser = new Parser(_parserHelper, parse);

        _parserHelper.schedule(parse);
    }
}
