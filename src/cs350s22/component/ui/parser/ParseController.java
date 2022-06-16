package cs350s22.component.ui.parser;
import java.util.ArrayList;

import cs350s22.component.A_Component;
import cs350s22.component.actuator.A_Actuator;
import cs350s22.component.controller.A_Controller;
import cs350s22.component.controller.dependency.A_Expression;
import cs350s22.component.controller.dependency.DependencySequencer;
import cs350s22.component.controller.dependency.Sequence;
import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;
import cs350s22.test.MyControllerSlaveForwarding;
import cs350s22.test.MyControllerSlaveNonforwarding;

public class ParseController {

	public static Object parseControllerCommand(A_ParserHelper parserHelper, String input) {
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


        	for(int i = 0; i < command.length; i++) {
        		if(command[i].equalsIgnoreCase("GROUPS")) {
        			i++;
        			while((!command[i].equalsIgnoreCase("DEPENDENCY")) || (!command[i].equalsIgnoreCase("WITH"))) {
        				groupString = command[i];
        				groupId = Identifier.make(groupString);
        				groupList.add(groupId);
        			}
        		}
        		if(command[i].equalsIgnoreCase("SEQUENCER")) {
        			i++;
        			seq = command[i];
        			A_Expression exp = null;
        			Identifier seqID = Identifier.make(seq);
        			ArrayList<Sequence> seqList = new ArrayList<Sequence>();
        			Sequence seqAct = new Sequence(seqID, exp);
        			seqList.add(seqAct);
        			ds = new DependencySequencer(seqList);
        		}
        			if(command[i].equalsIgnoreCase("WITH")) {
        				i++;
        				if(command[i].equalsIgnoreCase("COMPONENT")) {
        					i++;
        					comp = command[i];
        					compID = Identifier.make(comp);
        					if(parserHelper.getSymbolTableController().contains(compID)) {
        						A_Controller controllerTemp = parserHelper.getSymbolTableController().get(compID);
            					comps.add(controllerTemp);
        					}
        					if(parserHelper.getSymbolTableActuator().contains(compID)) {
        						A_Actuator actuator = parserHelper.getSymbolTableActuator().get(compID);
            					comps.add(actuator);
        					}
        					if(parserHelper.getSymbolTableSensor().contains(compID)) {
        						A_Sensor sensor = parserHelper.getSymbolTableSensor().get(compID);
        						comps.add(sensor);
        					}
        				}
        				else if(command[i].equalsIgnoreCase("COMPONENTS")) {
        					i++;
        					for(; i < command.length; i++) {
            					comp = command[i];
            					compID = Identifier.make(comp);
            					if(parserHelper.getSymbolTableController().contains(compID)) {
            						A_Controller controllerTemp = parserHelper.getSymbolTableController().get(compID);
                					comps.add(controllerTemp);
            					}
            					if(parserHelper.getSymbolTableActuator().contains(compID)) {
            						A_Actuator actuator = parserHelper.getSymbolTableActuator().get(compID);
                					comps.add(actuator);
            					}
            					if(parserHelper.getSymbolTableSensor().contains(compID)) {
            						A_Sensor sensor = parserHelper.getSymbolTableSensor().get(compID);
            						comps.add(sensor);
            					}
        				}
        			}
        		}
        	
       	}
        	if(command[2].equalsIgnoreCase("FORWARDING")) {
        		if(groupIndex == 0 && ds == null) {
        			 controller = new MyControllerSlaveForwarding(controllerID);
        		}
        		if(groupIndex != 0 && ds == null) {
        			 controller = new MyControllerSlaveForwarding(controllerID, groupList);
        		}
        		if(ds != null && groupIndex == 0) {
        			 controller = new MyControllerSlaveForwarding(controllerID, ds);
        		}
        		if(ds != null && groupIndex !=0) {
        			 controller = new MyControllerSlaveForwarding(controllerID, groupList, ds);
        		}
        		if(!comps.isEmpty()) {
        			controller.addComponents(comps);
        		}
        	}
        	if(command[2].equalsIgnoreCase("NONFORWARDING")) {
        		if(groupIndex == 0 && ds == null) {
        			 controller = new MyControllerSlaveNonforwarding(controllerID);
        		}
        		if(groupIndex != 0 && ds == null) {
        			 controller = new MyControllerSlaveNonforwarding(controllerID, groupList);
        		}
        		if(ds != null && groupIndex == 0) {
        			 controller = new MyControllerSlaveNonforwarding(controllerID, ds);
        		}
        		if(ds != null && groupIndex !=0) {
        			 controller = new MyControllerSlaveNonforwarding(controllerID, groupList, ds);
        		}
        		if(!comps.isEmpty()) {
        			controller.addComponents(comps);
        		}
        	}
        	return controller;
	}
	}