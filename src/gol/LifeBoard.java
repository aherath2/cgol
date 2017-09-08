package gol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.JPanel;

public class LifeBoard extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L;
	private static final Color DEFAULT_COLOR = new Color(15, 115, 140);
	
	protected static boolean circle;
	
	private Color cellColor;

	protected boolean[][] cellGrid; 
	private int boardWidth;
	private int boardHeight;
	private boolean gridOn;

	public LifeBoard() {
		this(3, 3);
	}

	public LifeBoard(int width, int height) {
		this(width, height, DEFAULT_COLOR);
	}

	public LifeBoard(int width, int height, Color c) {
		if(width > 2 && height > 2){
		boardWidth = width;
		boardHeight = height;
		} else{
			boardWidth = 3;
			boardHeight = 3;
		}
		setGrid(width, height);
		addMouseListener(this);
		if(c != null) cellColor = c;
		else cellColor = DEFAULT_COLOR;
		gridOn = true;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g); 
		Graphics2D g2 = (Graphics2D) g;

		double cellWidth =  (this.getWidth() / (double) boardWidth);
		double cellHeight = (this.getHeight() / (double) boardHeight);

		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				if(gridOn){
					g2.setColor(Color.BLACK);
					g2.drawLine((int) (x * cellWidth), 0,(int) (x * cellWidth), (int) ((y+1) * cellHeight));
					g2.drawLine(0, (int) (y * cellHeight),(int) ((x+1) * cellWidth), (int) (y * cellHeight));
				}
				if(circle) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (cellGrid[x][y]) {
					g2.setColor(cellColor);
					if(circle){
						g2.fillOval((int) (x * cellWidth), (int) (y * cellHeight), (int) (cellWidth), (int) (cellHeight));
					} else{
						int[] xs = {(int)(x * cellWidth),(int)((x+1) * cellWidth),(int)((x+1) * cellWidth),(int)(x * cellWidth)};
						int[] ys = {(int) (y * cellHeight),(int) (y * cellHeight),(int) ((y+1) * cellHeight),(int) ((y+1) * cellHeight)};
						g2.fillPolygon(xs, ys, 4);
					}
				}
			}
		}
	}

	public void nextGeneration() {
		boolean[][] nextGenGrid = new boolean[boardWidth][boardHeight];
		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				nextGenGrid[x][y] = aliveNextGen(x, y);
			}
		}
		cellGrid = nextGenGrid;
		repaint();
	}

	private byte getNeighborCount(int x, int y) {
													
		byte count = 0;
		if (x > 0 && y > 0 && cellGrid[x - 1][y - 1]) {
			count++;
		}
		if (x > 0 && cellGrid[x - 1][y]) {
			count++;
		}
		if (x > 0 && y < boardHeight - 1 && cellGrid[x - 1][y + 1]) {
			count++;
		}
		if (y > 0 && cellGrid[x][y - 1]) {
			count++;
		}
		if (y < boardHeight - 1 && cellGrid[x][y + 1]) {
			count++;
		}
		if (x < boardWidth - 1 && y > 0 && cellGrid[x + 1][y - 1]) {
			count++;
		}
		if (x < boardWidth - 1 && cellGrid[x + 1][y]) {
			count++;
		}
		if (x < boardWidth - 1 && y < boardHeight - 1 && cellGrid[x + 1][y + 1]) {
			count++;
		}
		return count;

	}

	private boolean aliveNextGen(int x, int y) {
		if (getNeighborCount(x, y) == 3) {
			return true;
		} else if (getNeighborCount(x, y) == 2) {
			return cellGrid[x][y];
		} else {
			return false;
		}
	}

	public void clear() {
		for (boolean[] bArray : cellGrid) {
			for (int i = 0; i < bArray.length; i++) {
				bArray[i] = false;
			}
		}
		repaint();
	}

	public void random(double chance) {
		clear();
		for (boolean[] bArray : cellGrid) {
			for (int i = 0; i < bArray.length; i++) {
				bArray[i] = chance >= Math.random();
			}
		}

		repaint();

	}

	public void setGrid(int width, int height) {
		if(width > 2 && height > 2){
		boardWidth = width;
		boardHeight = height;
		cellGrid = new boolean[width][height];
		repaint();
		}
	}

	public void gridOn(boolean b){
		gridOn = b;
	}
	
	public boolean gridIsOn(){
		return gridOn;
	}

	public void setCell(int x, int y, boolean state) {
		cellGrid[x][y] = state;
		repaint();
	}
	
	public boolean getCell(int x, int y){
		return cellGrid[x][y];
	}
	
	public Dimension getSize(){
		return new Dimension(boardWidth, boardHeight);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int row = (int) (e.getX() / (this.getWidth() / (double) boardWidth));
		int col = (int) (e.getY() / (this.getHeight() / (double) boardHeight));
		cellGrid[row][col] = !cellGrid[row][col];
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}
}