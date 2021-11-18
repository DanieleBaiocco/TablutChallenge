package it.unibo.ai.didattica.competition.ai.geneticAlgorithm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import it.unibo.ai.didattica.competition.ai.geneticAlgorithm.Fitness_fn;

public class Genotype {
    int num, n_offsprings, n_genes, tournament_size, max_generation;
    double mutation_prob, timeout;
    ArrayList<List<List<Double>>> white_population = new ArrayList<List<List<Double>>>();
    ArrayList<List<List<Double>>> black_population = new ArrayList<List<List<Double>>>();
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
            ArrayList<List<List<Double>>> new_white_population = new ArrayList<List<List<Double>>>();
            ArrayList<List<List<Double>>> new_black_population = new ArrayList<List<List<Double>>>();

            ArrayList<List<List<Double>>> parents_1 = new ArrayList<List<List<Double>>>();
            ArrayList<List<List<Double>>> parents_2 = new ArrayList<List<List<Double>>>();

            ArrayList<List<List<Double>>> white_children = new ArrayList<List<List<Double>>>();
            ArrayList<List<List<Double>>> black_children = new ArrayList<List<List<Double>>>();

            while(new_white_population.size() < this.num){
                parents_1 = this.tournament_selection(); //one white_parent and one black_parent
                parents_2 = this.tournament_selection();

                white_children = this.two_point_cross_over(parents_1.get(0), parents_2.get(0));
                black_children = this.two_point_cross_over(parents_1.get(1), parents_2.get(1));

                new_white_population.add(white_children.get(0));
                new_white_population.add(white_children.get(1));
                new_black_population.add(black_children.get(0));
                new_black_population.add(black_children.get(1));

            }

            System.out.printf("----------New Population %d Games ----------", generation);
            function.fit(new_white_population, new_black_population, this.timeout);
            this.truncation_selection(new_white_population, new_black_population);
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

            List<List<Double>> agent = new ArrayList<List<Double>>(){{
                add(Arrays.asList(winCondition, KingToEscape, winPaths, NumberOfWhite, NumberOfBlack, KingSurrounded, BlackMenaced, EscapesBlocked));
                add(Arrays.asList(0.0));
            }};

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

    public ArrayList<List<List<Double>>> tournament_selection(){
        ArrayList<List<List<Double>>> best_players = new ArrayList<List<List<Double>>>();
        List<List<Double>> best_white_player = new ArrayList<List<Double>>();
        List<List<Double>> best_black_player = new ArrayList<List<Double>>();

        for(int i = 0; i < this.tournament_size; i++){
            List<List<Double>> white_player = new ArrayList<List<Double>>();
            List<List<Double>> black_player = new ArrayList<List<Double>>();

            white_player = this.white_population.get(getRandomIntNumber(0, this.num));
            black_player = this.black_population.get(getRandomIntNumber(0, this.num));

            if(best_white_player.isEmpty() || white_player.get(1).get(0) > best_white_player.get(1).get(0)){
                best_white_player = white_player;
            }
            if(best_black_player.isEmpty() || black_player.get(1).get(0) > best_black_player.get(1).get(0)){
                best_black_player = black_player;
            }
        }

        best_players.add(best_white_player);
        best_players.add(best_black_player);

        return best_players;
    }

    public ArrayList<List<List<Double>>> two_point_cross_over(List<List<Double>> parent_1, List<List<Double>> parent_2){
        ArrayList<List<List<Double>>> children = new ArrayList<List<List<Double>>>();
        List<List<Double>> offspring_1 = new ArrayList<List<Double>>();
        List<List<Double>> offspring_2 = new ArrayList<List<Double>>();

        offspring_1.get(0).set(0, parent_1.get(0).get(0));
        offspring_1.get(0).set(1, parent_1.get(0).get(1));
        offspring_1.get(0).set(2, parent_1.get(0).get(2));
        offspring_1.get(0).set(3, parent_2.get(0).get(3));
        offspring_1.get(0).set(4, parent_2.get(0).get(4));
        offspring_1.get(0).set(5, parent_2.get(0).get(5));
        offspring_1.get(0).set(6, parent_2.get(0).get(6));
        offspring_1.get(0).set(7, parent_1.get(0).get(7));
        offspring_1.get(1).set(0, 0.0);

        offspring_2.get(0).set(0, parent_2.get(0).get(0));
        offspring_2.get(0).set(1, parent_2.get(0).get(1));
        offspring_2.get(0).set(2, parent_2.get(0).get(2));
        offspring_2.get(0).set(3, parent_1.get(0).get(3));
        offspring_2.get(0).set(4, parent_1.get(0).get(4));
        offspring_2.get(0).set(5, parent_1.get(0).get(5));
        offspring_2.get(0).set(6, parent_1.get(0).get(6));
        offspring_2.get(0).set(7, parent_2.get(0).get(7));
        offspring_2.get(1).set(0, 0.0);

        this.mutation(offspring_1);
        this.mutation(offspring_2);

        children.add(offspring_1);
        children.add(offspring_2);

        return children;
    };

    //CONTROLLARE I RANGE DEI GENI DA MUTARE E DI QUANTO MUTARLI
    public void mutation(List<List<Double>> offspring){
        if(this.getRandomNumber(0, 1) <= this.mutation_prob){
            int gene_number = getRandomIntNumber(0, this.n_genes);
            String mutated_gene = this.genes.get(gene_number);

            if(gene_number > 5 && gene_number < 9){
                offspring.get(0).set(gene_number, (0.01 * getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
            if(gene_number > 3 && gene_number < 6){
                offspring.get(0).set(gene_number, (0.1 * getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
            else{
                offspring.get(0).set(gene_number, (getRandomNumber(this.genes_bound.get(mutated_gene).get(0), this.genes_bound.get(mutated_gene).get(1))));
            }
        }
    };

    public void truncation_selection(ArrayList<List<List<Double>>> new_white_population, ArrayList<List<List<Double>>> new_black_population){
        /*I NEED TO SORT THIS.WHITE AND THIS.BLACK POPULATION BY SECOND PARAMETER OF EACH LIST of lists
        NEW_WHITE AND NEW_BLACK POPULATION
        PUT IN THIS.WHITE AND THIS.BLACK POPULATION HALF THIS.WHITE/THIS.BLACK AND HALF NEW_WHITE/NEW_BLACK*/
    };

};