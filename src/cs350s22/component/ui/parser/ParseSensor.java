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
	        	case "WATCHDOG":
	        		i++;
	                idThree = Identifier.make(argslist[i]);
	                watchdogList.add(parserHelper.getSymbolTableWatchdog().get(idThree));
	        		break;
	        	case "MAPPER":
	        		i++;
	                idFour = Identifier.make(argslist[i]);
	                mapper = parserHelper.getSymbolTableMapper().get(idFour);
	        		break;
	        	case "GET":
	        		idOne = Identifier.make(argslist[2]);
	                mySensor = parserHelper.getSymbolTableSensor().get(idOne);
	                System.out.println("The value of "+mySensor.getID() +" is "+ mySensor.getValueRaw());
	        		break;
	        	case "SET":
	        		idOne = Identifier.make(argslist[2]);

	                mySensor = parserHelper.getSymbolTableSensor().get(idOne);
	                mySensor.registerController(parserHelper.getControllerMaster());
	                String valueString = argslist[4];
	                int valueSet = Integer.parseInt(valueString);
	                mySensor.setValue(valueSet);
	        		break;
        	}
        }
        
    switch(type){
	    case "SPEED":
			if (mapper == null) {
	            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList);
	
	        } else if (identifierList.size() == 0 && repoList.size() == 0 && watchdogList.size() == 0) {
	            mySensor = new MySensor(identifier);
	        } else {
	            mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
	        }
			if(mapper != null) {
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
			if(mapper != null) {
				mySensor = new MySensor(identifier, identifierList, repoList, watchdogList, mapper);
				//mySensor.setValue(1.0);
			}
	        SymbolTable<A_Sensor> symSensor1 = parserHelper.getSymbolTableSensor();
	        symSensor1.add(mySensor.getID(), mySensor);
	        symSensor1.contains(mySensor.getID());
	        break;
    	}

    }

}
