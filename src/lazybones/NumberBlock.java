/*
 * Created on 25.03.2005
 *
 */
package lazybones;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 *
 */
public class NumberBlock extends JPanel {

  private static final long serialVersionUID = 3468623048482612161L;
    JButton b0 = new JButton("0");
    JButton b1 = new JButton("1");
    JButton b2 = new JButton("2");
    JButton b3 = new JButton("3");
    JButton b4 = new JButton("4");
    JButton b5 = new JButton("5");
    JButton b6 = new JButton("6");
    JButton b7 = new JButton("7");
    JButton b8 = new JButton("8");
    JButton b9 = new JButton("9");
    
    
    public NumberBlock() {
        initGUI();
    }
    
    private void initGUI() {
        setLayout(new GridLayout(4,3,10,10));
        
        b0.setActionCommand("0");
        b1.setActionCommand("1");
        b2.setActionCommand("2");
        b3.setActionCommand("3");
        b4.setActionCommand("4");
        b5.setActionCommand("5");
        b6.setActionCommand("6");
        b7.setActionCommand("7");
        b8.setActionCommand("8");
        b9.setActionCommand("9");
        
        b0.addActionListener(Controller.getController());
        b1.addActionListener(Controller.getController());
        b2.addActionListener(Controller.getController());
        b3.addActionListener(Controller.getController());
        b4.addActionListener(Controller.getController());
        b5.addActionListener(Controller.getController());
        b6.addActionListener(Controller.getController());
        b7.addActionListener(Controller.getController());
        b8.addActionListener(Controller.getController());
        b9.addActionListener(Controller.getController());
        
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
        add(b7);
        add(b8);
        add(b9);
        add(new JLabel()); // dummy
        add(b0);
        
    }
}
