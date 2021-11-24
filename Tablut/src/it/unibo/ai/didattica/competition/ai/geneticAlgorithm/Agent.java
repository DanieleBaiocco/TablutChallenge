package it.unibo.ai.didattica.competition.ai.geneticAlgorithm;
import java.util.List;
import java.util.Arrays;

public class Agent {
    double winCondition, KingToEscape, winPaths, NumberOfWhite, NumberOfBlack, KingSurrounded,
    BlackMenaced, EscapesBlocked;
    double score;

    Agent(){
    }

    public void setGenes(List<Double> values){
        this.winCondition = values.get(0);
        this.KingToEscape = values.get(1);
        this.winPaths = values.get(2);
        this.NumberOfWhite = values.get(3);
        this.NumberOfBlack = values.get(4);
        this.KingSurrounded = values.get(5);
        this.BlackMenaced = values.get(6);
        this.EscapesBlocked = values.get(7);
    }

    public void setGene(int i, Double gene){
        this.getGenes().set(i, gene);
    }

    public List<Double> getGenes(){
        return Arrays.asList(this.winCondition, this.KingToEscape, this.winPaths, 
        this.NumberOfWhite, this.NumberOfBlack, this.KingSurrounded,
        this.BlackMenaced, this.EscapesBlocked);
    }

    public Double getGene(int i){
        return this.getGenes().get(i);
    }

    public void setScore(Double score){
        this.score = score;
    }

    public Double getScore(){
        return this.score;
    }

    public boolean isUndefined(){
        if(this.winCondition + this.KingToEscape + this.winPaths + this.NumberOfWhite + this.NumberOfBlack 
        + this.KingSurrounded + this.BlackMenaced + this.EscapesBlocked + this.score == 0.0){
            return true;
        }
        else{
            return false;
        }
    }
}
