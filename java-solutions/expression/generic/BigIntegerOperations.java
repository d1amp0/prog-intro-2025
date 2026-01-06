package expression.generic;

import java.math.BigInteger;

public class BigIntegerOperations implements GenericOperations<BigInteger> {

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        return a.divide(b);
    }

    @Override
    public BigInteger negate(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger parseConst(String value) {
        return new BigInteger(value);
    }

    @Override
    public BigInteger count(BigInteger a) {
        return BigInteger.valueOf(a.bitCount());
    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        return a.min(b);
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        return a.max(b);
    }

    @Override
    public BigInteger valueOf(int value) {
        return BigInteger.valueOf(value);
    }
}
