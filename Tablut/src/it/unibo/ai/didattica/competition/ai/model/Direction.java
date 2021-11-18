package it.unibo.ai.didattica.competition.ai.model;

public enum Direction {
    UP(0), RIGHT(1), DOWN(2), LEFT(3);


    private final int i;
    Direction(int i) {
        this.i = i;
    }
    public int getIndex(){
        return i;
    }
    public Direction getOppositeDirection(){
        return Direction.values()[(this.getIndex()+2) %4];
    }
}
