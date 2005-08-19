/*
 * Created on 25.03.2005
 *
 */
package lazybones;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hampelratte.svdrp.commands.HITK;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 *  
 */
public class Controller implements ActionListener {

  private static Controller controller = new Controller();

  private Controller() {
  }

  public static Controller getController() {
    return controller;
  }

  public void actionPerformed(ActionEvent e) {
    VDRConnection.send(new HITK(e.getActionCommand()));
  }
}