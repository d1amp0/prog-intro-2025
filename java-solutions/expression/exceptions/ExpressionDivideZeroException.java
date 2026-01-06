package expression.exceptions;

public class ExpressionDivideZeroException extends ExpressionException {
    public ExpressionDivideZeroException() {
        super("division by zero");
    }
}