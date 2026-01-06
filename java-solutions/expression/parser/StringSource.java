package expression.parser;

public class StringSource implements CharSource {
    private final String string;
    private int pos;

    public StringSource(String string) {
        this.string = string;
    }

    @Override
    public boolean hasNext() {
        return pos < string.length();
    }

    @Override
    public char next() {
        return string.charAt(pos++);
    }

    @Override
    public IllegalArgumentException error(String message) {
        return new IllegalArgumentException(pos + ":" + message);
    }

    @Override
    public String nextString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < Math.min(length + pos, string.length()); i++) {
            sb.append(string.charAt(i));
        }
        return sb.toString();
    }
}
