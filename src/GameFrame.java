import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class GameFrame extends JFrame {
	/**
	 * Construct a frame to start playing game.
	 * @param CB a matrix representing the status of the checkerboard.
	 */
	public GameFrame(CBStatus CB) {
		setTitle("Russian Checkers Game");
		cfgPanel = new ConfigPanel(this);
		add(cfgPanel, BorderLayout.EAST);	
		checkerboard = new Checkerboard(CB);
		add(checkerboard, BorderLayout.CENTER);		
		infoPanel = new GameInfoPanel();
		add(infoPanel, BorderLayout.SOUTH);	
	}	

	public void setCheckerBoard(CBStatus CB) {
		checkerboard.resetCheckers(CB);
	}
	
	public void resetLabels() {
		infoPanel.reset();
	}

	/** 
	 * Return the player who gets the turn, either the human or the computer.
	 * @return the player who gets the turn.
	 */		
	public CBStatus getCurrentState() { 
		return checkerboard.getCurrentState();
	}
	
	/** 
	 * Show how a move is taken in the game window.
	 * @param path the path along which the move to be shown is taken.
	 */	
	public void showMove(Action path) {
		checkerboard.showMove(path);
	}	

	public void setText(int dep, int n, int maxP, int minP) {
		infoPanel.setText(dep, n, maxP, minP);
	}	
	
	public boolean isMoving(){return checkerboard.isMoving();}
	
	private Checkerboard checkerboard;
	private ConfigPanel cfgPanel;
	private GameInfoPanel infoPanel; // to be done

}


/**
 * A Panel containing two radio buttons to choose black chess or white one.
 * @param owner The GameFrame which contains this panel.
 */
class ConfigPanel extends JPanel{

	public ConfigPanel(final GameFrame owner){

		setLayout(new GridLayout(10, 1));
		/* construct two radio buttons: white and black */
		ButtonGroup g1 = new ButtonGroup();
		final JRadioButton black = new JRadioButton("Black", true);
		final JRadioButton white = new JRadioButton("White", false);
		g1.add(black);
		g1.add(white);
		add(black);
		add(white);
		add(new JLabel());
		
		/* construct two radio buttons: white and black */
		ButtonGroup g2 = new ButtonGroup();
		final JRadioButton beginner = new JRadioButton("beginner", true);
		final JRadioButton easy = new JRadioButton("easy", false);
		final JRadioButton medium = new JRadioButton("medium", true);
		final JRadioButton hard = new JRadioButton("hard", false);
		g2.add(beginner);
		g2.add(easy);
		g2.add(medium);
		g2.add(hard);
		add(beginner);
		add(easy);
		add(medium);
		add(hard);
		add(new JLabel());

		/* construct a button used to start the game or rechoose the color of the chess for the human */
		final JButton start = new JButton("New Game");
		start.setPreferredSize(new Dimension(100, 30));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					CheckersGame.moveLock.lock();
					CheckerStatus human = black.isSelected() ? CheckerStatus.BLACK : CheckerStatus.WHITE;
					CheckersGame.resetPlayer(human);
					
					if(beginner.isSelected()) Agent.setHardLevel(Agent.HardLevel.BEGINNER); // to be done
					else if(easy.isSelected()) Agent.setHardLevel(Agent.HardLevel.EASY);
					else if(medium.isSelected()) Agent.setHardLevel(Agent.HardLevel.ADVANCED);
					else Agent.setHardLevel(Agent.HardLevel.HARD);
					
					owner.setCheckerBoard(CheckersGame.initCBS);
					owner.resetLabels();
					CheckersGame.moveFinished.signalAll();
				}
				finally {
					CheckersGame.moveLock.unlock();
				}
			}
		});
		add(start);
	}
}

/**
 * A Panel containing  
 * to be done
 * @param owner The GameFrame which contains this panel.
 */
class GameInfoPanel extends JPanel{

	public GameInfoPanel(){
		setLayout(new GridLayout(2, 2));
		add(depth);
		add(maxPruning);
		add(nodes);
		add(minPruning);
	}
	
	public void setText(int dep, int n, int maxP, int minP) {
		depth.setText("MaxDepth/cutoff:                 "+dep);
		nodes.setText("# of generated nodes:        "+n);
		maxPruning.setText("# of pruning in MAX_VALUE:     "+maxP);
		minPruning.setText("# of pruning in MIN_VALUE:     "+minP);
	}
	
	public void reset(){
		depth.setText("MaxDepth/cutoff:                 ");
		nodes.setText("# of generated nodes:        ");
		maxPruning.setText("# of pruning in MAX_VALUE:     ");
		minPruning.setText("# of pruning in MIN_VALUE:     ");	
	}
	
	private JLabel depth = new JLabel("MaxDepth/cutoff:                 ");
	private JLabel nodes = new JLabel("# of generated nodes:        ");
	private JLabel maxPruning = new JLabel("# of pruning in MAX_VALUE:     ");
	private JLabel minPruning = new JLabel("# of pruning in MIN_VALUE:     ");
}