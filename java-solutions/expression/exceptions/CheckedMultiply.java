package expression.exceptions;

import expression.AnyExpression;
import expression.Multiply;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        if ((a == Integer.MIN_VALUE && b < 0) || (b == Integer.MIN_VALUE && a < 0)) {
            throw new ExpressionOverflowException();
        }
        int answer = a * b;
        if (a != 0 && answer / a != b) {
            throw new ExpressionOverflowException();
        }
        return answer;
    }
}