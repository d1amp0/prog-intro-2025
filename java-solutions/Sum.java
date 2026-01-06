public class Sum {
    public static void main(String[] args) {
        int sum = 0;
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
                    sum += Integer.parseInt(arg.substring(l, r));
                }
                l = r;
            }
        }
        System.out.print(sum);
    }
}
