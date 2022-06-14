package cs350s22.component.ui.parser;

import cs350s22.message.actuator.MessageActuatorReportPosition;
import cs350s22.message.actuator.MessageActuatorRequestPosition;
import cs350s22.message.ping.MessagePing;
import cs350s22.support.Identifier;

import java.util.ArrayList;
import java.util.Scanner;

public class ParseMessage {
    public static void parseMessageCommand(A_ParserHelper parserHelper, String command) throws ParseException {
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

    private static void sendMessagePing(A_ParserHelper parserHelper) {
        parserHelper.getCommandLineInterface().issueMessage(new MessagePing());
    }

    private static void sendMessagePositionRequest(A_ParserHelper parserHelper, String command) throws ParseException {
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

    private static void sendMessagePositionReport(A_ParserHelper parserHelper, String command) {
        ArrayList<ArrayList<Identifier>> messageIdsAndGroups = getMessageIDsAndGroups(command);
        if (!messageIdsAndGroups.get(0).isEmpty()) {
            MessageActuatorReportPosition reportToIds = new MessageActuatorReportPosition(messageIdsAndGroups.get(0));
            parserHelper.getCommandLineInterface().issueMessage(reportToIds);
        } else if (!messageIdsAndGroups.get(1).isEmpty()) {
            MessageActuatorReportPosition reportToGroups = new MessageActuatorReportPosition(messageIdsAndGroups.get(1));
            parserHelper.getCommandLineInterface().issueMessage(reportToGroups);
        }

    }

    private static ArrayList<ArrayList<Identifier>> getMessageIDsAndGroups(String command) {
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
}
