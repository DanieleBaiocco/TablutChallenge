package test.ai;

import it.unibo.ai.didattica.competition.ai.model.Direction;
import org.junit.Test;
import org.junit.Assert;
public class DirectionTest {

    @Test
    public void getOppositeDirectionTest(){
        Assert.assertSame(Direction.UP.getOppositeDirection(), Direction.DOWN);
        Assert.assertSame(Direction.RIGHT.getOppositeDirection(), Direction.LEFT);
        Assert.assertSame(Direction.DOWN.getOppositeDirection(), Direction.UP);
        Assert.assertSame(Direction.LEFT.getOppositeDirection(), Direction.RIGHT);
        Assert.assertNotSame(Direction.DOWN.getOppositeDirection(), Direction.LEFT);
    }
}
