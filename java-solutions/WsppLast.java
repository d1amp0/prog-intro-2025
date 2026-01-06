import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppLast {
    public static void main(String[] args) {
        String inputFilename = args[0], outputFilename = args[1];
        Map<String, Integer> orderMap = new HashMap<>();
        int order = 0;
        Comparator<String> comparator = (a, b) -> {
          int lenComparator = Integer.compare(a.length(), b.length());
          if (lenComparator != 0) return lenComparator;
          return Integer.compare(orderMap.get(a), orderMap.get(b));
        };
        Map<String, IntList> everyWord = new TreeMap<>(comparator);
        int wordIndex = 1;
        try {
            FastScanner reader = new FastScanner(inputFilename, true);
            try {
                while (reader.hasNextWord(true)) {
                    String word = reader.nextWord().toLowerCase();
                    int currentLine = reader.getCurrentLine();
                    // :NOTE: 3 обращения в мап вместо 2х
                    if (!orderMap.containsKey(word)) {
                        orderMap.put(word, order++);
                    }
                    everyWord.computeIfAbsent(word, key -> new IntList()).addWithReplace(wordIndex, currentLine);
                    wordIndex += 1;
                }
            } finally {
                reader.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found (reader): " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error (reader): " + e.getMessage());
        }
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outputFilename), StandardCharsets.UTF_8)
            );
            try {
                for (Map.Entry<String, IntList> entry : everyWord.entrySet()) {
                    IntList value = entry.getValue();
                    writer.write(entry.getKey() + " " + value.getCount());
                    for (int i = 0; i < value.getLength(); i++) {
                        writer.write(" " + value.getElement(i));
                    }
                    writer.newLine();
                }
            } finally {
                writer.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found (writer): " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error (writer): " + e.getMessage());
        }
    }
}