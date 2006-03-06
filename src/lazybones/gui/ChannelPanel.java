/* $Id: ChannelPanel.java,v 1.2 2006-03-06 20:42:02 hampelratte Exp $
 * 
 * Copyright (c) 2005, Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package lazybones.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import lazybones.*;
import tvbrowser.core.ChannelList;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.LSTC;
import devplugin.Channel;

public class ChannelPanel implements ActionListener {
    private static final long serialVersionUID = -655724917391419096L;

    private DefaultTableModel model;

    private JTable table = new JTable();

    private JButton up = new JButton();

    private JButton down = new JButton();

    private JButton refresh = new JButton(LazyBones.getTranslation("refresh_channels", "Refresh"));

    private JButton sort = new JButton(LazyBones.getTranslation("sort_channels", "Sort"));
    
    private JScrollPane scrollpane;

    private LazyBones lazyBones;

    public ChannelPanel(LazyBones control) {
        this.lazyBones = control;
        initComponents();
    }
    
    private void initComponents() {
        up.setIcon(lazyBones.getIcon("lazybones/Up12.gif"));
        down.setIcon(lazyBones.getIcon("lazybones/Down12.gif"));

        Object[] headers = { "TV-Browser", "VDR" };
        model = new DefaultTableModel(new Object[][] {}, headers) {
            private static final long serialVersionUID = 1366598326846158204L;

            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        devplugin.Channel[] c = ChannelList.getSubscribedChannels();
        Hashtable channelMapping = ProgramManager.getChannelMapping();
        for (int i = 0; i < c.length; i++) {
            Object[] row = { c[i], channelMapping.get(c[i].getId()) };
            model.addRow(row);
        }
        table.setModel(model);
        table.setDefaultRenderer(Object.class, new ChannelCellRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        scrollpane = new JScrollPane(table);
        refresh.addActionListener(this);
        sort.addActionListener(this);
        up.addActionListener(this);
        down.addActionListener(this);
    }

    public JPanel getPanel() {
		FormLayout layout = new FormLayout("left:85dlu:GROW, 3dlu, 120dlu, 3dlu, 3dlu",
			"160dlu:GROW, 2dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
        builder.add(scrollpane,       cc.xyw(1,  1, 5));
		builder.add(buttonBarPanel(), cc.xyw(1,  3, 5));
		
		return builder.getPanel();
    }
    
    private JPanel buttonBarPanel() {
        ButtonBarBuilder bb = ButtonBarBuilder.createLeftToRightBuilder();
        bb.addFixed(refresh);
        bb.addRelatedGap();
        bb.addGridded(sort);
        bb.addGlue();
        bb.addGridded(up);
        bb.addRelatedGap();
        bb.addGridded(down);
        return bb.getPanel();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refresh) {
            refreshChannelList();
        } else if (e.getSource() == sort) {
            trySort();
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
            if (!Utilities.isCellVisible(table, indices[indices.length - 1], 1)) {
                Utilities
                        .scrollToVisible(table, indices[indices.length - 1], 1);
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
                    // IDEA eventuell eine grenze in den optionen einbauen
                    if (/*chan.getId() < 500 &&*/ !tableContains(chan)) {
                        vdrchans.add(chan);
                    }
                }

                // add vdrchannels
                int max = model.getRowCount() -1;
                int count = 0;
                for (Iterator iter = vdrchans.iterator(); iter.hasNext();) {
                    VDRChannel element = (VDRChannel) iter.next();
                    if(count <= max) {
                        if(model.getValueAt(count, 1) != null) {
                            model.addRow(new Object[] { null, element });
                        } else {
                            model.setValueAt(element, count, 1);
                        }
                    } else { 
                        model.addRow(new Object[] { null, element });
                    }
                    count++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean tableContains(VDRChannel channel) {
        for (int i = 0; i < table.getRowCount(); i++) {
            Object o = model.getValueAt(i, 1);
            if (o != null) {
                VDRChannel tableChannel = (VDRChannel) o;
                if (tableChannel.equals(channel)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void restoreSelection(int[] indices) {
        table.getSelectionModel().setSelectionInterval(indices[0], indices[0]);
        for (int i = 1; i < indices.length; i++) {
            table.getSelectionModel().addSelectionInterval(indices[i],
                    indices[i]);
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
        ProgramManager.setChannelMapping(channelMapping);
    }
    
    public void trySort() {
        for (int i = 0; i < model.getRowCount(); i++) {
            ArrayList list = new ArrayList();
            Object tvbc = model.getValueAt(i,0);
            if(tvbc == null) {
                continue;
            }
            String tvbChan = ((Channel)tvbc).getName();
            tvbChan = tvbChan.toLowerCase();
            
            for (int j = 0; j < model.getRowCount(); j++) {
                Object o = model.getValueAt(j,1);
                if(o == null) {
                    swapCells(1, j, model.getRowCount()-1);
                    System.out.println("schiebe leere zelle nach unten");
                    continue;
                }
                String vdrChan = ((VDRChannel)o).getName();
                vdrChan = vdrChan.toLowerCase();
                int pos = vdrChan.indexOf(" ");
                if(pos > 0) {
                    vdrChan = vdrChan.substring(0, pos);
                }
                int percent = Utilities.percentageOfEquality(tvbChan, vdrChan);
                list.add(new Container(percent, j));
            }
            
            Collections.sort(list);
            Container c = (Container)list.get(list.size()-1);
            int index = c.getIndex();
            if (index != i && i < model.getRowCount()) {
                swapCells(1, i, index);
            }
        }
    }
    
    private class Container implements Comparable {
        private int percent;
        private int index;
        
        Container(int _percent, int _index) {
            percent = _percent;
            index = _index;
        }

        public int compareTo(Object o) {
            if(o instanceof Container) {
                Container c = (Container)o;
                if(c.getPercent() == percent) {
                    return 0;
                } else if(percent < c.getPercent()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return -1;
        }
        
        public int getPercent() {
            return percent;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String toString() {
            return percent+"% "+index;
        }
    }

}