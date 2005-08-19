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
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 *  
 */
public class NavigationBlock extends JPanel {
  private static final long serialVersionUID = 7226115547859845252L;

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
      .getLocalizerFor(NavigationBlock.class);

  JButton bMenu = new JButton(mLocalizer.msg("Menu","Menu"));

  JButton bBack = new JButton(mLocalizer.msg("Back","Back"));

  JButton bUp = new JButton(mLocalizer.msg("Up","Up"));

  JButton bDown = new JButton(mLocalizer.msg("Down","Down"));

  JButton bLeft = new JButton(mLocalizer.msg("Left","Left"));

  JButton bRight = new JButton(mLocalizer.msg("Right","Right"));

  JButton bOk = new JButton(mLocalizer.msg("OK","OK"));

  public NavigationBlock() {
    initGUI();
  }

  private void initGUI() {
    setLayout(new GridLayout(3, 3, 10, 10));

    bMenu.setActionCommand("MENU");
    bBack.setActionCommand("BACK");
    bUp.setActionCommand("UP");
    bDown.setActionCommand("DOWN");
    bLeft.setActionCommand("LEFT");
    bRight.setActionCommand("RIGHT");
    bOk.setActionCommand("OK");

    bMenu.addActionListener(Controller.getController());
    bBack.addActionListener(Controller.getController());
    bUp.addActionListener(Controller.getController());
    bDown.addActionListener(Controller.getController());
    bLeft.addActionListener(Controller.getController());
    bRight.addActionListener(Controller.getController());
    bOk.addActionListener(Controller.getController());

    add(bBack);
    add(bUp);
    add(bMenu);
    add(bLeft);
    add(bOk);
    add(bRight);
    add(new JLabel());
    add(bDown);
    add(new JLabel());
  }
}