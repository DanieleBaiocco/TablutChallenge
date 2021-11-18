package test.ai;

import it.unibo.ai.didattica.competition.ai.decisionmaking.HeuristicNiegghie;
import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.domain.State;
import it.unibo.ai.didattica.competition.domain.StateTablut;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HeuristicTest {

    private State initialState;
    private StateDecorator stateDecorator;
    private HeuristicNiegghie heuristicNiegghie;
    @Before
    public void setUp() throws IOException {
        this.initialState = new StateTablut();
        this.stateDecorator = new StateDecorator(initialState);
        this.heuristicNiegghie = new HeuristicNiegghie(stateDecorator);
    }

    @Test
    public void kingToEscapeTest(){
        Assert.assertEquals(this.heuristicNiegghie.kingToEscape(), 4);
        StateDecorator stateDecorator = new StateDecorator(StateTest.generateState1());
        HeuristicNiegghie heuristicNiegghie = new HeuristicNiegghie(stateDecorator);
        Assert.assertEquals(heuristicNiegghie.kingToEscape(), 0);
    }

    @Test
    public void winPathsTest(){
        StateDecorator stateDecorator = new StateDecorator(StateTest.generateState1());
        HeuristicNiegghie heuristicNiegghie = new HeuristicNiegghie(stateDecorator);
        Assert.assertEquals(heuristicNiegghie.winPaths(), 1);
    }


    @Test
    public void escapesBlockedTest(){
        StateDecorator stateDecorator1 = new StateDecorator(StateTest.generateState1());
        HeuristicNiegghie heuristicNiegghie1 = new HeuristicNiegghie(stateDecorator1);
        Assert.assertEquals(heuristicNiegghie1.escapesBlocked(), 1);

        StateDecorator stateDecorator2 = new StateDecorator(StateTest.generateState2());
        HeuristicNiegghie heuristicNiegghie2 = new HeuristicNiegghie(stateDecorator2);
        Assert.assertEquals(heuristicNiegghie2.escapesBlocked(), 3);

    }

    @Test
    public void whiteMenacedTest(){
        StateDecorator stateDecorator = new StateDecorator(StateTest.generateState3());
        HeuristicNiegghie heuristicNiegghie = new HeuristicNiegghie(stateDecorator);
        Assert.assertEquals(heuristicNiegghie.whiteMenaced(), 1);
        System.out.println(heuristicNiegghie.evaluate());
    }









}
