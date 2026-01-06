package expression.exceptions;

import expression.AnyExpression;
import expression.BinaryOperation;

public class CheckedGcd extends BinaryOperation {
    public CheckedGcd(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        if (b == 0) {
            if (a < 0) {
                if (a == Integer.MIN_VALUE) {
                    throw new ExpressionOverflowException();
                }
                return -a;
            }
            return a;
        }
        return calc(b, a % b);
    }

    @Override
    protected double calc(double a, double b) {
        throw new IllegalArgumentException("Cant find GCD for double");
    }

    @Override
    protected String getSign() {
        return "gcd";
    }
}