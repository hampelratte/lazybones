package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import util.ui.Localizer;

public class TimerPanel extends JPanel {
  private static final long serialVersionUID = 4866079997638571269L;

  private static final Localizer mLocalizer = Localizer
      .getLocalizerFor(TimerPanel.class);

  private LazyBones control;

  private JLabel lBefore = new JLabel(mLocalizer.msg("before", "Time buffer before program"));

  private JSpinner before;

  private JLabel lAfter = new JLabel(mLocalizer.msg("after", "Time buffer after program"));

  private JSpinner after;

  public TimerPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }

  private void initGUI() {
    int int_before = Integer.parseInt(control.getProperties().getProperty("timer.before"));
    int int_after = Integer.parseInt(control.getProperties().getProperty("timer.after"));
    before = new JSpinner();
    before.setValue(new Integer(int_before));
    ((JSpinner.DefaultEditor)before.getEditor()).getTextField().setColumns(2);
    after = new JSpinner();
    ((JSpinner.DefaultEditor)after.getEditor()).getTextField().setColumns(2);
    after.setValue(new Integer(int_after));

    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(lBefore, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(before, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(lAfter, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(after, gbc);

  }

  public void saveSettings() {
    control.getProperties().setProperty("timer.before", before.getValue().toString());
    control.getProperties().setProperty("timer.after", after.getValue().toString());
  }
}