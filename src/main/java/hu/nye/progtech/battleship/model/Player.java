package hu.nye.progtech.battleship.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import hu.nye.progtech.battleship.service.properties.ConfigReader;

/**
 * Player implementation.
 */
public class Player extends Statistics implements Comparable<Player> {
    private Board board;
    private final int numberOfShips;
    private String name;
    private ArrayList<Ship> ships;
    private boolean[][] hits;
    private Board cleanBoard;

    public Player(Board board, int numberOfShips) {
        super(0, 0, 0, 0);
        this.board = board;
        this.numberOfShips = numberOfShips;
        ships = new ArrayList<>();
        shipsArraySize(numberOfShips);
        int hitSize = Integer.parseInt(ConfigReader.getPropertyFromConfig("board.setting.board.size"));
        cleanBoard = new Board(hitSize);
        hits = new boolean[hitSize][hitSize];
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                hits[i][j] = false;
            }
        }
    }

    public Board getCleanBoard() {
        return cleanBoard;
    }

    public void setCleanBoard(Board cleanBoard) {
        this.cleanBoard = cleanBoard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Board getBoard() {
        return board;
    }

    public int getNumberOfShips() {
        return numberOfShips;
    }

    private void shipsArraySize(int numberOfShips) {
        for (int i = 1; i <= numberOfShips; i++) {
            this.ships.add(new Ship(i));
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    public boolean[][] getHits() {
        return hits;
    }

    public void setHits(boolean[][] hits) {
        this.hits = hits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return numberOfShips == player.numberOfShips && Objects.equals(board, player.board) &&
                Objects.equals(name, player.name) && Objects.equals(ships, player.ships) &&
                Arrays.deepEquals(hits, player.hits);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(board, numberOfShips, name, ships);
        result = 31 * result + Arrays.deepHashCode(hits);
        return result;
    }

    @Override
    public String toString() {
        return "Player{" +
                "board=" + board +
                ", numberOfShips=" + numberOfShips +
                ", name='" + name + '\'' +
                ", ships=" + ships +
                ", hits=" + Arrays.toString(hits) +
                '}';
    }

    @Override
    public int compareTo(Player o) {
        int thisPlayer = this.getNumberOfWins();
        Player temp = o;
        int otherPlayer = temp.getNumberOfWins();
        return otherPlayer - thisPlayer;
    }
}
