
import javax.swing.JFrame;

/**
 *
 * @author John Trapp
 */
public class Driver {

    public static void main(String[] args) {  //Creates the frame for the game.
        JFrame frame = new JFrame("Minesweeper!");
        frame.setSize(900, 900);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Minesweeper());
        frame.setVisible(true);
    }
}
