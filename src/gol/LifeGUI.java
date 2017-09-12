package gol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class LifeGUI extends JFrame implements ActionListener, KeyListener {
	
	/**
	 * Conway's Game of Life - Adrian Herath 2015
	 */
	private static final long serialVersionUID = 6683156496198903248L;
	
	@SuppressWarnings("unused")
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	@SuppressWarnings("unused")
	private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	private static final String TITLE = "Conway's Game of Life";
	private static final String STEP = "Step";
	private static final String START = "Start";
	private static final String STOP = "Stop";
	private static final String RANDOM = "Random";
	private static final String CLEAR = "Clear";
	private static final String SETTINGS = "Settings";
	private static final String BENCHMARK = "Benchmark";
	
	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(600,600);
	
	private static Color color;
	private static boolean gridOn = true;
	private static double randomChance = 0.37;
	
	private int x = 30;
	private int y = 30;
	private int threadSleep = 70;
	private boolean running = false;
	private boolean fullscreen = false;
	
	private Dimension saveWindowSize;
	private Point saveWindowLocation;
	
	private Thread thread;
	private LifeBoard board;
	private Settings settings;

	private Container userControl;
	private JButton stepBtn = new JButton(STEP);
	private JButton startStopBtn = new JButton(START);
	private JButton benchmarkBtn = new JButton(BENCHMARK);
	private JButton randomBtn = new JButton(RANDOM);
	private JButton clearBtn = new JButton(CLEAR);
	private JButton settingsBtn = new JButton(SETTINGS);

	public LifeGUI() {
		super(TITLE); 
		setSize(DEFAULT_WINDOW_SIZE);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		userControl = new Container();
		userControl.setLayout(new GridBagLayout());
		
		userControl.add(stepBtn);
		userControl.add(startStopBtn);
		userControl.add(benchmarkBtn);
		userControl.add(randomBtn);
		userControl.add(clearBtn);
		userControl.add(settingsBtn);
		
		add(userControl, BorderLayout.SOUTH);

		stepBtn.addActionListener(this);
		startStopBtn.addActionListener(this);
		benchmarkBtn.addActionListener(this);
		randomBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		settingsBtn.addActionListener(this);
		
		settings = new Settings();
		
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		board = new LifeBoard(x,y,color);
		add(board, BorderLayout.CENTER);
		board.gridOn(gridOn);
		
		startStopBtn.requestFocus();
		
		setVisible(true);

	}
	
	private void setFullscreen(boolean b){
		boolean hold = !!running;
		running = false;
		fullscreen = b;
		dispose();
		setUndecorated(fullscreen);
		if(fullscreen){
			saveWindowSize = getSize();
			saveWindowLocation = getLocation();
			setExtendedState(MAXIMIZED_BOTH);
			remove(userControl);
		} else{
			setExtendedState(0);
			setSize(saveWindowSize);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setLocation(saveWindowLocation);
			add(userControl, BorderLayout.SOUTH);
		}
		running = hold;
		setVisible(true);
	}
	
	@SuppressWarnings("serial")
	private class Settings extends JFrame implements ActionListener {
		
		private static final String WIDTH = "Width";
		private static final String HEIGHT = "Height";
		private static final String APPLY = "Apply";
		private static final String CANCEL = "Cancel";
		
		private static final String KEY_BINDINGS = ""
			+ "Key Bindings\n"
			+ "SPACE - Start/Stop\n"
			+ "C - Clear\nR - Random\n"
			+ "B - Benchmark\nUP - Step\n"
			+ "F11 - Toggle Fullscreen\n"
			+ "ESCAPE - Undo Fullscreen"
			;
		private final Dimension DEFAULT_SIZE = new Dimension(250, 325);
		
		private Container settingsContainer;
		private Container confirmContainer;
		private Container keyBindingContainer;
		
		private GridBagConstraints gbc;
		private JLabel widthLabel;
		private JLabel heightLabel;
		private JTextField widthBox;
		private JTextField heightBox;
		private JButton applyBtn;
		private JButton cancelBtn;
		private JTextArea keyBindingArea;
		
		public Settings() {
			super(SETTINGS);
			setSize(DEFAULT_SIZE);
			setResizable(false);
			setLayout(new BorderLayout());
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setLocationRelativeTo(null);
			setBackground(Color.WHITE);
			
			settingsContainer = new Container();
			settingsContainer.setLayout(new GridBagLayout());
			confirmContainer = new Container();
			confirmContainer.setLayout(new GridBagLayout());
			keyBindingContainer = new Container();
			keyBindingContainer.setLayout(new GridBagLayout());
			
			applyBtn = new JButton(APPLY);
			cancelBtn = new JButton(CANCEL);
			applyBtn.addActionListener(this);
			cancelBtn.addActionListener(this);
			
			widthLabel = new JLabel(WIDTH);
			heightLabel = new JLabel(HEIGHT);
			widthBox = new JTextField(Integer.toString(x),13);
			widthBox.setHorizontalAlignment(JTextField.RIGHT);
			heightBox = new JTextField(Integer.toString(y),13);
			heightBox.setHorizontalAlignment(JTextField.RIGHT);
			
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(10,10,10,10);
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			settingsContainer.add(widthLabel, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 0;
			settingsContainer.add(widthBox, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			settingsContainer.add(heightLabel, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 1;
			settingsContainer.add(heightBox, gbc);
			
			add(settingsContainer, BorderLayout.NORTH);
			
			confirmContainer.add(applyBtn);
			confirmContainer.add(cancelBtn);
			add(confirmContainer, BorderLayout.SOUTH);
			
			keyBindingArea = new JTextArea(KEY_BINDINGS,20,8);
			keyBindingArea.setEditable(false);
			keyBindingContainer.add(keyBindingArea);
			add(keyBindingContainer, BorderLayout.CENTER);
		}
		
		public void setVisible(boolean b){
			heightBox.setText(Integer.toString(y));
			widthBox.setText(Integer.toString(x));
			super.setVisible(b);
			applyBtn.requestFocus();
			if(running) startStop();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == applyBtn){
				x = Integer.parseInt(widthBox.getText());
				y = Integer.parseInt(heightBox.getText());
				refreshGrid();
				dispose();
			} else if(e.getSource() == cancelBtn){
				dispose();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void setGrid(int w, int h){
		board.setGrid(w, h);
	}
	
	private void benchmark(){
		board.clear();
		for (int i = 0; i < (int) board.getSize().getWidth(); i++) {
			board.setCell(i, (int) board.getSize().getHeight() / 2, true);
		}
		board.repaint();
	}
	
	private void startStop(){
		running = !running;
		if(running){
			startStopBtn.setText(STOP);
		} else{
			startStopBtn.setText(START);
		}
	}
	
	private void refreshGrid(){
		board.clear();
		board.setGrid(x, y);
		board.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		thread = new Thread() {
			public void run() {
				try {
					while (running) {
						board.nextGeneration();
						Thread.sleep(threadSleep);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();

		if (e.getSource() == stepBtn && !running) {
			board.nextGeneration();
		} else if (e.getSource() == startStopBtn) {
			startStop();
		} else if (e.getSource() == randomBtn) {
			if(running) startStop();
			board.random(randomChance);
		} else if (e.getSource() == clearBtn) {
			if(running) startStop();
			board.clear();
		} else if (e.getSource() == settingsBtn) {
			settings.setVisible(true);
		} else if (e.getSource() == benchmarkBtn && !running){
			benchmark();
		}
		requestFocus();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		thread = new Thread() { 
			public void run() {
				try {
					while (running) {
						board.nextGeneration();
						Thread.sleep(threadSleep);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
		
		int code = e.getKeyCode();
//		System.out.println(code);
		if(code == 122){
			setFullscreen(!fullscreen);
		} else if (code == 27){
			if(fullscreen) setFullscreen(false);
		} else if (code == 82){
			if(running) startStop();
			board.random(randomChance);
		} else if (code == 32){
			startStop();
		} else if (code == 67){
			if(running) startStop();
			board.clear();
		} else if (code == 66){
			if(running) startStop();
			benchmark();
		} else if (code == 38){
			board.nextGeneration();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		
		color = Color.BLUE;
		randomChance = 0.33;
		LifeBoard.circle = false;
		new LifeGUI();
	}
}
