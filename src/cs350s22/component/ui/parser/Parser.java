package cs350s22.component.ui.parser;

import cs350s22.support.Identifier;

import java.io.IOException;
import java.util.ArrayList;

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
            //Can't figure out @configure, javadoc is unfinished.
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

    public static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord){
        if(!startKeyWord.toUpperCase().endsWith("S")){
            startKeyWord = startKeyWord.toUpperCase() + "S";
        }
        String[] items = command.split(startKeyWord);
        items = items[1].trim().split(" ");
        ArrayList<Identifier> ids = new ArrayList<>();
        for (String item : items) {
            ids.add(Identifier.make(item));
        }
        return ids;
    }

    public static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord, String endKeyWord){
        ArrayList<Identifier> ids = new ArrayList<>();
        if(!command.toUpperCase().contains(startKeyWord.toUpperCase())){
            return ids;
        }
        if(!command.toUpperCase().contains(endKeyWord.toUpperCase())){
            return getSetOfIds(command, startKeyWord);
        }

        //handle s vs no s
        if(!startKeyWord.toUpperCase().endsWith("S")){
            String word = startKeyWord;
            startKeyWord = startKeyWord.toUpperCase() + "S";
            if(!command.toUpperCase().contains(startKeyWord)) {
                command = command.replaceFirst(word, startKeyWord);
            }
        }

        String[] items = command.split(startKeyWord);
        items = items[1].split(endKeyWord)[0].trim().split(" ");
        for (String item : items) {
            ids.add(Identifier.make(item));
        }
        return ids;
    }

    public static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord, String... endKeyWords) {
        ArrayList<Identifier> ids = new ArrayList<>();
        if (!command.toUpperCase().contains(startKeyWord.toUpperCase())) {
            return ids;
        }
        for (String endKeyWord : endKeyWords) {
            if (command.toUpperCase().contains(endKeyWord.toUpperCase())) {
                return getSetOfIds(command, startKeyWord, endKeyWord);
            }
        }
        return getSetOfIds(command, startKeyWord);
    }
}