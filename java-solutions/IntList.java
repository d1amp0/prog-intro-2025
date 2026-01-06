import java.util.Arrays;

public class IntList {
    private int[] elements;
    private int[] elementsLine;
    private int length;
    private int count;
    private static final int DEFAULT_LENGTH = 32;

    public IntList() {
        elements = new int[DEFAULT_LENGTH];
        elementsLine = new int[DEFAULT_LENGTH];
        length = 0;
        count = 0;
    }

    public IntList(int index) {
        elements = new int[DEFAULT_LENGTH];
        elements[0] = index;
        length = 1;
        count = 1;
    }

    public IntList(int index, int line) {
        elements = new int[DEFAULT_LENGTH];
        elementsLine = new int[DEFAULT_LENGTH];
        elements[0] = index;
        elementsLine[0] = line;
        length = 1;
        count = 1;
    }

    public void add(int value) {
        if (length == elements.length) {
            elements = Arrays.copyOf(elements, 2 * length);
        }
        elements[length++] = value;
    }

    public void addWithReplace(int value, int line) {
        if (length > 0 && elementsLine[length - 1] == line) {
            elements[length - 1] = value;
        } else {
            if (length == elementsLine.length) {
                elementsLine = Arrays.copyOf(elementsLine, 2 * length);
            }
            elementsLine[length] = line;
            add(value);
        }
        count++;
    }

    public int getLength() {
        return length;
    }

    public int getCount() {
        return count;
    }

    public int getElement(int index) {
        return elements[index];
    }

    public int[] getElements() {
        return elements;
    }
}