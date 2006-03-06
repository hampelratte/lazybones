/* $Id: TimerPanel.java,v 1.1 2006-03-06 19:51:51 hampelratte Exp $
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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import lazybones.LazyBones;

import util.ui.Localizer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TimerPanel {
    private static final long serialVersionUID = 4866079997638571269L;

    private static final Localizer mLocalizer = Localizer
            .getLocalizerFor(TimerPanel.class);

    private LazyBones control;

    private final String lBefore = mLocalizer.msg("before",
			"Buffer before program");

	private final String ttBefore = mLocalizer.msg("before.tooltip",
			"Time buffer before program");

	private JSpinner before;

	private final String lAfter = mLocalizer.msg("after",
			"Buffer after program");

	private final String ttAfter = mLocalizer.msg("after.tooltip",
			"Time buffer after program");

    private JSpinner after;
	private JLabel labBefore, labAfter; 
    
    private JLabel lPrio = new JLabel(mLocalizer.msg("prio", "Priority"));
    private JSpinner prio;
    private JLabel lLifetime = new JLabel(mLocalizer.msg("lifetime", "Lifetime"));
    private JSpinner lifetime;

    public TimerPanel(LazyBones control) {
        this.control = control;
        initComponents();
    }
    
    private void initComponents() {
        int int_before = Integer.parseInt(control.getProperties().getProperty(
                "timer.before"));
        int int_after = Integer.parseInt(control.getProperties().getProperty(
                "timer.after"));
        int int_prio = Integer.parseInt(control.getProperties().getProperty(
                "timer.prio"));
        int int_lifetime = Integer.parseInt(control.getProperties().getProperty(
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
		FormLayout layout = new FormLayout("left:75dlu, 3dlu, 25dlu",
			"pref, 2dlu, pref, 2dlu, pref, 2dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.add(labBefore, cc.xy (1,  1));
		builder.add(before,    cc.xy (3,  1));
		
		builder.add(labAfter,  cc.xy (1,  3));
		builder.add(after,     cc.xy (3,  3));
        
        builder.add(lPrio,     cc.xy (1,  5));
        builder.add(prio,      cc.xy (3,  5));
        
        builder.add(lLifetime, cc.xy (1,  7));
        builder.add(lifetime,  cc.xy (3,  7));
	
		return builder.getPanel();
    }

    public void saveSettings() {
        control.getProperties().setProperty("timer.before",
                before.getValue().toString());
        control.getProperties().setProperty("timer.after",
                after.getValue().toString());
        control.getProperties().setProperty("timer.prio",
                prio.getValue().toString());
        control.getProperties().setProperty("timer.lifetime",
                lifetime.getValue().toString());
    }
}