import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RunGameOfLife extends JFrame implements ActionListener, ChangeListener {
	
	 //creating controls
	 private JRadioButton start = new JRadioButton("Start");
	 private JRadioButton stop = new JRadioButton("Stop");
	 private static JButton reset = new JButton("Reset/Randomize");
	 private JSlider speed = new JSlider(JSlider.VERTICAL, 0, 2000, 1000);
	 private static JSlider gridSize = new JSlider(JSlider.VERTICAL, 10, 80, 20);
	 
	//creating a timer
	private Timer timer = new Timer(speed.getValue(), this);
	private JLabel steps = new JLabel("Steps: 0 ");
	private int counter;
    
	//creating the grid 
	static int [][] gridValues = new int[gridSize.getValue()][gridSize.getValue()];
	
	//will determine whether the board should be randomized or follow game rules
	static int state = -1;
	static int source;

	
	//makes frame, starts graphics
	public static void main(String[] args) {
		RunGameOfLife frame = new RunGameOfLife();
		
		frame.setSize(600,600);
		frame.setVisible(true);
		reset.doClick();
		generateValues(-1);
		
	}
	
	public RunGameOfLife () {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0,0));
		setContentPane(contentPane);
		
		GameOfLifeGraphics panel = new GameOfLifeGraphics();
		panel.setBackground(Color.WHITE);
		contentPane.add(panel, BorderLayout.CENTER);
		
		JPanel topBar = topBar();
		contentPane.add(topBar, BorderLayout.NORTH);
		
		JPanel leftBar = leftBar();
		contentPane.add(leftBar, BorderLayout.WEST);
	}
	
	//creating a JPanel on top of the screen with controls
	private JPanel topBar() {
		
		//creating a top bar with controls
		JPanel topBar = new JPanel();
		Color myPurp = new Color(191, 123, 123);
		topBar.setBackground(myPurp);
		
		//reset button with listener
		topBar.add(reset);
		reset.addActionListener(this);
		
		//creating button that starts simulation
		start.setSelected(false);
		start.addActionListener(this);
		topBar.add(start);
		
		//creating button that stops simulation
		stop.setSelected(true);
		stop.addActionListener(this);
		topBar.add(stop);
		
		//button group to ensure you cannot start and stop at same time
		ButtonGroup startStop = new ButtonGroup();
		startStop.add(start);
		startStop.add(stop);
		
		//creating label that shows # of steps
		steps.setHorizontalAlignment(JLabel.CENTER);
		topBar.add(steps);
		
		
		return topBar;
	}
	
	private JPanel leftBar() {
		
		//creating a left bar with controls
		JPanel leftBar = new JPanel();
		Color myPurp = new Color(191, 123, 123);
		leftBar.setBackground(myPurp);
		leftBar.setLayout(new BoxLayout(leftBar, BoxLayout.Y_AXIS));
		
		//creating slider that determines speed
		JLabel milliSecs = new JLabel("ms between", JLabel.LEFT);
		JLabel generations = new JLabel("generations", JLabel.LEFT);
		speed.addChangeListener(this);
		speed.setPaintTicks(true);
		speed.setMajorTickSpacing(500);
		speed.setPaintLabels(true);
		speed.setMinorTickSpacing(250);
		leftBar.add(milliSecs);
		leftBar.add(generations);
		leftBar.add(speed);
		
		//creating slider that determines grid size
		JLabel dimensionsLabel = new JLabel("dimensions");
		gridSize.addChangeListener(this);
		gridSize.setPaintTicks(true);
		gridSize.setMajorTickSpacing(10);
		gridSize.setPaintLabels(true);
		leftBar.add(dimensionsLabel);
		leftBar.add(gridSize);
		pack();
		
		return leftBar;
	}
	
	//action listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//start button starts timer
		if (e.getSource() == start) {
			timer.start();
		}
		//stop button stops timer
		if (e.getSource() == stop) {
			timer.stop();
		}
		
		//follows rule of game and repaints board
		if(e.getSource() == timer) {
			SetState(1);
			generateValues(source);
			counter++;
			steps.setText("Steps: " + counter);
			repaint();
		}
		
		//randomizes board, stops game and timer
		if (e.getSource() == reset) {
			timer.stop();
			SetState(-1);
			generateValues(source);
			repaint();
			counter = 0;
			steps.setText("Steps: " + counter);
			stop.setSelected(true); 
		}
	}
	
	//state change listeners for JSlider
	@Override
	public void stateChanged(ChangeEvent e) {
		//changes speed of animation
		if (e.getSource() == speed) {
			timer.stop();
			timer.setDelay(speed.getValue());
			stop.setSelected(true);
		}
		//changes # of boxes
		if (e.getSource() == gridSize) {
			timer.stop();
			stop.setSelected(true);
			int [][] newGrid = new int [gridSize.getValue()][gridSize.getValue()];
			gridValues = newGrid;
			SetState(-1);
			generateValues(source);
			counter = 0;
			steps.setText("Steps: " + counter);
			repaint();
		}
	}
		/*
		 * method that generates the grid of values, either randomly
		 * or derived from the previous board following the rules of 
		 * the game
		 */
		public static int [][] generateValues (int sources) {
			Random rand = new Random();
			int [][] tempGridValues = new int[gridSize.getValue()][gridSize.getValue()]; //holds new values temporarily
			
			//random 2D array of either ones and zeros
			if (sources == -1) {
				for (int i = 0; i < gridSize.getValue(); i++ ) {
					for (int j = 0; j < gridSize.getValue(); j++) {
						gridValues[i][j] = rand.nextInt(2);
					}
				}
				return gridValues;
			}
			//assigns new grid values based on game rules
			else {
				for (int i = 0; i < gridSize.getValue(); i++ ) {
					for (int j = 0; j < gridSize.getValue(); j++) {
						int alivePoints = checkAlive(i, j);
						if (gridValues[i][j] == 0 && alivePoints == 3) {
							tempGridValues[i][j] = 1;
						}
						else if (gridValues[i][j] == 1 && (alivePoints < 2 ||alivePoints > 3)) {
							tempGridValues[i][j] = 0;
						}
						else {
							tempGridValues[i][j] = gridValues[i][j];
						}
					}
				}
				gridValues = tempGridValues;
				return gridValues;
			}
			
		}
		//allows GameOfLifeGraphics to get values of grid
		public static int[][] GetValues() {
			return gridValues;
		}
		
		//allows GameOfLifeGraphics to get size of grid
		public static int GetSize() {
			return gridSize.getValue();
		}
				
		/*
		 * this method counts the number of neighbors each
		 * cell has. it utilizes modular arithmetic to treat
		 * the side columns as neighbors to each other, and
		 * the top and bottom rows as neighbors
		 */
		public static int checkAlive(int i, int j) {
			int counter = 0;
			
			for (int a = -1; a < 2; a++) {
				for(int b = -1; b < 2; b++) {
					int columnValue = (i + a + gridSize.getValue()) % gridSize.getValue();
					int rowValue = (j + b + gridSize.getValue()) % gridSize.getValue();
					counter += gridValues[columnValue][rowValue];
				}
			}
			counter -= gridValues[i][j];
			return counter;
		}
		
		//methods that determines state of board (random or not)
		public static void SetState (int state) {
		    source = state;
		}
		
		public static int GetState() {
			return source;
		}
}