package expression.exceptions;

public class ParserOverflowException extends ParserException {
    public ParserOverflowException(String number, int position) {
        super("Overflow while parsing for number: " + number + " in position: " + position);
    }
}