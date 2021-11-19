package test.ai;

import it.unibo.ai.didattica.competition.ai.decisionmaking.HeuristicNiegghie;
import it.unibo.ai.didattica.competition.ai.decisionmaking.MinMax;
import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.domain.Action;
import it.unibo.ai.didattica.competition.domain.State;
import it.unibo.ai.didattica.competition.domain.Turn;
import org.junit.Test;

public class MinMaxTest {

    //@Test
    /*public void minMaxComputationTest(){
        State state = StateTest.generateStartState();
        State randomState = StateTest.generateRandomState();
        System.out.println(randomState);
        /*for(int i = 0; i< 10; i++){
            randomState = StateTest.generateRandomState();
            System.out.println(randomState);
            HeuristicNiegghie heuristic = new HeuristicNiegghie(new StateDecorator(randomState));
            System.out.println(heuristic.evaluate());
        }
        MinMax minMax = new MinMax(Turn.WHITE, 4);
        Action a = minMax.makeDecision(randomState, 30);
        System.out.println(a);

    }*/
}
