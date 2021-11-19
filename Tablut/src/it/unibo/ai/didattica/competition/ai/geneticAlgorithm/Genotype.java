package it.unibo.ai.didattica.competition.ai.geneticAlgorithm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class Genotype {
    int num, n_offsprings, n_genes, tournament_size, max_generation;
    double mutation_prob, timeout;
    Population white_population = new Population();
    Population black_population = new Population();
    Fitness_fn function = new Fitness_fn();

    HashMap<Integer, String> genes = new HashMap<Integer, String>(){{
        put(0, "winCondition");
        put(1, "KingToEscape");
        put(2, "winPaths");
        put(3, "NumberOfWhite");
        put(4, "NumberOfBlack");
        put(5, "KingSurrounded");
        put(6, "BlackMenaced");
        put(7, "EscapesBlocked");
    }};

    HashMap<String, ArrayList<Double>> genes_bound = new HashMap<String, ArrayList<Double>>(){{
        put("winCondition", new ArrayList<Double>(Arrays.asList(1.0, 500.0)));
        put("KingToEscape", new ArrayList<Double>(Arrays.asList(-50.0, 0.0)));
        put("winPaths", new ArrayList<Double>(Arrays.asList(0.0, 50.0)));
        put("NumberOfWhite", new ArrayList<Double>(Arrays.asList(0.0, 5.0)));
        put("NumberOfBlack", new ArrayList<Double>(Arrays.asList(-5.0, 0.0)));
        put("KingSurrounded", new ArrayList<Double>(Arrays.asList(-10.0, 0.0)));
        put("BlackMenaced", new ArrayList<Double>(Arrays.asList(0.0, 5.0)));
        put("EscapesBlocked", new ArrayList<Double>(Arrays.asList(-5.0, 0.0)));
    }};


    Genotype(){
        this.num = 10; /* deve essere pari*/
        this.n_offsprings = 2;
        this.n_genes = 8;
        this.mutation_prob = 0.2;
        this.tournament_size = 3;
        this.max_generation = 3;
        this.timeout = 59.5;
    };

    Genotype(int num, int n_offsprings, int n_genes, double mutation_prob, int tourn_size, int max_generation, double timeout){
        this.num = num;
        this.n_offsprings = n_offsprings;
        this.n_genes = n_genes;
        this.mutation_prob = mutation_prob;
        this.tournament_size = tourn_size;
        this.max_generation = max_generation;
        this.timeout = timeout;
    };

    public void start(){
        this.initialize_population();
        System.out.println("---------- Initial Population Game ----------");
        function.fit(this.white_population, this.black_population, this.timeout);

        int generation = 0;

        while(generation < this.max_generation){
            generation += 1;
            Population new_white_population = new Population();
            Population new_black_population = new Population();

            Population parents_1 = new Population();
            Population parents_2 = new Population();

            Population white_children = new Population();
            Population black_children = new Population();

            while(new_white_population.getSize() < this.num){
                parents_1 = this.tournament_selection(); //one white_parent and one black_parent
                parents_2 = this.tournament_selection();

                white_children = this.two_point_cross_over(parents_1.getIndividual(0), parents_2.getIndividual(0));
                black_children = this.two_point_cross_over(parents_1.getIndividual(1), parents_2.getIndividual(1));

                new_white_population.add(white_children.getIndividual(0));
                new_white_population.add(white_children.getIndividual(1));
                new_black_population.add(black_children.getIndividual(0));
                new_black_population.add(black_children.getIndividual(1));

            }

            System.out.printf("----------New Population %d Games ----------", generation);
            function.fit(new_white_population, new_black_population, this.timeout);
            //this.truncation_selection(new_white_population, new_black_population);
            System.out.printf("----------Generation %d ----------", generation);
            function.fit(this.white_population, this.black_population, this.timeout);

            //to do: Sort white and Black population to obtain in position 0 the best white/black player according to the second parameter [useful to print some infos]
        }


    };

    public void initialize_population(){

        for(int i = 0; i < 2*this.num; i++){
            double winCondition = getRandomNumber(genes_bound.get("winCondition").get(0), genes_bound.get("winCondition").get(1));
            double KingToEscape = getRandomNumber(genes_bound.get("KingToEscape").get(0), genes_bound.get("KingToEscape").get(1));
            double winPaths = getRandomNumber(genes_bound.get("winPaths").get(0), genes_bound.get("winPaths").get(1));
            double NumberOfWhite = getRandomNumber(genes_bound.get("NumberOfWhite").get(0), genes_bound.get("NumberOfWhite").get(1));
            double NumberOfBlack = getRandomNumber(genes_bound.get("NumberOfBlack").get(0), genes_bound.get("NumberOfBlack").get(1));
            double KingSurrounded = getRandomNumber(genes_bound.get("KingSurrounded").get(0), genes_bound.get("KingSurrounded").get(1));
            double BlackMenaced = getRandomNumber(genes_bound.get("BlackMenaced").get(0), genes_bound.get("BlackMenaced").get(1));
            double EscapesBlocked = getRandomNumber(genes_bound.get("EscapesBlocked").get(0), genes_bound.get("EscapesBlocked").get(1));

            Agent agent = new Agent();
            agent.setGenes(Arrays.asList(winCondition, KingToEscape, winPaths, NumberOfWhite, NumberOfBlack, KingSurrounded, BlackMenaced, EscapesBlocked));
            agent.setScore(0.0);

            if (i < this.num){
                this.white_population.add(agent);
            }
            else{
                this.black_population.add(agent);
            }
        };
    };

    public Double getRandomNumber(double min, double max) {
        Random random = new Random();
        return random.nextDouble()*(max - min) + min;
    };

    public int getRandomIntNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt()*(max - min) + min;
    };

    public Population tournament_selection(){
        Population best_players = new Population();
        Agent best_white_player = new Agent();
        Agent best_black_player = new Agent();

        for(int i = 0; i < this.tournament_size; i++){
            Agent white_player = new Agent();
            Agent black_player = new Agent();

            white_player = this.white_population.getIndividual(getRandomIntNumber(0, this.num));
            black_player = this.black_population.getIndividual(getRandomIntNumber(0, this.num));

            if(best_white_player.isUndefined() || white_player.getScore() > best_white_player.getScore()){
                best_white_player = white_player;
            }
            if(best_black_player.isUndefined() || black_player.getScore() > best_black_player.getScore()){
                best_black_player = black_player;
            }
        }

        best_players.add(best_white_player);
        best_players.add(best_black_player);

        return best_players;
    }

    public Population two_point_cross_over(Agent parent_1, Agent parent_2){
        Population children = new Population();
        Agent offspring_1 = new Agent();
        Agent offspring_2 = new Agent();

        offspring_1.setGenes(Arrays.asList(parent_1.getGene(0), parent_1.getGene(1), parent_2.getGene(2), parent_2.getGene(3), 
                                           parent_2.getGene(4), parent_2.getGene(5), parent_1.getGene(6), parent_1.getGene(7)));
        offspring_1.setScore(0.0);
        
        offspring_2.setGenes(Arrays.asList(parent_2.getGene(0), parent_2.getGene(1), parent_1.getGene(2), parent_1.getGene(3), 
                                           parent_1.getGene(4), parent_1.getGene(5), parent_2.getGene(6), parent_2.getGene(7)));
        offspring_2.setScore(0.0);

        this.mutation(offspring_1);
        this.mutation(offspring_2);

        children.add(offspring_1);
        children.add(offspring_2);

        return children;
    };

    //CONTROLLARE I RANGE DEI GENI DA MUTARE E DI QUANTO MUTARLI
    public void mutation(Agent offspring){
        if(this.getRandomNumber(0, 1) <= this.mutation_prob){
            int gene_number = getRandomIntNumber(0, this.n_genes);
            String mutated_gene = this.genes.get(gene_number);

            if(gene_number > 5 && gene_number < 9){
                offspring.setGene(gene_number, (0.01 * getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
            if(gene_number > 3 && gene_number < 6){
                offspring.setGene(gene_number, (0.1 * getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
            else{
                offspring.setGene(gene_number, (getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
        }
    };

    public void truncation_selection(ArrayList<List<List<Double>>> new_white_population, ArrayList<List<List<Double>>> new_black_population){
        /*I NEED TO SORT THIS.WHITE AND THIS.BLACK POPULATION BY SECOND PARAMETER OF EACH LIST of lists
        NEW_WHITE AND NEW_BLACK POPULATION
        PUT IN THIS.WHITE AND THIS.BLACK POPULATION HALF THIS.WHITE/THIS.BLACK AND HALF NEW_WHITE/NEW_BLACK*/
    };

};