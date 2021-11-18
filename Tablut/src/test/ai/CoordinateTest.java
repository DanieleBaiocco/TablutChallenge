package test.ai;

import it.unibo.ai.didattica.competition.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.ai.model.Direction;
import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.ai.utility.Pair;
import it.unibo.ai.didattica.competition.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

public class CoordinateTest {
    private State initialState;
    private StateDecorator stateDecorator;
    @Before
    public void setUp() throws IOException {
        this.initialState = new StateTablut();
        this.stateDecorator = new StateDecorator(initialState);

    }
    @Test
    public void lookTest(){
        Coordinate coor = new Coordinate(0, 1);
        Pair<Coordinate, Pawn> pair1 =coor.look(Direction.DOWN, 4, this.stateDecorator);
        Assert.assertEquals(pair1.getSecond(), Pawn.BLACK);
        Assert.assertEquals(pair1.getFirst().getRow(), 4);
        Assert.assertEquals(pair1.getFirst().getCol(), 1);

        Pair<Coordinate, Pawn> pair2 = pair1.getFirst().look(Direction.RIGHT, 3, this.stateDecorator);
        Assert.assertEquals(pair2.getSecond(), Pawn.KING);
        Assert.assertEquals(pair2.getFirst().getRow(), 4);
        Assert.assertEquals(pair2.getFirst().getCol(), 4);

        Pair<Coordinate, Pawn> pair3 = pair2.getFirst().look(Direction.UP, 2, this.stateDecorator);
        Assert.assertEquals(pair3.getSecond(), Pawn.WHITE);
        Assert.assertEquals(pair3.getFirst().getRow(), 2);
        Assert.assertEquals(pair3.getFirst().getCol(), 4);

        Pair<Coordinate, Pawn> pair4 = pair3.getFirst().look(Direction.LEFT, 1, this.stateDecorator);
        Assert.assertEquals(pair4.getSecond(), Pawn.EMPTY);
        Assert.assertEquals(pair4.getFirst().getRow(), 2);
        Assert.assertEquals(pair4.getFirst().getCol(), 3);
    }

    @Test
    public void closeToTest(){
        Coordinate coor = new Coordinate(1, 1);

        Coordinate coorClose1 = new Coordinate(1, 0);
        Coordinate coorClose2 = new Coordinate(0, 1);
        Coordinate coorClose3 = new Coordinate(1, 2);
        Coordinate coorClose4 = new Coordinate(2, 1);

        Coordinate coor5 = new Coordinate(0, 0);

        Assert.assertTrue(coor.closeTo(coorClose1));
        Assert.assertTrue(coor.closeTo(coorClose2));
        Assert.assertTrue(coor.closeTo(coorClose3));
        Assert.assertTrue(coor.closeTo(coorClose4));
        Assert.assertFalse(coor.closeTo(coor5));

    }

}
