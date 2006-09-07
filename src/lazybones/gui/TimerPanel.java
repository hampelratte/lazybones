/* $Id: TimerPanel.java,v 1.4 2006-09-07 13:34:36 hampelratte Exp $
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

import info.clearthought.layout.TableLayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import lazybones.LazyBones;
import lazybones.TimerManager;

public class TimerPanel implements MouseListener, ActionListener {
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

    private String lMappings = LazyBones.getTranslation("mappings", "Title mappings");
    private JLabel labMappings;
    private JTable mappingTable;
    private JScrollPane mappingPane;
    private JButton addRow;
    private JButton delRow;
    
    private JPopupMenu mappingPopup = new JPopupMenu();

    public TimerPanel() {
        initComponents();
    }
    
    private void initComponents() {
        int int_before = Integer.parseInt(LazyBones.getProperties().getProperty(
                "timer.before"));
        int int_after = Integer.parseInt(LazyBones.getProperties().getProperty(
                "timer.after"));
        int int_prio = Integer.parseInt(LazyBones.getProperties().getProperty(
                "timer.prio"));
        int int_lifetime = Integer.parseInt(LazyBones.getProperties().getProperty(
                "timer.lifetime"));
        before = new JSpinner();
        before.setValue(new Integer(int_before));
        before.setToolTipText(ttBefore);
        ((JSpinner.DefaultEditor) before.getEditor()).getTextField()
                .setColumns(2);
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
        prio.setModel(new SpinnerNumberModel(int_prio,0,99,1));
        lifetime = new JSpinner();
        ((JSpinner.DefaultEditor) lifetime.getEditor()).getTextField().setColumns(2);
        lifetime.setModel(new SpinnerNumberModel(int_lifetime,0,99,1));
        
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
    }

    public JPanel getPanel() {
        final double P = TableLayout.PREFERRED;
        final double F = TableLayout.FILL;
        double[][] size = {{0, P, F, P, P, 0},  // cols
                           {0, P, 0, P, F, 0}}; // rows
        
        TableLayout layout = new TableLayout(size);
        layout.setHGap(10);
        layout.setVGap(10);
		
        JPanel buffers = new JPanel(new GridLayout(2,2,5,5));
        buffers.add(labBefore);
        buffers.add(before);
        buffers.add(labAfter);
        buffers.add(after);
        
        JPanel prioLifetime = new JPanel(new GridLayout(2,2,5,5));
        prioLifetime.add(lPrio);
        prioLifetime.add(prio);
        prioLifetime.add(lLifetime);
        prioLifetime.add(lifetime);
        
        JPanel panel = new JPanel(layout);
		panel.add(buffers, "1,1,1,1");
        panel.add(prioLifetime, "3,1,4,1");
		        
        panel.add(labMappings, "1,3,1,3");
        panel.add(mappingPane, "1,4,3,4");
        
        double[][] size2 = {{P},     // cols
                            {P, P}}; // rows
        TableLayout layout2 = new TableLayout(size2);
        layout2.setVGap(10);
        JPanel dummy = new JPanel(layout2);
        dummy.add(addRow, "0,0,0,0");
        dummy.add(delRow, "0,1,0,1");
        
        panel.add(dummy, "4,4,4,4");

        return panel;
    }

    public void saveSettings() {
        LazyBones.getProperties().setProperty("timer.before",
                before.getValue().toString());
        LazyBones.getProperties().setProperty("timer.after",
                after.getValue().toString());
        LazyBones.getProperties().setProperty("timer.prio",
                prio.getValue().toString());
        LazyBones.getProperties().setProperty("timer.lifetime",
                lifetime.getValue().toString());
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
        if ((e.getSource() == mappingPane || 
             e.getSource() == mappingTable) && e.getButton() == MouseEvent.BUTTON3) {
            mappingPopup.show(e.getComponent(), e.getX(), e.getY());
        } 
    }

    public void actionPerformed(ActionEvent e) {
        if("ADD".equals(e.getActionCommand())) {
            TitleMapping mapping = TimerManager.getInstance().getTitleMapping();
            mapping.put("", "");
        } else if("DEL".equals(e.getActionCommand())) {
            TitleMapping mapping = TimerManager.getInstance().getTitleMapping();
            int[] indices = mappingTable.getSelectedRows();
            for (int i = indices.length-1; i >= 0; i--) {
                mapping.removeRow(indices[i]);
            }
        }
    }
}