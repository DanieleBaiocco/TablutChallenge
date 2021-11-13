package it.unibo.ai.didattica.competition.tablut.ai.decisionmaking;

import it.unibo.ai.didattica.competition.tablut.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.tablut.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.IState;
import it.unibo.ai.didattica.competition.tablut.domain.Turn;

import java.util.List;
import java.util.function.BiPredicate;

public class MinMax {

    private final Turn player;
    private int depth;
    private HeuristicNiegghie heuristic;

    private Action bestAction;

    public MinMax(Turn player, int depth) {
        this.player = player;
        this.depth = depth;
        this.heuristic = new HeuristicNiegghie();

    }

    //TODO
    //capisci perchè non usi il timeout e il player del costruttore
    public Action makeDecision(IState state, int timeout) {
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        minmaxComputation(new StateDecorator(state), this.depth, alpha, beta, state.getTurn() == Turn.WHITE);
        return this.bestAction;
    }

    public double minmaxComputation(IState currentState, int depth, double alpha, double beta, boolean maximisingPlayer){
        Turn currentTurn = currentState.getTurn();
        StateDecorator stateDeco = new StateDecorator(currentState);
        if(depth == 0 || currentTurn == Turn.WHITEWIN || currentTurn == Turn.BLACKWIN){
            return this.heuristic.evaluate(stateDeco, depth);
        }
        List<Action> possibleMoves = this.getAllPossibleMoves(stateDeco, currentTurn);
        if(maximisingPlayer){
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Action action : possibleMoves){
                double evaluation = this.minmaxComputation(TablutUtility.getInstance().movePawn(
                        currentState.clone(), action),
                        depth - 1, alpha, beta,false);
                maxEval = updateEvalAndBestAction((x, y) -> x < y, maxEval, evaluation, depth, action);
                alpha = Math.max(alpha, evaluation);
                if(beta <= alpha)
                    break;
            }
            return maxEval;
        }
        else{
            double minEval = Double.POSITIVE_INFINITY;
            for(Action action : possibleMoves){
                double evaluation = this.minmaxComputation(TablutUtility.getInstance().movePawn(currentState.clone(), action),depth - 1, alpha, beta,true);
                minEval = updateEvalAndBestAction(((x, y) -> y < x), minEval, evaluation, depth, action);
                beta = Math.min(beta, evaluation);
                if(beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    private double updateEvalAndBestAction(BiPredicate<Double, Double> predicate,
                                         double value, double evaluation, int depth,
                                         Action action) {
        if(predicate.test(value, evaluation)){
            value = evaluation;
            if(depth == this.depth)
                this.bestAction = action;
        }
        return value;
    }


    private List<Action> getAllPossibleMoves(StateDecorator stateDeco, Turn turn) {
        if(turn == Turn.WHITE)
            return stateDeco.getAllWhiteMoves();
        return  stateDeco.getAllBlackMoves();
    }
}
