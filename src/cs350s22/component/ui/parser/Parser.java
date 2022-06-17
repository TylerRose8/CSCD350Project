package cs350s22.component.ui.parser;

import cs350s22.component.A_Component;
import cs350s22.component.actuator.A_Actuator;
import cs350s22.component.actuator.ActuatorLinear;
import cs350s22.component.actuator.ActuatorRotary;
import cs350s22.component.controller.A_Controller;
import cs350s22.component.controller.dependency.A_Expression;
import cs350s22.component.controller.dependency.DependencySequencer;
import cs350s22.component.controller.dependency.Sequence;
import cs350s22.component.logger.LoggerMessage;
import cs350s22.component.logger.LoggerMessageSequencing;
import cs350s22.component.sensor.A_Sensor;
import cs350s22.component.sensor.mapper.A_Mapper;
import cs350s22.component.sensor.mapper.MapperEquation;
import cs350s22.component.sensor.mapper.MapperInterpolation;
import cs350s22.component.sensor.mapper.function.equation.EquationNormalized;
import cs350s22.component.sensor.mapper.function.equation.EquationPassthrough;
import cs350s22.component.sensor.mapper.function.equation.EquationScaled;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolationMap;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorLinear;
import cs350s22.component.sensor.mapper.function.interpolator.InterpolatorSpline;
import cs350s22.component.sensor.mapper.function.interpolator.loader.MapLoader;
import cs350s22.component.sensor.reporter.A_Reporter;
import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.reporter.ReporterFrequency;
import cs350s22.component.sensor.watchdog.*;
import cs350s22.component.sensor.watchdog.mode.A_WatchdogMode;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeAverage;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeInstantaneous;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeStandardDeviation;
import cs350s22.message.actuator.MessageActuatorReportPosition;
import cs350s22.message.actuator.MessageActuatorRequestPosition;
import cs350s22.message.ping.MessagePing;
import cs350s22.support.Clock;
import cs350s22.support.Filespec;
import cs350s22.support.Identifier;
import cs350s22.test.ActuatorPrototype;
import cs350s22.test.MyControllerSlaveForwarding;
import cs350s22.test.MyControllerSlaveNonforwarding;
import cs350s22.test.MySensor;

import java.io.IOException;
import java.util.*;

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
            parseMetaCommand(parserHelper, command);
            //Can't figure out @configure, javadoc is unfinished.
        } else {
            String type = command[1].toUpperCase();
            switch (type) {
                case "ACTUATOR" -> parseActuatorCommand(parserHelper, input);
                case "CONTROLLER", "DEPENDENCY" -> parseControllerCommand(parserHelper, input);
                case "MAPPER" -> parseMapperCommand(parserHelper, input);
                case "MESSAGE" -> parseMessageCommand(parserHelper, input);
                case "NETWORK" -> parseNetworkCommand(parserHelper, input);
                case "REPORTER" -> parseReporterCommand(parserHelper, input);
                case "SENSOR" -> parseSensorCommand(parserHelper, input);
                case "WATCHDOG" -> parseWatchdogCommand(parserHelper, input);
                default -> System.out.println("Invalid command");
            }
        }
    }

    // ACTUATOR METHODS
    //==================================================================================================================
    private void parseActuatorCommand(A_ParserHelper parserHelper, String command) throws ParseException {
        switch (command.split(" ")[0].toUpperCase()) {
            case "CREATE" -> createNewActuator(parserHelper, command);
            default -> throw new ParseException("Undefined actuator command: " + command.split(" ")[0].toUpperCase());
        }
    }

    private void createNewActuator(A_ParserHelper parserHelper, String command) throws ParseException {
        //Get the actuator type and ID
        String[] commandSplit = command.split(" ");
        String actuatorType = commandSplit[2];
        Identifier actuatorId = Identifier.make(commandSplit[3]);

        //Find the groups to put the actuator in
        List<Identifier> groups = Parser.getSetOfIds(command, "GROUP", "SENSOR", "ACCELERATION");
        for (Identifier group : groups) {
            System.out.println("Grouping " + actuatorId + " into " + group);
        }

        //Find the sensor ids
        List<Identifier> sensorIds = Parser.getSetOfIds(command, "SENSOR", "ACCELERATION");
        List<A_Sensor> sensors = new ArrayList<>();
        if (!sensorIds.isEmpty()) {
            for (Identifier sensor : sensorIds) {
                System.out.println("Adding " + sensor);
                sensors.add(parserHelper.getSymbolTableSensor().get(sensor));
            }
        }

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

        if (!sensors.isEmpty()) {
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

    // CONTROLLER METHODS
    //==================================================================================================================
    private void parseControllerCommand(A_ParserHelper parserHelper, String input) {
        String[] command = input.split(" ");
        String controllerString = command[3];
        Identifier controllerID = Identifier.make(controllerString);
        String type;
        int groupIndex = 0;
        String seq;
        String comp;
        Identifier compID;
        DependencySequencer ds = null;
        ArrayList<A_Component> comps = new ArrayList<A_Component>();
        ArrayList<Identifier> groupList = new ArrayList<Identifier>();
        A_Controller controller = null;
        String groupString = null;
        Identifier groupId = null;


        for (int i = 0; i < command.length; i++) {
            if (command[i].equalsIgnoreCase("GROUPS")) {
                i++;
                while ((!command[i].equalsIgnoreCase("DEPENDENCY")) || (!command[i].equalsIgnoreCase("WITH"))) {
                    groupString = command[i];
                    groupId = Identifier.make(groupString);
                    groupList.add(groupId);
                }
            }
            if (command[i].equalsIgnoreCase("SEQUENCER")) {
                i++;
                seq = command[i];
                A_Expression exp = null;
                Identifier seqID = Identifier.make(seq);
                ArrayList<Sequence> seqList = new ArrayList<Sequence>();
                Sequence seqAct = new Sequence(seqID, exp);
                seqList.add(seqAct);
                ds = new DependencySequencer(seqList);
            }
            if (command[i].equalsIgnoreCase("WITH")) {
                i++;
                if (command[i].equalsIgnoreCase("COMPONENT")) {
                    i++;
                    comp = command[i];
                    compID = Identifier.make(comp);
                    if (parserHelper.getSymbolTableController().contains(compID)) {
                        A_Controller controllerTemp = parserHelper.getSymbolTableController().get(compID);
                        comps.add(controllerTemp);
                    }
                    if (parserHelper.getSymbolTableActuator().contains(compID)) {
                        A_Actuator actuator = parserHelper.getSymbolTableActuator().get(compID);
                        comps.add(actuator);
                    }
                    if (parserHelper.getSymbolTableSensor().contains(compID)) {
                        A_Sensor sensor = parserHelper.getSymbolTableSensor().get(compID);
                        comps.add(sensor);
                    }
                } else if (command[i].equalsIgnoreCase("COMPONENTS")) {
                    i++;
                    for (; i < command.length; i++) {
                        comp = command[i];
                        compID = Identifier.make(comp);
                        if (parserHelper.getSymbolTableController().contains(compID)) {
                            A_Controller controllerTemp = parserHelper.getSymbolTableController().get(compID);
                            comps.add(controllerTemp);
                        }
                        if (parserHelper.getSymbolTableActuator().contains(compID)) {
                            A_Actuator actuator = parserHelper.getSymbolTableActuator().get(compID);
                            comps.add(actuator);
                        }
                        if (parserHelper.getSymbolTableSensor().contains(compID)) {
                            A_Sensor sensor = parserHelper.getSymbolTableSensor().get(compID);
                            comps.add(sensor);
                        }
                    }
                }
            }

        }
        if (command[2].equalsIgnoreCase("FORWARDING")) {
            if (groupIndex == 0 && ds == null) {
                controller = new MyControllerSlaveForwarding(controllerID);
            }
            if (groupIndex != 0 && ds == null) {
                controller = new MyControllerSlaveForwarding(controllerID, groupList);
            }
            if (ds != null && groupIndex == 0) {
                controller = new MyControllerSlaveForwarding(controllerID, ds);
            }
            if (ds != null && groupIndex != 0) {
                controller = new MyControllerSlaveForwarding(controllerID, groupList, ds);
            }
            if (!comps.isEmpty()) {
                controller.addComponents(comps);
            }
        }
        if (command[2].equalsIgnoreCase("NONFORWARDING")) {
            if (groupIndex == 0 && ds == null) {
                controller = new MyControllerSlaveNonforwarding(controllerID);
            }
            if (groupIndex != 0 && ds == null) {
                controller = new MyControllerSlaveNonforwarding(controllerID, groupList);
            }
            if (ds != null && groupIndex == 0) {
                controller = new MyControllerSlaveNonforwarding(controllerID, ds);
            }
            if (ds != null && groupIndex != 0) {
                controller = new MyControllerSlaveNonforwarding(controllerID, groupList, ds);
            }
            if (!comps.isEmpty()) {
                controller.addComponents(comps);
            }
        }
    }

    // MAPPER METHODS
    //==================================================================================================================
    private void parseMapperCommand(A_ParserHelper parserHelper, String input) throws IOException {
        String[] command = input.split(" ");
        String type;
        double scaleValue = 0;
        double normValueOne = 0;
        double normValueTwo = 0;

        type = command[3];
        String val = command[2];
        Identifier id = Identifier.make(val);

        switch (type) {
            case "EQUATION":
                if (command[4].contains("PASSTHROUGH")) {
                    EquationPassthrough ep = new EquationPassthrough();
                    MapperEquation me = new MapperEquation(ep);
                    parserHelper.getSymbolTableMapper().add(id, me);
                }
                if (command[4].contains("SCALE")) {
                    scaleValue = Double.parseDouble(command[5]);
                    EquationScaled es = new EquationScaled(scaleValue);
                    MapperEquation me = new MapperEquation(es);
                    parserHelper.getSymbolTableMapper().add(id, me);
                }
                if (command[4].contains("NORMALIZE")) {
                    normValueOne = Double.parseDouble(command[5]);
                    normValueTwo = Double.parseDouble(command[6]);
                    EquationNormalized es = new EquationNormalized(normValueOne, normValueTwo);
                    MapperEquation me = new MapperEquation(es);
                    parserHelper.getSymbolTableMapper().add(id, me);
                }
                break;
            case "INTERPOLATION":
                Filespec filepath = new Filespec(command[6]);
                MapLoader mapLoader = new MapLoader(filepath);
                InterpolationMap interMap = mapLoader.load();
                if (command[4].contains("LINEAR")) {
                    InterpolatorLinear interpolator = new InterpolatorLinear(interMap);
                    MapperInterpolation mapInter = new MapperInterpolation(interpolator);
                    parserHelper.getSymbolTableMapper().add(id, mapInter);
                } else if (command[4].contains("SPLINE")) {
                    InterpolatorSpline interpolator = new InterpolatorSpline(interMap);
                    MapperInterpolation mapInter = new MapperInterpolation(interpolator);
                    parserHelper.getSymbolTableMapper().add(id, mapInter);
                }
                break;
        }
    }

    // MESSAGE METHODS
    //==================================================================================================================
    private void parseMessageCommand(A_ParserHelper parserHelper, String command) throws ParseException {
        if (command.toUpperCase().contains("PING")) {
            sendMessagePing(parserHelper);
        } else if (command.toUpperCase().contains("POSITION REQUEST")) {
            sendMessagePositionRequest(parserHelper, command);
        } else if (command.toUpperCase().contains("POSITION REPORT")) {
            sendMessagePositionReport(parserHelper, command);
        } else {
            throw new ParseException("Undefined message command.");
        }
    }

    private void sendMessagePing(A_ParserHelper parserHelper) {
        parserHelper.getCommandLineInterface().issueMessage(new MessagePing());
    }

    private void sendMessagePositionRequest(A_ParserHelper parserHelper, String command) throws ParseException {
        ArrayList<ArrayList<Identifier>> messageIdsAndGroups = getMessageIDsAndGroups(command);
        try {
            double value = Double.parseDouble(command.split(" ")[command.split(" ").length - 1]);
            if (!messageIdsAndGroups.get(0).isEmpty()) {
                MessageActuatorRequestPosition reportToIds = new MessageActuatorRequestPosition(messageIdsAndGroups.get(0), value);
                parserHelper.getCommandLineInterface().issueMessage(reportToIds);
            } else if (!messageIdsAndGroups.get(1).isEmpty()) {
                MessageActuatorRequestPosition reportToGroups = new MessageActuatorRequestPosition(messageIdsAndGroups.get(1), value);
                parserHelper.getCommandLineInterface().issueMessage(reportToGroups);
            }
        } catch (Exception e) {
            throw new ParseException("Bad value sent in MessagePositionRequest: " + command.split(" ")[command.split(" ").length - 1]);
        }
    }

    private void sendMessagePositionReport(A_ParserHelper parserHelper, String command) {
        ArrayList<ArrayList<Identifier>> messageIdsAndGroups = getMessageIDsAndGroups(command);
        if (!messageIdsAndGroups.get(0).isEmpty()) {
            MessageActuatorReportPosition reportToIds = new MessageActuatorReportPosition(messageIdsAndGroups.get(0));
            parserHelper.getCommandLineInterface().issueMessage(reportToIds);
        } else if (!messageIdsAndGroups.get(1).isEmpty()) {
            MessageActuatorReportPosition reportToGroups = new MessageActuatorReportPosition(messageIdsAndGroups.get(1));
            parserHelper.getCommandLineInterface().issueMessage(reportToGroups);
        }

    }

    private ArrayList<ArrayList<Identifier>> getMessageIDsAndGroups(String command) {
        //Get the ID's to send the message to.
        ArrayList<Identifier> sendToIds = new ArrayList<>();
        Scanner in = new Scanner(command.substring(command.toUpperCase().indexOf("MESSAGE") + 7, !command.toUpperCase().contains("GROUPS") ? command.toUpperCase().indexOf("POSITION") : command.toUpperCase().indexOf("GROUPS")));
        while (in.hasNext()) {
            sendToIds.add(Identifier.make(in.next()));
        }

        //Get the groups to send the message to.
        ArrayList<Identifier> sendToGroups = new ArrayList<>();
        if (command.toUpperCase().contains("GROUPS")) {
            in = new Scanner(command.substring(command.toUpperCase().indexOf("GROUPS") + 7, command.toUpperCase().indexOf("POSITION")));
            while (in.hasNext()) {
                sendToGroups.add(Identifier.make(in.next()));
            }
        }
        ArrayList<ArrayList<Identifier>> idsAndGroups = new ArrayList<>();
        idsAndGroups.add(sendToIds);
        idsAndGroups.add(sendToGroups);
        return idsAndGroups;
    }

    // META METHODS
    //==================================================================================================================
    private void parseMetaCommand(A_ParserHelper parserHelper, String[] command) throws ParseException, IOException {
        String type = command[0].toUpperCase();
        //there is no space between @ and the first word so it would still be [0]
        switch (type) {
            case "@CLOCK":
                createClock(parserHelper, command);
                break;
            case "@EXIT":
                parserHelper.exit();
                break;
            case "@RUN":
                parserHelper.run(command[1]);
                break;
            case "@CONFIGURE":
                configure(parserHelper, command);
                break;
        }

    }


    private void configure(A_ParserHelper parserHelper, String[] command) throws IOException {
        for (int i = 1; i < command.length; i++) {
            String type = command[i].toUpperCase();
            switch (type) {
                case "LOG":
                    i++;
                    Filespec filepath = Filespec.make(command[i]);
                    LoggerMessage.initialize(filepath);
                    break;
                case "DOT":
                    break;
                case "SEQUENCE":
                    i++;
                    Filespec filepathTwo = Filespec.make(command[i]);
                    Filespec filepathThree = null;
                    i++;
                    if (command[i].equalsIgnoreCase("NETWORK")) {
                        i++;
                        filepathThree = Filespec.make(command[i]);
                    }
                    LoggerMessageSequencing.initialize(filepathTwo, filepathThree);
                    break;
            }
        }
    }


    private void createClock(A_ParserHelper parserHelper, String[] command) {
        Clock clock = Clock.getInstance();

        if (command.length == 1) {
            System.out.println(clock.getTime());
            return;
        }
        String type = command[1].toUpperCase();

        String value;
        int val;
        double valDouble;
        switch (type) {
            case "WAIT":
                valDouble = Double.parseDouble(command[3]);
                if (command[2].equalsIgnoreCase("FOR")) {
                    clock.waitFor(valDouble);
                } else if (command[2].equalsIgnoreCase("UNTIL")) {
                    clock.waitUntil(valDouble);
                }
                break;
            case "PAUSE":
                clock.isActive(false);
                break;
            case "RESUME":
                clock.isActive(true);
                break;
            case "ONESTEP":
                if (command.length > 2) {
                    value = command[2];
                    val = Integer.parseInt(value);
                    clock.onestep(val);
                } else {
                    clock.onestep();
                }
                break;
            case "SET":
                if (command[2].equalsIgnoreCase("RATE")) {
                    value = command[3];
                    val = Integer.parseInt(value);
                    clock.setRate(val);
                }
                break;
        }
    }

    // NETWORK METHODS
    //==================================================================================================================
    private void parseNetworkCommand(A_ParserHelper parserHelper, String input) {
        ArrayList<A_Component> list = new ArrayList<>();

        List<Identifier> components = Parser.getSetOfIds(input, "COMPONENT");

        for (Identifier component : components) {
            if (parserHelper.getSymbolTableController().contains(component)) {
                list.add(parserHelper.getSymbolTableController().get(component));
            } else if (parserHelper.getSymbolTableActuator().contains(component)) {
                list.add(parserHelper.getSymbolTableActuator().get(component));
            } else if (parserHelper.getSymbolTableSensor().contains(component)) {
                list.add(parserHelper.getSymbolTableSensor().get(component));
            }
        }
        parserHelper.getControllerMaster().addComponents(list);
        parserHelper.getNetwork().writeOutput();
    }

    // REPORTER METHODS
    //==================================================================================================================
    private void parseReporterCommand(A_ParserHelper parserHelper, String command) throws ParseException {

        switch (command.split(" ")[0].toUpperCase()) {
            case "CREATE" -> createNewReporter(parserHelper, command);
            default -> throw new ParseException("Undefined actuator command: " + command.split(" ")[0].toUpperCase());
        }
    }

    private void createNewReporter(A_ParserHelper parserHelper, String command) throws ParseException {
        //Get the actuator type and ID
        String[] commandSplit = command.split(" ");
        String reporterType = commandSplit[2];
        Identifier reporterId = Identifier.make(commandSplit[3]);
        int value = Integer.parseInt(commandSplit[commandSplit.length - 1]);

        //Get notify ids
        Scanner in = new Scanner(command.substring(command.toUpperCase().indexOf("NOTIFY") + 7));
        String next = in.next();
        ArrayList<Identifier> notifyIds = Parser.getSetOfIds(command, "IDS", "GROUPS", "DELA");
        //Get group ids
        ArrayList<Identifier> groupIds = Parser.getSetOfIds(command, "GROUPS", "DELTA");

        //Create the reporter and add it to the network
        A_Reporter reporter;
        switch (reporterType.toUpperCase()) {
            case "CHANGE" -> reporter = new ReporterChange(notifyIds, groupIds, value);
            case "FREQUENCY" -> reporter = new ReporterFrequency(notifyIds, groupIds, value);
            default -> throw new ParseException("Undefined reporter type: " + reporterType);
        }
        parserHelper.getSymbolTableReporter().add(reporterId, reporter);
    }

    // SENSOR METHODS
    //==================================================================================================================
    private void parseSensorCommand(A_ParserHelper parserHelper, String input) throws IOException, ParseException {

        String[] argslist = input.split(" ");
        A_Sensor mySensor;
        A_Mapper mapper = null;
        Identifier identifier = Identifier.make(argslist[3]);
        Identifier idOne;
        Identifier idTwo;
        Identifier idThree;
        Identifier idFour;
        Identifier sensor;
        double value;

        A_Reporter a_reporter;

        sensor = Identifier.make(argslist[1]);


        List<Identifier> identifierList = new ArrayList<>();
        List<A_Reporter> repoList = new ArrayList<>();
        List<A_Watchdog> watchdogList = new ArrayList<>();


        //identifier = Identifier.make(argslist[2]);
        //Scanner scanner = new Scanner(input);
        //String commandText = scanner.next();
        String type = argslist[2].toUpperCase();

        //String id = argslist[3];


        for (int i = 0; i < argslist.length; i++) {
            String type1 = argslist[i].toUpperCase();
            switch (type1) {

                case "GROUP":
                    i++;
                    idOne = Identifier.make(argslist[i]);
                    identifierList.add(idOne);
                    break;
                case "RERPORTER":
                    i++;
                    idTwo = Identifier.make(argslist[i]);
                    repoList.add(parserHelper.getSymbolTableReporter().get(idTwo));
                    break;
                case "RERPORTERS":
                    i++;
                    while (argslist[i].equalsIgnoreCase("WATCHDOG") || argslist[i].equalsIgnoreCase("WATCHDOGS") || argslist[i].equalsIgnoreCase("MAPPER") || i < argslist.length - 1) {
                        idTwo = Identifier.make(argslist[i]);
                        repoList.add(parserHelper.getSymbolTableReporter().get(idTwo));
                        i++;
                    }
                    break;
                case "WATCHDOG":
                    i++;
                    idThree = Identifier.make(argslist[i]);
                    watchdogList.add(parserHelper.getSymbolTableWatchdog().get(idThree));
                    break;
                case "WATCHDOGS":
                    i++;
                    while (argslist[i].equalsIgnoreCase("MAPPER") || i < argslist.length - 1) {
                        idThree = Identifier.make(argslist[i]);
                        watchdogList.add(parserHelper.getSymbolTableWatchdog().get(idThree));
                        i++;
                    }
                    break;
                case "MAPPER":
                    i++;
                    idFour = Identifier.make(argslist[i]);
                    mapper = parserHelper.getSymbolTableMapper().get(idFour);
                    break;
                case "GET":
                    idOne = Identifier.make(argslist[2]);
                    mySensor = parserHelper.getSymbolTableSensor().get(idOne);
                    System.out.println("The value of " + mySensor.getID() + " is " + mySensor.getValue());
                    break;
                case "SET":
                    idOne = Identifier.make(argslist[2]);

                    mySensor = parserHelper.getSymbolTableSensor().get(idOne);
                    //mySensor.registerController(parserHelper.getControllerMaster());
                    String valueString = argslist[4];
                    int valueSet = Integer.parseInt(valueString);
                    mySensor.setValue(valueSet);
                    break;
            }
        }

        switch (type) {
            case "SPEED":
                if (mapper == null) {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);

                } else if (identifierList.size() == 0 && repoList.size() == 0 && watchdogList.size() == 0) {
                    mySensor = new MySensor(identifier);
                } else {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                }
                if (mapper != null) {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                }
                SymbolTable<A_Sensor> symSensor = parserHelper.getSymbolTableSensor();
                symSensor.add(mySensor.getID(), mySensor);
                break;
            case "POSITION":
                if (mapper == null) {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);

                } else if (identifierList.size() == 0 && repoList.size() == 0 && watchdogList.size() == 0) {
                    mySensor = new MySensor(identifier);

                } else {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                }
                if (mapper != null) {
                    mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                }
                SymbolTable<A_Sensor> symSensor1 = parserHelper.getSymbolTableSensor();
                symSensor1.add(mySensor.getID(), mySensor);
                symSensor1.contains(mySensor.getID());
                break;
        }

    }

    // WATCHDOG METHODS
    //==================================================================================================================

    private void parseWatchdogCommand(A_ParserHelper parserHelper, String input) throws IOException {
        String[] command = input.split(" ");
        Scanner commandScanner = new Scanner(input);
        Identifier id;
        A_WatchdogMode mode = null;
        double valueOne = 0;
        double valueTwo = 0;
        int valueThree = 0;

        String watchdogCreator = command[2].toUpperCase();


        if (!(commandScanner.next().equalsIgnoreCase("CREATE") &&
                commandScanner.next().equalsIgnoreCase("WATCHDOG"))) {
            throw new IllegalStateException("is not CREATE WATCHDOG");
        }

        SymbolTable<A_Watchdog> watchdogSymbolTable = parserHelper.getSymbolTableWatchdog();

        String watchdogType = commandScanner.next();

        switch (watchdogType) {
            case "ACCELERATION":

                id = Identifier.make(commandScanner.next());

                if (commandScanner.next().equalsIgnoreCase("Mode")) {

                    String watchdogMode = commandScanner.next();

                    if (watchdogMode.equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        // use method recordValue() if one is detected in the command

                    }
                    if (watchdogMode.equalsIgnoreCase("AVERAGE")) {
                        //mode =new WatchdogModeAverage();
                        //need to add watchdogModeAverage(int windowsize)
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        } else {
                            mode = new WatchdogModeAverage();
                        }


                    }
                    if (watchdogMode.equalsIgnoreCase("STANDARD")) {
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        //need to add watchdogModeDeviation(int windowsize)
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        } else {
                            mode = new WatchdogModeStandardDeviation();
                        }

                    }
                } else {
                    throw new IllegalStateException("mode is missing");
                }
                //if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {
                while (commandScanner.hasNext()) {
                    String thresholdInput = commandScanner.next();
                    if (thresholdInput.contains("LOW")) {
                        valueOne = Double.parseDouble(commandScanner.next());
                    }
                    if (thresholdInput.contains("HIGH")) {
                        valueTwo = Double.parseDouble(commandScanner.next());
                        //System.out.println(valueTwo);

                    }
                    if (thresholdInput.contains("GRACE")) {
                        //valueThree = Double.parseDouble(commandScanner.next());
                        valueThree = Integer.parseInt(commandScanner.next());

                    }
                }
                //} else {
                //   throw new IllegalStateException("THRESHOLD is missing");
                // }

                break;
            case "BAND":
                // String band;//this is here to deal with intellij correction on case
                id = Identifier.make((commandScanner.next()));
                if (commandScanner.next().equalsIgnoreCase("Mode")) {

                    String watchdogMode = commandScanner.next();

                    if (watchdogMode.equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();


                    }
                    if (watchdogMode.equalsIgnoreCase("AVERAGE")) {
                        //mode =new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        } else {
                            mode = new WatchdogModeAverage();
                        }
                    }
                    if (watchdogMode.equalsIgnoreCase("STANDARD")) {
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        } else {
                            mode = new WatchdogModeStandardDeviation();
                        }

                    }

                    //mode = parserHelper.getSymbolTableWatchdog().get(Identifier.make(commandScanner.next())).getMode();//mode is still a problem
                } else {
                    throw new IllegalStateException("mode is missing");
                }
                // if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {
                while (commandScanner.hasNext()) {
                    String thresholdInput = commandScanner.next();
                    if (thresholdInput.contains("LOW")) {
                        valueOne = Double.parseDouble(commandScanner.next());
                    }
                    if (thresholdInput.contains("HIGH")) {
                        valueTwo = Double.parseDouble(commandScanner.next());

                    }
                    if (thresholdInput.contains("GRACE")) {
                        //valueThree = Double.parseDouble(commandScanner.next());
                        valueThree = Integer.parseInt(commandScanner.next());
                    }
                }

                //} else {
                //    throw new IllegalStateException("THRESHOLD is missing");
                //}
                break;

            case "NOTCH":
                // int t; //this is here to deal with intellij
                id = Identifier.make((commandScanner.next()));
                if (commandScanner.next().equalsIgnoreCase("Mode")) {

                    String watchdogMode = commandScanner.next();

                    if (watchdogMode.equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();


                    }
                    if (watchdogMode.equalsIgnoreCase("AVERAGE")) {
                        //mode =new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        } else {
                            mode = new WatchdogModeAverage();
                        }


                    }
                    if (watchdogMode.equalsIgnoreCase("STANDARD")) {
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        } else {
                            mode = new WatchdogModeStandardDeviation();
                        }
                    }
                    //mode = parserHelper.getSymbolTableWatchdog().get(Identifier.make(commandScanner.next())).getMode();//mode is still a problem
                } else {
                    throw new IllegalStateException("mode is missing");
                }
                //if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {
                while (commandScanner.hasNext()) {
                    String thresholdInput = commandScanner.next();
                    if (thresholdInput.contains("LOW")) {
                        valueOne = Double.parseDouble(commandScanner.next());
                    }
                    if (thresholdInput.contains("HIGH")) {
                        valueTwo = Double.parseDouble(commandScanner.next());

                    }
                    if (thresholdInput.contains("GRACE")) {
                        //valueThree = Double.parseDouble(commandScanner.next());
                        valueThree = Integer.parseInt(commandScanner.next());
                    }
                }

                //} else {
                //  throw new IllegalStateException("THRESHOLD is missing");
                //}

                break;
            case "LOW":
                //double low;
                id = Identifier.make((commandScanner.next()));

                if (commandScanner.next().equalsIgnoreCase("Mode")) {

                    String watchdogMode = commandScanner.next();

                    if (watchdogMode.equalsIgnoreCase("INSTANTANEOUS")) {
                        id = Identifier.make(commandScanner.next());
                        mode = new WatchdogModeInstantaneous();


                    }
                    if (watchdogMode.equalsIgnoreCase("AVERAGE")) {
                        //mode = new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        } else {
                            mode = new WatchdogModeAverage();
                        }
                    }
                    if (watchdogMode.equalsIgnoreCase("STANDARD")) {
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        } else {
                            mode = new WatchdogModeStandardDeviation();
                        }


                    }
                    //mode = parserHelper.getSymbolTableWatchdog().get(Identifier.make(commandScanner.next())).getMode();//mode is still a problem
                    //} else {
                    //  throw new IllegalStateException("mode is missing");
                    //}
                    //if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {
                    while (commandScanner.hasNext()) {
                        String thresholdInput = commandScanner.next();
                        if (thresholdInput.contains("THRESHOLD")) {
                            //commandScanner.next();
                            valueOne = Double.parseDouble(commandScanner.next());
                        }
                        if (thresholdInput.contains("GRACE")) {
                            //valueThree = Double.parseDouble(commandScanner.next());
                            valueThree = Integer.parseInt(commandScanner.next());
                        }
                    }

                } //else {
                //   throw new IllegalStateException("THRESHOLD is missing");
                //}

                break;

            case "HIGH":

                id = Identifier.make((commandScanner.next()));
                if (commandScanner.next().equalsIgnoreCase("Mode")) {

                    String watchdogMode = commandScanner.next();

                    if (watchdogMode.equalsIgnoreCase("INSTANTANEOUS")) {
                        id = Identifier.make(commandScanner.next());
                        mode = new WatchdogModeInstantaneous();


                    }
                    if (watchdogMode.equalsIgnoreCase("AVERAGE")) {

                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        } else {
                            mode = new WatchdogModeAverage();
                        }

                    }
                    if (watchdogMode.equalsIgnoreCase("STANDARD")) {
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()) {
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        } else {
                            mode = new WatchdogModeStandardDeviation();
                        }

                    }

                } else {
                    throw new IllegalStateException("mode is missing");
                }
                // if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {

                while (commandScanner.hasNext()) {
                    String thresholdInput = commandScanner.next();
                    if (thresholdInput.contains("THRESHOLD")) {
                        valueOne = Double.parseDouble(commandScanner.next());
                    }
                    if (thresholdInput.contains("GRACE")) {

                        //valueThree = Double.parseDouble(commandScanner.next());
                        valueThree = Integer.parseInt(commandScanner.next());
                    }
                }

                //} else {
                //    throw new IllegalStateException("THRESHOLD is missing");
                // }

                break;


            default:
                throw new IllegalStateException("Unexpected value: " + watchdogType);


        }
        //Watchdog creation
        switch (watchdogCreator) {
            case "ACCELERATION":
                if (valueThree == 0) {
                    WatchdogAcceleration watchdogAcceleration = new WatchdogAcceleration(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogAcceleration);
                    System.out.println(watchdogAcceleration);
                } else {
                    WatchdogAcceleration watchdogAccelerationGrace = new WatchdogAcceleration(valueOne, valueTwo, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogAccelerationGrace);
                    System.out.println(watchdogAccelerationGrace);
                }
                break;
            case "BAND":
                if (valueThree == 0) {
                    WatchdogBand watchdogBandTwo = new WatchdogBand(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogBandTwo);
                    System.out.println(watchdogBandTwo);
                } else {
                    WatchdogBand watchdogBand = new WatchdogBand(valueOne, valueTwo, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogBand);
                    System.out.println(watchdogBand);
                }
                break;
            case "NOTCH":
                if (valueThree == 0) {
                    WatchdogNotch watchdogNotchTwo = new WatchdogNotch(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogNotchTwo);
                    System.out.println(watchdogNotchTwo);
                } else {
                    WatchdogNotch watchdogNotch = new WatchdogNotch(valueOne, valueTwo, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogNotch);
                    System.out.println(watchdogNotch);
                }
                break;
            case "LOW":
                if (valueThree == 0) {
                    WatchdogLow watchdogLowTwo = new WatchdogLow(valueOne, mode);
                    watchdogSymbolTable.add(id, watchdogLowTwo);
                    System.out.println(watchdogLowTwo);
                } else {
                    WatchdogLow watchdogLow = new WatchdogLow(valueOne, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogLow);
                    System.out.println(watchdogLow);
                }
                break;
            case "HIGH":
                if (valueThree == 0) {
                    WatchdogHigh watchdogHighTwo = new WatchdogHigh(valueOne, mode);
                    watchdogSymbolTable.add(id, watchdogHighTwo);
                    System.out.println(watchdogHighTwo);
                } else {
                    WatchdogHigh watchdogHigh = new WatchdogHigh(valueOne, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogHigh);
                    System.out.println(watchdogHigh);
                }
                break;
        }
    }

    // UTILITY METHODS
    //==================================================================================================================
    private static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord) {
        //handle s vs no s
        if (!startKeyWord.toUpperCase().endsWith("S")) {
            String word = startKeyWord;
            startKeyWord = startKeyWord.toUpperCase() + "S";
            if (!command.toUpperCase().contains(startKeyWord)) {
                command = command.replaceFirst(word, startKeyWord);
            }
        }
        String[] items = command.split(startKeyWord);
        items = items[1].trim().split(" ");
        ArrayList<Identifier> ids = new ArrayList<>();
        for (String item : items) {
            ids.add(Identifier.make(item));
        }
        return ids;
    }

    private static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord, String endKeyWord) {
        ArrayList<Identifier> ids = new ArrayList<>();
        if (!command.toUpperCase().contains(startKeyWord.toUpperCase())) {
            return ids;
        }
        if (!command.toUpperCase().contains(endKeyWord.toUpperCase())) {
            return getSetOfIds(command, startKeyWord);
        }

        //handle s vs no s
        if (!startKeyWord.toUpperCase().endsWith("S")) {
            String word = startKeyWord;
            startKeyWord = startKeyWord.toUpperCase() + "S";
            if (!command.toUpperCase().contains(startKeyWord)) {
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

    private static ArrayList<Identifier> getSetOfIds(String command, String startKeyWord, String... endKeyWords) {
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