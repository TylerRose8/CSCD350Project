package cs350s22.startup;

import cs350s22.component.ui.parser.A_ParserHelper;
import cs350s22.component.ui.parser.Parser;
import cs350s22.component.ui.parser.ParserHelper;

//=================================================================================================================================================================================
public class Startup
{
   private final A_ParserHelper _parserHelper = new ParserHelper();
   
   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   public Startup()
   {
      System.out.println("Welcome to your Startup class, Hello World!!!");
   }

   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   public static void main(final String[] arguments) throws Exception
   {
      Startup startup = new Startup();
      
      // this command must come first. The filenames do not matter here
      //startup.parse("@CONFIGURE LOG \"a.txt\" DOT SEQUENCE \"b.txt\" NETWORK \"c.txt\" XML \"d.txt\"");
      //startup.parse("@CLOCK SET RATE 1");
      startup.parse("CREATE MAPPER myMapper EQUATION PASSTHROUGH");
      
      startup.parse("CREATE MAPPER myMapper1 EQUATION SCALE 10");
      
      
      startup.parse("CREATE MAPPER myMapper2 EQUATION NORMALIZE 10 20");
      
      //These work but they need a .map file to work.
      startup.parse("CREATE MAPPER myMapper3 INTERPOLATION LINEAR DEFINITION \"mapfile.map\"");
      startup.parse("CREATE MAPPER myMapper4 INTERPOLATION SPLINE DEFINITION \"C:/temp/definition.map\"\"");


      // run your tests like this
      startup.parse("@exit");
   }
   
   // -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
   private void parse(final String parse) throws Exception
   {
      System.out.println("PARSE> "+ parse);
      
      Parser parser = new Parser(_parserHelper, parse);
      
      parser.parse();
   }
}
