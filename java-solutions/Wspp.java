import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Wspp {
    public static void main(String[] args) {
        String inputFilename = args[0], outputFilename = args[1];
        Map<String, IntList> everyWord = new LinkedHashMap<>();
        int wordIndex = 1;
        try {
            FastScanner reader = new FastScanner(inputFilename, true);
            try {
                while (reader.hasNextWord(false)) {
                    String word = reader.nextWord().toLowerCase();
                    if (everyWord.containsKey(word)) {
                        IntList value = everyWord.get(word);
                        value.add(wordIndex);
                        everyWord.put(word, value);
                    } else {
                        everyWord.put(word, new IntList(wordIndex));
                    }
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
                    new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF8")
            );
            try {
                for (Map.Entry<String, IntList> entry : everyWord.entrySet()) {
                    IntList value = entry.getValue();
                    writer.write(entry.getKey() + " " + value.getLength());
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