package cs350s22.component.ui.parser;

public class Parser implements ParserConstants{
    private final A_ParserHelper parserHelper;
    private final String input;
    public Parser(A_ParserHelper helper, String input) {
        this.parserHelper = helper;
        this.input = input;
    }

    public void parse() {
        System.out.println("Hello, world!");
    }
}
