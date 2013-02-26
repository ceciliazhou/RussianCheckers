import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
	
/**
 * An enum type to represent the status of a checker.
 * UNAVAILABLE -- not available to seat a chess.
 * EMPTY -- unoccupied dark checker.
 * BLACK -- dark checker occupied by black piece.
 * WHITE -- dark checker occupied by white piece.
 */
enum CheckerStatus { UNAVAILABLE, EMPTY, BLACK,	WHITE };

/**
 * A component to represent a checker.
 * A checker is either occupialbe or not. 
 * Available checkers will be drawn in light background color, while unoccupiable checkers in dark background color.
 * An available checker can be filled with a white or black chess or left empty.
 */
public class Checker extends JComponent{
	/**
	 * Construct a Checker.
	 * @param status 
	 */
	public Checker(CheckerStatus status){
		this.status = status;
	}

	/** 
	 * Paint the checker.
	 * If the checker is currently selected, it will be highlighted by drawing a blue border.
	 * If the checker is filled with a chess, the chess will be drawn as a filled circle.
	 * @param g the Graphics object to protect
	 */	
	@Override protected void paintComponent(Graphics g){
	
		/* draw the background */
		Graphics2D g2 = (Graphics2D) g;
		Color boardColor = status == CheckerStatus.UNAVAILABLE ? Color.WHITE : Color.GRAY;
		g2.setColor(boardColor);
		g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
		
		/* draw a border if selected */
		if(highlighted) 
			setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLUE));
		else 
			setBorder(null);
		
		/* draw a piece of chess if occupied */
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	
		if(status == CheckerStatus.BLACK){
			g2.setColor(Color.BLACK);
			g2.fillOval(0+10, 0+10, getWidth()-20, getHeight()-20);
		}
		else if(status == CheckerStatus.WHITE){
			g2.setColor(Color.WHITE);
			g2.fillOval(0+10, 0+10, getWidth()-20, getHeight()-20);
		}			
	}
	
	/**
	 * Turn on/off the highlightness.
	 * @param highlighted true to turn on highlightness or false to turn off.
	 */
	public void highlight(boolean highlighted) {
		this.highlighted = highlighted;
		repaint();
	}

	/**
	 * Return the status of the checker.
	 * @return the status of the checker. It can be UNAVAILABLE, EMPTY, BLACK or WHTTE.
	 */	
	public CheckerStatus getStatus(){ return status; }
		
	/**
	 * Set the checker empty or occupied by a chess and repaint the checker.
	 * @param status The new status to be set to the checker. It can be EMPTY, BLACK or WHTTE.
	 */	
	public void changeStatus(CheckerStatus status){
		if(status == CheckerStatus.UNAVAILABLE) return;
		this.status = status;
		repaint();
	}
	
	/** current status of the checker, can be UNAVAILABLE, EMPTY, BLACK or WHTTE. */
	private CheckerStatus status;
	/** highlight the checker when it's selected. */
	private boolean highlighted = false;


}
