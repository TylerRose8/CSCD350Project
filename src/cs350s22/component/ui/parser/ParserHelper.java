package cs350s22.component.ui.parser;

import cs350s22.support.Identifier;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Predicate;

public class ParserHelper extends A_ParserHelper {
    public ParserHelper() {
        super();
        System.out.println("Welcome to your ParserHelper");
    }

    private ArrayList<Identifier> getSetOfIds(String command, String startKeyWord, String endKeyWord){
        ArrayList<Identifier> ids = new ArrayList<>();
        if(!command.toUpperCase().contains(startKeyWord.toUpperCase())){
            return ids;
        }
        if(!command.toUpperCase().contains(endKeyWord.toUpperCase())){
            return getSetOfIds(command, startKeyWord);
        }

        String[] items = command.split(startKeyWord);
        items = items[1].split(endKeyWord)[0].trim().split(" ");
        for (String item : items) {
            ids.add(Identifier.make(item));
        }
        return ids;
    }

    private ArrayList<Identifier> getSetOfIds(String command, String startKeyWord){

        String[] items = command.split(startKeyWord);
        items = items[1].trim().split(" ");
        ArrayList<Identifier> ids = new ArrayList<>();
        for (String item : items) {
            ids.add(Identifier.make(item));
        }
        return ids;
    }
}