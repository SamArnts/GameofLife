import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GameOfLifeGraphics extends JPanel {
	
	private int columns;
	private int rows;
	private int [][] gridValues;
	
	
	public GameOfLifeGraphics() {
		
	}
	
	//method that paints the grid and alive cells
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		gridValues = RunGameOfLife.GetValues();
		columns = RunGameOfLife.GetSize();
		rows = RunGameOfLife.GetSize();
		
		int width = getWidth();
		int height = getHeight();
		
		int blockWidth = width / columns;
		int blockHeight = height / rows;
		
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
					int x = i * blockWidth;
					int y = j * blockHeight;
					
					Color myOrange = new Color(233, 84, 32);
					g.setColor(myOrange);
					g.drawRect(x, y, blockWidth, blockHeight);
					
					if (gridValues[i][j] == 1) {
						g.fillRect(x, y, blockWidth, blockHeight);
					}
			}
		}
	}
	
}