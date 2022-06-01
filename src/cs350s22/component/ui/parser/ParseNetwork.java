package cs350s22.component.ui.parser;

import java.util.ArrayList;

import cs350s22.component.*;
import cs350s22.component.actuator.A_Actuator;
import cs350s22.component.controller.A_Controller;
import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;

public class ParseNetwork {

	public static Object parseNetworkCommand(A_ParserHelper parserHelper, String input) {
        String[] command = input.split(" ");
		String type;	
		String val;
		ArrayList<A_Component> list = new ArrayList<A_Component>();
		A_Controller controller;
		A_Actuator actuator;
		A_Sensor sensor;
		Identifier id;
		
        	type = command[4];
        	switch(type) {
        	case "COMPONENT":
        		val = command[5];
        		id = Identifier.make(val);
        		controller = parserHelper.getSymbolTableController().get(id);
        		actuator = parserHelper.getSymbolTableActuator().get(id);
        		sensor = parserHelper.getSymbolTableSensor().get(id);

        		list.add(controller);
        		list.add(actuator);
        		list.add(sensor);

        		parserHelper.getControllerMaster().addComponents(list);
        		break;
        		
        	case "COMPONENTS":
        		for(int i = 5; i < command.length; i++) {
        			val = command[i];
            		id = Identifier.make(val);
            		controller = parserHelper.getSymbolTableController().get(id);
            		actuator = parserHelper.getSymbolTableActuator().get(id);
            		sensor = parserHelper.getSymbolTableSensor().get(id);

            		list.add(controller);
            		list.add(actuator);
            		list.add(sensor);

            		parserHelper.getControllerMaster().addComponents(list);

        		}
        		break;
        		
        	}
				
        
        return null;
		
	}

}
