package expression.generic;

import expression.exceptions.ExpressionDivideZeroException;
import expression.exceptions.ExpressionOverflowException;

public class IntegerCheckOperations implements GenericOperations<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        if ((b > 0 && a > Integer.MAX_VALUE - b) || (b < 0 && a < Integer.MIN_VALUE - b)) {
            throw new ExpressionOverflowException();
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if ((b < 0 && a > Integer.MAX_VALUE + b) || (b > 0 && a < Integer.MIN_VALUE + b)) {
            throw new ExpressionOverflowException();
        }
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if ((a == Integer.MIN_VALUE && b < 0) || (b == Integer.MIN_VALUE && a < 0)) {
            throw new ExpressionOverflowException();
        }
        int answer = a * b;
        if (a != 0 && answer / a != b) {
            throw new ExpressionOverflowException();
        }
        return answer;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (b == 0) {
            throw new ExpressionDivideZeroException();
        } else if (a == Integer.MIN_VALUE && b == -1) {
            throw new ExpressionOverflowException();
        }
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        if (a == Integer.MIN_VALUE) {
            throw new ExpressionOverflowException();
        }
        return -a;
    }

    @Override
    public Integer parseConst(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Integer.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Integer.max(a, b);
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }
}
