package cs350s22.component.ui.parser;

import cs350s22.component.sensor.A_Sensor;
import cs350s22.component.sensor.mapper.A_Mapper;
import cs350s22.component.sensor.mapper.MapperEquation;
import cs350s22.component.sensor.reporter.A_Reporter;
import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.watchdog.A_Watchdog;
import cs350s22.support.Identifier;
import cs350s22.test.MySensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class ParseSensor {
    public static void parseSensorCommand(A_ParserHelper parserHelper, String input) throws IOException, ParseException {

        String[] argslist = input.split(" ");
        A_Mapper mapper = null;
        Identifier identifier = Identifier.make(argslist[2]);
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
        Scanner scanner = new Scanner(input);
        String commandText = scanner.next();
        String type = argslist[2].toUpperCase();
        String id = argslist[3];

        for (int i = 4; i < argslist.length; i++) {
            if (argslist[i].toUpperCase().contains("GROUP")) {
                i++;
                while (i <= argslist.length & !argslist[i].equalsIgnoreCase("Reporter")) {

                    idOne = Identifier.make(argslist[i]);
                    identifierList.add(idOne);
                    //i++;
                }

            }
            if (argslist[i].toUpperCase().contains("REPORTER")) {
                i++;
                while (!argslist[i].equalsIgnoreCase("Watchdog") || !argslist[i].equalsIgnoreCase("MAPPER")) {

                    idTwo = Identifier.make(argslist[i]);
                    //A_Reporter aReporter = parserHelper.getSymbolTableReporter().get(idTwo);
                    //aReporter.getReportingIDs().add(idTwo);
                    repoList.add(parserHelper.getSymbolTableReporter().get(idTwo));
                    i++;

                }

            }
            if (argslist[i].toUpperCase().contains("WATCHDOG")) {
                i++;
                while (i <= argslist.length & !argslist[i].equalsIgnoreCase("MAPPER")) {
                    idThree = Identifier.make(argslist[i]);
                    //A_Watchdog watchdogSymbolTable = parserHelper.getSymbolTableWatchdog().get(idThree);// if u push make sure it doesnt go to the local variable
                    watchdogList.add(parserHelper.getSymbolTableWatchdog().get(idThree));
                    i++;
                }
            }
            if (i < argslist.length & argslist[i].toUpperCase().contains("MAPPER")) {
                idFour = Identifier.make(argslist[i]);
                mapper = parserHelper.getSymbolTableMapper().get(idFour);


            }

        }
        //work on this


        MySensor mySensor;
        Scanner creatorScanner = new Scanner(input);
        System.out.println("set if");
        if (creatorScanner.next().equalsIgnoreCase("SET")){
            idOne = Identifier.make(argslist[2]);
            System.out.println("1st if");
            mySensor = new MySensor(idOne);
            creatorScanner.next();
            creatorScanner.next();
            creatorScanner.next();
            if (creatorScanner.hasNextDouble()){
                System.out.println("inside if bloc");
                value = creatorScanner.nextDouble();
                mySensor.setValue(value);

            }
        }if (creatorScanner.next().equalsIgnoreCase("GET")) {
            creatorScanner.next();
            idOne = Identifier.make(argslist[2]);
            creatorScanner.next();
            mySensor = new MySensor(idOne);
            mySensor.getValue();
        }else {

            switch (type) {
                case "SPEED":
                    if (mapper == null) {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);

                    } else if (identifierList.size() == 0 && repoList.size() == 0 && watchdogList.size() == 0) {
                        mySensor = new MySensor(identifier);
                    } else {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                    }
                    parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);
                /* while (creatorScanner.hasNext()) {
                    //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs
                    if (creatorScanner.next().contains("REPORTERS") && creatorScanner.next().contains("WATCHDOGS") && !creatorScanner.next().contains("MAPPER") && creatorScanner.next().contains("GROUP")) {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);
                        parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);
                        //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs, A_Mapper mapper)
                    }
                    if (creatorScanner.next().contains("REPORTERS") && creatorScanner.next().contains("WATCHDOGS") && creatorScanner.next().contains("MAPPER") && !creatorScanner.next().contains("GROUP")) {
                        if(mapper==null) {
                            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);

                        }else {
                            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList,mapper);
                        }
                        parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);
                    }
                }*/

                    break;
                case "POSITION":
                    if (mapper == null) {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);

                    } else if (identifierList.size() == 0 && repoList.size() == 0 && watchdogList.size() == 0) {
                        mySensor = new MySensor(identifier);

                    } else {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                    }
                    parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);

                /*while (creatorScanner.hasNext()) {
                    //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs
                    if (creatorScanner.next().contains("REPORTERS") && creatorScanner.next().contains("WATCHDOGS") && !creatorScanner.next().contains("MAPPER") && creatorScanner.next().contains("GROUP")) {
                        mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);
                        parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);
                        //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs, A_Mapper mapper)
                    }
                    if (creatorScanner.next().contains("REPORTERS") && creatorScanner.next().contains("WATCHDOGS") && creatorScanner.next().contains("MAPPER") && !creatorScanner.next().contains("GROUP")) {
                        if (mapper == null) {
                            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);
                        }else{
                            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                        }
                        parserHelper.getSymbolTableSensor().add(mySensor.getID(), mySensor);*/
                    break;

            }
            //need for loop for four variations of sensor

       /* Scanner creatorScanner = new Scanner(input);
        //MySensor mySensor;
        while (creatorScanner.hasNext()){
            //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs
            if (creatorScanner.next().contains("REPORTERS")  && creatorScanner.next().contains("WATCHDOGS") && !creatorScanner.next().contains("MAPPER") && creatorScanner.next().contains("GROUP")){
                 mySensor = new MySensor(identifier,identifierList, repoList, watchdogList);
                parserHelper.getSymbolTableSensor().add(mySensor.getID(),mySensor);
                //public MySensor(Identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs, A_Mapper mapper)
            }if (creatorScanner.next().contains("REPORTERS") && creatorScanner.next().contains("WATCHDOGS") && creatorScanner.next().contains("MAPPER") && !creatorScanner.next().contains("GROUP")){
                mySensor= new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
                parserHelper.getSymbolTableSensor().add(mySensor.getID(),mySensor);

            }
        }*/
            //System.out.println(identifier.getName());
            //System.out.println(identifierList.size());
            //System.out.println(repoList.size());
            //System.out.println(repoList.size());
            //MySensor mySensor = new MySensor(identifier,identifierList,repoList,watchdogList,mapper);
            //get symbol table from parser helper
            //parserHelper.getSymbolTableSensor().add(mySensor.getID(),mySensor);//add to the network symbol table so it is visible
            //finalize it and save
            //SymbolTable<A_Sensor> sensorTable= new SymbolTable<>();
            //sensorTable.add(sensor,mySensor);

            //}
        }
    }

}
