package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import tvbrowser.core.ChannelList;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
import devplugin.Channel;
import devplugin.Date;
import devplugin.Plugin;
import devplugin.Program;

/**
 * Shown, if a timer should be edited. 
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class TimerOptionsDialog extends Thread implements ActionListener, MouseListener {
  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
      .getLocalizerFor(TimerOptionsDialog.class);

  private JLabel lChannels = new JLabel(mLocalizer.msg("channel","Channel"));
  private JComboBox channels = new JComboBox();
  
  private JLabel lDay = new JLabel(mLocalizer.msg("day","Day"));
  private DayChooser dayChooser; 
  private BrowseTextField day;
  
  private JLabel lStarttime = new JLabel(mLocalizer.msg("start","Start"));
  private JSpinner starttime = new JSpinner();
  
  private JLabel lEndtime = new JLabel(mLocalizer.msg("stop","Stop"));
  private JSpinner endtime = new JSpinner();
  
  private JLabel lPriority = new JLabel(mLocalizer.msg("priority","Priorit�t"));
  private JSpinner priority = new JSpinner();
  
  private JLabel lLifetime = new JLabel(mLocalizer.msg("lifetime","Lifetime"));
  private JSpinner lifetime = new JSpinner();
  
  private JLabel lTitle = new JLabel(mLocalizer.msg("title","Title"));
  private JTextField title = new JTextField();
  
  private JTextArea description = new JTextArea();
  
  private JPopupMenu popupmenu = new JPopupMenu();
  
  private JMenuItem tvbDescription = new JMenuItem(mLocalizer.msg("tvbDescription","Take TV-Browsers' description"));
  private JMenuItem vdrDescription = new JMenuItem(mLocalizer.msg("vdrDescription","Take VDRs' description"));
  
  private JButton ok = new JButton();

  private JButton cancel = new JButton();
  
  private LazyBones control;

  private JDialog dialog;
  
  private JPanel panel = new JPanel();
  
  private VDRTimer timer;

  private boolean confirmation = false;
  
  private boolean update = false;

  public TimerOptionsDialog(LazyBones control, VDRTimer timer, boolean update) {
    this.update = update;
    this.control = control;
    this.timer = timer;
    dayChooser = new DayChooser(timer);
    day = new BrowseTextField(dayChooser);
    initGUI();
  }

  private void initGUI() {
    dialog = new JDialog(control.getParent(), true);
    dialog.setTitle(mLocalizer.msg("windowtitle", "Timer Options"));
    panel.setLayout(new GridBagLayout());
    dialog.getContentPane().add(panel);
    GridBagConstraints gbc = new GridBagConstraints();
    
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.weightx = 0.3;
    gbc.weighty = 0.1;
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(lTitle, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(lChannels, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(lDay, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(lStarttime, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(lEndtime, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 5;
    panel.add(lPriority, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 6;
    panel.add(lLifetime, gbc);
    
    gbc.gridx = 1;
    gbc.gridy = 1;
    panel.add(channels, gbc);
    devplugin.Channel[] c = ChannelList.getSubscribedChannels();
    for (int i = 0; i < c.length; i++) {
      channels.addItem(c[i]);
    }
    Date date = new Date(timer.getStartTime());
    Program prog = Plugin.getPluginManager().getProgram(date, timer.getTvBrowserProgID());
    channels.setSelectedItem(prog.getChannel());
    
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    description.setRows(10);
    description.setLineWrap(true);
    description.setWrapStyleWord(true);
    if("".equals(timer.getDescription()) && !"".equals(prog.getDescription())) {
	    description.setText(prog.getDescription());
	    description.append("\n\n"+prog.getChannel().getCopyrightNotice());
    } else {
      description.setText(timer.getDescription());
    }
    panel.add(new JScrollPane(description), gbc);
    popupmenu.add(tvbDescription);
    tvbDescription.addActionListener(this);
    popupmenu.add(vdrDescription);
    vdrDescription.addActionListener(this);
    popupmenu.setLabel(mLocalizer.msg("chooseDescription", "Choose Description"));
    description.addMouseListener(this);
    if(!update) {
      description.setToolTipText(mLocalizer.msg("chooseDescriptionTooltip", "Use right mouse button to choose the description"));
    }
    
    gbc.gridx = 0;
    gbc.gridy = 8;
    gbc.gridwidth = 1;
    gbc.weighty = 0.1;
    panel.add(cancel, gbc);
    
    gbc.weightx = 1.0;
    
    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(title, gbc);
    title.setText(timer.getFile());

    gbc.gridx = 1;
    gbc.gridy = 2;
    panel.add(day, gbc);
    day.setText(timer.getDayString());
    day.setEditable(false);
    
    gbc.gridx = 1;
    gbc.gridy = 3;
    panel.add(starttime, gbc);
    SpinnerTimeModel model = new SpinnerTimeModel();
    int hour = timer.getStartTime().get(Calendar.HOUR_OF_DAY);
    int minute = timer.getStartTime().get(Calendar.MINUTE);
    model.setValue(new Time(hour, minute));
    starttime.setModel(model);
    
    gbc.gridx = 1;
    gbc.gridy = 4;
    panel.add(endtime, gbc);
    model = new SpinnerTimeModel();
    hour = timer.getEndTime().get(Calendar.HOUR_OF_DAY);
    minute = timer.getEndTime().get(Calendar.MINUTE);
    model.setValue(new Time(hour, minute));
    endtime.setModel(model);
    
    gbc.gridx = 1;
    gbc.gridy = 5;
    panel.add(priority, gbc);
    priority.setModel(new SpinnerNumberModel(timer.getPriority(),0,99,1));
    
    gbc.gridx = 1;
    gbc.gridy = 6;
    panel.add(lifetime, gbc);
    lifetime.setModel(new SpinnerNumberModel(timer.getLifetime(),0,99,1));
    
    gbc.gridx = 1;
    gbc.gridy = 8;
    panel.add(ok, gbc);
    
    ok.setText(mLocalizer.msg("ok", "OK"));
    cancel.setText(mLocalizer.msg("cancel", "Cancel"));

    ok.addActionListener(this);
    cancel.addActionListener(this);
    
    if(update) {
      day.setEnabled(false);
      channels.setEnabled(false);
      starttime.setEnabled(false);
      endtime.setEnabled(false);
    }
  }
  
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == ok) {
      confirmation = true;
      timer.setFile(title.getText());
      Channel c = (Channel)channels.getSelectedItem();
      VDRChannel vdrc = (VDRChannel)control.getChannelMapping().get(c.getId());
      timer.setChannel(vdrc.getId());
      Calendar start = timer.getStartTime();
      start.set(Calendar.HOUR_OF_DAY, ((Time)starttime.getValue()).getHour());
      start.set(Calendar.MINUTE, ((Time)starttime.getValue()).getMinute());
      timer.setStartTime(start);
      Calendar end = timer.getEndTime();
      end.set(Calendar.HOUR_OF_DAY, ((Time)endtime.getValue()).getHour());
      end.set(Calendar.MINUTE, ((Time)endtime.getValue()).getMinute());
      timer.setEndTime(end);
      timer.setPriority( ((Integer)priority.getValue()).intValue() );
      timer.setLifetime( ((Integer)lifetime.getValue()).intValue() );
      timer.setDescription(description.getText());
      dialog.dispose();
    } else if (e.getSource() == cancel) {
      confirmation = false;
      dialog.dispose();
    } else if (e.getSource() == tvbDescription) {
      Date date = new Date(timer.getStartTime());
      Program prog = Plugin.getPluginManager().getProgram(date, timer.getTvBrowserProgID());
      description.setText(prog.getDescription());
	  description.append("\n\n"+prog.getChannel().getCopyrightNotice());
    } else if (e.getSource() == vdrDescription) {
      description.setText(timer.getDescription());
    }
  }

  public void start() {
    dialog.setSize(400, 500);
    dialog.setLocation(50,50);
    dialog.setVisible(true);
    dialog.pack();
  }


  public boolean getConfirmation() {
    return confirmation;
  }


  public void mouseClicked(MouseEvent e) {
    if(e.getButton() == MouseEvent.BUTTON3 && !update) {
      popupmenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

 
  public void mouseEntered(MouseEvent arg0) {}
  public void mouseExited(MouseEvent arg0) {}
  public void mousePressed(MouseEvent arg0) {}
  public void mouseReleased(MouseEvent arg0) {}
}