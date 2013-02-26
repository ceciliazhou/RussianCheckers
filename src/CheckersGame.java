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
		
		/* start a thread representing the computer player */
		new Thread(computer).start();
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
	 * Return true if the either the human or the computer wins the game or it reaches an even case, false otherwise.
	 * @return true if the either the human or the computer wins the game or it reaches an even case, false otherwise.
	 */		
	public static boolean isGameover() { return gameover; }	

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
		try {
			CheckersGame.moveLock.lock();
			CBStatus state = gameFrame.getCurrentState();
			if(Agent.goalTest(state, currentPlayer.getChess())) {
				gameover = true;
				String msg = currentPlayer == human ? "Congratulations! You win!" : "Sorry, you lose.";
				JOptionPane.showMessageDialog(gameFrame, msg);	
				gameover = false;	
				gameFrame.setCheckerBoard(initCBS);
				gameFrame.resetLabels();
				resetPlayer(human.getChess());
			}
			else{
				currentPlayer = ((currentPlayer == human) ? computer : human); //for testing
				// System.out.println("finished a move.............." + state);
			}
			moveFinished.signalAll();
		}
		finally {
			CheckersGame.moveLock.unlock();
		}
	}
	
	public static boolean isMoving(){return gameFrame.isMoving();}
	
	public static void setText(int dep, int n, int maxP, int minP) {
		gameFrame.setText(dep, n, maxP, minP);
	}	
		
	/** game over when anyone wins the game or the players are even. */
	private static boolean gameover = false;
	
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
										// {1, 0, 1, 0, 3, 0}});	/* for test */	
	// public static final CBStatus initCBS = new CBStatus( new int[][]{
										// {0, 1, 0, 3, 0, 1},
										// {3, 0, 3, 0, 3, 0},
										// {0, 2, 0, 2, 0, 2},
										// {3, 0, 1, 0, 1, 0},
										// {0, 2, 0, 2, 0, 2},
										// {2, 0, 2, 0, 3, 0}});	/* for test */										
	public static final CBStatus initCBS = new CBStatus( new int[][]{	
										{0, 3, 0, 3, 0, 3},
										{3, 0, 3, 0, 3, 0},
										{0, 1, 0, 1, 0, 1},
										{1, 0, 1, 0, 1, 0},
										{0, 2, 0, 2, 0, 2},
										{2, 0, 2, 0, 2, 0}});											

    /** the window displaying the game state */														
	private static GameFrame gameFrame = new GameFrame(initCBS);
	public static final Lock moveLock = new ReentrantLock();
	public static final Condition moveFinished = moveLock.newCondition();;

}






