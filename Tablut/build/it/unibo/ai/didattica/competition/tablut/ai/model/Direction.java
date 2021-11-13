package it.unibo.ai.didattica.competition.tablut.ai.model;

public enum Direction {
    UP(0), RIGHT(1), DOWN(2), LEFT(3);


    private final int i;
    Direction(int i) {
        this.i = i;
    }
    public int getIndex(){
        return i;
    }

}
