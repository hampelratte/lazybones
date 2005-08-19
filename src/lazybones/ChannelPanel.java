package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tvbrowser.core.ChannelList;
import util.ui.Localizer;
import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.LSTC;

public class ChannelPanel extends JPanel implements ActionListener {
  private static final long serialVersionUID = -655724917391419096L;

  private static final Localizer mLocalizer = Localizer
      .getLocalizerFor(ChannelPanel.class);

  private DefaultTableModel model;

  private JTable table = new JTable();

  private JButton up = new JButton();

  private JButton down = new JButton();

  private JButton refresh = new JButton(mLocalizer.msg("refresh_channels", ""));

  private JScrollPane scrollpane;

  private LazyBones control;


  public ChannelPanel(LazyBones control) {
    this.control = control;
    initGUI();
  }


  private void initGUI() {
    up.setIcon(control.getIcon("lazybones/Up24.gif"));
    down.setIcon(control.getIcon("lazybones/Down24.gif"));

    setLayout(new GridBagLayout());
    Object[] headers = { "TV-Browser", "VDR" };
    model = new DefaultTableModel(new Object[][] {}, headers) {
      private static final long serialVersionUID = 1366598326846158204L;
      public boolean isCellEditable(int row, int col) {
        return false;
      }
    };
    devplugin.Channel[] c = ChannelList.getSubscribedChannels();
    Hashtable channelMapping = control.getChannelMapping();
    for (int i = 0; i < c.length; i++) {
      Object[] row = { c[i], channelMapping.get(c[i].getId()) };
      model.addRow(row);
    }
    table.setModel(model);
    table.setDefaultRenderer(Object.class, new ChannelCellRenderer());
    table.getTableHeader().setReorderingAllowed(false);
    scrollpane = new JScrollPane(table);
    add(scrollpane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
        GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5,
            5, 5), 0, 0));
    refresh.addActionListener(this);
    add(refresh, new GridBagConstraints(0, 1, 1, 1, 0.1, 0.1,
        GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
        new Insets(0, 5, 5, 5), 0, 0));
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(2, 1, 5, 5));
    up.addActionListener(this);
    buttonPanel.add(up);
    down.addActionListener(this);
    buttonPanel.add(down);
    add(buttonPanel, new GridBagConstraints(1, 0, 1, 1, 0.1, 0.1,
        GridBagConstraints.WEST, GridBagConstraints.NONE,
        new Insets(5, 5, 5, 5), 0, 0));
  }


  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == refresh) {
      refreshChannelList();
    } else if (e.getSource() == up) {
      int[] indices = table.getSelectedRows();
      Arrays.sort(indices);
      if (indices[0] > 0) {
        for (int i = 0; i < indices.length; i++) {
          moveUp(indices[i]);
          indices[i]--;
        }
      }
      restoreSelection(indices);
      if (!Utilities.isCellVisible(table, indices[0], 1)) {
        Utilities.scrollToVisible(table, indices[0], 1);
      }
    } else if (e.getSource() == down) {
      int[] indices = table.getSelectedRows();
      Arrays.sort(indices);
      if (indices[indices.length - 1] < model.getRowCount() - 1) {
        for (int i = indices.length - 1; i >= 0; i--) {
          moveDown(indices[i]);
          indices[i]++;
        }
      }
      restoreSelection(indices);
      if (!Utilities.isCellVisible(table, indices[indices.length-1], 1)) {
        Utilities.scrollToVisible(table, indices[indices.length-1], 1);
      }
    }
  }


  private void refreshChannelList() {
    try {
      Response res = VDRConnection.send(new LSTC());
      if (res != null) {
        String channels = res.getMessage();
        StringTokenizer st1 = new StringTokenizer(channels, "\n");
        ArrayList vdrchans = new ArrayList();
        while (st1.hasMoreTokens()) {
          String line = st1.nextToken();
          int pos = line.indexOf(" ");
          VDRChannel chan = new VDRChannel();
          int id = Integer.parseInt(line.substring(0, pos));
          chan.setId(id);
          String restOfLine = line.substring(pos + 1);
          StringTokenizer st2 = new StringTokenizer(restOfLine, ";");
          String name = st2.nextToken();
          if (name.indexOf(':') > 0) {
            name = name.substring(0, name.indexOf(':'));
          }
          if (name.indexOf(',') > 0) {
            name = name.substring(0, name.indexOf(','));
          }
          chan.setName(name);
          if (chan.getId() < 500 && !tableContains(chan)) {
            vdrchans.add(chan);
          }
        }

        // add vdrchannels
        for (Iterator iter = vdrchans.iterator(); iter.hasNext();) {
          VDRChannel element = (VDRChannel) iter.next();
          model.addRow(new Object[] { null, element });
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  
  private boolean tableContains(VDRChannel channel) {
    for(int i=0; i<table.getRowCount(); i++) {
      Object o = model.getValueAt(i, 1);
	  if(o != null) {
		VDRChannel tableChannel = (VDRChannel)o;
		if(tableChannel.equals(channel)) {
		  return true;
		}
      }
    }
    return false;
  }

  private void restoreSelection(int[] indices) {
    table.getSelectionModel().setSelectionInterval(indices[0], indices[0]);
    for (int i = 1; i < indices.length; i++) {
      table.getSelectionModel().addSelectionInterval(indices[i], indices[i]);
    }
  }


  private void swapCells(int col, int from, int to) {
    Object objTo = model.getValueAt(to, col);
    Object objFrom = model.getValueAt(from, col);
    model.setValueAt(objFrom, to, col);
    model.setValueAt(objTo, from, col);
  }


  private void moveUp(int i) {
    swapCells(1, i, i - 1);
  }


  private void moveDown(int i) {
    swapCells(1, i, i + 1);
  }


  public void saveSettings() {
    Hashtable channelMapping = new Hashtable();
    for (int i = 0; i < model.getRowCount(); i++) {
      devplugin.Channel c = (devplugin.Channel) model.getValueAt(i, 0);

      if (c != null) {
        String id = c.getId();
        Object o = model.getValueAt(i, 1);
        if (o != null) {
          VDRChannel vdrc = (VDRChannel) o;
          channelMapping.put(id, vdrc);
        }
      }
    }
    control.setChannelMapping(channelMapping);
  }
}