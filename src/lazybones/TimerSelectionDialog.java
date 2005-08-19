package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import util.ui.ProgramList;
import devplugin.Program;

/**
 * Shown, if a Program and a VDRTimer have totally different titles.
 * The user has to choose the right program, then.
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 */
public class TimerSelectionDialog implements ActionListener {
  private static final util.ui.Localizer mLocalizer = util.ui.Localizer
  .getLocalizerFor(TimerSelectionDialog.class);
  private JButton ok = new JButton();
  private JButton cancel = new JButton();
  private DefaultListModel model = new DefaultListModel();
  private ProgramList list = new ProgramList(model);
  private Program selectedProgram = null;
  private LazyBones control;
  private JDialog dialog;
  
  public TimerSelectionDialog(LazyBones control) {
    this.control = control;
    initGUI();
  }
  
  private void initGUI() {
    dialog = new JDialog(control.getParent(), true);
    dialog.setTitle(mLocalizer.msg("title","Select VDR-program"));
    dialog.getContentPane().setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(5,5,5,5);
    dialog.getContentPane().add(new JLabel(
        mLocalizer.msg("message","<html>I couldn\'t find a program,"
            + " which matches the selected one.<br>Please select the"
            + " right program in the given list and press OK.</html>")), gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    dialog.getContentPane().add(new JScrollPane(list), gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    dialog.getContentPane().add(cancel, gbc);
    
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    dialog.getContentPane().add(ok, gbc);
    
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    ok.setText(mLocalizer.msg("ok","OK"));
    cancel.setText(mLocalizer.msg("cancel","Cancel"));
    
    ok.addActionListener(this);
    cancel.addActionListener(this);
  }
  
  public void showSelectionDialog(Program[] programs) {
    dialog.setSize(1024,768);
    model.removeAllElements();
    for (int i = 0; i < programs.length; i++) {
      model.addElement(programs[i]);
    }
    dialog.pack();
    dialog.setVisible(true);
  }
  
  public Program getProgram() {
    return selectedProgram;
  }


  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == ok) {
      int index = list.getSelectedIndex();
      if(index>=0)
        selectedProgram = (Program) model.get(list.getSelectedIndex());
    } else if(e.getSource() == cancel) {
      selectedProgram = null;
    }
    
    dialog.dispose();
  }
}
