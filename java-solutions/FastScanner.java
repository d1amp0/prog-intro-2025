import java.io.*;
import java.util.Arrays;


public class FastScanner {
    private BufferedReader reader;
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    private boolean hasNextLine = true;
    private boolean hasNextInt = false;
    private boolean hasNextWord = false;
    private int bufferPos = 0;
    private int bufferSize = 0;
    private int nextInt;
    private boolean isBufferEnd = false;
    private String nextWord;
    private char symbol;
    private int currentLine = 0;

    public FastScanner(InputStream input) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(input));
    }

    public FastScanner(String line, boolean isFile) throws IOException, FileNotFoundException {
        if (isFile) {
            this.reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(line), "UTF8")
            );
        } else {
            this.reader = new BufferedReader(new StringReader(line));
        }
    }

    private void checkBufferPos() throws IOException {
        if (bufferPos >= bufferSize) {
            bufferSize = reader.read(buffer);
            bufferPos = 0;
            isBufferEnd = (bufferSize == -1);
        }
    }

    private void nextChar() throws IOException {
        symbol = buffer[bufferPos++];
    }

    public boolean hasNextLine() throws IOException {
        if (!hasNextLine) {
            return false;
        }
        checkBufferPos();
        return !isBufferEnd;
    }

    public String nextLine() throws IOException {
        if (!hasNextLine) {
            return "";
        }

        int length = 0;
        char[] lineBuffer = new char[DEFAULT_BUFFER_SIZE];
        while (true) {
            checkBufferPos();
            if (isBufferEnd) {
                hasNextLine = false;
                break;
            }
            nextChar();
            if (isEndOfLine(symbol)) {
                break;
            }
            if (length >= lineBuffer.length) {
                lineBuffer = Arrays.copyOf(lineBuffer, lineBuffer.length * 2);
            }
            lineBuffer[length++] = symbol;
        }
        return new String(lineBuffer, 0, length);

    }

    public boolean hasNextInt() throws IOException {
        if (hasNextInt) {
            return true;
        }

        boolean negative = false;
        do {
            checkBufferPos();
            if (isBufferEnd) {
                return false;
            }
            nextChar();
        } while (!isIntChar(symbol));
        if (symbol == '-') {
            negative = true;
            checkBufferPos();
            if (isBufferEnd) {
                return false;
            }
            nextChar();
            if (!Character.isDigit(symbol)) {
                return false;
            }
        }
        nextInt = 0;
        while (Character.isDigit(symbol)) {
            nextInt = nextInt * 10 + (symbol - '0');
            checkBufferPos();
            if (isBufferEnd) {
                break;
            }
            nextChar();
        }
        if (negative) {
            nextInt = -nextInt;
        }
        hasNextInt = true;
        return true;
    }

    public int nextInt() throws IOException {
        hasNextInt = false;
        return nextInt;
    }

    public boolean hasNextWord(boolean isExtendedWord) throws IOException {
        if (hasNextWord) {
            return true;
        }

        int length = 0;
        char[] wordBuffer = new char[DEFAULT_BUFFER_SIZE];
        do {
            checkBufferPos();
            if (isBufferEnd) {
                return false;
            }
            nextChar();
            isEndOfLine(symbol);
        } while (!isWordLetter(symbol, isExtendedWord));
        while (isWordLetter(symbol, isExtendedWord)) {
            if (length >= wordBuffer.length) {
                wordBuffer = Arrays.copyOf(wordBuffer, wordBuffer.length * 2);
            }
            wordBuffer[length++] = symbol;
            checkBufferPos();
            if (isBufferEnd) {
                break;
            }
            nextChar();
        }
        nextWord = new String(wordBuffer, 0, length);
        hasNextWord = true;
        return true;
    }

    public String nextWord() {
        hasNextWord = false;
        return nextWord;
    }

    private boolean isEndOfLine(char symbol) throws IOException {
        if (symbol == '\r') {
            checkBufferPos();
            if (!isBufferEnd && buffer[bufferPos] == '\n') {
                bufferPos++;
            }
            currentLine++;
            return true;
        } else if (symbol == '\n') {
            currentLine++;
            return true;
        }
        return false;
    }

    private boolean isIntChar(char symbol) {
        return Character.isDigit(symbol) || symbol == '-';
    }

    public static boolean isWordLetter(char symbol, boolean isExtendedWord) {
        if (isExtendedWord) {
            return Character.isLetter(symbol)
                    || Character.getType(symbol) == Character.DASH_PUNCTUATION
                    || symbol == '\''
                    || symbol == '$'
                    || symbol == '_'
                    || Character.isDigit(symbol);
        }
        return Character.isLetter(symbol)
                || Character.getType(symbol) == Character.DASH_PUNCTUATION
                || symbol == '\'';
    }

    public void close() throws IOException {
        reader.close();
    }

    public int getCurrentLine() {
        return currentLine;
    }
}
