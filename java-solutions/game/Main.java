package game;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class Main {
    public static final int minAscii = 32, maxAscii = 126;
    public static final Random random = new Random();

    public static void main(String[] args) {
        Board board = new MNKBoard(10, 10, 5, true);
        List<Player> playersList = new ArrayList<>();
        Set<Character> usedSymbols = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            char symbol = getUniqueRandomChar(usedSymbols);
            usedSymbols.add(symbol);
            playersList.add(new HumanPlayer(symbol));
        }
        final Competition competition = new Competition(board, playersList, false);
        competition.start();
    }

    public static char getUniqueRandomChar(Set<Character> usedSymbols) {
        char symbol = (char) (random.nextInt(maxAscii - minAscii + 1) + minAscii);
        while (usedSymbols.contains(symbol)) {
            symbol = (char) (random.nextInt(maxAscii - minAscii + 1) + minAscii);
        }
        return symbol;
    }
}
