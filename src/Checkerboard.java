import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.Timer;
import java.io.*;
import java.util.concurrent.*;
import java.util.List;

/**
 * A panel containing a grid of checkers to represent the chessboard.
 */
public class Checkerboard extends JPanel {

    /**
     * Construct a Checkerboard.
     * @param CB the states of the checkers
     */ 
    public Checkerboard(CBStatus CB) {
        initCheckers(CB);
        setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        addMouseListener(new CheckerSelectedHandler());
    }
    
    /**
     * Set up the checkerboard before starting a new game.
     * @param CB the states of the checkers
     */     
    private void initCheckers(CBStatus CB) {
        int row = CB.rows();
        int col = CB.columns();
        setLayout(new GridLayout(row, col, 2, 2));  
        checkers = new Checker[row][col];   
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {  
                checkers[i][j] = new Checker(CB.get(i,j));
                checkers[i][j].setPreferredSize(new Dimension(60, 60));
                add(checkers[i][j]);
            }
        }               
    }
    
    public void resetCheckers(CBStatus CB) {
        int row = CB.rows();
        int col = CB.columns();
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {  
                checkers[i][j].changeStatus(CB.get(i,j));
            }
        }
    }
    
    /** 
     * Return the player who gets the turn, either the human or the computer.
     * @return the player who gets the turn.
     */     
    public CBStatus getCurrentState() { 
        if(isMoving) return null;

        int x = checkers.length;
        int y = checkers[0].length;
        CBStatus CB = new CBStatus(x, y);
        for(int i = 0; i < x; i++)
            for(int j = 0; j < y; j++)
                CB.set(i, j, checkers[i][j].getStatus());       
        return CB;

    }   
    
    /** 
     * Show how a move is taken in the game window.
     * @param path the path along which the move to be shown is taken.
     */ 
    public void showMove(Action path) {
        /* starting a SwingWorker thread to show the moves on the checkerboard. */  
        CheckerMover mover = new CheckerMover(path, this);
        isMoving = true;
        mover.execute();
    }
    
    private class CheckerMover extends SwingWorker<Integer, Step>
    {
        public CheckerMover(Action path, Checkerboard cb){ this.path = path; this.cb = cb; }

        @Override public Integer doInBackground() throws IOException, InterruptedException {
            if(path == null) return 0;
            for(int i = 0; i < path.size(); i++) {
                Step s = path.get(i);
                // System.out.println("..." + s);
                publish(s);
                Thread.sleep(600);
            }
            return 0;
        }

        @Override public void process(List<Step> steps) {
            if (isCancelled()) return;
            if(steps == null) return;
            for (Step s : steps)
                cb.make1Step(s);
        }

        @Override public void done()
        {
            try{
                isMoving = false;
                CheckersGame.updateState();
            } catch (Exception ex) {}
        }

        private Action path;
        private Checkerboard cb;
    };

    private void make1Step(Step s) {
        CheckerStatus status = checkers[s.from.x][s.from.y].getStatus();
        checkers[s.from.x][s.from.y].changeStatus(CheckerStatus.EMPTY);
        checkers[s.to.x][s.to.y].changeStatus(status);
        if(s.how == HowToMove.JUMP) {
            int midX = (s.from.x + s.to.x)/2;
            int midY = (s.from.y + s.to.y)/2;
            checkers[midX][midY].changeStatus(CheckerStatus.EMPTY); 
        }
    }
    
    /**
     * Find the checker in which the specified point (x, y) stands.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return the row and column indexes of the checker if it's found, otherwise null.
     */ 
    private Location find(int x, int y) {
        Checker selected = (Checker)getComponentAt(x, y);       
        for(int i = 0; i < checkers.length; i++) 
            for(int j = 0; j < checkers[i].length; j++)     
                if (checkers[i][j] == selected) return new Location(i,j);
        return null;
    }   
    
    private class CheckerSelectedHandler extends MouseAdapter {
        
        public void mouseClicked(MouseEvent event) {
            Location selected = find(event.getX(), event.getY());
            CheckersGame.humanChooseChecker(selected, checkers[selected.x][selected.y].getStatus());            
        }       
    }
    
    public boolean isMoving() { return isMoving; }

    /** an array of checkers in the checkerboard */
    private Checker[][] checkers;       
    private boolean isMoving = false;

}



