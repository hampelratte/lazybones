/*
 * Created on 26.03.2005
 *
 */
package lazybones;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;



/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 *
 */
public class ColorButtonBlock extends JPanel {
    
    private static final long serialVersionUID = 3052192662507759492L;
    JButton bRed = new JButton("  ");
    JButton bGreen = new JButton("  ");
    JButton bYellow = new JButton("  ");
    JButton bBlue = new JButton("  ");

    public ColorButtonBlock() {
        initGUI();
    }

    private void initGUI() {
        
        bRed.setActionCommand("RED");
        bGreen.setActionCommand("GREEN");
        bYellow.setActionCommand("YELLOW");
        bBlue.setActionCommand("BLUE");
        
        bRed.addActionListener(Controller.getController());
        bGreen.addActionListener(Controller.getController());
        bYellow.addActionListener(Controller.getController());
        bBlue.addActionListener(Controller.getController());
        
        bRed.setBackground(Color.RED);
        bGreen.setBackground(Color.GREEN);
        bYellow.setBackground(Color.YELLOW);
        bBlue.setBackground(Color.BLUE);
        
        setLayout(new GridLayout(1,4,10,10));
        add(bRed);
        add(bGreen);
        add(bYellow);
        add(bBlue);
    }
}