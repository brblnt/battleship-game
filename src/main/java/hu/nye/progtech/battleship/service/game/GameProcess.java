package hu.nye.progtech.battleship.service.game;

import hu.nye.progtech.battleship.model.Board;
import hu.nye.progtech.battleship.model.GameState;
import hu.nye.progtech.battleship.model.OpponentAI;
import hu.nye.progtech.battleship.model.Player;
import hu.nye.progtech.battleship.service.ai.GenerateMoves;
import hu.nye.progtech.battleship.service.input.imp.UserInputReader;
import hu.nye.progtech.battleship.service.modify.FullShipHit;
import hu.nye.progtech.battleship.service.properties.ConfigReader;
import hu.nye.progtech.battleship.service.validate.ValidateHit;
import hu.nye.progtech.battleship.ui.draw.Draw;
import hu.nye.progtech.battleship.ui.draw.PrintWrapper;
import hu.nye.progtech.battleship.ui.draw.impl.CommandLineDrawImpl;

/**
 * This class manage game process.
 */
public class GameProcess {
    private static final Draw CLDI = new CommandLineDrawImpl();
    private static Player player;
    private static OpponentAI ai;

    private static int boardSize;
    private static boolean[][] hits;
    private static GameState actualGame;
    private static FullShipHit fullShipHit;

    /**
     * Store who step last for check who win.
     * true : player
     * false : bot .
     */
    private static boolean lastStep = true;

    /**
     * Init participnats.
     */
    public static void initParticipants(Player p, OpponentAI oai) {
        actualGame = new GameState();
        player = p;
        ai = oai;
        fullShipHit = new FullShipHit();
        fullShipHit.setAi(ai);
        fullShipHit.setPlayer(player);
        fullShipHit.init();
        boardSize = Integer.parseInt(ConfigReader.getPropertyFromConfig("board.setting.board.size"));
        hits = new boolean[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                hits[i][j] = false;
            }
        }
    }

    private static void save() {
        actualGame.setBot(ai);
        actualGame.setPlayer(player);
    }

    /**
     * Players always start the game.
     */
    public static void game() {
        PrintWrapper.printLine(
                ConfigReader.getPropertyFromConfig("game.text.opponentname") + " " + ai.getName() + " !");
        playerStep();

    }

    private static void playerStep() {
        ai.setBoard(fullShipHit.newBoardForAi());
        player.setCleanBoard(fullShipHit.newBoardForPlayer());
        lastStep = true;
        PrintWrapper.printLine(ConfigReader.getPropertyFromConfig("game.text.playerturn"));
        PrintWrapper.printSpace(2);
        CLDI.drawBoard(player.getCleanBoard());
        PrintWrapper.printSpace(2);
        PrintWrapper.printLine(ConfigReader.getPropertyFromConfig("game.text.playerturnhelp"));
        String input;
        boolean run = true;
        boolean again = false;
        do {
            input = UserInputReader.readInput();
            if (ValidateHit.positionOnBoard(input,
                    Integer.parseInt(ConfigReader.getPropertyFromConfig("board.setting.board.size")))) {
                run = false;
                break;
            } else if (ValidateHit.positionNotHiteBefore(input, player)) {
                run = false;
                break;
            }
            PrintWrapper.printLine(ConfigReader.getPropertyFromConfig("game.text.targetnotvalid"));
        } while (run);
        if (hit(input)) {
            playerStep();
        }
        if (win(ai.getBoard()) && lastStep) {
            GameEnding.win(player);
        }
        save();
        botStep();
    }

    private static void botStep() {
        ai.setBoard(fullShipHit.newBoardForAi());
        player.setCleanBoard(fullShipHit.newBoardForPlayer());
        lastStep = false;
        PrintWrapper.printLine(ConfigReader.getPropertyFromConfig("game.text.botturn"));
        PrintWrapper.printSpace(2);
        while (true) {
            if (win(player.getBoard()) && !lastStep) {
                GameEnding.lose(player);
            }
            if (!GenerateMoves.botStep(player, ai)) {
                save();
                playerStep();
                break;
            }
        }
    }


    private static boolean win(Board b) {
        for (int i = 0; i < b.getBoardSize(); i++) {
            for (int j = 0; j < b.getBoardSize(); j++) {
                if (b.getMatrixForBoard()[i][j] == '1') {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hit(String target) {
        boolean hit = false;
        String[] slices = target.split(":");
        int size = 0;
        for (char a = 'A'; a <= 'Z'; a++) {
            if (slices[0].toUpperCase().charAt(0) == a) {
                break;
            }
            size++;
        }
        int firstPos = size;
        int secondPos = Integer.parseInt(slices[1]) - 1;
        if (ai.getBoard().getMatrixForBoard()[firstPos][secondPos] == '1') {
            hit = true;
            player.getCleanBoard().getMatrixForBoard()[firstPos][secondPos] = '+';
            ai.getBoard().getMatrixForBoard()[firstPos][secondPos] = '+';
        } else {
            player.getCleanBoard().getMatrixForBoard()[firstPos][secondPos] = 'X';
        }
        hits[firstPos][secondPos] = true;
        player.setHits(hits);
        return hit;
    }

}
