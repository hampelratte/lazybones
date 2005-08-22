/* $Id: ProgramSelectionDialog.java,v 1.3 2005-08-22 16:24:37 hampelratte Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.*;

import util.ui.ProgramList;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
import devplugin.Program;

/**
 * Shown, if a Program and a VDRTimer have totally different titles. The user
 * has to choose the right program, then.
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class ProgramSelectionDialog extends Thread implements ActionListener {
    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(ProgramSelectionDialog.class);

    private JButton ok = new JButton();

    private JButton cancel = new JButton();

    private DefaultListModel model = new DefaultListModel();

    private ProgramList list = new ProgramList(model);

    private Program selectedProgram = null;

    private LazyBones control;

    private Hashtable vdr2browser;

    private JDialog dialog;

    private Program[] programs;

    private VDRTimer timer;

    public ProgramSelectionDialog(LazyBones control, Hashtable vdr2browser) {
        this.control = control;
        this.vdr2browser = vdr2browser;
    }

    private void initGUI() {
        dialog = new JDialog(control.getParent(), true);
        dialog.setTitle(mLocalizer.msg("title", "Select Program"));
        dialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        String msg = mLocalizer
                .msg(
                        "message",
                        "<html>I couldn\'t find a program, which matches the"
                                + " timer <b>%timer%</b> of VDR.<br>Please select the right"
                                + " program in the given list and press OK.</html>");
        msg = msg.replaceAll("%timer%", timer.getTitle());
        dialog.getContentPane().add(new JLabel(msg), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        dialog.getContentPane().add(new JScrollPane(list), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        dialog.getContentPane().add(cancel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        dialog.getContentPane().add(ok, gbc);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ok.setText(mLocalizer.msg("ok", "OK"));
        cancel.setText(mLocalizer.msg("cancel", "Cancel"));

        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    public void showSelectionDialog(Program[] programs, VDRTimer timer) {
        this.programs = programs;
        this.timer = timer;
        start();
    }

    public Program getProgram() {
        return selectedProgram;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                selectedProgram = (Program) model.get(list.getSelectedIndex());

                String idString = selectedProgram.getID() + "###";
                int year = selectedProgram.getDate().getYear();
                int month = selectedProgram.getDate().getMonth();
                int day = selectedProgram.getDate().getDayOfMonth();
                idString += day + "_" + month + "_" + year + "###";
                idString += selectedProgram.getChannel().getId();
                vdr2browser.put(timer.toNEWT(), idString);
                control.getTimersFromVDR();
                // selectedProgram.mark(control);
            }
        } else if (e.getSource() == cancel) {
            selectedProgram = null;
        }

        dialog.dispose();
    }

    public void run() {
        initGUI();
        dialog.setSize(1024, 768);
        model.removeAllElements();
        for (int i = 0; i < programs.length; i++) {
            model.addElement(programs[i]);
        }
        dialog.pack();
        dialog.setVisible(true);
    }
}