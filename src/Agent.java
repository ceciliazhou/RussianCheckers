import java.util.*;
import java.awt.*;

/**
 * Agent provides a couple of static methods for a game to compute a best move 
 * or find a legal full path between two given locations and so on.
 */
public class Agent {
	private static int cutoffDepth = 5; /* the depth on which a cutoff is triggered. */
	static int nodeGenerated = 0; /* number of node generated in a search */
	static int maxDepth = 0; /* the max depth a search goes to, at most equal to cutoffDepth. */

	/**
	 * An enum representing the game level.
	 */	
	public enum HardLevel{
		BEGINNER(5), EASY(9), ADVANCED(13), HARD(15);
		private int code;  
		private HardLevel(int code) { this.code = code; }     
		public int getCode() { return code; }  
	}
	
	/**
	 * Set the game level.
	 * @param level how hard the game is. Could be BEGINNER, EASY, ADVANCED or HARD.
	 */
	public static void setHardLevel(HardLevel level){ cutoffDepth = level.getCode(); }
	/**
	 * Return true if the checkerboard status represents a goal for the color, false otherwise.
	 * A goal means either the opponent has no legal move or no checkers at all.
	 * @param state a matrix of CheckerStatus representing the current status of the checkerboard.
	 * @param color the color of checkers owned by the player.
	 * @return true if the checkerboard status represents a goal for the color, false otherwise.
	 */
	public static boolean goalTest(CBStatus state, CheckerStatus color) {
		CheckerStatus other = color == CheckerStatus.WHITE ? CheckerStatus.BLACK : CheckerStatus.WHITE;
		for(int i = 0; i < state.rows(); i++)
			for(int j = 0; j < state.columns(); j++) 
				if(state.get(i, j) == other && isMovable(state, new Location(i,j))) 
					return false;
		return true;
	}

	/**
	 * Compute and return a best move for the given player based on the given checkerboard status.
	 * @param state a matrix of CheckerStatus representing the current status of the checkerboard.
	 * @param rep the color of checkers owned by the player.
	 * @return a best move for the given player based on the given checkerboard status.
	 */	
	public static SearchResult bestMove(CBStatus state, CheckerStatus rep) {
		nodeGenerated = 0;
		maxDepth = 0;
		return maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
	}
	
	/**
	 * This class represent a collection of infomation about a search result.
	 * action -- an actoin representing a best move.
	 * value -- how much is the move evaluated.
	 * maxPruning -- number of times pruning occurs in a maxValue function.
	 * minPruning -- number of times pruning occurs in a minValue function.
	 */		
	public static class SearchResult{
		public Action action;
		public int value;
		public int maxPruning;
		public int minPruning;
		public SearchResult(Action a, int v, int p1, int p2) {
			action = a;
			value = v;
			maxPruning = p1;
			minPruning = p2;
		}
		public SearchResult(){}
		
		/* for test */
		public String toString(){
			StringBuilder sb = new StringBuilder("Action:\n");
			if(action != null)
				for(int i = 0; i < action.size(); i++) {
					Step s = action.get(i);
					if(s != null)
						sb.append("\t" + s.toString() + "\n");
				}
			sb.append("value: " + value);
			sb.append("\nmaxPruning: " + maxPruning);
			sb.append("\nminPruning: " + minPruning);
			return sb.toString();
		}
	}

	/**
	 * Compute and return the value of the best choice for the current player found in the search tree rooted in the given state.
	 * @param state a matrix of CheckerStatus representing the current status of the checkerboard.
	 * @param alpha the value of the best choice found so far at any choice point along the path for MAX.
	 * @param beta the value of the best choice found so far at any choice point along the path for MIN.
	 * @param depth the depth at which this procedure is called.
	 * @return the value of the best choice for the current player found in the search tree rooted in the given state.
	 */	
	private static SearchResult maxValue(CBStatus state, int alpha, int beta, int depth) {
		// System.out.println("MAX----MAX----depth: [ " +depth +" ]\n"+state);
		nodeGenerated++;
		if(cutoffTest(state, depth)) 
			return new SearchResult(null, evaluate(state), 0, 0);
		
		CheckerStatus rep = CheckersGame.getCurrentPlayer().getChess();
		SearchResult result = new SearchResult(null, Integer.MIN_VALUE, 0, 0);
		Action action = possibleActions(state, rep);
		try {
			for(int i = 0; i < action.size(); i++) {
				Step step = action.get(i);
				if(step == null) continue;
				CBStatus nextState = state.clone();			
				Action path = findPath(nextState, step.from, step.to);
				// System.out.println(nextState);
				SearchResult minRes = minValue(nextState, alpha, beta, depth+1);
				result.maxPruning += minRes.maxPruning;
				result.minPruning += minRes.minPruning;
				if(result.value < minRes.value) {
					result = minRes;
					result.action = path;
				}
				if(result.value >= beta) {
					result.maxPruning++;
					return result;
				}
				alpha = alpha > result.value ? alpha : result.value;
			}
		} catch (CloneNotSupportedException e) {};
		return result;
	}

	/**
	 * Compute and return the value of the best choice for the opponent found in the search tree rooted in the given state.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param alpha the value of the best choice found so far at any choice point along the path for MAX.
	 * @param beta the value of the best choice found so far at any choice point along the path for MIN.
	 * @param depth the depth at which this procedure is called.
	 * @return the value of the best choice for the current found in the search tree rooted in the given state.
	 */	
	private static SearchResult minValue(CBStatus state, int alpha, int beta, int depth) {
		// System.out.println("MIN----MIN----depth: " +depth +"\n"+ state);
		nodeGenerated++;
		if(cutoffTest(state, depth)) 
			return new SearchResult(null, evaluate(state), 0, 0);

		CheckerStatus rep = CheckersGame.getCurrentPlayer().getChess();
		rep = rep == CheckerStatus.WHITE ? CheckerStatus.BLACK : CheckerStatus.WHITE;			
		SearchResult result = new SearchResult(null, Integer.MAX_VALUE, 0, 0);
		Action action = possibleActions(state, rep);
		try {
			for(int i = 0; i < action.size(); i++) {
				Step step = action.get(i);
				if(step == null) continue;
					CBStatus nextState = state.clone();	
					Action path = findPath(nextState, step.from, step.to);
					// System.out.println(nextState);
					rep = rep == CheckerStatus.WHITE ? CheckerStatus.BLACK : CheckerStatus.WHITE;
					SearchResult maxRes = maxValue(nextState, alpha, beta, depth+1);
					result.maxPruning += maxRes.maxPruning;
					result.minPruning += maxRes.minPruning;
					if(result.value > maxRes.value) {
						result = maxRes;
						result.action = path;
					}
					if(result.value <= alpha) {
						result.minPruning++;
						return result;
					}
					beta = beta < result.value ? beta : result.value;
			}
		} catch (CloneNotSupportedException e) {};
		return result;
	}

	/**
	 * Test and return true if the given checkerboard status represents a goal of the current player 
	 * or it reaches to the cutoff level in a search, false otherwise.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param depth the depth at which this procedure is called.
	 * @return true if the given checkerboard status represents a goal of the current player 
	 * or it reaches to the cutoff level in a search, false otherwise.
	 */		
	private static boolean cutoffTest(CBStatus state, int depth) {
		if(maxDepth < depth){ maxDepth = depth;	}
		return (depth == cutoffDepth || goalTest(state, CheckerStatus.WHITE) || goalTest(state, CheckerStatus.BLACK));
	}

	/**
	 * Compute and return the value of the given state for the current player.
	 * If the state is a goal state for the current player, it returns a MAX value (1000).
	 * If the state is a goal state for the opponent player, it returns a MIN value (-1000).
	 * Otherwise, it returns an estimated value resulted as a weighted sum based on <br/>
	 *  -- the difference of numbers of chesses currently owned by the two players. <br/>
	 *  -- the difference of numbers of possible moves can be taken by the two players. <br/>
	 *  -- the difference of numbers of possible jumps can be taken by the two players. <br/>
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @return the value of the given state for the current player.
	 */		
	private static int evaluate(CBStatus state) {
		CheckerStatus rep = CheckersGame.getCurrentPlayer().getChess();
		CheckerStatus other = rep == CheckerStatus.WHITE ? CheckerStatus.BLACK : CheckerStatus.WHITE;
		if(goalTest(state, rep)) return 1000;
		if(goalTest(state, other)) return -1000;

		int deltaChess = 0;
		int deltaMoves = 0;
		int deltaJumps =0;
		for(int i = 0; i < state.rows(); i++) {
			for(int j = 0; j < state.columns(); j++) {
				if(state.get(i,j) == rep){
					deltaChess += 1;
					Action moves = possibleMoves(state, new Location(i,j));
					Action jumps = possibleJumps(state, new Location(i,j));
					if(moves != null) deltaMoves += moves.size();
					if(jumps != null) deltaJumps += jumps.size();
				}
				else if(state.get(i,j) == other){
					deltaChess -= 1;
					Action moves = possibleMoves(state, new Location(i,j));
					Action jumps = possibleJumps(state, new Location(i,j));
					if(moves != null) deltaMoves -= moves.size();
					if(jumps != null) deltaJumps -= jumps.size();
				}
			}
		}
		// System.out.println("value: " + (8*deltaChess + deltaMoves + 10*deltaJumps) +"\tdeltaChess: " + deltaChess + "\tdeltaMoves: " + deltaMoves + "\tdeltaJumps: " + deltaJumps);
		return 8*deltaChess + deltaMoves + 10*deltaJumps;
	}

	/**
	 * Find and return the full path starting at from and go next to to. 
	 * Extensive path may be availabe if it takes a jump and more jumps are availbe from to.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param from the location to start with.
	 * @param to the immediate next location targeted.
	 * @return a full path starting at from and go next to to. 
	 */		
	public static Action findPath(CBStatus state, Location from, Location to) {
		/* find the path between from and to */
		HowToMove how = moveTo(state, from, to);
		if(how == HowToMove.NONE) return null;
		Step step = new Step(from, to, how);
		
		/* build a full path and the resulting state */
		Action result = new Action();
		result.add(step);
		makeAMove(state, step);	
		if(step.how == HowToMove.JUMP) {
			/* take every possible chance to jump */
			Action moves = possibleJumps(state, to);
			while(moves != null && moves.size() > 0) {
				step = moves.get(0);
				result.add(step);
				makeAMove(state, step); 
				moves = possibleJumps(state, step.to);
			}
		}		
		return result;
	}

	/**
	 * Compute the successor state by taking an action of step on state.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param step a single step to be taken.
	 */			
	private static void makeAMove(CBStatus state, Step step){
		// //System.out.println(step);
		state.set(step.to, state.get(step.from));
		state.set(step.from, CheckerStatus.EMPTY);
		if(step.how == HowToMove.JUMP) 
			state.set((step.from.x + step.to.x)/2, (step.from.y + step.to.y)/2, CheckerStatus.EMPTY);
	}
	
	/**
	 * Return true if there is any possible move/jump can be made starting at from, false otherwise.
	 * @return true if there is any possible move/jump can be made starting at from, false otherwise.
	 */	
	private static boolean isMovable(CBStatus state, Location from) {
		return canMove(state, from) || canJump(state, from);
	}

	/**
	 * Return true if there is any possible move can be made starting at from, false otherwise.
	 * @return true if there is any possible move can be made starting at from, false otherwise.
	 */	
	private static boolean canMove(CBStatus state, Location from) {
		if(!state.contains(from)) return false;
		CheckerStatus self = state.get(from);
		if(self == CheckerStatus.UNAVAILABLE || self == CheckerStatus.EMPTY) return false;
		/* can move forward only */
		int[][] moveDistance = self == CheckerStatus.BLACK ? new int[][]{{-1, -1}, {-1, 1}} : new int[][]{{1, -1}, {1, 1}};
		for(int[] m:moveDistance) {
			Location to = new Location(from.x + m[0], from.y + m[1]);
			if(state.contains(to) && state.get(to) == CheckerStatus.EMPTY)
				return true;
		}
		return false;
	}

	/**
	 * Return true if there is any possible jump can be made starting at from, false otherwise.
	 * @return true if there is any possible jump can be made starting at from, false otherwise.
	 */	
	private static boolean canJump(CBStatus state, Location from) {
		if(!state.contains(from)) return false;
		CheckerStatus self = state.get(from);
		if(self == CheckerStatus.UNAVAILABLE || self == CheckerStatus.EMPTY) return false;
		/* can jump forward or backword */
		CheckerStatus other = self == CheckerStatus.BLACK ? CheckerStatus.WHITE : CheckerStatus.BLACK;
		int[][] jumpDistance = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
		for(int[] j:jumpDistance) {
			Location to = new Location(from.x + j[0], from.y + j[1]);
			Location mid = new Location(from.x + j[0]/2, from.y + j[1]/2);
			if(state.contains(to) && state.get(to) == CheckerStatus.EMPTY && state.get(mid) == other)
				return true;
		}
		return false;
	}

	/**
	 * Compute and return all possible jumps starting at from in the given state.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param from the location to start with.
	 * @return all possible jumps starting at from in the given state.
	 */		
	private static Action possibleJumps(CBStatus state, Location from) {
		if(!state.contains(from)) return null;
		CheckerStatus self = state.get(from);
		if(self == CheckerStatus.UNAVAILABLE || self == CheckerStatus.EMPTY) return null;
		
		/* can jump forward or backword */
		Action steps = new Action();
		CheckerStatus other = self == CheckerStatus.BLACK ? CheckerStatus.WHITE : CheckerStatus.BLACK;
		int[][] jumpDistance = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
		for(int[] j:jumpDistance) {
			Location to = new Location(from.x + j[0], from.y + j[1]);
			Location mid = new Location(from.x + j[0]/2, from.y + j[1]/2);
			if(state.contains(to) && state.get(to) == CheckerStatus.EMPTY && state.get(mid) == other)
				steps.add(new Step(from, to, HowToMove.JUMP));
		}	
		return steps;
	}

	/**
	 * Compute and return all possible moves starting at from in the given state.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param from the location to start with.
	 * @return all possible moves starting at from in the given state.
	 */		
	private static Action possibleMoves(CBStatus state, Location from) {
		if(!state.contains(from)) return null;
		CheckerStatus self = state.get(from);
		if(self == CheckerStatus.UNAVAILABLE || self == CheckerStatus.EMPTY) return null;
		
		/* can move forward only */
		Action steps = new Action();
		int[][] directions = self == CheckerStatus.BLACK ? new int[][]{{-1, -1}, {-1, 1}} : new int[][]{{1, -1}, {1, 1}};
		for(int[] d:directions) {
			Location to = new Location(from.x + d[0], from.y + d[1]);
			if(state.contains(to) && state.get(to) == CheckerStatus.EMPTY)
				steps.add(new Step(from, to, HowToMove.MOVE));
		}		
		return steps;
	}

	/**
	 * Compute and return how to move from from to to.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param from the location to start with.
	 * @param to the immediate next location targeted.
	 * @return how to move from from to to. Either NONE, MOVE or JUMP.
	 */			
	private static HowToMove moveTo(CBStatus state, Location from, Location to) {
		if(state.get(to) != CheckerStatus.EMPTY) return HowToMove.NONE;

		CheckerStatus self = state.get(from);
		CheckerStatus other = self == CheckerStatus.WHITE ? CheckerStatus.BLACK : CheckerStatus.WHITE;
		int x = to.x - from.x;
		int y = to.y - from.y;
		int direction = self == CheckerStatus.BLACK ? -1 : 1;
		if(x == direction && y*y == 1)
			return HowToMove.MOVE;
		else if(x*x == 4 && y*y == 4 && state.get((from.x+to.x)/2, (from.y+to.y)/2) == other)
			return HowToMove.JUMP;
		else 
			return HowToMove.NONE;
	}

	/**
	 * Compute and return all possible actions can be made by rep in the given state.
	 * @param state a matrix of CheckerStatus representing the opponent status of the checkerboard.
	 * @param rep checker status representing the player.
	 * @return all possible actions can be made by rep in the given state.
	 */			
	private static Action possibleActions(CBStatus state, CheckerStatus rep) {
		Action action = new Action();
		for(int i = 0; i < state.rows(); i++) {
			for(int j = 0; j < state.columns(); j++) {
				if(state.get(i,j) == rep){
					action.combine(possibleMoves(state, new Location(i,j)));
					action.combine(possibleJumps(state, new Location(i,j)));
				}
			}
		}
		return action;		
	}
}

/**
 * A simple class used to represent the row index and column index of a cell in a matrix.
 * x -- index of row.
 * y -- index of column.
 */	
class Location {
	public int x;
	public int y;
	public Location(int x, int y) { this.x = x; this.y = y;}

	@Override public boolean equals(Object otherObj) {
		if(otherObj == this) return true;
		if(otherObj == null) return false;
		if(getClass() != otherObj.getClass()) return false;
		Location other = (Location) otherObj;
		return x == other.x && y == other.y;
	}
	
	public String toString(){ return String.format("(%d, %d)", x, y);}
}

/**
 * An enum type to represent the method a move can be taken from a checker to another.
 * NONE  -- Cannot happen.
 * MOVE  -- Move from the starting checker forward to the target empty checker.
 * WHITE -- Jump from the starting checker over a checker ocuupied by the opponent
 *          and land onto the target empty checker.
 */
enum HowToMove { NONE, MOVE, JUMP }

class Step {
	public HowToMove how;
	public Location from;
	public Location to;	
	public Step(Location from, Location to, HowToMove how) {
		this.from = from;
		this.to = to;
		this.how = how;
	}
	
	/* for testing */
	public String toString() {
		return String.format(how.toString() + " from (" + from.x + ", " + from.y + ") to (" + to.x + ", " + to.y + ")");
	}
}

/** 
 * A matrix of checkers status representing the checkboard status.
 */
class CBStatus implements Cloneable{
	private CheckerStatus[][] CB;

	public CBStatus(int x, int y) {
		CB = new CheckerStatus[x][y];
	}

	public CBStatus(int[][] array) {
		int x = array.length;
		int y = array[0].length;
		CB = new CheckerStatus[x][y];
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				CB[i][j] = CheckerStatus.values()[array[i][j]];
	}

	@Override public CBStatus clone() throws CloneNotSupportedException {
		CBStatus cb = (CBStatus)super.clone();
		cb.CB = CB.clone();
		for(int i = 0; i < cb.CB.length; i++)
			cb.CB[i] = CB[i].clone();
		return cb;
	}

	/* for test */
	public String toString(){
		StringBuilder sb = new StringBuilder("------------------------\n");
		for(CheckerStatus[] row:CB) {
			sb.append("|");
			for(CheckerStatus cell:row){
				if(cell == CheckerStatus.WHITE)
					sb.append("  O");
				else if(cell == CheckerStatus.BLACK)
					sb.append("  @");
				else 
					sb.append("  -");
			}
			sb.append("  |\n");
		}
		return sb.toString();
	}
	
	public int rows() {return (CB!=null) ? CB.length : 0;}
	public int columns() {return ( CB!=null && CB.length != 0 )? CB[0].length : 0;}
	
	public CheckerStatus get(Location loc) { return CB[loc.x][loc.y]; }
	public CheckerStatus get(int x, int y) { return CB[x][y]; }
	public void set(Location loc, CheckerStatus s) { CB[loc.x][loc.y] = s; }	
	public void set(int x, int y, CheckerStatus s) { CB[x][y] = s; }	

	public boolean contains(Location loc) { return loc.x >= 0 && loc.x < rows() && loc.y >= 0 && loc.y < columns(); }
}

/**
 * An action is an array of steps. 
 * It can be used to represent a full path in which each contiguous step has a from same as the previous step's to. 
 * It can be also used to represent a series of non-related steps.
 */
class Action {
	private ArrayList<Step> path = new ArrayList<Step>();
	public void add(Step s){ path.add(s); }
	public int size(){ return path.size(); }
	public Step get(int i){ return i<path.size() && i>=0 ? path.get(i) : null;}

	/**
	 * Concatinate another action with this one.
	 * @param another the other action to be concatinated.
	 */	
	public void combine(Action another){
		if(another == null) return;
		for(int i = 0; i < another.size(); i++)
			path.add(another.get(i));
	}
	
	/* for test */
	public String toString(){
		if(path.size()==0) return "";
		String str = "";
		for(Step s:path)
			str = str+s.toString();
		return str;
	}
}


