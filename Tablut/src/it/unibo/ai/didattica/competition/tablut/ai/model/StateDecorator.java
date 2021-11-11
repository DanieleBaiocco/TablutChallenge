package it.unibo.ai.didattica.competition.tablut.ai.model;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.List;
import java.util.Map;

public class StateDecorator {
    private State state;

    public StateDecorator(State state){
        this.state = state;
    }

    //TODO
    public List<Action> getAllWhiteMoves(){
        return null;
    }
    //TODO
    public List<Action> getAllBlackMoves(){
        return null;
    }

    //TODO
    public Map<Integer, List<Coordinate>> getPieces(){
        return null;
    }

    public State getState() {
        return state;
    }
}
