package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class PreviewSettingsPanel extends JPanel {
  private static final long serialVersionUID = 5046636902877005743L;

  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
      .getLocalizerFor(PreviewSettingsPanel.class);

  private LazyBones control;

  private JLabel lURL = new JLabel(mLocalizer.msg("url", "URL to preview picture"));

  private JTextField url;

  private JLabel lPicturePath = new JLabel(mLocalizer.msg("path", "Path to preview picture"));
  
  private JTextArea description = new JTextArea(mLocalizer.msg("desc","The URL is the URL, where"
      + " VDRRemoteControl can download the preview image. The path is the path to the preview"
      + " image on the VDR host. This should be the document root of the webserver, which has" 
      + " been specified in the URL"), 10, 40);

  private JTextField picturePath;

  public PreviewSettingsPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }

  private void initGUI() {
    url = new JTextField(20);
    url.setText(control.getProperties().getProperty("preview.url"));
    picturePath = new JTextField(20);
    picturePath.setText(control.getProperties().getProperty("preview.path"));

    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.NORTHWEST;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    add(lURL, gbc);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(url, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.NONE;
    add(lPicturePath, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(picturePath, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    JScrollPane scrollpane = new JScrollPane(description);
    add(scrollpane, gbc);
    description.setEditable(false);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    scrollpane.setBorder(BorderFactory.createEmptyBorder());
    description.setBackground(UIManager.getColor("JPanel.background"));
  }

  public void saveSettings() {
    control.getProperties().setProperty("preview.url", url.getText());
    control.getProperties().setProperty("preview.path", picturePath.getText());
  }
}