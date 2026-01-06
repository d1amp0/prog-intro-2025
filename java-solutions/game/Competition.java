package game;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Competition {
    private final Board board;
    private final List<Player> nextUpperBracket;
    private final List<Player> currentUpperBracket;
    private final List<Player> nextLowerBracket;
    private final List<Player> currentLowerBracket;
    private final List<List<Player>> places;
    private final Random random;
    private final boolean log;

    public Competition(Board board, List<Player> players, boolean log) {
        this.board = board;
        this.currentUpperBracket = players;
        this.nextUpperBracket = new ArrayList<>();
        this.currentLowerBracket = new ArrayList<>();
        this.nextLowerBracket = new ArrayList<>();
        this.places = new ArrayList<>();
        this.random = new Random();
        this.log = log;
    }

    public void printBrackets() {
        System.out.print("Current upper bracket: ");
        for (Player p : currentUpperBracket) {
            System.out.print(p.getSelfSymbol() + " ");
        }
        System.out.println();
        System.out.print("Next upper bracket: ");
        for (Player p : nextUpperBracket) {
            System.out.print(p.getSelfSymbol() + " ");
        }
        System.out.println();
        System.out.print("Current lower bracket: ");
        for (Player p : currentLowerBracket) {
            System.out.print(p.getSelfSymbol() + " ");
        }
        System.out.println();
        System.out.print("Next lower bracket: ");
        for (Player p : nextLowerBracket) {
            System.out.print(p.getSelfSymbol() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void updateBrackets() {
        currentUpperBracket.addAll(nextUpperBracket);
        nextUpperBracket.clear();
        currentLowerBracket.addAll(nextLowerBracket);
        nextLowerBracket.clear();
    }

    private void findLuckyPlayers(boolean isUpperBracket) {
        int playersCount;
        if (isUpperBracket) {
            playersCount = currentUpperBracket.size();
        } else {
            playersCount = currentLowerBracket.size();
        }
        int competitivePlayers = 1;
        if (playersCount == 1) {
            competitivePlayers = 0;
        } else {
            while (2 * competitivePlayers <= playersCount) {
                competitivePlayers *= 2;
            }
        }
        int luckyPlayersCount = playersCount - competitivePlayers;
        for (int i = 0; i < luckyPlayersCount; i++) {
            int luckyPlayerIndex = random.nextInt(0, playersCount - i);
            if (isUpperBracket) {
                Player luckyPlayer = currentUpperBracket.remove(luckyPlayerIndex);
                nextUpperBracket.add(luckyPlayer);
            } else {
                Player luckyPlayer = currentLowerBracket.remove(luckyPlayerIndex);
                nextLowerBracket.add(luckyPlayer);
            }
        }
        if (log) {
            printBrackets();
        }
    }

    private int playGame(Player player1, Player player2) {
        System.out.println("Game between " + player1.getSelfSymbol() + " and " + player2.getSelfSymbol());
        int result = 0;
        while (result == 0) {
            final Game game = new Game(board, player1, player2, log);
            result = game.play();
            board.clear();
        }
        System.out.println("Game is ended! Final position:");
        System.out.println(board);
        return result;
    }

    private void playUpperBracket() {
        int size = currentUpperBracket.size();
        for (int i = 0; i < size; i += 2) {
            int playerIndex1 = random.nextInt(0, size - i);
            Player player1 = currentUpperBracket.remove(playerIndex1);
            int playerIndex2 = random.nextInt(0, size - i - 1);
            Player player2 = currentUpperBracket.remove(playerIndex2);
            int result = playGame(player1, player2);
            if (result == 1) {
                nextUpperBracket.add(player1);
                nextLowerBracket.add(player2);
            } else {
                nextLowerBracket.add(player1);
                nextUpperBracket.add(player2);
            }
        }
        if (log) {
            printBrackets();
        }
    }

    private void playLowerBracket() {
        int size = currentLowerBracket.size();
        if (size != 0) {
            List<Player> currentPlace = new ArrayList<>();
            for (int i = 0; i < size; i += 2) {
                int playerIndex1 = random.nextInt(size - i);
                Player player1 = currentLowerBracket.remove(playerIndex1);
                int playerIndex2 = random.nextInt(size - i - 1);
                Player player2 = currentLowerBracket.remove(playerIndex2);
                int result = playGame(player1, player2);
                if (result == 1) {
                    nextLowerBracket.add(player1);
                    currentPlace.add(player2);
                } else {
                    nextLowerBracket.add(player2);
                    currentPlace.add(player1);
                }
            }
            places.add(currentPlace);
        }
        if (log) {
            printBrackets();
        }
    }

    public void start() {
        findLuckyPlayers(true);
        playUpperBracket();
        updateBrackets();
        while (currentUpperBracket.size() != 1 || currentLowerBracket.size() != 1) {
            findLuckyPlayers(true);
            findLuckyPlayers(false);
            playUpperBracket();
            playLowerBracket();
            updateBrackets();
        }
        Player finalPlayer1 = currentUpperBracket.get(0), finalPlayer2 = currentLowerBracket.get(0);
        int result = playGame(finalPlayer1, finalPlayer2);
        if (result == 1) {
            places.add(List.of(finalPlayer2));
            places.add(List.of(finalPlayer1));
        } else {
            places.add(List.of(finalPlayer1));
            places.add(List.of(finalPlayer2));
        }
        printFinalTable();
    }

    private void printFinalTable() {
        int placesCount = places.size();
        for (int i = 0; i < placesCount; i++) {
            System.out.print(i + 1);
            System.out.print(") Players: ");
            for (Player player : places.get(placesCount - i - 1)) {
                System.out.print(player.getSelfSymbol() + " ");
            }
            System.out.println();
        }
    }
}
