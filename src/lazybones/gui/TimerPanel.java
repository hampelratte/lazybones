/* $Id: TimerPanel.java,v 1.3 2006-04-01 14:02:10 hampelratte Exp $
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import lazybones.LazyBones;

public class TimerPanel {
    private final String lBefore = LazyBones.getTranslation("before",
			"Buffer before program");

	private final String ttBefore = LazyBones.getTranslation("before.tooltip",
			"Time buffer before program");

	private JSpinner before;

	private final String lAfter = LazyBones.getTranslation("after",
			"Buffer after program");

	private final String ttAfter = LazyBones.getTranslation("after.tooltip",
			"Time buffer after program");

    private JSpinner after;
	private JLabel labBefore, labAfter; 
    
    private JLabel lPrio = new JLabel(LazyBones.getTranslation("priority", "Priority"));
    private JSpinner prio;
    private JLabel lLifetime = new JLabel(LazyBones.getTranslation("lifetime", "Lifetime"));
    private JSpinner lifetime;

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
        ((JSpinner.DefaultEditor) after.getEditor()).getTextField().setColumns(
				2);
        after.setToolTipText(ttAfter);
        after.setValue(new Integer(int_after));
        labAfter = new JLabel(lAfter);
        labAfter.setToolTipText(ttAfter);
        labAfter.setLabelFor(after);
        
        prio = new JSpinner();
        ((JSpinner.DefaultEditor) prio.getEditor()).getTextField()
        .setColumns(2);
        prio.setModel(new SpinnerNumberModel(int_prio,0,99,1));
        lifetime = new JSpinner();
        ((JSpinner.DefaultEditor) lifetime.getEditor()).getTextField()
        .setColumns(2);
        lifetime.setModel(new SpinnerNumberModel(int_lifetime,0,99,1));
    }

    public JPanel getPanel() {
        final double P = TableLayout.PREFERRED;
        double[][] size = {{0, P, P}, //cols
                           {0, P, P, P, P}}; // rows
        
        TableLayout layout = new TableLayout(size);
        layout.setHGap(10);
        layout.setVGap(10);
		
        JPanel panel = new JPanel(layout);
		panel.add(labBefore, "1,1,1,1");
		panel.add(before,    "2,1,2,1");
		
        panel.add(labAfter,  "1,2,1,2");
        panel.add(after,     "2,2,2,2");
        
        panel.add(lPrio,     "1,3,1,3");
        panel.add(prio,      "2,3,2,3");
        
        panel.add(lLifetime, "1,4,1,4");
        panel.add(lifetime,  "2,4,2,4");
	
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
}