package it.unibo.ai.didattica.competition.ai.decisionmaking;

import it.unibo.ai.didattica.competition.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.ai.model.Direction;
import it.unibo.ai.didattica.competition.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.ai.utility.Pair;
import it.unibo.ai.didattica.competition.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.domain.Pawn;
import it.unibo.ai.didattica.competition.domain.Turn;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeuristicNiegghie {

    private final List<Coordinate> camps;
    private final List<Coordinate> winningPos;
    private final Coordinate castle;
    private final StateDecorator state;
    private final List<Coordinate> whitePawns;
    private final List<Coordinate> blackPawns;
    private final Coordinate king;

    static int staticWeights[] = new int[] { 500, // re sulla casella vincente
            -20, // numero di pezzi (minore) fra il re e la fuga in una sola mossa.
            +20,
            2, // numero di pedine bianche
            -2, // numero di pedine nere
            -5, // numero di pedine nere, castelli o accampamenti direttamente vicino al re
            // (pericolo di cattura)
            +1, // numero di neri che rischiano la cattura
            -1, //numero di bianchi che rischiano la cattura
            -1, // numero di mosse fino ad ora
            +1, // numero di bianchi e neri che si fornteggiano(rispetto al re)
            -2, // numero di caselle di fuga con un nero davanti
            +0//???TODO pedine mangiate che liberano una posizione vicino al re
    };

    public HeuristicNiegghie(StateDecorator state) {
        this.camps = TablutUtility.getInstance().getCamps();
        this.winningPos = TablutUtility.getInstance().getWinningPos();
        this.castle = TablutUtility.getInstance().getCastle();
        this.state = state;
        Map<Pawn, List<Coordinate>> listMap = state.getPieces();
        this.whitePawns = listMap.get(Pawn.WHITE);
        this.blackPawns = listMap.get(Pawn.BLACK);
        this.king = listMap.get(Pawn.KING).get(0);

    }

    //TODO CAPIRE SE SERVE DEPTH
    public double evaluate() {
        double value = staticWeights[0] * winCondition()
                + staticWeights[1] * kingToEscape()
                + staticWeights[2] * winPaths()
                + staticWeights[3] * this.whitePawns.size()
                + staticWeights[4] * this.blackPawns.size()
                + staticWeights[5] * kingSurrounded()
                + staticWeights[6] * blackMenaced()
                + staticWeights[7] * whiteMenaced()
                + staticWeights[8] * 0
                + staticWeights[9] * 0
                + staticWeights[10] * escapesBlocked();
        /*System.out.println(" "+ winCondition()
        + " "+ kingToEscape()
        + " "+ winPaths()
        + " "+ this.whitePawns.size()
        + " "+ this.blackPawns.size()
        + " "+ kingSurrounded()
        + " "+ blackMenaced()
        + " "+ whiteMenaced()
        + " "+ 0
        + " "+ 0
        + " "+ escapesBlocked()
        + " value: "+ value);*/
        return value;
    }

    /**
     * checks if either black or white won
     */
    private int winCondition() {
        if(this.state.getTurn() == Turn.WHITEWIN)
            return 1;
        if(this.state.getTurn() == Turn.BLACKWIN)
            return -1;
        return 0;
    }



    public int kingToEscape() {
        List<Long> pawnCouts = new ArrayList<>();
        Stream<Direction>  directionStream = buildDirectionBoolStream(this.king, this.state.getBoard().length);
        directionStream.forEach(dir -> {
            List<Pair<Coordinate, Pawn>> pawnsInOneDir = this.state.LookDirection(dir, this.king);
            long count = pawnsInOneDir.stream()
                    .filter(pawnPair -> pawnPair.getSecond() != Pawn.EMPTY
                            || this.camps.contains(pawnPair.getFirst())).count();
            pawnCouts.add(count);
        });
        OptionalInt optionalInt = pawnCouts.stream().mapToInt(Math::toIntExact).min();
        //TODO capire come calcolare il default, capire se serve contare quanti pedoni ci sono
        //TODO anche in direzioni che portano a campi
        return optionalInt.isPresent() ?  optionalInt.getAsInt() : 4;
    }

    public int winPaths(){
        final Integer[] winningPaths = {0};
        Stream<Direction> directionStream = buildDirectionBoolStream( this.king, this.state.getBoard().length);
        directionStream.forEach(dir -> {
             // TODO le posizioni potrebbero essere aggiunte alla lista in modo sbagliato(controllalo)
             List<Pair<Coordinate, Pawn>> pawnsInOneDir = this.state.LookDirection(dir, this.king);
             Optional<Pair<Coordinate, Pawn>> optCordPawn = pawnsInOneDir.stream()
                     .takeWhile(pawnPair -> pawnPair.getSecond() == Pawn.EMPTY && !this.camps.contains(pawnPair.getFirst()))
                     .reduce((x, y) -> y);
             if(optCordPawn.isPresent()){
                 Pair<Coordinate, Pawn> lastcordpawn = optCordPawn.get();
                 if(this.winningPos.contains(lastcordpawn.getFirst()))
                     winningPaths[0]++;
             }
        });
        return winningPaths[0];
    }


    public Stream<Direction> buildDirectionBoolStream(Coordinate kingPosition, int boardSize) {
        Map<Direction, Boolean> dirBool = new HashMap<>();
        dirBool.put(Direction.UP, true);
        dirBool.put(Direction.RIGHT, true);
        dirBool.put(Direction.DOWN, true);
        dirBool.put(Direction.LEFT, true);
        updateDirBoolMap(Direction.UP, dirBool,
                camps ->camps.contains(new Coordinate(0, kingPosition.getCol())),
                this.camps );
        updateDirBoolMap(Direction.RIGHT, dirBool,
                camps -> camps.contains(new Coordinate(kingPosition.getRow(), boardSize - 1)),
                this.camps );
        updateDirBoolMap(Direction.DOWN, dirBool,
                camps -> camps.contains(new Coordinate(boardSize - 1, kingPosition.getCol())),
                this.camps );
        updateDirBoolMap(Direction.LEFT, dirBool,
                camps -> camps.contains(new Coordinate(kingPosition.getRow(), 0)),
                this.camps );
        return dirBool.entrySet().stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey);
    }

    private void updateDirBoolMap(Direction dir, Map<Direction, Boolean> dirBool,
                                  Predicate<List<Coordinate>> pred, List<Coordinate> camps){
        if(dirBool.get(dir)) {
            if (pred.test(camps)) {
                dirBool.put(dir, false);
                dirBool.put(dir.getOppositeDirection(),false);
            }
        }
    }

    public double kingSurrounded(){
        Stream<Coordinate> s = Stream.concat(this.blackPawns.stream(), this.camps.stream());
        double count = s.filter(this.king::closeTo).count();
        if(this.king.closeTo(this.castle)) count++;
        return count;
    }
    //----

    //piu è alto il contatore piu va a favore del bianco (piu è buona la posizione). Quindi il peso dovrà esser
    //positivo. Nel caso simmetrico il peso sarà negativo perchè premia il nero.

    /*funzione che guarda ogni nero e controlla se sta per essere mangiato.
    le condizioni sono che deve avere un lato occupato da un bianco/una base/un trono (?) e l'opposto libero
    e deve esserci un pedone bianco che può muoversi e in una sola mossa
    catturare il nero.
    */
    public int blackMenaced() {
       return menaced(this.blackPawns, pawn -> pawn == Pawn.WHITE || pawn.equalsPawn("K"));
    }

    public int whiteMenaced(){
        return menaced(this.whitePawns, pawn -> pawn == Pawn.BLACK);
    }

    private int menaced(List<Coordinate> coloredPieces, Predicate<Pawn> pred){
        int countToReturn = 0;
        Map<Direction, Boolean> dirLeftToCheck = new HashMap<>();
        for (Direction dir : Direction.values()) dirLeftToCheck.put(dir, true);
        for (Coordinate coord : coloredPieces) {
            for (Direction dir : Direction.values()) {
                if (dirLeftToCheck.get(dir)){
                    dirLeftToCheck.put(dir, false);
                    Pair<Coordinate, Pawn> pairToLook;
                    try {
                        pairToLook = coord.look(dir, this.state);
                    }catch (IndexOutOfBoundsException e){
                        continue;
                    }
                    if (pred.test(pairToLook.getSecond()) || pairToLook.getSecond() == Pawn.THRONE
                            || this.camps.contains(pairToLook.getFirst())) {
                        Direction oppositeDirection = dir.getOppositeDirection();
                        dirLeftToCheck.put(oppositeDirection, false);
                        Pair<Coordinate, Pawn> pairCapturePosition;
                        try {
                            pairCapturePosition = coord.look(oppositeDirection, this.state);
                        }catch (IndexOutOfBoundsException e){
                            continue;
                        }
                        //controllo sul castle?
                        if (pairCapturePosition.getSecond() == Pawn.EMPTY
                                && !this.camps.contains(pairCapturePosition.getFirst())
                                && !pairCapturePosition.getFirst().equals(this.castle)) {
                            List<Direction> captureDirs = Arrays.stream(Direction.values()).collect(Collectors.toList());
                            captureDirs.remove(dir);
                            for (Direction captureDir : captureDirs) {
                                List<Pair<Coordinate, Pawn>> menacers = this.state.LookDirection(captureDir, pairCapturePosition.getFirst());
                                Optional<Pair<Coordinate, Pawn>> optFirstNonEmpty = menacers.stream().filter(
                                    pair -> pair.getSecond() != Pawn.EMPTY 
                                    || camps.contains(pair.getFirst())
                                    || pair.getFirst().equals(this.castle)).findFirst();
                                if (optFirstNonEmpty.isPresent() && pred.test(optFirstNonEmpty.get().getSecond())) {
                                    countToReturn++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return countToReturn;
    }

    /*
    riporta il numero di caselle di escape che sono occupate o che hanno in line diretta un nero
        come prima pedina visibile
    */
    public int escapesBlocked() {
        int count = 0;
        int length = this.state.getBoard().length;
        List<Coordinate> winPosUp = this.winningPos.stream().filter(c -> c.getRow() == 0).collect(Collectors.toList());
        List<Coordinate> winPosDx = this.winningPos.stream().filter(c -> c.getCol() == length-1).collect(Collectors.toList());
        List<Coordinate> winPosDown = this.winningPos.stream().filter(c -> c.getRow() == length -1).collect(Collectors.toList());
        List<Coordinate> winPosSx = this.winningPos.stream().filter(c -> c.getCol() == 0).collect(Collectors.toList());
        count += escapesBlockedCalculus(winPosUp, Direction.DOWN);
        count += escapesBlockedCalculus(winPosDx, Direction.LEFT);
        count += escapesBlockedCalculus(winPosDown, Direction.UP);
        count += escapesBlockedCalculus(winPosSx, Direction.RIGHT);
        return count;
    }

    private int escapesBlockedCalculus(List<Coordinate> winPositions, Direction dir) {
        int countToReturn = 0;
        for(Coordinate winPos : winPositions){
            if(this.state.getPawn(winPos.getRow(), winPos.getCol()) != Pawn.EMPTY)
                countToReturn++;
            else{
                List<Pair<Coordinate, Pawn>> pawns = this.state.LookDirection(dir, winPos);
                Optional<Pair<Coordinate, Pawn>> optFirstNonEmpty = pawns.stream().filter(
                    pair -> pair.getSecond() != Pawn.EMPTY 
                    || camps.contains(pair.getFirst())).findFirst();
                if (optFirstNonEmpty.isPresent() && optFirstNonEmpty.get().getSecond() == Pawn.BLACK)
                    countToReturn++;
            }
        }
        return countToReturn;
    }
}
