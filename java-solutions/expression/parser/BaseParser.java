package expression.parser;

public class BaseParser {
    protected CharSource source;
    private int position;
    private char ch;
    public static final char END = (char) -1;


    public BaseParser(CharSource source) {
        this.position = -1;
        this.source = source;
        take();
    }

    protected boolean test(char c) {
        return ch == c;
    }

    protected char take() {
        char res = ch;
        ch = source.hasNext() ? source.next() : END;
        position += 1;
        return res;
    }

    protected boolean take(char c) {
        if (test(c)) {
            take();
            return true;
        }
        return false;
    }

    protected boolean take(String s) {
        String source_s = source.nextString(s.length() - 1);
        if (source_s.equals(s.substring(1))) {
            for (int i = 0; i < s.length(); i++) {
                take();
            }
            return true;
        }
        return false;
    }

    protected boolean checkEOF() {
        return ch == END;
    }

    protected void expect(char c) {
        if (!take(c)) {
            throw source.error("Expected: " + c + " , but found: " + ch);
        }
    }

    protected void expect(String s) {
        for (char c : s.toCharArray()) {
            expect(c);
        }
    }

    protected boolean between(char start, char end) {
        return start <= ch && ch <= end;
    }

    protected void skipWS() {
        while (Character.isWhitespace(ch)) {
            take();
        }
    }

    public int getPosition() {
        return position;
    }
}
