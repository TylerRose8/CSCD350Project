package cs350s22.component.ui.parser;

import java.io.IOException;

public class Parser implements ParserConstants {
    private final A_ParserHelper parserHelper;
    private final String input;

    public Parser(A_ParserHelper helper, String input) {
        this.parserHelper = helper;
        this.input = input;
    }

    public void parse() throws ParseException, IOException {
        String[] command = input.split(" ");
        if (command[0].contains("@")) {
            ParseMeta.parseMetaCommand(parserHelper, command);
        } else {
            String type = command[1].toUpperCase();
            switch (type) {
                case "ACTUATOR" -> ParseActuator.parseActuatorCommand(parserHelper, input);
                case "CONTROLLER", "DEPENDENCY" -> ParseController.parseControllerCommand(parserHelper, input);
                case "MAPPER" -> ParseMapper.parseMapperCommand(parserHelper, input);
                case "MESSAGE" -> ParseMessage.parseMessageCommand(parserHelper, input);
                case "NETWORK" -> ParseNetwork.parseNetworkCommand(parserHelper, input);
                case "REPORTER" -> ParseReporter.parseReporterCommand(parserHelper, input);
                case "SENSOR" -> ParseSensor.parseSensorCommand(parserHelper, input);
                case "WATCHDOG" -> ParseWatchdog.parseWatchdogCommand(parserHelper, input);
                default -> System.out.println("Invalid command");
            }
        }
    }
}