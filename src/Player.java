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
	 * Try to move. If it's not this player's turn, it will wait until notified.
	 */		
	public void tryTurn() {
		try {
			CheckersGame.moveLock.lock();
			while(CheckersGame.getCurrentPlayer() != this || CheckersGame.isMoving()) 
				CheckersGame.moveFinished.await();				

			if(!CheckersGame.isGameover()) {
				// System.out.println("................................." + chess.toString() + " is moving...");
				move();				
			}
		} 
		catch(InterruptedException e) {
		} 
		finally {
			CheckersGame.moveLock.unlock();
		}
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
			action = Agent.findPath(CheckersGame.getCurrentState(), from, to);	
			if(action != null)
				tryTurn();
		}
	}
	
	/**
	 * Find the path between from and to, take a move, show the move on the checkerboard.
	 */
	protected void move() {
		CheckersGame.showMove(action);
		from = null;
	}

	/** the chess the current move starts from */
	private Location from;
	private Action action;		
}

class ComputerPlayer extends Player implements Runnable {
	
	/** 
	 * construct a computer player and set its role by specifying the checkers owned by this player. 
	 * @param chessColor the chess owned by this player.
	 */
	public ComputerPlayer(CheckerStatus chessColor) {
		super(chessColor);
	}
	
	@Override public void run() {
		while(true) {
			tryTurn();
		}
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