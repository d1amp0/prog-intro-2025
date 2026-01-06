package expression.exceptions;

public class ParserParenthesisException extends ParserException {
    public ParserParenthesisException(char ch, int position) {
        super("Expected ')' to close expression, but found: " + ch + " in position: " + position);
    }
}