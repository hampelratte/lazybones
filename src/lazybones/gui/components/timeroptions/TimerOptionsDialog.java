/* $Id: TimerOptionsDialog.java,v 1.5 2008-09-09 11:39:12 hampelratte Exp $
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
package lazybones.gui.components.timeroptions;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import lazybones.LazyBones;
import lazybones.Timer;
import util.ui.Localizer;
import util.ui.UiUtilities;
import util.ui.WindowClosingIf;
import devplugin.Program;

/**
 * Shown, if a timer should be edited.
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class TimerOptionsDialog implements ActionListener, WindowClosingIf {
    
    private JButton ok = new JButton();

    private JButton cancel = new JButton();

    private LazyBones control;

    private JDialog dialog;

    private TimerOptionsPanel top;
    
    private JPanel panel;
    
    /**
     * The actual timer
     */
    private Timer timer;
    
    /**
     * A clone of the timer containing the old settings
     */
    private Timer oldTimer;
    
    private Program program;
    
    private boolean accepted = false;
    
    public enum Mode {
        NEW,
        UPDATE,
        VIEW
    }
    
    public TimerOptionsDialog(Timer timer, Program prog, Mode mode) {
        this.control = LazyBones.getInstance();
        top = new TimerOptionsPanel(timer, prog, mode);
        
        this.timer = timer;
        this.oldTimer = (Timer) timer.clone();
        this.program = prog;
        
        initGUI();
    }

    private void initGUI() {
        dialog = new JDialog(control.getParent(), true);
        dialog.setTitle(LazyBones.getTranslation("windowtitle_timerOptions", "Timer Options"));
        dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        panel = new JPanel(new GridBagLayout());
        
        // register escape listener
        UiUtilities.registerForClosing(this);
        
        dialog.getContentPane().add(panel);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(top, gbc);
        dialog.addWindowListener(top);

        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(cancel, gbc);
        
        gbc.gridx = 1;
        panel.add(ok, gbc);

        ok.setText(Localizer.getLocalization(Localizer.I18N_OK));
        cancel.setText(Localizer.getLocalization(Localizer.I18N_CANCEL));

        ok.addActionListener(this);
        cancel.addActionListener(this);

        dialog.setSize(400, 500);
        dialog.setLocation(50, 50);
        dialog.setVisible(true);
        dialog.pack();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            timer = top.getTimer();
            dialog.dispose();
            accepted = true;
        } else if (e.getSource() == cancel) {
            dialog.dispose();
        } 
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getOldTimer() {
        return oldTimer;
    }

    public void setOldTimer(Timer oldTimer) {
        this.oldTimer = oldTimer;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void close() {
        dialog.dispose();
    }

    public JRootPane getRootPane() {
        return dialog.getRootPane();
    }
}