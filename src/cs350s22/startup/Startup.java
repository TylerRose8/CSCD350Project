package cs350s22.startup;

import cs350s22.component.logger.LoggerActuator;
import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.Parser;
import cs350s22.component.ui.parser.ParserHelper;
import cs350s22.support.Filespec;

//=================================================================================================================================================================================
public class Startup
{
   private final A_ParserHelper _parserHelper = new ParserHelper();
   
   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   public Startup()
   {
      System.out.println("Welcome to your Startup class");
      LoggerActuator.initialize(Filespec.make("out.txt"));
   }

   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   public static void main(final String[] arguments) throws Exception
   {
      Startup startup = new Startup();
      
      // this command must come first. The filenames do not matter here
      startup.parse("@CONFIGURE LOG \"a.txt\" DOT SEQUENCE \"b.txt\" NETWORK \"c.txt\" XML \"d.txt\"");
      //startup.parse("CREATE ACTUATOR LINEAR myActuator0 ACCELERATION LEADIN 0.1 LEADOUT -0.2 RELAX 0.3 VELOCITY LIMIT 5 VALUE MIN 1 MAX 10 INITIAL 2 JERK LIMIT 3");
      //test C.1: PASSTHROUGH
      /*startup.parse("CREATE MAPPER myMapper EQUATION PASSTHROUGH");
      startup.parse("CREATE SENSOR POSITION mySensor1 MAPPER myMapper");*/
      //startup.parse("CREATE CONTROLLER FORWARDING myController1 WITH COMPONENTS mySensor1");
      /*startup.parse("GET SENSOR mySensor1 VALUE");
      startup.parse("SET SENSOR mySensor1 VALUE 10");
      startup.parse("GET SENSOR mySensor1 VALUE");*/
      //startup.parse("@CLOCK WAIT FOR 5");
      //startup.parse("BUILD NETWORK WITH COMPONENTS myActuator0");

      startup.parse("CREATE WATCHDOG BAND myWatchdog1 MODE INSTANTANEOUS THRESHOLD LOW 1 HIGH 3");
      startup.parse("CREATE WATCHDOG BAND myWatchdog2 MODE INSTANTANEOUS THRESHOLD LOW 10 HIGH 30");
      startup.parse("CREATE SENSOR POSITION mySensor1 WATCHDOGS myWatchdog1 myWatchdog2");
      startup.parse("BUILD NETWORK WITH COMPONENTS mySensor1");
      startup.parse("SET SENSOR mySensor1 VALUE 2");

      
      //test C.2: SCALED
      /*
      startup.parse("CREATE MAPPER myMapper2 EQUATION SCALE 10");
      startup.parse("CREATE SENSOR POSITION mySensor2 MAPPER myMapper2");
      startup.parse("CREATE CONTROLLER FORWARDING myController2 WITH COMPONENTS mySensor2");
      startup.parse("GET SENSOR mySensor2 VALUE");
      startup.parse("SET SENSOR mySensor2 VALUE 1");
      startup.parse("GET SENSOR mySensor2 VALUE");
      
      //test C.3: NORMALIZED
      
      startup.parse("CREATE MAPPER myMapper3 EQUATION NORMALIZE 10 20");
      startup.parse("CREATE SENSOR POSITION mySensor3 MAPPER myMapper3");
      startup.parse("CREATE CONTROLLER FORWARDING myController3 WITH COMPONENTS mySensor3");
      startup.parse("GET SENSOR mySensor3 VALUE");
      startup.parse("SET SENSOR mySensor3 VALUE 15");
      startup.parse("GET SENSOR mySensor3 VALUE");
      */
      // run your tests like this
      startup.parse("@exit");
   }
   
   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   private void parse(final String parse) throws Exception
   {
      System.out.println("PARSE> "+ parse);

      //Parser parser = new Parser(_parserHelper, parse);

      _parserHelper.schedule(parse);
}
}
