package expression.exceptions;

public class ExpressionOverflowException extends ExpressionException {
    public ExpressionOverflowException() {
        super("overflow");
    }
}