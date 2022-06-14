package cs350s22.component.ui.parser;

import cs350s22.component.sensor.A_Sensor;
import cs350s22.component.sensor.mapper.A_Mapper;
import cs350s22.component.sensor.reporter.A_Reporter;
import cs350s22.component.sensor.reporter.ReporterChange;
import cs350s22.component.sensor.watchdog.A_Watchdog;
import cs350s22.support.Identifier;
import cs350s22.test.MySensor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseSensor {

    public static void parseSensorCommand(A_ParserHelper parserHelper, String input) throws IOException {

        String[] argslist = input.split(" ");
        A_Mapper mapper = null;
        Identifier identifier = Identifier.make(argslist[2]);
        Identifier idOne;// for
        Identifier idTwo;
        Identifier idThree;
        Identifier idFour;
        Identifier sensor;
        /*for(int i = 0;i < argslist.length; i++){
            System.out.println(argslist[i]);
        }*/

        System.out.println(argslist[2]);
        //if (argslist[0].equals("CREATE") && argslist[1].equals("SENSOR")) {

            sensor = Identifier.make(argslist[1]);


            List<Identifier> identifierList = new ArrayList<>();
            List<A_Reporter> repoList= new ArrayList<>();
            List<A_Watchdog>  watchdogList = new ArrayList<>();


            //identifier = Identifier.make(argslist[2]);
            

            switch (argslist[2]) {

                case "SPEED":
                    for(int i = 2; i < argslist.length;i++){
                        if(argslist[i].contains("REPORTER")){
                            idTwo = Identifier.make(argslist[i+1]);
                            SymbolTable<A_Reporter> reporterSymbolTable = parserHelper.getSymbolTableReporter();
                            repoList.add(reporterSymbolTable.get(idTwo));
                        }if (argslist[i].contains("WATCHDOG")){
                            idThree = Identifier.make(argslist[i+1]);
                            SymbolTable<A_Watchdog> watchdogSymbolTable = parserHelper.getSymbolTableWatchdog();// if u push make sure it doesnt go to the local variable
                            watchdogList.add(watchdogSymbolTable.get(idThree));
                        }if (argslist[i].contains("MAPPER")){
                            idFour = Identifier.make(argslist[i+1]);
                            mapper = parserHelper.getSymbolTableMapper().get(idFour);

                        }

                    }
                    break;
                case "POSITION":
                    for (int i = 2; i< argslist.length; i++){
                        if(argslist[i].contains("REPORTER")){
                            idTwo = Identifier.make(argslist[i+1]);
                            //work on reorter since its adding an object that doesn't exist
                            //parsers to create a reporter
                            SymbolTable<A_Reporter> reporterSymbolTable = parserHelper.getSymbolTableReporter();
                            repoList.add(reporterSymbolTable.get(idTwo));
                        }if (argslist[i].contains("WATCHDOG")){
                            idThree = Identifier.make(argslist[i+1]);
                            SymbolTable<A_Watchdog> watchdogSymbolTable = parserHelper.getSymbolTableWatchdog();// if u push make sure it doesnt go to the local variable
                            watchdogList.add(watchdogSymbolTable.get(idThree));
                        }if (argslist[i].contains("MAPPER")){
                            idFour = Identifier.make(argslist[i+1]);
                            mapper = parserHelper.getSymbolTableMapper().get(idFour);

                        }

                    }
                    break;

                /*    identifierList.add(identifier);
                    //groups id
                    idOne = Identifier.make(argslist[3]);
                    identifierList.add(idOne);
                    //reporter
                    if (argslist[6].equals("REPORTER")) {
                        idTwo = Identifier.make(argslist[6]);
                        //id getName
                        SymbolTable<A_Reporter> reporterSymbolTable = parserHelper.getSymbolTableReporter();
                        repoList.add(reporterSymbolTable.get(idTwo));
                    //watchdog
                    }if (argslist[8].equals("WATCHDOG")) {//  need to grab watchdog at position 9 or at position//probably implement a for loop
                        idThree = Identifier.make(argslist[9]);
                        //getName
                        SymbolTable<A_Watchdog> watchdogSymbolTable = parserHelper.getSymbolTableWatchdog();// if u push make sure it doesnt go to the local variable
                        watchdogList.add(watchdogSymbolTable.get(idThree));
                        //mapper
                    }if (argslist[6].equals("MAPPER")){
                        idFour = Identifier.make(argslist[6]);
                        // create object that holds the value of the name of the id
                        // .getName().add(argsList[6])
                        //
                        //SymbolTable<A_Mapper> mapperSymbolTable = parserHelper.getSymbolTableMapper();
                        mapper = parserHelper.getSymbolTableMapper().get(idFour);
                    }
                    break;

                // psoition equals to position 2 idOne = Identifier.make(argslist[2]) identifierList.add(idOne)
                case "POSITION":
                    //this is here just to ignore the merge that intellij recommends
                    sensor = Identifier.make(argslist[1]);

                    identifierList.add(identifier);
                    //groups id
                    idOne =Identifier.make(argslist[3]);
                    identifierList.add(idOne);
                    //reporter
                    if (argslist[4].equals("REPORTER")) {
                        idTwo = Identifier.make(argslist[4]);
                        SymbolTable<A_Reporter> reporterSymbolTable = parserHelper.getSymbolTableReporter();
                        repoList.add(reporterSymbolTable.get(idTwo));
                        //watchdog
                    }if (argslist[5].equals("WATCHDOG")) {
                        idThree = Identifier.make(argslist[5]);
                        SymbolTable<A_Watchdog> watchdogSymbolTable = parserHelper.getSymbolTableWatchdog();
                        watchdogList.add(watchdogSymbolTable.get(idThree));
                        //mapper
                    }if (argslist[6].equals("MAPPER")){
                        idFour = Identifier.make(argslist[6]);
                        //SymbolTable<A_Mapper> mapperSymbolTable = parserHelper.getSymbolTableMapper();
                        mapper = parserHelper.getSymbolTableMapper().get(idFour);
                    }

                    //need identifier id, List<Identifier> groups, List<A_Reporter> reporters, List<A_Watchdog> watchdogs, A_Mapper mapper
                    break;*/

            }
        System.out.println(identifier.getName());
        System.out.println(identifierList.size());
        System.out.println(repoList.size());
        System.out.println(repoList.size());
            MySensor mySensor = new MySensor(identifier,identifierList,repoList,watchdogList,mapper);
            //get symbol table from parser helper
            parserHelper.getSymbolTableSensor().add(mySensor.getID(),mySensor);//add to the network symbol table so it is visible
            //finalize it and save
            //SymbolTable<A_Sensor> sensorTable= new SymbolTable<>();
            //sensorTable.add(sensor,mySensor);

        //}
    }
}
