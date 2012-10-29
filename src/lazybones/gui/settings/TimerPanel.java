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
package lazybones.gui.settings;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import lazybones.ConflictFinder;
import lazybones.LazyBones;
import lazybones.TimerManager;
import lazybones.TitleMapping;
import lazybones.gui.components.historycombobox.JHistoryComboBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class TimerPanel implements MouseListener, ActionListener {
    private static transient Logger logger = LoggerFactory.getLogger(TimerPanel.class);

    private final String lBefore = LazyBones.getTranslation("before", "Buffer before program");

    private final String ttBefore = LazyBones.getTranslation("before.tooltip", "Time buffer before program");

    private JSpinner before;

    private final String lAfter = LazyBones.getTranslation("after", "Buffer after program");

    private final String ttAfter = LazyBones.getTranslation("after.tooltip", "Time buffer after program");

    private JSpinner after;

    private JLabel labBefore, labAfter;

    private JLabel lPrio = new JLabel(LazyBones.getTranslation("priority", "Priority"));

    private JSpinner prio;

    private JLabel lLifetime = new JLabel(LazyBones.getTranslation("lifetime", "Lifetime"));

    private JSpinner lifetime;

    private JLabel lNumberOfCards = new JLabel(LazyBones.getTranslation("numberOfCards", "Number of DVB cards"));

    private JSpinner numberOfCards;

    private String lMappings = LazyBones.getTranslation("mappings", "Title mappings");

    private JLabel labMappings;

    private JTable mappingTable;

    private JScrollPane mappingPane;

    private JButton addRow;

    private JButton delRow;
    private JCheckBox cbVPS;
    private JLabel lVPS = new JLabel(LazyBones.getTranslation("vpsDefault", "VPS by default"));

    private JLabel lDescSource;

    private JComboBox cbDescSource;

    private JPopupMenu mappingPopup = new JPopupMenu();

    private JLabel lDefaultDirectory;

    private JHistoryComboBox cbDefaultDirectory;

    public TimerPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        Properties props = LazyBones.getProperties();
        int int_before = Integer.parseInt(props.getProperty("timer.before"));
        int int_after = Integer.parseInt(props.getProperty("timer.after"));
        int int_prio = Integer.parseInt(props.getProperty("timer.prio"));
        int int_lifetime = Integer.parseInt(props.getProperty("timer.lifetime"));
        int int_numberOfCards = Integer.parseInt(props.getProperty("numberOfCards"));
        int descSourceTvb = Integer.parseInt(props.getProperty("descSourceTvb"));
        boolean vpsDefault = Boolean.parseBoolean(props.getProperty("vps.default"));
        List<String> defaultDirectoryHistory = new ArrayList<String>();

        // load default directory history
        XStream xstream = new XStream();
        try {
            defaultDirectoryHistory = (List<String>) xstream.fromXML(props.getProperty("default.directory.history"));
        } catch (Exception e) {
            logger.warn("Couldn't load history of default directories", e);
        }

        before = new JSpinner();
        before.setValue(new Integer(int_before));
        before.setToolTipText(ttBefore);
        ((JSpinner.DefaultEditor) before.getEditor()).getTextField().setColumns(2);
        labBefore = new JLabel(lBefore);
        labBefore.setToolTipText(ttBefore);
        labBefore.setLabelFor(before);

        after = new JSpinner();
        ((JSpinner.DefaultEditor) after.getEditor()).getTextField().setColumns(2);
        after.setToolTipText(ttAfter);
        after.setValue(new Integer(int_after));
        labAfter = new JLabel(lAfter);
        labAfter.setToolTipText(ttAfter);
        labAfter.setLabelFor(after);

        prio = new JSpinner();
        ((JSpinner.DefaultEditor) prio.getEditor()).getTextField().setColumns(2);
        prio.setModel(new SpinnerNumberModel(int_prio, 0, 99, 1));
        lifetime = new JSpinner();
        ((JSpinner.DefaultEditor) lifetime.getEditor()).getTextField().setColumns(2);
        lifetime.setModel(new SpinnerNumberModel(int_lifetime, 0, 99, 1));

        numberOfCards = new JSpinner();
        ((JSpinner.DefaultEditor) numberOfCards.getEditor()).getTextField().setColumns(2);
        numberOfCards.setModel(new SpinnerNumberModel(int_numberOfCards, 1, 10, 1));

        labMappings = new JLabel(lMappings);
        mappingTable = new JTable(TimerManager.getInstance().getTitleMapping());
        mappingPane = new JScrollPane(mappingTable);
        mappingTable.addMouseListener(this);
        mappingPane.addMouseListener(this);

        JMenuItem itemAdd = new JMenuItem(LazyBones.getTranslation("add_row", "Add row"));
        itemAdd.setActionCommand("ADD");
        itemAdd.addActionListener(this);
        JMenuItem itemDel = new JMenuItem(LazyBones.getTranslation("del_rows", "Delete selected rows"));
        itemDel.setActionCommand("DEL");
        itemDel.addActionListener(this);
        mappingPopup.add(itemAdd);
        mappingPopup.add(itemDel);

        addRow = new JButton(LazyBones.getTranslation("add_row", "Add row"));
        addRow.setActionCommand("ADD");
        addRow.addActionListener(this);
        delRow = new JButton(LazyBones.getTranslation("del_rows", "Delete selected rows"));
        delRow.setActionCommand("DEL");
        delRow.addActionListener(this);

        lDescSource = new JLabel(LazyBones.getTranslation("desc_source", "Use description from TV-Browser"));
        cbDescSource = new JComboBox();
        cbDescSource.addItem("VDR");
        cbDescSource.addItem("TV-Browser");
        cbDescSource.addItem(LazyBones.getTranslation("timer_desc_longest", "longest description"));
        cbDescSource.setSelectedIndex(descSourceTvb);

        cbVPS = new JCheckBox();
        cbVPS.setSelected(vpsDefault);

        lDefaultDirectory = new JLabel(LazyBones.getTranslation("default_directory", "Default directory"));
        cbDefaultDirectory = new JHistoryComboBox(defaultDirectoryHistory);
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagLayout panelLayout = new GridBagLayout();
        // panelLayout.columnWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1 };
        // panelLayout.columnWidths = new int[] { 7, 7, 7, 7, 7 };
        // panelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.0, 0.1 };
        // panelLayout.rowHeights = new int[] { 7, 7, 7, 20, 7, 7, 29, 7 };
        panel.setLayout(panelLayout);
        panel.setPreferredSize(new java.awt.Dimension(1021, 672));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = WEST;

        // left column of spinners
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 5, 5);
        panel.add(labBefore, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(before, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(labAfter, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(after, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 15, 5, 5);
        panel.add(lDescSource, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(cbDescSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lVPS, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(cbVPS, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lNumberOfCards, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(numberOfCards, gbc);

        // history combobox for the default directory
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 15, 30, 5);
        panel.add(lDefaultDirectory, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(5, 5, 30, 5);
        gbc.gridwidth = 3;
        panel.add(cbDefaultDirectory, gbc);

        // right column of spinners
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 50, 5, 5);
        panel.add(lPrio, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(prio, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 50, 5, 5);
        panel.add(lLifetime, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(lifetime, gbc);

        // mapping
        panel.add(labMappings, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 15, 5, 5), 0, 0));
        panel.add(mappingPane, new GridBagConstraints(0, 7, 4, 2, 1.0, 1.0, WEST, BOTH, new Insets(0, 15, 15, 5), 0, 0));

        // buttons
        panel.add(addRow, new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(0, 5, 5, 15), 0, 0));
        panel.add(delRow, new GridBagConstraints(4, 8, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(5, 5, 5, 15), 0, 0));

        return panel;
    }

    public void saveSettings() {
        Properties props = LazyBones.getProperties();
        props.setProperty("timer.before", before.getValue().toString());
        props.setProperty("timer.after", after.getValue().toString());
        props.setProperty("timer.prio", prio.getValue().toString());
        props.setProperty("timer.lifetime", lifetime.getValue().toString());
        props.setProperty("numberOfCards", numberOfCards.getValue().toString());
        props.setProperty("descSourceTvb", Integer.toString(cbDescSource.getSelectedIndex()));
        props.setProperty("vps.default", Boolean.toString(cbVPS.isSelected()));

        // save default directory history
        cbDefaultDirectory.addCurrentItemToHistory();
        XStream xstream = new XStream();
        props.setProperty("default.directory", cbDefaultDirectory.getText());
        props.setProperty("default.directory.history", xstream.toXML(cbDefaultDirectory.getHistory()));

        ConflictFinder.getInstance().findConflicts();
        ConflictFinder.getInstance().handleConflicts();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if ((e.getSource() == mappingPane || e.getSource() == mappingTable) && e.getButton() == MouseEvent.BUTTON3) {
            mappingPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("ADD".equals(e.getActionCommand())) {
            TitleMapping mapping = TimerManager.getInstance().getTitleMapping();
            mapping.put("", "");
        } else if ("DEL".equals(e.getActionCommand())) {
            TitleMapping mapping = TimerManager.getInstance().getTitleMapping();
            int[] indices = mappingTable.getSelectedRows();
            for (int i = indices.length - 1; i >= 0; i--) {
                mapping.removeRow(indices[i]);
            }
        }
    }
}