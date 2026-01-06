package expression.exceptions;

import expression.AnyExpression;
import expression.Divide;

public class CheckedDivide extends Divide {
    public CheckedDivide(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        if (b == 0) {
            throw new ExpressionDivideZeroException();
        } else if (a == Integer.MIN_VALUE && b == -1) {
            throw new ExpressionOverflowException();
        }
        return a / b;
    }
}