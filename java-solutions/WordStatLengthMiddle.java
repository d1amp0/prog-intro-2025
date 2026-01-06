import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordStatLengthMiddle {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        String inputFilename = args[0], outputFilename = args[1];
        LinkedHashMap<String, Integer> everyWord = new LinkedHashMap<>();
        try {
            FastScanner reader = new FastScanner(inputFilename, true);
            try {
                while (reader.hasNextWord(false)) {
                    String word = reader.nextWord();
                    if (word.length() >= 7) {
                        everyWord.merge(word.substring(3, word.length() - 3).toLowerCase(), 1, Integer::sum);
                    }
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
                List<Map.Entry<String, Integer>> entries = new ArrayList<>(everyWord.entrySet());
                entries.sort(Comparator.comparingInt(a -> a.getKey().length()));
                for (Map.Entry<String, Integer> entry : entries) {
                    writer.write(entry.getKey() + " " + entry.getValue());
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