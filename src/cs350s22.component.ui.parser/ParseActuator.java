package cs350s22.component.ui.parser;

import cs350s22.component.actuator.ActuatorLinear;
import cs350s22.component.actuator.ActuatorRotary;
import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;
import cs350s22.test.ActuatorPrototype;

import java.util.*;


public class ParseActuator {
    public static void parseActuatorCommand(A_ParserHelper parserHelper, String command) throws ParseException {
        switch (command.split(" ")[0].toUpperCase()) {
            case "CREATE" -> createNewActuator(parserHelper, command);
            default -> throw new ParseException("Undefined actuator command: " + command.split(" ")[0].toUpperCase());
        }
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
    }
}
