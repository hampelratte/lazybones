package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.ui.Localizer;

public class PlayerPanel extends JPanel {
  private static final long serialVersionUID = 151916691928714906L;

  private static final Localizer mLocalizer = Localizer
      .getLocalizerFor(PlayerPanel.class);

  private LazyBones control;

  private JLabel lPlayer = new JLabel(mLocalizer.msg("player", "Player"));

  private JTextField player;

  private JLabel lParams = new JLabel(mLocalizer.msg("params", "Parameters"));

  private JTextField params;
  
  private JCheckBox switchBefore = new JCheckBox(mLocalizer.msg("switch_before","Switch to channel before streaming"));

  public PlayerPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }

  private void initGUI() {
    player = new JTextField(20);
    player.setText(control.getProperties().getProperty("player"));
    params = new JTextField(20);
    params.setText(control.getProperties().getProperty("player_params"));
    switchBefore.setSelected(new Boolean(control.getProperties().getProperty("switchBefore")).booleanValue());

    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(lPlayer, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(player, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(lParams, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(params, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    add(switchBefore, gbc);

  }

  public void saveSettings() {
    control.getProperties().setProperty("player", player.getText());
    control.getProperties().setProperty("player_params", params.getText());
    control.getProperties().setProperty("switchBefore", new Boolean(switchBefore.isSelected()).toString());
  }
}