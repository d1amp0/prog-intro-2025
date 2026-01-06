package expression.exceptions;

public class ParserWrongNumberAndVariableException extends ParserException {
    public ParserWrongNumberAndVariableException(char ch, int position) {
        super("Expected variable(x, y, z) or number, but found: " + ch + " in position: " + position);
    }
}