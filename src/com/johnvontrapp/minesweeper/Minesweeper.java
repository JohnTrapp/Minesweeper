package com.johnvontrapp.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author John Trapp
 */
public class Minesweeper extends JPanel {

	private JButton[][] board; // The array of buttons, GUI
	private int[][] matrix; // The array that stores data and does work
	private int[][] numbers; // The array that stores the numbers of mines around it
	private JButton reset; // Button that resets the board
	private JLabel bMinesLeft; // Button that says mines left
	private JLabel bTimer; // Button that displays the time
	
	private final ImageIcon flag = createImageIcon("images/flag.png", "Minesweeper Flag"); // Flag icon http://commons.wikimedia.org/wiki/File:Minesweeper-flag.png
	private final ImageIcon happy = createImageIcon("images/happy.png", "Happy Face"); // Happy face http://www.saimasays.com/images/minesweeper.GIF
	private final ImageIcon win = createImageIcon("images/win.png", "Win Face"); // Sunglasses guy http://www.saimasays.com/images/minesweeper.GIF
	private final ImageIcon dead = createImageIcon("images/dead.png", "Dead"); // X_X http://www.saimasays.com/images/minesweeper.GIF
	
	private int misses; // counts the number of misses for a win
	private int size; // Size of the board
	private int numMines; // Sets the number of mines
	private int numMisses; // Sets the number of misses needed to win
	private int minesLeft; // Sets the number of mines left
	private final ClockListener cl = new ClockListener(); // Listener for timer
	private final Timer t = new Timer(1000, cl); // Timer
	private int seconds; // Seconds

	public Minesweeper() {

		size = setSize();

		setLayout(new BorderLayout());

		JPanel north = new JPanel();

		north.setLayout(new GridLayout(1, 3));
		add(north, BorderLayout.NORTH); // Adds the label JPanel
		reset = new JButton("", happy);
		reset.setDisabledIcon(happy);
		reset.addActionListener(new Handler2()); // adds reset button
		reset.setEnabled(false);

		Font f = new Font("DialogInput", Font.BOLD, 40);
		bMinesLeft = new JLabel("" + minesLeft, SwingConstants.CENTER); // adds how many mines are left
		bMinesLeft.setFont(f);
		bMinesLeft.setOpaque(true);
		bMinesLeft.setForeground(Color.GREEN);
		bMinesLeft.setBackground(Color.BLACK);

		bTimer = new JLabel("00", SwingConstants.CENTER); // adds seconds timer
		bTimer.setFont(f);
		bTimer.setOpaque(true);
		bTimer.setForeground(Color.GREEN);
		bTimer.setBackground(Color.BLACK);

		north.add(bMinesLeft); // Adds the labels

		north.add(reset);

		north.add(bTimer);

		JPanel center = new JPanel();

		center.setLayout(new GridLayout(size, size)); // Puts the actual game in
		add(center, BorderLayout.CENTER);

		board = new JButton[size][size]; // The array of buttons
		matrix = new int[size][size]; // The array of mines
		numbers = new int[size][size]; // The array of proximity numbers

		for (int r = 0; r < size; r++) { // Sets up the board
			for (int c = 0; c < size; c++) {
				board[r][c] = new JButton();
				board[r][c].setBackground(Color.BLUE);
				board[r][c].addActionListener(new Handler1(r, c));
				board[r][c].addMouseListener(new MouseAdapter() {

					public void mouseClicked(MouseEvent e) { // listener for right click
						if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
							JButton button = (JButton) e.getSource();
							if (button.isEnabled()) { // places flag
								minesLeft--;
								bMinesLeft.setText("" + minesLeft);
								button.setEnabled(false);
								button.setBackground(null);
								button.setForeground(null);
								button.setIcon(flag);
								button.setDisabledIcon(flag);
							} else if (button.getBackground() != Color.white) { // removes flag
								minesLeft++;
								bMinesLeft.setText("" + minesLeft);
								button.setEnabled(true);
								button.setIcon(null);
								button.setBackground(Color.BLUE);
							}
						}
					}

					public void mousePressed(MouseEvent e) { // Unused mouse events
					}

					public void mouseReleased(MouseEvent e) {
					}

					public void mouseEntered(MouseEvent e) {
					}

					public void mouseExited(MouseEvent e) {
					}
				});
				center.add(board[r][c]); // adds the array of buttons to center panel
			}
		}

		placeMines(); // Gee, I wounder what this does?

		placeNumbers();// I have no idea what this does...

		t.start(); // Starts the timer.
	}

	private int setSize() { // Sets the size of the board
		String s = "-1";
		Object[] possibilities = { "Easy", "Medium", "Hard", "Impossible" };
		s = (String) JOptionPane.showInputDialog(null, "Welcome to Minesweeper!\nPlease chose a difficulty.",
				"Minesweeper", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Medium");
		if (s.equals("Easy")) {
			size = 10;
			numMines = 15;
			minesLeft = 15;
			numMisses = 85;
		} else if (s.equals("Medium")) {
			size = 15;
			numMines = 30;
			minesLeft = 30;
			numMisses = 195;
		} else if (s.equals("Hard")) {
			size = 20;
			numMines = 80;
			minesLeft = 80;
			numMisses = 320;
		} else if (s.equals("Impossible")) {
			size = 20;
			numMines = 399;
			minesLeft = 399;
			numMisses = 1;
		}
		return size;
	}

	private void resetBoard() { // resets the board
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				board[r][c].setBackground(Color.blue);
				board[r][c].setIcon(null);
				board[r][c].setDisabledIcon(null);
				board[r][c].setEnabled(true);
				board[r][c].setText("");
				matrix[r][c] = 0;
			}
		}
		misses = 0;
	}

	private void placeMines() { // places the mines.
		int rrow;
		int rcol;
		if (numMines == 399) {
			for (int r = 0; r < size; r++) { // Makes them all mines for impossible diff.
				for (int c = 0; c < size; c++) {
					matrix[r][c] = 1;
				}
			}
			rrow = (int) (Math.random() * (size - 1));
			rcol = (int) (Math.random() * (size - 1));
			matrix[rrow][rcol] = 0; // Creates one empty one
			return;
		}
		for (int n = 0; n < numMines; n++) { // creates mines for the other diff.
			do {
				rrow = (int) (Math.random() * (size - 1));
				rcol = (int) (Math.random() * (size - 1));
			} while (matrix[rrow][rcol] == 1);

			matrix[rrow][rcol] = 1;
		}
	}

	private void placeNumbers() { // adds in proximity numbers
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				if (matrix[r][c] == 1) {
					numbers[r][c] = -1;
				} else {
					int count = 0;
					if (r > 0 && c > 0 && matrix[r - 1][c - 1] == 1) { // Checks upper left
						count++;
					}
					if (c > 0 && matrix[r][c - 1] == 1) { // Checks left
						count++;
					}
					if (r < (size - 1) && c > 0 && matrix[r + 1][c - 1] == 1) { // Checks lower left
						count++;
					}
					if (r > 0 && matrix[r - 1][c] == 1) { // Checks above
						count++;
					}
					if (r < (size - 1) && matrix[r + 1][c] == 1) { // Checks below
						count++;
					}
					if (r > 0 && c < (size - 1) && matrix[r - 1][c + 1] == 1) { // Checks upper right
						count++;
					}
					if (c < (size - 1) && matrix[r][c + 1] == 1) { // Checks Right
						count++;
					}
					if (r < (size - 1) && c < (size - 1) && matrix[r + 1][c + 1] == 1) { // Checks lower right
						count++;
					}
					numbers[r][c] = count;
				}
			}
		}
	}

	public boolean floodFill(int r, int c) { // Removes surrounding zeros
		boolean floodFillUsed = false;
		if (r >= 0 && r <= (size - 1) && c >= 0 && c <= (size - 1)) { // Gets rid of those pesky "array out of bounds"
																		// errors
			if ((matrix[r][c] != 1 && board[r][c].isEnabled() == true
					&& numbers[r][c] == 0) /* || zeroNextToIt() == true */) {
				floodFillUsed = true; // above checks to see if the cell is not a mine, is actually enabled, and
										// proximity count is 0.
				board[r][c].setBackground(Color.WHITE);
				board[r][c].setEnabled(false);
				if (numbers[r][c] == 0) {
					board[r][c].setText("-");
				} else {
					board[r][c].setText("" + numbers[r][c]);
				}

				floodFill(r - 1, c); // Super-fun recursion happining here
				floodFill(r + 1, c);
				floodFill(r, c - 1);
				floodFill(r, c + 1);

				misses++; // Counts the number of misses to check for a win.
			} else { // To-do: also shows surrounding numbers like in the real game.
			} // Causes stack overflow.
		}
		return floodFillUsed; // Returns boolean to correct for extra misses++

	}

	private class Handler1 implements ActionListener { // when you click on a button
		// makes no difference who you are.

		private int myRow, myCol;

		public Handler1(int r, int c) { // Sets the correct button
			myRow = r;
			myCol = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (matrix[myRow][myCol] == 1) { // Uh-oh, you clicked on a mine! n00b
				for (int r = 0; r < size; r++) {
					for (int c = 0; c < size; c++) { // disables the board
						board[r][c].setEnabled(false);
					}
				}
				t.stop();
				reset.setEnabled(true); // enables reset button.
				reset.setIcon(dead);// Puts dead face top label.

				for (int r = 0; r < size; r++) {
					for (int c = 0; c < size; c++) {
						if (matrix[r][c] == 1) { // shows the mines
							board[r][c].setBackground(Color.black);
						}
					}
				}

				board[myRow][myCol].setBackground(Color.red); // makes the mine you clicked on red
			} else { // You did not click on a mine! Congrats!
				if (floodFill(myRow, myCol) == true) { // does the floodFill
					misses--; // if flood fill happenes, removes extra misses++
				}
				board[myRow][myCol].setBackground(Color.white); // Sets the button
				board[myRow][myCol].setEnabled(false);
				if (numbers[myRow][myCol] == 0) {
					board[myRow][myCol].setText("-");
				} else {
					board[myRow][myCol].setText("" + numbers[myRow][myCol]);
				}
				misses++;
				reset.setIcon(happy);
				reset.setDisabledIcon(happy);
				checkForWin();
			}
		}
	}

	private void checkForWin() { // Checks for Win.
		// System.out.println(misses); //Uncomment for debug
		if (misses == numMisses) {
			t.stop();
			for (int r = 0; r < size; r++) {
				for (int c = 0; c < size; c++) { // Disables board
					board[r][c].setEnabled(false);
				}
			}
			minesLeft = 0;
			bMinesLeft.setText("" + minesLeft);
			reset.setEnabled(true);
			reset.setIcon(win); // Sunglasses guy! From actual game!

			for (int r = 0; r < size; r++) {
				for (int c = 0; c < size; c++) {
					if (matrix[r][c] == 1) { // Makes mines black
						board[r][c].setBackground(Color.black);

					}
				}
			}
		}
	}

	private class Handler2 implements ActionListener { // Reset listener

		@Override
		public void actionPerformed(ActionEvent e) {
			reset.setIcon(happy);
			reset.setDisabledIcon(happy);
			resetBoard();
			placeMines(); // Who knows what these commands do...
			placeNumbers();
			reset.setEnabled(false);
			minesLeft = numMines;
			bMinesLeft.setText("" + minesLeft);
			seconds = 0;
			t.restart();
		}
	}

	// Manages image creation. #ThanksOracleForCode!
	// Code provided by Oracle, although it's common code. Just thought I'd put it
	// in...
	// http://docs.oracle.com/javase/tutorial/uiswing/components/icon.html
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	// Formats clock and displays it.
	private class ClockListener implements ActionListener {

		private String second;

		@Override
		public void actionPerformed(ActionEvent e) {
			NumberFormat formatter = new DecimalFormat("00");

			second = formatter.format(seconds);
			bTimer.setText(String.valueOf(second));
			seconds++;
		}
	}
}
//End code. Wow, what a lot of lines for a "simple" game. It's not even done yet!

/*************************************************
 * To-do: * 1) Add high score tracker * 2) Change starting dialog to radio
 * buttons * 3) Format comments for javadocs * 4) Fix form fill to show
 * surrounding numbers * 5) I'm sure there's something else... * 6) GUI
 * improvements *
 *************************************************/
