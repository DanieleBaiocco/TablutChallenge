package it.unibo.ai.didattica.competition.tablut.ai.geneticAlgorithm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

public class Genotype {
    int num, n_offsprings, n_genes, tournament_size, max_generation;
    double mutation_prob, timeout;
    ArrayList<List<List<Double>>> white_population = new ArrayList<List<List<Double>>>();
    ArrayList<List<List<Double>>> black_population = new ArrayList<List<List<Double>>>();
    /*ArrayList<> roulette_prob = new ArrayList<>();*/

    HashMap<Integer, String> genes = new HashMap<Integer, String>(){{
        put(1, "KingEscaped");
        put(2, "KingToEscape");
        put(3, "NumberOfWhite");
        put(4, "NumberOfBlack");
        put(5, "KingSurrounded");
        put(6, "BlackMenaced");
        put(7, "?");
        put(8, "??");
        put(9, "EscapesBlocked");
    }};

    HashMap<String, ArrayList<Double>> genes_bound = new HashMap<String, ArrayList<Double>>(){{
        put("KingEscaped", new ArrayList<Double>(Arrays.asList(1.0, 500.0)));
        put("KingToEscape", new ArrayList<Double>(Arrays.asList(-50.0, 0.0)));
        put("NumberOfWhite", new ArrayList<Double>(Arrays.asList(0.0, 5.0)));
        put("NumberOfBlack", new ArrayList<Double>(Arrays.asList(-5.0, 0.0)));
        put("KingSurrounded", new ArrayList<Double>(Arrays.asList(-10.0, 0.0)));
        put("BlackMenaced", new ArrayList<Double>(Arrays.asList(0.0, 5.0)));
        /*put("?", new ArrayList<Double>(Arrays.asList(0.0, 0.0)));
        put("??", new ArrayList<Double>(Arrays.asList(0.0, 0.0)));*/
        put("EscapesBlocked", new ArrayList<Double>(Arrays.asList(-5.0, 0.0)));
    }};


    Genotype(){
        this.num = 10; /* deve essere pari*/
        this.n_offsprings = 2;
        this.n_genes = 9;
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
        /*fitness_fn(this.white_population, this.black_population, this.timeout);*/
    
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
                parents_1 = this.tournament_selection();
                parents_2 = this.tournament_selection();

                white_children = this.two_point_cross_over(parents_1.get(0), parents_2.get(0));
                black_children = this.two_point_cross_over(parents_1.get(1), parents_2.get(1));
            }
        }
    
    
    };

    public void initialize_population(){

        for(int i = 0; i < 2*this.num; i++){
            double KingEscaped = getRandomNumber(genes_bound.get("KingEscaped").get(0), genes_bound.get("KingEscaped").get(1));
            double KingToEscape = getRandomNumber(genes_bound.get("KingToEscape").get(0), genes_bound.get("KingToEscape").get(1));
            double NumberOfWhite = getRandomNumber(genes_bound.get("NumberOfWhite").get(0), genes_bound.get("NumberOfWhite").get(1));
            double NumberOfBlack = getRandomNumber(genes_bound.get("NumberOfBlack").get(0), genes_bound.get("NumberOfBlack").get(1));
            double KingSurrounded = getRandomNumber(genes_bound.get("KingSurrounded").get(0), genes_bound.get("KingSurrounded").get(1));
            double BlackMenaced = getRandomNumber(genes_bound.get("BlackMenaced").get(0), genes_bound.get("BlackMenaced").get(1));
            /*double ? = getRandomNumberUsingNextInt(genes_bound.get("?").get(0), genes_bound.get("?").get(1));
            double ?? = getRandomNumberUsingNextInt(genes_bound.get("??").get(0), genes_bound.get("??").get(1));*/
            double EscapesBlocked = getRandomNumber(genes_bound.get("EscapesBlocked").get(0), genes_bound.get("EscapesBlocked").get(1));

            List<List<Double>> agent = new ArrayList<List<Double>>(){{
                add(Arrays.asList(KingEscaped, KingToEscape, NumberOfWhite, NumberOfBlack, KingSurrounded, BlackMenaced, EscapesBlocked));
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

    public ArrayList<List<List<Double>>> two_point_cross_over(List<List<Double>> w_parent_1, List<List<Double>> w_parent_2){
        ArrayList<List<List<Double>>> children = new ArrayList<List<List<Double>>>();

        return children;
    }

};

