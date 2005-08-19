package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.ui.Localizer;

public class ConnectionPanel extends JPanel {
  private static final long serialVersionUID = 4141920557801647729L;

  private static final Localizer mLocalizer = Localizer
      .getLocalizerFor(ConnectionPanel.class);

  private LazyBones control;

  private JLabel lHost = new JLabel(mLocalizer.msg("host", "Host"));

  private JTextField host;

  private JLabel lPort = new JLabel(mLocalizer.msg("port", "Port"));

  private JTextField port;

  public ConnectionPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }

  private void initGUI() {
    host = new JTextField(20);
    host.setText(control.getProperties().getProperty("host"));
    port = new JTextField(20);
    port.setText(control.getProperties().getProperty("port"));

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(lHost, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(host, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(lPort, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(port, gbc);
  }

  public void saveSettings() {
    String h = host.getText();
    int p = 2001;
    try {
      p = Integer.parseInt(port.getText());
    } catch(NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, mLocalizer.msg("invalidPort",
      "<html>You have entered a wrong value for the port.<br>Port 2001 will be used instead.</html>"));
      p = 2001;
      port.setText("2001");
    }
    VDRConnection.host = h;
    VDRConnection.port = p;

    control.getProperties().setProperty("host", h);
    control.getProperties().setProperty("port", port.getText());
  }
}