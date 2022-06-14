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

                    }if(watchdogMode.equalsIgnoreCase("AVERAGE")){
                        //mode =new WatchdogModeAverage();
                        //need to add watchdogModeAverage(int windowsize)
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        }else {
                            mode =new WatchdogModeAverage();
                        }



                    }if (watchdogMode.equalsIgnoreCase("STANDARD")){
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        //need to add watchdogModeDeviation(int windowsize)
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        }else{
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


                    }if(watchdogMode.equalsIgnoreCase("AVERAGE")){
                        //mode =new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        }else {
                            mode =new WatchdogModeAverage();
                        }
                    }if (watchdogMode.equalsIgnoreCase("STANDARD")){
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        }else{
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


                    }if(watchdogMode.equalsIgnoreCase("AVERAGE")){
                        //mode =new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        }else {
                            mode =new WatchdogModeAverage();
                        }



                    }if (watchdogMode.equalsIgnoreCase("STANDARD")){
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        }else{
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


                    }if(watchdogMode.equalsIgnoreCase("AVERAGE")){
                        //mode = new WatchdogModeAverage();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        }else {
                            mode =new WatchdogModeAverage();
                        }
                    }if (watchdogMode.equalsIgnoreCase("STANDARD")){
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                            mode = new WatchdogModeStandardDeviation(modeValue);
                        }else{
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


                    }if(watchdogMode.equalsIgnoreCase("AVERAGE")){

                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            mode = new WatchdogModeAverage(modeValue);
                        }else {
                            mode =new WatchdogModeAverage();
                        }

                    }if (watchdogMode.equalsIgnoreCase("STANDARD")){
                        commandScanner.next();
                        //mode = new WatchdogModeStandardDeviation();
                        if (commandScanner.hasNextInt()){
                            int modeValue = commandScanner.nextInt();
                            //System.out.println(modeValue);
                                mode = new WatchdogModeStandardDeviation(modeValue);
                        }else{
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
        switch (watchdogCreator){
            case "ACCELERATION":
                if (valueThree == 0){
                    WatchdogAcceleration watchdogAcceleration = new WatchdogAcceleration(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogAcceleration);
                    System.out.println(watchdogAcceleration.toString());
                }else {
                    WatchdogAcceleration watchdogAccelerationGrace = new WatchdogAcceleration(valueOne, valueTwo,mode , valueThree);
                    watchdogSymbolTable.add(id, watchdogAccelerationGrace);
                    System.out.println(watchdogAccelerationGrace.toString());
                }
                break;
            case "BAND":
                if (valueThree == 0){
                    WatchdogBand watchdogBandTwo = new WatchdogBand(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogBandTwo);
                    System.out.println(watchdogBandTwo.toString());
                }else{
                    WatchdogBand watchdogBand = new WatchdogBand(valueOne,valueTwo,mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogBand);
                    System.out.println(watchdogBand.toString());
                }
                break;
            case "NOTCH":
                if(valueThree == 0){
                    WatchdogNotch watchdogNotchTwo = new WatchdogNotch(valueOne, valueTwo, mode);
                    watchdogSymbolTable.add(id, watchdogNotchTwo);
                    System.out.println(watchdogNotchTwo.toString());
                }else{
                    WatchdogNotch watchdogNotch = new WatchdogNotch(valueOne, valueTwo, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogNotch);
                    System.out.println(watchdogNotch.toString());
                }
                break;
            case "LOW":
                if (valueThree == 0){
                    WatchdogLow watchdogLowTwo = new WatchdogLow(valueOne, mode);
                    watchdogSymbolTable.add(id, watchdogLowTwo);
                    System.out.println(watchdogLowTwo.toString());
                }else{
                    WatchdogLow watchdogLow = new WatchdogLow(valueOne, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogLow);
                    System.out.println(watchdogLow.toString());
                }
                break;
            case "HIGH":
                if (valueThree == 0){
                    WatchdogHigh watchdogHighTwo = new WatchdogHigh(valueOne, mode);
                    watchdogSymbolTable.add(id, watchdogHighTwo);
                    System.out.println(watchdogHighTwo.toString());
                }else {
                    WatchdogHigh watchdogHigh = new WatchdogHigh(valueOne, mode, valueThree);
                    watchdogSymbolTable.add(id, watchdogHigh);
                    System.out.println(watchdogHigh.toString());
                }
                break;
        }
        //nothing wrong with code below but trying to make it better using above implementation
        /*if (watchdogType.contains("ACCELERATION") && watchdogType.contains("GRACE")) {

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
        }*/

    }

}
