package expression.exceptions;

import expression.AnyExpression;
import expression.Negate;

public class CheckedNegate extends Negate {
    public CheckedNegate(AnyExpression exp) {
        super(exp);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int answer = exp.evaluate(x, y, z);
        if (answer == Integer.MIN_VALUE) {
            throw new ExpressionOverflowException();
        }
        return -answer;
    }
}