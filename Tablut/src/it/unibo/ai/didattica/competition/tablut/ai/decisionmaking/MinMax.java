package it.unibo.ai.didattica.competition.tablut.ai.decisionmaking;

import it.unibo.ai.didattica.competition.tablut.ai.UtilityFeatures;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

import javax.swing.text.Utilities;
import java.util.List;

public class MinMax {

    private final Turn player;
    private int depth;
    private HeuristicNiegghie heuristic;


    public MinMax(Turn player, int depth) {
        this.player = player;
        this.depth = depth;
        this.heuristic = new HeuristicNiegghie();

    }

    public Action makeDecision(State state, int timeout) {
        //ovviamente per ogni azione disponibile si fa il min value su di essa perchè tocca all'opponent
        //credo? però in teoria un distinguo è necessario no?

        //non capisco perchè dover sempre far il min sui figli e poi massimizzare il risultato dei figli,
        //indipendentemente da se gioco come bianco (attacco) o come nero (difesa).
        // l'euristica in teoria dà valori positivi a cose buone per l'attacco, e valori negativi per
        //cose buone per la difesa, in modo da poter esser utilizzata in entrambe le situazioni.
        //Quindi se gioco come nero (difesa) devo prendere il valore dell'euristica più basso dalle possibili
        //mosse, se gioco come bianco, quello più alto no???

        //credo basti negare l'heuristic function quando gioco come nero
        return null;
    }

    public double minmaxComputation(State currentState, int depth, boolean maximisingPlayer){
        Turn currentTurn = currentState.getTurn();
        if(depth == 0 || currentTurn == Turn.WHITEWIN || currentTurn == Turn.BLACKWIN){
            return this.heuristic.evaluate(currentState, depth);
        }
        List<Action> possibleMoves = this.getAllPossibleMoves(currentState, currentTurn);
        if(maximisingPlayer){
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Action action : possibleMoves){
                double evaluation = this.minmaxComputation(move(currentState.clone(), action), depth - 1, false);
                maxEval = Math.max(maxEval, evaluation);
            }
            return maxEval;
        }
        else{
            double minEval = Double.POSITIVE_INFINITY;
            for(Action action : possibleMoves){
                double evaluation = this.minmaxComputation(move(currentState.clone(), action), depth - 1, true);
                minEval = Math.min(minEval, evaluation);
            }
            return minEval;
        }
    }

    private State move(State currentState, Action action) {
        return null;
    }

    private List<Action> getAllPossibleMoves(State state, Turn turn) {
        if(turn == Turn.WHITE)
            return UtilityFeatures.getAllPossibleWhiteMoves(state);
        return UtilityFeatures.getAllPossibleBlackMoves(state);
    }
}
