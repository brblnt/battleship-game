package hu.nye.progtech.battleship.service.game;

import java.io.ByteArrayInputStream;

import ch.qos.logback.core.encoder.EchoEncoder;
import hu.nye.progtech.battleship.model.Board;
import hu.nye.progtech.battleship.model.OpponentAI;
import hu.nye.progtech.battleship.model.Player;
import hu.nye.progtech.battleship.service.exception.ConfigurationNotFoundException;
import hu.nye.progtech.battleship.service.properties.ConfigReader;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link GameProcess}
 */
public class GameProcessTest {

    private GameProcess underTest;

    private final String NAME = "C:1";
    private ConfigReader cr;
    private Player p;
    private OpponentAI ai;

    @Test
    public void testGameProcessInitShouldNotThrownException() throws ConfigurationNotFoundException {
        //given
        underTest = new GameProcess();
        cr = new ConfigReader("");
        p = new Player(new Board(Integer.parseInt(cr.getPropertyFromConfig("board.setting.board.size"))),
                Integer.parseInt(ConfigReader.getPropertyFromConfig("board.setting.numberofships")));
        ai = new OpponentAI();
        ByteArrayInputStream in = new ByteArrayInputStream(NAME.getBytes());
        System.setIn(in);
        //when
        underTest.initParticipants(p, ai);
        try {
            underTest.game();
        } catch (Exception e) {
            System.out.println(e);
        }
        //then - excp
    }
}
