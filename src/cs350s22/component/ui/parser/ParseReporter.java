package cs350s22.component.ui.parser;

import cs350s22.component.sensor.reporter.A_Reporter;
import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.reporter.ReporterFrequency;
import cs350s22.support.Identifier;

import java.util.ArrayList;
import java.util.Scanner;

public class ParseReporter {
    public static void parseReporterCommand(A_ParserHelper parserHelper, String command) throws ParseException {

        switch (command.split(" ")[0].toUpperCase()) {
            case "CREATE" -> createNewReporter(parserHelper, command);
            default -> throw new ParseException("Undefined actuator command: " + command.split(" ")[0].toUpperCase());
        }
    }

    private static void createNewReporter(A_ParserHelper parserHelper, String command) throws ParseException {
        //Get the actuator type and ID
        String[] commandSplit = command.split(" ");
        String reporterType = commandSplit[2];
        Identifier reporterId = Identifier.make(commandSplit[3]);
        int value = Integer.parseInt(commandSplit[commandSplit.length - 1]);

        //Get notify ids
        Scanner in = new Scanner(command.substring(command.toUpperCase().indexOf("NOTIFY") + 7));
        String next = in.next();
        ArrayList<Identifier> notifyIds = Parser.getSetOfIds(command,"IDS","GROUPS","DELA");
        //Get group ids
        ArrayList<Identifier> groupIds = Parser.getSetOfIds(command,"GROUPS", "DELTA");

        //Create the reporter and add it to the network
        A_Reporter reporter;
        switch (reporterType.toUpperCase()) {
            case "CHANGE" -> reporter = new ReporterChange(notifyIds, groupIds, value);
            case "FREQUENCY" -> reporter = new ReporterFrequency(notifyIds, groupIds, value);
            default -> throw new ParseException("Undefined reporter type: " + reporterType);
        }
        parserHelper.getSymbolTableReporter().add(reporterId, reporter);
    }
}
