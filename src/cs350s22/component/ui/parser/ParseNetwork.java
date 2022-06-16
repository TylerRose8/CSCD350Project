package cs350s22.component.ui.parser;

import cs350s22.component.A_Component;
import cs350s22.component.actuator.A_Actuator;
import cs350s22.component.controller.A_Controller;
import cs350s22.component.sensor.A_Sensor;
import cs350s22.support.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ParseNetwork {

    public static void parseNetworkCommand(A_ParserHelper parserHelper, String input) {
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
}
