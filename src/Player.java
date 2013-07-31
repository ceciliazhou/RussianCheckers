import java.awt.*;
import java.util.*;

public abstract class Player {

    /** the color of chess owned by this player */
    private CheckerStatus chess;
    
    /** 
     * construct a player and set its role by specifying the checkers owned by this player. 
     * @param chess the chess owned by this player.
     */
    public Player(CheckerStatus chess) {
        this.chess = chess;
    }

    /** 
     * Set the player's role by specifying the checkers owned by this player. 
     * @param chess the chess owned by this player.
     */ 
    public void setChess(CheckerStatus chess) {
        this.chess = chess;
    }
    
    /** 
     * Return the status of the checkers owned by this player.
     * @return the status of the checkers owned by this player.
     */ 
     public CheckerStatus getChess() {
        return chess;
    }

    /** 
     * Take a move. 
     */     
    protected abstract void move();
}

class HumanPlayer extends Player {
    
    /** 
     * construct a human player and set its role by specifying the checkers owned by this player. 
     * @param chessColor the chess owned by this player.
     */
    public HumanPlayer(CheckerStatus chessColor) {
        super(chessColor);
    }

    /** 
     * Set where to start with.
     * @param from the location to start from.   
     */         
    public void startFrom(Location from) {
        this.from = from;
    }

    /** 
     * Set where to aim at. If there is a legal path from from to to, a move will take place.
     * @param to the target.
     */     
    public void goTo(Location to) {
        if(from != null) {
            this.to = to;
            move();
        }
    }
    
    /**
     * Find the path between from and to, take a move, show the move on the checkerboard.
     */
    protected void move() {
        Action action = Agent.findPath(CheckersGame.getCurrentState(), from, to);  
        if(action != null){
            CheckersGame.showMove(action);
            from = null;
            to = null;
        }
    }

    /** the chess the current move starts from */
    private Location from;
    private Location to;
}

class ComputerPlayer extends Player {
    
    /** 
     * construct a computer player and set its role by specifying the checkers owned by this player. 
     * @param chessColor the chess owned by this player.
     */
    public ComputerPlayer(CheckerStatus chessColor) {
        super(chessColor);
    }
    
    /**
     * Find the best move and take the action, show the move on the checkerboard.
     */
    protected void move() {
        CBStatus curState = CheckersGame.getCurrentState();
        // // System.out.println("computer starts calculate moving..............");
        // System.out.println(curState);
        Agent.SearchResult result = Agent.bestMove(curState, getChess());
        CheckersGame.showMove(result.action);
        System.out.println(String.format("max depth: %d, generated nodes: %d, %d prunings take place in maxValue and %d in minValue", 
                    Agent.maxDepth, Agent.nodeGenerated, result.maxPruning, result.minPruning));
    }
        
}