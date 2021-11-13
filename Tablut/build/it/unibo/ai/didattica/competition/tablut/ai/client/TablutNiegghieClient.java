package it.unibo.ai.didattica.competition.tablut.ai.client;

import it.unibo.ai.didattica.competition.tablut.ai.decisionmaking.MinMax;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Turn;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutNiegghieClient extends TablutClient {

    public static final String NAME = "Niegghie";
    private final int depth;

    public TablutNiegghieClient(String player, int timeout, String ipAddress, int depth) throws UnknownHostException, IOException {
        super(player, NAME, timeout, ipAddress);
        this.depth = depth;
    }
    public TablutNiegghieClient(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
        super(player, NAME, timeout, ipAddress);
        this.depth = 4;
    }

    public static void main(String[] args){
        try {
            if(args.length != 3 && args.length != 4) {
                throw new IllegalArgumentException("black/white, timeout, ip-address {, depth}");
            }
            TablutClient tablutClient = null;
            if(args.length == 3){
                tablutClient = new TablutNiegghieClient(args[0], Integer.parseInt(args[1]), args[2]);
            }
            else {
                tablutClient = new TablutNiegghieClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
            }
            tablutClient.run();
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        MinMax minmax = new MinMax(this.getPlayer(), this.depth);
        try {
            this.declareName();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        if(this.getPlayer()== Turn.WHITE){
            buildClient(minmax, Turn.WHITE);
        }else{
            buildClient(minmax, Turn.BLACK);
        }
    }

    private void buildClient(MinMax minmax, Turn myTurn) {
        boolean exit = false;
        while(!exit){
            try {
                this.read();
                Turn currentTurn = this.getCurrentState().getTurn();
                if(currentTurn == Turn.WHITEWIN || currentTurn == Turn.BLACKWIN || currentTurn == Turn.DRAW){
                    System.out.println(currentTurn.toString());
                    exit = true;
                }
                else if(currentTurn == myTurn){
                    Action action = minmax.makeDecision(this.getCurrentState().clone(), this.getTimeout());
                    this.write(action);
                }else{
                    System.out.println("Waiting for opponent");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
