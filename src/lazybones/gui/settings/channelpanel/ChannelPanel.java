/*
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
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
package lazybones.gui.settings.channelpanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.gui.settings.channelpanel.dnd.ListTransferHandler;
import lazybones.gui.settings.channelpanel.dnd.TableTransferHandler;
import lazybones.utils.Utilities;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.swing.DecoratableTextField;
import org.hampelratte.swing.TextFieldClearButtonDecorator;
import org.hampelratte.swing.TextFieldHintDecorator;

import util.ui.Localizer;

public class ChannelPanel implements ActionListener {
    private DefaultTableModel tableModel;

    private JButton up = new JButton();

    private JButton down = new JButton();

    private JButton assign = new JButton();

    private JButton remove = new JButton();

    private JButton refresh = new JButton(LazyBones.getTranslation("refresh_channels", "Refresh"));

    private JButton autoAssign = new JButton(LazyBones.getTranslation("sort_channels", "Sort"));

    private JScrollPane tableScrollpane;

    private JScrollPane listScrollpane;

    private FilterListModel listModel = new FilterListModel();
    private JList<Channel> list = new JList<Channel>(listModel);

    private DecoratableTextField channelFilterTextfield = new DecoratableTextField();

    private JTable table = new JTable();

    private JSpinner minChannelNumber = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    private JSpinner maxChannelNumber = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    public ChannelPanel() {
        initComponents();
    }

    private void initComponents() {
        up.setIcon(LazyBones.getInstance().createImageIcon("action", "go-up", 16));
        down.setIcon(LazyBones.getInstance().createImageIcon("action", "go-down", 16));
        assign.setIcon(LazyBones.getInstance().createImageIcon("action", "go-previous", 16));
        remove.setIcon(LazyBones.getInstance().createImageIcon("action", "go-next", 16));
        refresh.setIcon(LazyBones.getInstance().createImageIcon("action", "view-refresh", 16));

        Object[] headers = { "TV-Browser", "VDR" };
        tableModel = new DefaultTableModel(new Object[][] {}, headers) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        devplugin.Channel[] c = tvbrowser.core.ChannelList.getSubscribedChannels();
        Map<String, Channel> channelMapping = ChannelManager.getChannelMapping();
        for (int i = 0; i < c.length; i++) {
            Object[] row = { c[i], channelMapping.get(c[i].getId()) };
            tableModel.addRow(row);
        }
        table.setModel(tableModel);
        table.setDefaultRenderer(Object.class, new ChannelCellRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowHorizontalLines(false);
        table.setRowHeight(23);
        table.setDragEnabled(true);
        table.setTransferHandler(new TableTransferHandler(list));

        list.setCellRenderer(new ChannelListCellrenderer());
        list.setDragEnabled(true);
        list.setTransferHandler(new ListTransferHandler());

        tableScrollpane = new JScrollPane(table);
        listScrollpane = new JScrollPane(list);

        MinMaxChannelListener minMaxListener = new MinMaxChannelListener();
        String minChannel = LazyBones.getProperties().getProperty("minChannelNumber");
        minChannelNumber.setValue(new Integer(minChannel));
        minChannelNumber.addChangeListener(minMaxListener);
        String maxChannel = LazyBones.getProperties().getProperty("maxChannelNumber");
        maxChannelNumber.setValue(new Integer(maxChannel));
        maxChannelNumber.addChangeListener(minMaxListener);

        refresh.addActionListener(this);
        autoAssign.addActionListener(this);
        up.addActionListener(this);
        down.addActionListener(this);
        assign.addActionListener(this);
        remove.addActionListener(this);
        channelFilterTextfield.getDocument().addDocumentListener(new ChannelFilterDocumentListener());
        channelFilterTextfield.addDecorator(new TextFieldHintDecorator(LazyBones.getTranslation("filter_channels", "Filter channels")));
        channelFilterTextfield.addDecorator(new TextFieldClearButtonDecorator());
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // table
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(tableScrollpane, gbc);

        // channel filter
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weighty = 0;
        panel.add(channelFilterTextfield, gbc);

        // list
        gbc.gridy = 1;
        gbc.weighty = 1;
        panel.add(listScrollpane, gbc);

        // channel interval spinner
        JPanel channelInterval = new JPanel();
        channelInterval.add(new JLabel(Localizer.getLocalization(Localizer.I18N_CHANNELS)));
        channelInterval.add(minChannelNumber);
        channelInterval.add(new JLabel("-"));
        channelInterval.add(maxChannelNumber);
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(channelInterval, gbc);

        // assign buttons
        JPanel assignButtonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        assignButtonPanel.add(assign);
        assignButtonPanel.add(remove);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(assignButtonPanel, gbc);

        // autoAssign
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridheight = 1;
        panel.add(autoAssign, gbc);

        // up
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(up, gbc);

        // down
        gbc.gridx = 2;
        gbc.gridy = 3;
        panel.add(down, gbc);

        // refresh
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(refresh, gbc);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refresh) {
            refreshChannelList();
        } else if (e.getSource() == autoAssign) {
            try {
                tryToAssignChannels();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
            if (indices[indices.length - 1] < tableModel.getRowCount() - 1) {
                for (int i = indices.length - 1; i >= 0; i--) {
                    moveDown(indices[i]);
                    indices[i]++;
                }
            }
            restoreSelection(indices);
            if (!Utilities.isCellVisible(table, indices[indices.length - 1], 1)) {
                Utilities.scrollToVisible(table, indices[indices.length - 1], 1);
            }
        } else if (e.getSource() == remove) {
            int[] rows = table.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                Object channel = table.getValueAt(rows[i], 1);
                if (channel != null) {
                    listModel.addElement((Channel) channel);
                    table.setValueAt(null, rows[i], 1);
                }
            }
        } else if (e.getSource() == assign) {
            int[] rows = list.getSelectedIndices();
            int tableRow = table.getSelectedRow();
            if (tableRow < 0) {
                return;
            }

            for (int i = rows.length - 1; i >= 0; i--) {
                int row = tableRow + i;
                if (row > table.getRowCount() - 1) {
                    continue;
                }

                Object tableValue = table.getValueAt(row, 1);
                if (tableValue != null) {
                    listModel.addElement((Channel) tableValue);
                }
                table.setValueAt(listModel.getElementAt(rows[i]), row, 1);
                listModel.removeElement(listModel.getElementAt(rows[i]));
            }
        }
    }

    private void refreshChannelList() {
        // set min and max channel number
        LazyBones.getProperties().setProperty("minChannelNumber", minChannelNumber.getValue().toString());
        LazyBones.getProperties().setProperty("maxChannelNumber", maxChannelNumber.getValue().toString());
        listModel.setMinChannel((int) minChannelNumber.getValue());
        listModel.setMaxChannel((int) maxChannelNumber.getValue());

        try {
            listModel.clear();
            ChannelManager.getInstance().update();
            List<Channel> vdrchans = ChannelManager.getInstance().getChannels();
            if (vdrchans != null) {
                // add vdrchannels to channel list
                for (Iterator<Channel> iter = vdrchans.iterator(); iter.hasNext();) {
                    Channel chan = iter.next();
                    int index = getChannelIndex(chan);
                    if (index == -1) {
                        listModel.addElement(chan);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getChannelIndex(Channel chan) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object o = tableModel.getValueAt(i, 1);
            if (o != null && o instanceof Channel) {
                Channel c = (Channel) o;
                if (c.equals(chan)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private void restoreSelection(int[] indices) {
        table.getSelectionModel().setSelectionInterval(indices[0], indices[0]);
        for (int i = 1; i < indices.length; i++) {
            table.getSelectionModel().addSelectionInterval(indices[i], indices[i]);
        }
    }

    private void swapCells(int col, int from, int to) {
        Object objTo = tableModel.getValueAt(to, col);
        Object objFrom = tableModel.getValueAt(from, col);
        tableModel.setValueAt(objFrom, to, col);
        tableModel.setValueAt(objTo, from, col);
    }

    private void moveUp(int i) {
        swapCells(1, i, i - 1);
    }

    private void moveDown(int i) {
        swapCells(1, i, i + 1);
    }

    public void saveSettings() {
        // set min and max channel number
        LazyBones.getProperties().setProperty("minChannelNumber", minChannelNumber.getValue().toString());
        LazyBones.getProperties().setProperty("maxChannelNumber", maxChannelNumber.getValue().toString());

        Hashtable<String, Channel> channelMapping = new Hashtable<String, Channel>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            devplugin.Channel c = (devplugin.Channel) tableModel.getValueAt(i, 0);

            if (c != null) {
                String id = c.getId();
                Object o = tableModel.getValueAt(i, 1);
                if (o != null) {
                    Channel vdrc = (Channel) o;
                    channelMapping.put(id, vdrc);
                }
            }
        }
        ChannelManager.setChannelMapping(channelMapping);
    }

    public void tryToAssignChannels() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            ArrayList<Container> list = new ArrayList<Container>();
            Object tvbc = tableModel.getValueAt(i, 0);
            if (tvbc == null) {
                continue;
            }
            String tvbChan = ((devplugin.Channel) tvbc).getName();
            tvbChan = tvbChan.toLowerCase();

            for (int j = 0; j < listModel.getSize(); j++) {
                Channel chan = listModel.getElementAt(j);
                String vdrChan = chan.getName();
                vdrChan = vdrChan.toLowerCase();
                int percent = Utilities.percentageOfEquality(tvbChan, vdrChan);
                list.add(new Container(percent, i, chan));
            }

            Collections.sort(list);
            if (list.size() <= 0) {
                return;
            }
            Container c = list.get(list.size() - 1);
            int index = c.getIndex();
            Object o = tableModel.getValueAt(index, 1);
            if (o == null || "".equals(o.toString())) {
                tableModel.setValueAt(c.getChannel(), index, 1);
                listModel.removeElement(c.getChannel());
            }
        }
    }

    private class ChannelFilterDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            listModel.setFilter(channelFilterTextfield.getText());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            listModel.setFilter(channelFilterTextfield.getText());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            listModel.setFilter(channelFilterTextfield.getText());
        }
    }

    private class MinMaxChannelListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            int value = (int) ((JSpinner) e.getSource()).getValue();
            if (e.getSource() == minChannelNumber) {
                listModel.setMinChannel(value);
            } else if (e.getSource() == maxChannelNumber) {
                listModel.setMaxChannel(value);
            }
        }

    }

    private class Container implements Comparable<Container> {
        private int percent;
        private int index;
        private Channel channel;

        Container(int percent, int index, Channel channel) {
            this.percent = percent;
            this.index = index;
            this.channel = channel;
        }

        @Override
        public int compareTo(Container c) {
            if (c.getPercent() == percent) {
                return 0;
            } else if (percent < c.getPercent()) {
                return -1;
            } else {
                return 1;
            }
        }

        public int getPercent() {
            return percent;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return percent + "% " + index + " " + channel.toString();
        }

        public Channel getChannel() {
            return channel;
        }

        @SuppressWarnings("unused")
        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }
}