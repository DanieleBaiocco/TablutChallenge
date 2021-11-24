package it.unibo.ai.didattica.competition.ai.geneticAlgorithm;
import java.util.ArrayList;
import java.util.List;

public class Population {
    List<Agent> population = new ArrayList<Agent>();

    Population(){

    }

    public void add(Agent agent){
        this.population.add(agent);
    }

    public void setPopulation(List<Agent> population){
        this.population = population;
    }

    public void setIndividual(int i, Agent individual){
        this.population.set(i, individual);
    }

    public Agent getIndividual(int i){
        return this.population.get(i);
    }

    public List<Agent> getPopulation(){
        return this.population;
    }

    public int getSize(){
        return this.population.size();
    }

    public void SortPopulation(){
        for(int i = 0; i < this.getSize(); i++) {
            boolean flag = false;
            for(int j = 0; j < this.getSize()-1; j++) {
                if(this.getIndividual(i).getScore() > this.getIndividual(j+1).getScore()) {
                    Agent k = this.getIndividual(j);
                    this.setIndividual(j, this.getIndividual(j+1));
                    this.setIndividual(j+1, k);
                    flag=true; 
                }
            }
            if(!flag) break; 
        }
    }
}
