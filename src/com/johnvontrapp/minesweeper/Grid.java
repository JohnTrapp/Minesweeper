package com.johnvontrapp.minesweeper;

import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * @author John Trapp
 *
 */
public class Grid extends JPanel {
	private GridSquare[][] board;
	
	public Grid(int rows, int columns) {
		super();
		
		setLayout(new GridLayout(rows, columns));
		
		board = new GridSquare[rows][columns];
		
		for(GridSquare[] button : board) {
			
		}
	}
}
