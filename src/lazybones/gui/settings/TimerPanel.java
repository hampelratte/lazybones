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

import javax.swing.*;

import devplugin.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import lazybones.LazyBones;
import lazybones.TimerManager;
import lazybones.TitleMapping;
import lazybones.gui.components.historycombobox.JHistoryComboBox;
import util.ui.MarkPriorityComboBoxRenderer;

public class TimerPanel implements MouseListener, ActionListener {
    private static Logger logger = LoggerFactory.getLogger(TimerPanel.class);

    private final String lBefore = LazyBones.getTranslation("before", "Buffer before program");

    private final String ttBefore = LazyBones.getTranslation("before.tooltip", "Time buffer before program");

    private JSpinner before;

    private final String lAfter = LazyBones.getTranslation("after", "Buffer after program");

    private final String ttAfter = LazyBones.getTranslation("after.tooltip", "Time buffer after program");

    private JSpinner after;

    private JLabel labBefore;
    private JLabel labAfter;

    private JLabel lPrio = new JLabel(LazyBones.getTranslation("priority", "Priority"));

    private JSpinner prio;

    private JLabel lLifetime = new JLabel(LazyBones.getTranslation("lifetime", "Lifetime"));

    private JSpinner lifetime;

    private JLabel lTimelineStartHour = new JLabel(LazyBones.getTranslation("timelineStartHour", "Timeline starts at time of day"));

    private JSpinner timelineStartHour;

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

    private JCheckBox cbShowTimerConflicts;
    private JLabel lShowTimerConflicts = new JLabel(LazyBones.getTranslation("showTimerConflicts", "Show note on timer conflicts"));

    private JCheckBox cbShowTimerConflictsInList;
    private JLabel lShowTimerConflictsInList = new JLabel(LazyBones.getTranslation("showTimerConflictsInList", "Show conflicts in timer list"));

    private JLabel lDescSource;
    private JComboBox<DescriptionSelectorItem> cbDescSource;

    private JLabel lSeriesTitle;
    private JComboBox<SeriesTitleSelectorItem> cbSeriesTitle;

    private JPopupMenu mappingPopup = new JPopupMenu();

    private JLabel lDefaultDirectory;
    private JHistoryComboBox cbDefaultDirectory;

    private JLabel lMarkPriority;
    private JComboBox<String> cbMarkPriority;

    private TimerManager timerManager;

    public TimerPanel(TimerManager timerManager) {
        this.timerManager = timerManager;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        Properties props = LazyBones.getProperties();
        int intBefore = Integer.parseInt(props.getProperty("timer.before"));
        int intAfter = Integer.parseInt(props.getProperty("timer.after"));
        int intPrio = Integer.parseInt(props.getProperty("timer.prio"));
        int intLifetime = Integer.parseInt(props.getProperty("timer.lifetime"));
        int intNumberOfCards = Integer.parseInt(props.getProperty("numberOfCards"));
        int intTimelineStartHour = Integer.parseInt(props.getProperty("timelineStartHour"));
        String descSourceTvb = props.getProperty("descSourceTvb");
        String seriesTitle = props.getProperty("timer.series.title");
        boolean vpsDefault = Boolean.parseBoolean(props.getProperty("vps.default"));
        boolean showTimerConflicts = Boolean.parseBoolean(props.getProperty("timer.conflicts.show", "true"));
        boolean showTimerConflictsInList = Boolean.parseBoolean(props.getProperty("timer.conflicts.inTimerList", "true"));
        List<String> defaultDirectoryHistory = new ArrayList<>();
        int selectedMarkPriority = Integer.parseInt(props.getProperty("markPriority")) + 1;

        // load default directory history
        XStream xstream = new XStream();
        try {
            defaultDirectoryHistory = (List<String>) xstream.fromXML(props.getProperty("default.directory.history"));
        } catch (Exception e) {
            logger.warn("Couldn't load history of default directories", e);
        }

        before = new JSpinner();
        before.setValue(intBefore);
        before.setToolTipText(ttBefore);
        ((JSpinner.DefaultEditor) before.getEditor()).getTextField().setColumns(2);
        labBefore = new JLabel(lBefore);
        labBefore.setToolTipText(ttBefore);
        labBefore.setLabelFor(before);

        after = new JSpinner();
        ((JSpinner.DefaultEditor) after.getEditor()).getTextField().setColumns(2);
        after.setToolTipText(ttAfter);
        after.setValue(intAfter);
        labAfter = new JLabel(lAfter);
        labAfter.setToolTipText(ttAfter);
        labAfter.setLabelFor(after);

        prio = new JSpinner();
        ((JSpinner.DefaultEditor) prio.getEditor()).getTextField().setColumns(2);
        prio.setModel(new SpinnerNumberModel(intPrio, 0, 99, 1));
        lifetime = new JSpinner();
        ((JSpinner.DefaultEditor) lifetime.getEditor()).getTextField().setColumns(2);
        lifetime.setModel(new SpinnerNumberModel(intLifetime, 0, 99, 1));

        numberOfCards = new JSpinner();
        ((JSpinner.DefaultEditor) numberOfCards.getEditor()).getTextField().setColumns(2);
        numberOfCards.setModel(new SpinnerNumberModel(intNumberOfCards, 1, 10, 1));

        timelineStartHour = new JSpinner();
        ((JSpinner.DefaultEditor) timelineStartHour.getEditor()).getTextField().setColumns(2);
        timelineStartHour.setModel(new SpinnerNumberModel(intTimelineStartHour, 0, 23, 1));

        labMappings = new JLabel(lMappings);
        mappingTable = new JTable(timerManager.getTitleMapping());
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

        lDescSource = new JLabel(LazyBones.getTranslation("desc_source", "Timer description"));
        cbDescSource = new JComboBox<>();
        DescriptionComboBoxModel dcbm = new DescriptionComboBoxModel(true, false);
        dcbm.setSelected(descSourceTvb);
        cbDescSource.setModel(dcbm);

        lSeriesTitle = new JLabel(LazyBones.getTranslation("series_title", "Timer title for series"));
        cbSeriesTitle = new JComboBox<>();
        SeriesTitleComboBoxModel stcbm = new SeriesTitleComboBoxModel();
        stcbm.setSelected(seriesTitle);
        cbSeriesTitle.setModel(stcbm);


        cbVPS = new JCheckBox();
        cbVPS.setSelected(vpsDefault);

        cbShowTimerConflicts = new JCheckBox();
        cbShowTimerConflicts.setSelected(showTimerConflicts);

        cbShowTimerConflictsInList = new JCheckBox();
        cbShowTimerConflictsInList.setSelected(showTimerConflictsInList);

        lDefaultDirectory = new JLabel(LazyBones.getTranslation("default_directory", "Default directory"));
        cbDefaultDirectory = new JHistoryComboBox(defaultDirectoryHistory);

        lMarkPriority = new JLabel(LazyBones.getTranslation("mark_priority", "Mark priority"));
        DefaultComboBoxModel<String> cbMarkPriorityModel = new DefaultComboBoxModel<>();
        int maxPrio = Program.getHighlightingPriorityMaximum();
        for (int i = Program.PRIORITY_MARK_NONE; i <= maxPrio; i++) {
            cbMarkPriorityModel.addElement(i + " ".repeat(20));
        }
        cbMarkPriority = new JComboBox<>(cbMarkPriorityModel);
        cbMarkPriority.setRenderer(new MarkPriorityComboBoxRenderer());
        cbMarkPriority.setSelectedIndex(selectedMarkPriority);
        cbMarkPriority.addItemListener(e -> props.setProperty("markPriority", Integer.toString(cbMarkPriority.getSelectedIndex()-1)));
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagLayout panelLayout = new GridBagLayout();
        panel.setLayout(panelLayout);
        panel.setPreferredSize(new java.awt.Dimension(1021, 672));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = WEST;

        int row = 0;
        // left column of spinners
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(15, 15, 5, 5);
        panel.add(labBefore, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(before, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(labAfter, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(after, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(15, 15, 5, 5);
        panel.add(lSeriesTitle, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(cbSeriesTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lDescSource, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(cbDescSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lVPS, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(cbVPS, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lShowTimerConflicts, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(cbShowTimerConflicts, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lShowTimerConflictsInList, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(cbShowTimerConflictsInList, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lNumberOfCards, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(numberOfCards, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lTimelineStartHour, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(timelineStartHour, gbc);

        // history combobox for the default directory
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 5, 5);
        panel.add(lDefaultDirectory, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 3;
        panel.add(cbDefaultDirectory, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 15, 30, 5);
        panel.add(lMarkPriority, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.insets = new Insets(5, 5, 30, 5);
        gbc.gridwidth = 3;
        panel.add(cbMarkPriority, gbc);

        // mapping
        panel.add(labMappings, new GridBagConstraints(0, row++, 1, 1, 0.0, 0.0, WEST, NONE, new Insets(5, 15, 5, 5), 0, 0));
        panel.add(mappingPane, new GridBagConstraints(0, row, 4, 2, 1.0, 1.0, WEST, BOTH, new Insets(0, 15, 15, 5), 0, 0));

        // buttons
        panel.add(addRow, new GridBagConstraints(4, row++, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(0, 5, 5, 15), 0, 0));
        panel.add(delRow, new GridBagConstraints(4, row, 1, 1, 0.0, 0.0, NORTHWEST, HORIZONTAL, new Insets(5, 5, 5, 15), 0, 0));

        // right column of spinners
        row = 0;
        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 50, 5, 5);
        panel.add(lPrio, gbc);

        gbc.gridx = 3;
        gbc.gridy = row++;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(prio, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 50, 5, 5);
        panel.add(lLifetime, gbc);

        gbc.gridx = 3;
        gbc.gridy = row;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(lifetime, gbc);
        return panel;
    }

    public void saveSettings() {
        Properties props = LazyBones.getProperties();
        props.setProperty("timer.before", before.getValue().toString());
        props.setProperty("timer.after", after.getValue().toString());
        props.setProperty("timer.prio", prio.getValue().toString());
        props.setProperty("timer.lifetime", lifetime.getValue().toString());
        props.setProperty("timer.series.title", ((SeriesTitleSelectorItem) cbSeriesTitle.getSelectedItem()).getId());
        props.setProperty("numberOfCards", numberOfCards.getValue().toString());
        props.setProperty("timelineStartHour", timelineStartHour.getValue().toString());
        props.setProperty("descSourceTvb", ((DescriptionSelectorItem) cbDescSource.getSelectedItem()).getId());
        props.setProperty("vps.default", Boolean.toString(cbVPS.isSelected()));
        props.setProperty("timer.conflicts.show", Boolean.toString(cbShowTimerConflicts.isSelected()));
        props.setProperty("timer.conflicts.inTimerList", Boolean.toString(cbShowTimerConflictsInList.isSelected()));


        // save default directory history
        cbDefaultDirectory.addCurrentItemToHistory();
        XStream xstream = new XStream();
        props.setProperty("default.directory", cbDefaultDirectory.getText());
        props.setProperty("default.directory.history", xstream.toXML(cbDefaultDirectory.getHistory()));

        LazyBones.getInstance().synchronize();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // not interested in this event
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not interested in this event
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not interested in this event
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // not interested in this event
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
            TitleMapping mapping = timerManager.getTitleMapping();
            mapping.put("", "");
        } else if ("DEL".equals(e.getActionCommand())) {
            TitleMapping mapping = timerManager.getTitleMapping();
            int[] indices = mappingTable.getSelectedRows();
            for (int i = indices.length - 1; i >= 0; i--) {
                mapping.removeRow(indices[i]);
            }
        }
    }
}
