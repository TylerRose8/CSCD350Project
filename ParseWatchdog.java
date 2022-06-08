package cs350s22.component.ui.parser;

import cs350s22.component.sensor.watchdog.*;
import cs350s22.component.sensor.watchdog.mode.A_WatchdogMode;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeAverage;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeInstantaneous;
import cs350s22.component.sensor.watchdog.mode.WatchdogModeStandardDeviation;
import cs350s22.support.Identifier;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class ParseWatchdog {

    public static void parseWatchdogCommand(A_ParserHelper parserHelper, String input) throws IOException {
        //String[] command = input.split(" ");
        Scanner commandScanner = new Scanner(input);
        Identifier id;
        A_WatchdogMode mode = null;
        double valueOne = 0;
        double valueTwo = 0;
        double valueThree = 0;


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
                    //mode needs to get INSTANTANEOUS
                    //ask teammates if this is suppose to be how its done
                    if (commandScanner.next().equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        //mode.getAggregrateValue();
                        //System.out.println(mode);
                        
                    }if(commandScanner.next().equalsIgnoreCase("AVERAGE")){
                        mode =new WatchdogModeAverage();
                        //mode.getAggregrateValue();

                    }if (commandScanner.next().equalsIgnoreCase("STANDARD DEVIATION")){
                        mode = new WatchdogModeStandardDeviation();

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
                            valueThree = Double.parseDouble(commandScanner.next());
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
                    if (commandScanner.next().equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        //mode.getAggregrateValue();
                        //System.out.println(mode);

                    }if(commandScanner.next().equalsIgnoreCase("AVERAGE")){
                        mode =new WatchdogModeAverage();
                        //mode.getAggregrateValue();

                    }if (commandScanner.next().equalsIgnoreCase("STANDARD DEVIATION")){
                        mode = new WatchdogModeStandardDeviation();

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
                            valueThree = Double.parseDouble(commandScanner.next());
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
                    if (commandScanner.next().equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        //mode.getAggregrateValue();
                        //System.out.println(mode);

                    }if(commandScanner.next().equalsIgnoreCase("AVERAGE")){
                        mode =new WatchdogModeAverage();
                        //mode.getAggregrateValue();

                    }if (commandScanner.next().equalsIgnoreCase("STANDARD DEVIATION")){
                        mode = new WatchdogModeStandardDeviation();

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
                            valueThree = Double.parseDouble(commandScanner.next());
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
                    if (commandScanner.next().equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        //mode.getAggregrateValue();
                        //System.out.println(mode);

                    }if(commandScanner.next().equalsIgnoreCase("AVERAGE")){
                        mode =new WatchdogModeAverage();
                        //mode.getAggregrateValue();

                    }if (commandScanner.next().equalsIgnoreCase("STANDARD DEVIATION")){
                        mode = new WatchdogModeStandardDeviation();

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
                            valueThree = Double.parseDouble(commandScanner.next());
                        }
                    }

                //} else {
                 //   throw new IllegalStateException("THRESHOLD is missing");
                //}

                break;

            case "HIGH":
                char high;
                id = Identifier.make((commandScanner.next()));
                if (commandScanner.next().equalsIgnoreCase("Mode")) {
                    if (commandScanner.next().equalsIgnoreCase("INSTANTANEOUS")) {
                        mode = new WatchdogModeInstantaneous();
                        //mode.getAggregrateValue();
                        //System.out.println(mode);

                    }if(commandScanner.next().equalsIgnoreCase("AVERAGE")){
                        mode =new WatchdogModeAverage();
                        //mode.getAggregrateValue();

                    }if (commandScanner.next().equalsIgnoreCase("STANDARD DEVIATION")){
                        mode = new WatchdogModeStandardDeviation();

                    }
                    //mode = parserHelper.getSymbolTableWatchdog().get(Identifier.make(commandScanner.next())).getMode();//mode is still a problem
                } else {
                    throw new IllegalStateException("mode is missing");
                }
                if (commandScanner.next().equalsIgnoreCase("THRESHOLD")) {

                    while (commandScanner.hasNext()) {
                        String thresholdInput = commandScanner.next();
                       /* if (thresholdInput.contains("LOW")) {
                            valueOne = Double.parseDouble(commandScanner.next());
                        }
                        if (thresholdInput.contains("HIGH")) {
                            valueTwo = Double.parseDouble(commandScanner.next());

                        }*/
                        if (thresholdInput.contains("GRACE")) {

                            valueThree = Double.parseDouble(commandScanner.next());
                        }
                    }

                } else {
                    throw new IllegalStateException("THRESHOLD is missing");
                }

                break;


            default:
                throw new IllegalStateException("Unexpected value: " + watchdogType);


        }
        if (watchdogType.contains("ACCELERATION") && watchdogType.contains("GRACE")) {

            WatchdogAcceleration watchdogAccelerationGrace = new WatchdogAcceleration(valueOne, valueTwo,mode , (int) valueThree);
            watchdogSymbolTable.add(id, watchdogAccelerationGrace);
        }
        if (watchdogType.contains("ACCELERATION") && !watchdogType.contains("GRACE")) {
            WatchdogAcceleration watchdogAcceleration = new WatchdogAcceleration(valueOne, valueTwo, mode);
            watchdogSymbolTable.add(id, watchdogAcceleration);
        }
        if (watchdogType.contains("BAND") && watchdogType.contains("GRACE")) {
            WatchdogBand watchdogBand = new WatchdogBand(valueOne,valueTwo,mode,(int) valueThree);
            watchdogSymbolTable.add(id, watchdogBand);
        }
        if (watchdogType.contains("BAND") && !watchdogType.contains("GRACE")) {
            WatchdogBand watchdogBandTwo = new WatchdogBand(valueOne, valueTwo, mode);
            watchdogSymbolTable.add(id, watchdogBandTwo);
        }
        if (watchdogType.contains("NOTCH") && watchdogType.contains("GRACE")) {
            WatchdogNotch watchdogNotch = new WatchdogNotch(valueOne, valueTwo, mode, (int) valueThree);
            watchdogSymbolTable.add(id, watchdogNotch);
        }
        if (watchdogType.contains("NOTCH") && !watchdogType.contains("GRACE")){
            WatchdogNotch watchdogNotchTwo = new WatchdogNotch(valueOne, valueTwo, mode);
            watchdogSymbolTable.add(id, watchdogNotchTwo);
        }
        if (watchdogType.contains("LOW") && watchdogType.contains("GRACE")) {
            WatchdogLow watchdogLow = new WatchdogLow(valueOne, mode, (int) valueThree);
            watchdogSymbolTable.add(id, watchdogLow);
        }
        if (watchdogType.contains("LOW") && !watchdogType.contains("GRACE")){
            WatchdogLow watchdogLowTwo = new WatchdogLow(valueOne, mode);
            watchdogSymbolTable.add(id, watchdogLowTwo);
        }
        if (watchdogType.contains("HIGH") && watchdogType.contains("GRACE")){
            WatchdogHigh watchdogHigh = new WatchdogHigh(valueOne, mode, (int) valueThree);
            watchdogSymbolTable.add(id, watchdogHigh);

        }
        if (watchdogType.contains("HIGH") && !watchdogType.contains("GRACE")){
            WatchdogHigh watchdogHighTwo = new WatchdogHigh(valueOne, mode);
            watchdogSymbolTable.add(id, watchdogHighTwo);
        }

    }
}
