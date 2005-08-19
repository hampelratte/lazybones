/*
 * Created on 25.03.2005
 *
 */
package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.CHAN;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 *  
 */
public class RemoteControl extends JPanel implements ActionListener {

  private static final long serialVersionUID = 4617969625415777142L;

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
  .getLocalizerFor(RemoteControl.class);
  
  private NumberBlock numBlock;

  private NavigationBlock navBlock;

  private ColorButtonBlock colorButtonBlock;

  private JButton watch = new JButton(mLocalizer.msg("Watch","Watch"));

  private LazyBones parent;

  public RemoteControl(LazyBones parent) {
    this.parent = parent;
    initGUI();
  }

  private void initGUI() {
    setLayout(new GridBagLayout());

    numBlock = new NumberBlock();
    add(numBlock, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5,
            10, 5), 0, 0));
    navBlock = new NavigationBlock();
    add(navBlock, new GridBagConstraints(0, 1, 2, 1, 0.1, 0.1,
        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
        new Insets(10, 5, 5, 5), 0, 0));
    colorButtonBlock = new ColorButtonBlock();
    add(colorButtonBlock, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
        new Insets(5, 5, 10, 5), 0, 0));

    watch.addActionListener(Controller.getController());
    watch.addActionListener(this);
    add(watch, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,
            5, 5, 5), 0, 0));
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == watch) {
      Response res = VDRConnection.send(new CHAN());
      if(res != null && res.getCode() == 250) {
        int chan = Integer.parseInt(res.getMessage().split(" ")[0]);
        Player.play(chan, parent);
      }
    }
  }
}