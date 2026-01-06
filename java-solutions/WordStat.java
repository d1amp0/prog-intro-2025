import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordStat {
    public static void main(String[] args) {
        String inputFilename = args[0], outputFilename = args[1];
        LinkedHashMap<String, Integer> everyWord = new LinkedHashMap<>();
        try {
            FastScanner reader = new FastScanner(inputFilename, true);
            try {
                while (reader.hasNextWord(false)) {
                    String word = reader.nextWord();
                    everyWord.merge(word.toLowerCase(), 1, Integer::sum);
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
                for (Map.Entry<String, Integer> entry : everyWord.entrySet()) {
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