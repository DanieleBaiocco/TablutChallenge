package it.unibo.ai.didattica.competition.ai.decisionmaking;

import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.domain.Action;
import it.unibo.ai.didattica.competition.domain.IState;
import it.unibo.ai.didattica.competition.domain.Turn;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiPredicate;

public class MinMax {

    private final Turn player;
    private int depth;
    private HeuristicNiegghie heuristic;

    private Action bestAction;

    public MinMax(Turn player, int depth) {
        this.player = player;
        this.depth = depth;
    }

    //TODO
    //capisci perchè non usi il timeout e il player del costruttore
    public Action makeDecision(IState state, int timeout) {
        //System.out.println("inizio");
        this.heuristic = new HeuristicNiegghie(new StateDecorator(state));
        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;
        ExecutorService asyncService = Executors.newCachedThreadPool();
        Future<Double> futureTask = asyncService.submit(()-> minmaxComputation(state, this.depth, alpha, beta, state.getTurn() == Turn.WHITE));
        try {
            double result = futureTask.get(timeout - 1, TimeUnit.SECONDS);
        } 
        catch (TimeoutException e){
            futureTask.cancel(true);
            System.out.println("TIMEOUT EXCEPTION");
        }
        catch (ExecutionException e){}
        catch (InterruptedException e){}

        if (futureTask.isCancelled()){
            //TODO fai cose!!
            //randomMove()
        }

        return this.bestAction;
    }

    public double minmaxComputation(IState currentState, int depth, double alpha, double beta, boolean maximisingPlayer){
        Turn currentTurn = currentState.getTurn();
        StateDecorator stateDeco = new StateDecorator(currentState);
        if(depth == 0 || currentTurn == Turn.WHITEWIN || currentTurn == Turn.BLACKWIN){
            /*Random random = new Random();
            if (random.nextInt(10000)==1) System.out.println(currentState.toString());
            System.out.println("Evluation: "+this.heuristic.evaluate());*/
            HeuristicNiegghie euristicTest = new HeuristicNiegghie(new StateDecorator(currentState));
            return euristicTest.evaluate();
        }
        List<Action> possibleMoves = this.getAllPossibleMoves(stateDeco, currentTurn);
        Collections.shuffle(possibleMoves);
        //System.out.println("all possible moves, size "+ possibleMoves.size());
        if(maximisingPlayer){
            double maxEval = Double.NEGATIVE_INFINITY;
            for(Action action : possibleMoves){
                double evaluation = this.minmaxComputation(TablutUtility.getInstance().movePawn(
                        currentState.clone(), action),
                        depth - 1, alpha, beta, false);
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
                double evaluation = this.minmaxComputation(TablutUtility.getInstance().movePawn(currentState.clone(), action),depth - 1, alpha, beta, true);
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
