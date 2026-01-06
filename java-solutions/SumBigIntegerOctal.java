import java.math.BigInteger;


public class SumBigIntegerOctal {
    public static void main(String[] args) {
        BigInteger sum = BigInteger.ZERO;
        for (String arg : args) {
            int l = 0, r = 0;
            while (r < arg.length()) {
                while (r < arg.length() && Character.isWhitespace(arg.charAt(r))) {
                    r++;
                    l++;
                }
                while (r < arg.length() && !Character.isWhitespace(arg.charAt(r))) {
                    r++;
                }
                if (l != r) {
                    String item = arg.substring(l, r);
                    if (item.endsWith("o") || item.endsWith("O")) {
                        sum = sum.add(new BigInteger(item.substring(0, item.length() - 1), 8));
                    } else {
                        sum = sum.add(new BigInteger(item));
                    }
                }
                l = r;
            }
        }
        System.out.print(sum);
    }
}
