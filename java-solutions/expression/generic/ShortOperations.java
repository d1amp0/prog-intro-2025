package expression.generic;


public class ShortOperations implements GenericOperations<Short> {

    @Override
    public Short add(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short subtract(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short multiply(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short divide(Short a, Short b) {
        return (short) (a / b);
    }

    @Override
    public Short negate(Short a) {
        return (short) -a;
    }

    @Override
    public Short parseConst(String value) {
        return Short.valueOf(value);
    }

    @Override
    public Short count(Short a) {
        return (short)(Integer.bitCount(a & 0xffff));
    }

    @Override
    public Short min(Short a, Short b) {
        if (a <= b) {
            return a;
        }
        return b;
    }

    @Override
    public Short max(Short a, Short b) {
        if (a <= b) {
            return b;
        }
        return a;
    }

    @Override
    public Short valueOf(int value) {
        return (short)value;
    }
}
