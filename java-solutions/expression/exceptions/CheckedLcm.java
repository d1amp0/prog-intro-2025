package expression.exceptions;

import expression.AnyExpression;
import expression.BinaryOperation;
import expression.Const;

public class CheckedLcm extends BinaryOperation {
    public CheckedLcm(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        CheckedGcd gcd = new CheckedGcd(new Const(a), new Const(b));
        CheckedDivide divide = new CheckedDivide(new Const(a), new Const(gcd.evaluate(0, 0, 0)));
        CheckedMultiply answer = new CheckedMultiply(new Const(divide.evaluate(0, 0, 0)), new Const(b));
        return answer.evaluate(0, 0, 0);
    }

    @Override
    protected double calc(double a, double b) {
        throw new IllegalArgumentException("Cant find LCM for double");
    }

    @Override
    protected String getSign() {
        return "lcm";
    }
}