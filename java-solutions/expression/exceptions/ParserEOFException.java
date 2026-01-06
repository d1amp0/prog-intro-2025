package expression.exceptions;

public class ParserEOFException extends ParserException {
    public ParserEOFException(char ch, int position) {
        super("Expected EOF, but found: " + ch + " in position: " + position);
    }
}