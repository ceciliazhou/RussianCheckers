import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * to be done
 * @author Cecilia
 */
public class CheckersGame {
    
    public static void main(String[] args) {
        
        /* start the game window */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.pack();       
                gameFrame.setVisible(true);
            }
        }); 
    }
    
    /** 
     * Reset the role of the players according to the chess color of the human player.
     * Player, either the human or the computer, holding BLACK chesses goes first.
     * @param status the checker status representing human, either BLACK or WHITE.
     */
    public static void resetPlayer(CheckerStatus status) {
        human.setChess(status);
        if(status == CheckerStatus.BLACK) {
            currentPlayer = human;
            computer.setChess(CheckerStatus.WHITE);
        } else {
            currentPlayer = computer;
            computer.setChess(CheckerStatus.BLACK);
        }
    } 

    /**
     * Handle a checkSelected event.
     * @param cell the location of the selected checker.
     */
    public static void humanChooseChecker(Location cell, CheckerStatus status) { 
        if(status == CheckerStatus.EMPTY)
            human.goTo(cell); 
        else if(status == human.getChess()) 
            human.startFrom(cell);
    }
    
    /** 
     * Return the player who gets the turn, either the human or the computer.
     * @return the player who gets the turn.
     */ 
    public static Player getCurrentPlayer() { return currentPlayer; }
    
    /** 
     * Return the player who gets the turn, either the human or the computer.
     * @return the player who gets the turn.
     */     
    public static CBStatus getCurrentState() { return gameFrame.getCurrentState(); }

    /** 
     * Show how a move is taken in the game window.
     * @param path the path along which the move to be shown is taken.
     */ 
    public static void showMove(Action path) { 
        gameFrame.showMove(path); 
    }   
    
    /** 
     * Update the game state and test if it reaches the end of the game.
     * If yes, congrats to the winner.
     * Otherwise, change turn to next player.
     */     
    public static void updateState() {
        CBStatus state = gameFrame.getCurrentState();
        if(Agent.goalTest(state, currentPlayer.getChess())) {
            String msg = currentPlayer == human ? "Congratulations! You win!" : "Sorry, you lose.";
            JOptionPane.showMessageDialog(gameFrame, msg);  
            gameFrame.setCheckerBoard(initCBS);
            resetPlayer(human.getChess());
        }
        else{
            currentPlayer = ((currentPlayer == human) ? computer : human);
            if(currentPlayer == computer)
                computer.move();
        }
    }
    
    public static boolean isMoving(){return gameFrame.isMoving();}
        
    /** players in the game */
    private static HumanPlayer human = new HumanPlayer(CheckerStatus.BLACK);
    private static ComputerPlayer computer = new ComputerPlayer(CheckerStatus.WHITE);
    private static Player currentPlayer = human;
    
    /** initial checkerboard state, initially matching the inital game state */
    // public static final CBStatus initCBS = new CBStatus( new int[][]{
                                        // {0, 1, 0, 3, 0, 1},
                                        // {3, 0, 3, 0, 3, 0},
                                        // {0, 2, 0, 1, 0, 1},
                                        // {3, 0, 2, 0, 1, 0},
                                        // {0, 1, 0, 1, 0, 1},
                                        // {1, 0, 1, 0, 3, 0}});    /* for test */  
    // public static final CBStatus initCBS = new CBStatus( new int[][]{
                                        // {0, 1, 0, 3, 0, 1},
                                        // {3, 0, 3, 0, 3, 0},
                                        // {0, 2, 0, 2, 0, 2},
                                        // {3, 0, 1, 0, 1, 0},
                                        // {0, 2, 0, 2, 0, 2},
                                        // {2, 0, 2, 0, 3, 0}});    /* for test */                                      
    public static final CBStatus initCBS = new CBStatus( new int[][]{   
                                        {0, 3, 0, 3, 0, 3},
                                        {3, 0, 3, 0, 3, 0},
                                        {0, 1, 0, 1, 0, 1},
                                        {1, 0, 1, 0, 1, 0},
                                        {0, 2, 0, 2, 0, 2},
                                        {2, 0, 2, 0, 2, 0}});                                           

    /** the window displaying the game state */                                                     
    private static GameFrame gameFrame = new GameFrame(initCBS);
}






