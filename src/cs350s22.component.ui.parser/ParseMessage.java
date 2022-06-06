package cs350s22.component.ui.parser;

public class ParseMessage {
    public static void parseMessageCommand(A_ParserHelper parserHelper, String command) throws ParseException {
        if(command.ToUppercase().contains("PING"){
            sendMessagePing(parserHelper, command);
        } else if( command.ToUpperCase().contians("POSITION REQUEST"){
            sendMessagePositionRequest(parserHelper, command);
        } else if( command.ToUpperCase().contians("POSITION REPORT"){
            sendMessagePositionReport(parserHelper, command);
        } else {
            throw new ParseException("Undefined message command.");
        }
    }

    private static void sendMessagePing(A_ParserHelper parserHelper, String command) throws ParseException {

    }

    private static void sendMessagePositionRequest(A_ParserHelper parserHelper, String command) throws ParseException {

    }

    private static void sendMessagePositionReport(A_ParserHelper parserHelper, String command) throws ParseException {

    }

    private static List<List<Identifier>> getMessageIDsAndGroups(String command){
        //Get the ID's to send the message to.
        List<Identifier> sendToIds = new List<Identifier>();
        Scanner in = new Scanner(command.substring(command.toUpperCase().indexOf("MESSAGE") + 7, command.toUpperCase().indexOf("GROUPS") == -1 ? command.toUpperCase().indexOf("POSITION") : command.toUpperCase().indexOf("GROUPS")));
        while(in.hasNext()){
            sendToIds.add(Identifier.parseIdentifier(in.next()));
        }

        //Get the groups to send the message to.
        List<Group> sendToGroups = new List<Group>();
        if(command.toUpperCase().indexOf("GROUPS") != -1){
            in = new Scanner(command.substring(command.toUpperCase().indexOf("GROUPS") + 7, command.toUpperCase().indexOf("POSITION")));
            while(in.hasNext()){
                sendToGroups.add(Group.parseGroup(in.next()));
            }
        }
        List<List<Identifier>> idsAndGroups = new List<List<Identifier>>();
        idsAndGroups.add(sendToIds);
        idsAndGroups.add(sendToGroups);
        return idsAndGroups;
    }

    private static void createNewActuator(A_ParserHelper parserHelper, String command) throws ParseException {
        //Get the actuator type and ID
        String[] commandSplit = command.split(" ");
        String actuatorType = commandSplit[2];
        Identifier actuatorId = Identifier.make(commandSplit[3]);

        //Find the groups to put the actuator in
        int groupsLastIndex = command.toUpperCase().indexOf("SENSOR") - 1 == -1 ? command.toUpperCase().indexOf("ACCELERATION") - 1 : command.toUpperCase().indexOf("SENSOR") - 1;
        String groupParams = command.substring(command.toUpperCase().indexOf("GROUP") + 5, groupsLastIndex);
        List<Identifier> groups = Identifier.makeList(groupParams.split(" "));

        //Get all numeric parameters of the actuator
        String valueParams = command.substring(command.toUpperCase().indexOf("ACCELERATION") + 13);
        Scanner in = new Scanner(valueParams);
        in.useDelimiter(" ");
        String paramName = in.next();
        String[] params = {"LEADIN", "LEADOUT", "RELAX", "VELOCITY LIMIT", "VALUE MIN", "MAX", "INITIAL", "JERK LIMIT"};
        Map<String, Double> actuatorParams = new HashMap<>();
        for (String param : params) {
            if (param.contains(paramName)) {
                if (param.contains(" ")) {
                    in.next();
                }
                actuatorParams.put(param, in.nextDouble());
            } else {
                throw new ParseException("Invalid actuator parameter or parameters out of order");
            }
            if (in.hasNext()) {
                paramName = in.next();
            }
        }

        //Find the sensors to put the actuator and put it in symbol table
        int sensorsLastIndex = command.toUpperCase().indexOf("ACCELERATION") - 1 == -1 ? -1 : command.toUpperCase().indexOf("ACCELERATION") - 1;
        List<A_Sensor> sensors = new ArrayList<>();
        if (sensorsLastIndex == -1) {
            String sensorParams = command.substring(command.toUpperCase().indexOf("SENSOR") + 6);
            String[] sensorIdArray = sensorParams.split(" ");
            for (String sensorId : sensorIdArray) {
                SymbolTable<A_Sensor> sensorTable = parserHelper.getSymbolTableSensor();
                sensors.add(sensorTable.get(Identifier.make(sensorId)));
            }
            ActuatorPrototype prototype = new ActuatorPrototype(actuatorId, groups, actuatorParams.get("LEADIN"), actuatorParams.get("LEADOUT"), actuatorParams.get("RELAX"), actuatorParams.get("VELOCITY LIMIT"), actuatorParams.get("INITIAL"), actuatorParams.get("VALUE MIN"), actuatorParams.get("MAX"), actuatorParams.get("JERK LIMIT"), sensors);
            parserHelper.getSymbolTableActuator().add(actuatorId, prototype);
        } else {
            //Create non-prototype actuator by type and place into symbol table
            if (actuatorType.equalsIgnoreCase("linear")) {
                ActuatorLinear actuator = new ActuatorLinear(sensors, actuatorId, groups);
                parserHelper.getSymbolTableActuator().add(actuatorId, actuator);
            } else if (actuatorType.equalsIgnoreCase("rotary")) {
                ActuatorRotary actuator = new ActuatorRotary(sensors, actuatorId, groups);
                parserHelper.getSymbolTableActuator().add(actuatorId, actuator);
            } else {
                throw new ParseException("Invalid actuator type");
            }
        }
    }    }
}
