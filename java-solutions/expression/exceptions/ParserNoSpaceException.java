package expression.exceptions;

public class ParserNoSpaceException extends ParserException {
    public ParserNoSpaceException(char ch, int position) {
        super("Space/parenthesis after gcd/lcm exprected, but found: " + ch + " in position: " + position);
    }
}