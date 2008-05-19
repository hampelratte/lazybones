/* $Id: ProgramSelectionDialog.java,v 1.11 2008-05-19 12:00:48 hampelratte Exp $
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

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import lazybones.LazyBones;
import lazybones.Timer;
import lazybones.TimerManager;
import util.ui.Localizer;
import util.ui.ProgramList;
import devplugin.Program;

/**
 * Shown, if a Program and a VDRTimer have totally different titles. The user
 * has to choose the right program, then.
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class ProgramSelectionDialog extends Thread implements ActionListener {
    private JButton ok = new JButton();

    private JButton cancel = new JButton();
    
    private JButton never = new JButton();
    
    private DefaultListModel model = new DefaultListModel();

    private ProgramList list = new ProgramList(model);

    private Program selectedProgram = null;

    private JDialog dialog;

    private Timer timer;

    public ProgramSelectionDialog(Program[] programs, Timer timer) {
        this.timer = timer;
        
        if (programs.length > 0) {
            initGUI();
            dialog.setSize(600, 400);
            dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            model.removeAllElements();
            for (int i = 0; i < programs.length; i++) {
                model.addElement(programs[i]);
            }
            
            dialog.setVisible(true);
        }
    }

    private void initGUI() {
        dialog = new JDialog(LazyBones.getInstance().getParent(), true);
        dialog.setTitle(LazyBones.getTranslation("windowtitle_programselect", "Select Program"));
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        Date date = new Date(timer.getStartTime().getTimeInMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateString = sdf.format(date);
        String title = timer.getPath() + timer.getTitle();
        String msg = LazyBones.getTranslation("message_programselect",
                        "<html>I couldn\'t find a program, which matches the"
                                + " timer <b>{0}</b> at <b>{1}</b>VDR.<br>Please select the right"
                                + " program in the given list and press OK.</html>",
                        title, dateString);
        dialog.getContentPane().add(new JLabel(msg), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        dialog.getContentPane().add(cancel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        dialog.getContentPane().add(never, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        dialog.getContentPane().add(ok, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.weighty = 1.0;
        dialog.getContentPane().add(new JScrollPane(list), gbc);

        //list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ok.setText(Localizer.getLocalization(Localizer.I18N_OK));
        never.setText(LazyBones.getTranslation("never","Never assign"));
        cancel.setText(Localizer.getLocalization(Localizer.I18N_CANCEL));

        ok.addActionListener(this);
        never.addActionListener(this);
        cancel.addActionListener(this);
    }

    public Program getProgram() {
        return selectedProgram;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            int[] indices = list.getSelectedIndices();
            for (int i = 0; i < indices.length; i++) {
                if (indices[i] >= 0) {
                    try {
                        selectedProgram = (Program) model.get(indices[i]);
                        TimerManager.getInstance().timerCreatedOK(selectedProgram, timer);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (e.getSource() == cancel) {
            selectedProgram = null;
        } else if (e.getSource() == never ) {
            timer.setReason(Timer.NO_PROGRAM);
            TimerManager.getInstance().replaceStoredTimer(timer);
        }

        dialog.dispose();
    }
}