package cs350s22.component.ui.parser;

import java.io.IOException;

import cs350s22.component.logger.LoggerMessage;
import cs350s22.component.logger.LoggerMessageSequencing;
import cs350s22.support.Clock;
import cs350s22.support.Filespec;

public class ParseMeta {

	public static void parseMetaCommand(A_ParserHelper parserHelper, String[] command) throws ParseException, IOException {
		String type = command[0].toUpperCase();
		//there is no space between @ and the first word so it would still be [0]
		switch(type) {
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


	private static void configure(A_ParserHelper parserHelper, String[] command) throws IOException {
		for(int i = 1; i < command.length; i++) {
			String type = command[i].toUpperCase();
			switch(type) {
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
					if(command[i].toUpperCase().equals("NETWORK")) {
						i++;
						filepathThree = Filespec.make(command[i]);
					}
					LoggerMessageSequencing.initialize(filepathTwo, filepathThree);
					break;
			}
		}
	}


	private static void createClock(A_ParserHelper parserHelper, String[] command) {
				String type = command[1].toUpperCase();
				String value;
				int val;
				Clock clock = Clock.getInstance();
				switch(type) {
				case "PAUSE":
					clock.isActive(false);
					break;
				case "RESUME":
					clock.isActive(true);
					break;
				case "ONESTEP":
					if(command.length > 2) {
						value = command[2];
						val = Integer.parseInt(value);
						clock.onestep(val);
					}
					else {
						clock.onestep();
					}
					break;
				case "SET":
					if(command[2].toUpperCase().equals("RATE")) {
						value = command[3];
						val = Integer.parseInt(value);
						clock.setRate(val);
					}
					break;
				}
	}
}
