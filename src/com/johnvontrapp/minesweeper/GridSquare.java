package com.johnvontrapp.minesweeper;

import java.awt.Color;

import javax.swing.JButton;

/**
 * @author John Trapp
 *
 */
public class GridSquare extends JButton {
	private static final long serialVersionUID = 974286983238183761L;

	private boolean isMine;
	private int number;

	public GridSquare() {
		super();

		setBackground(Color.BLUE);
	}

	public void revealButton() {
		setEnabled(false);

		if (isMine) {
			setBackground(Color.red);
		} else {
			setBackground(Color.white);
			if (number == 0) {
				setText("-");
			} else {
				setText(Integer.toString(number));
			}
		}
	}

	public void resetButton() {
		setBackground(Color.BLUE);
		setIcon(null);
		setDisabledIcon(null);
		setEnabled(true);
		setText("");
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
}
