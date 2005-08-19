package lazybones;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import util.ui.ImageUtilities;
import util.ui.Localizer;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 * The root container for the settings tabs
 *  
 */
public class VDRSettingsPanel implements devplugin.SettingsTab {

  private static final Localizer mLocalizer = Localizer
      .getLocalizerFor(VDRSettingsPanel.class);

  private LazyBones control = new LazyBones();

  private JTabbedPane tabbedPane;

  private ChannelPanel channelPanel;

  private ConnectionPanel connectionPanel;

  private PlayerPanel playerPanel;
  
  private TimerPanel timerPanel;
  
  private PreviewSettingsPanel previewPanel;

  public VDRSettingsPanel(LazyBones control) {
    this.control = control;
  }

  /*
   * (non-Javadoc)
   * 
   * @see devplugin.SettingsTab#createSettingsPanel()
   */
  public JPanel createSettingsPanel() {
    if(tabbedPane == null) {
	    tabbedPane = new JTabbedPane();
	    tabbedPane.setPreferredSize(new Dimension(380, 380));
	
	    connectionPanel = new ConnectionPanel(control);
	    tabbedPane.addTab(mLocalizer.msg("connection", ""), connectionPanel);
	
	    channelPanel = new ChannelPanel(control);
	    tabbedPane.addTab(mLocalizer.msg("channels", ""), channelPanel);
	
	    playerPanel = new PlayerPanel(control);
	    tabbedPane.addTab(mLocalizer.msg("player", ""), playerPanel);
	    
	    timerPanel = new TimerPanel(control);
	    tabbedPane.addTab(mLocalizer.msg("timer", ""), timerPanel);
	    
	    previewPanel = new PreviewSettingsPanel(control);
	    tabbedPane.addTab(mLocalizer.msg("remoteControl", ""), previewPanel);
    }
    
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(tabbedPane, BorderLayout.CENTER);
    return p;
  }

  /*
   * (non-Javadoc)
   * 
   * @see devplugin.SettingsTab#saveSettings()
   */
  public void saveSettings() {
    channelPanel.saveSettings();
    connectionPanel.saveSettings();
    playerPanel.saveSettings();
    timerPanel.saveSettings();
    previewPanel.saveSettings();
  }

  /*
   * (non-Javadoc)
   * 
   * @see devplugin.SettingsTab#getIcon()
   */
  public Icon getIcon() {
    return new ImageIcon(ImageUtilities.createImageFromJar(
        "lazybones/vdr16.png", VDRSettingsPanel.class));
  }

  /*
   * (non-Javadoc)
   * 
   * @see devplugin.SettingsTab#getTitle()
   */
  public String getTitle() {
    return "Lazy Bones";
  }

}