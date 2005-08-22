/* $Id: TimerPanel.java,v 1.3 2005-08-22 16:24:37 hampelratte Exp $
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
package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import util.ui.Localizer;

public class TimerPanel extends JPanel {
    private static final long serialVersionUID = 4866079997638571269L;

    private static final Localizer mLocalizer = Localizer
            .getLocalizerFor(TimerPanel.class);

    private LazyBones control;

    private JLabel lBefore = new JLabel(mLocalizer.msg("before",
            "Time buffer before program"));

    private JSpinner before;

    private JLabel lAfter = new JLabel(mLocalizer.msg("after",
            "Time buffer after program"));

    private JSpinner after;

    public TimerPanel(LazyBones control) {
        this.control = control;
        initGUI();
    }

    private void initGUI() {
        int int_before = Integer.parseInt(control.getProperties().getProperty(
                "timer.before"));
        int int_after = Integer.parseInt(control.getProperties().getProperty(
                "timer.after"));
        before = new JSpinner();
        before.setValue(new Integer(int_before));
        ((JSpinner.DefaultEditor) before.getEditor()).getTextField()
                .setColumns(2);
        after = new JSpinner();
        ((JSpinner.DefaultEditor) after.getEditor()).getTextField().setColumns(
                2);
        after.setValue(new Integer(int_after));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(lBefore, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(before, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(lAfter, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(after, gbc);

    }

    public void saveSettings() {
        control.getProperties().setProperty("timer.before",
                before.getValue().toString());
        control.getProperties().setProperty("timer.after",
                after.getValue().toString());
    }
}