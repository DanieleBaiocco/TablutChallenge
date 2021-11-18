package test.ai;

import it.unibo.ai.didattica.competition.ai.decisionmaking.HeuristicNiegghie;
import it.unibo.ai.didattica.competition.ai.decisionmaking.MinMax;
import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.domain.Action;
import it.unibo.ai.didattica.competition.domain.State;
import it.unibo.ai.didattica.competition.domain.Turn;
import org.junit.Test;

public class MinMaxTest {

    @Test
    public void minMaxComputationTest(){
        State state = StateTest.generateStateForMinMax();
        MinMax minMax = new MinMax(Turn.WHITE, 4);
        Action a = minMax.makeDecision(state, 60);
        System.out.println(a);

    }
}
