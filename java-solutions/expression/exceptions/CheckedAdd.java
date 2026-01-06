package expression.exceptions;

import expression.AnyExpression;
import expression.Add;

public class CheckedAdd extends Add {
    public CheckedAdd(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        if ((b > 0 && a > Integer.MAX_VALUE - b) || (b < 0 && a < Integer.MIN_VALUE - b)) {
            throw new ExpressionOverflowException();
        }
        return a + b;
    }
}