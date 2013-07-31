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
	}	

	public void setCheckerBoard(CBStatus CB) {
		checkerboard.resetCheckers(CB);
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

	
	public boolean isMoving(){return checkerboard.isMoving();}
	
	private Checkerboard checkerboard;
	private ConfigPanel cfgPanel;

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
					// owner.resetLabels();
					CheckersGame.moveFinished.signalAll();
				}
				finally {
					CheckersGame.moveLock.unlock();
				}
			}
		});
		JPanel p = new JPanel();
		p.add(start);
		add(p);
	}
}
