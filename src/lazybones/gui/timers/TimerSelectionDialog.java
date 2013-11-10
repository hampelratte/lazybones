/*
 * Copyright (c) Henrik Niehaus
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
package lazybones.gui.timers;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerProgram;

import org.hampelratte.svdrp.responses.highlevel.Timer;

import util.ui.Localizer;
import util.ui.ProgramList;
import util.ui.UiUtilities;
import util.ui.WindowClosingIf;
import devplugin.Program;

/**
 * Shown, if a Program and a VDRTimer have totally different titles. The user has to choose the right program, then.
 *
 * @author <a href="hampelratte@users.sf.net">hampelratte@users.sf.net</a>
 */
public class TimerSelectionDialog implements ActionListener, WindowClosingIf {
    private final JButton ok = new JButton();

    private final JButton cancel = new JButton();

    private final DefaultListModel<Program> model = new DefaultListModel<Program>();

    private final ProgramList list = new ProgramList(model);

    private Program selectedProgram = null;

    private final LazyBones control;

    private JDialog dialog;

    private final Program[] programs;

    private final Program originalProgram;

    private final LazyBonesTimer timerOptions;

    public TimerSelectionDialog(Program[] programs, LazyBonesTimer timerOptions, Program prog) {
        this.control = LazyBones.getInstance();
        this.programs = programs;
        this.timerOptions = timerOptions;
        this.originalProgram = prog;
        initGUI();
        UiUtilities.registerForClosing(this);
    }

    private void initGUI() {
        dialog = new JDialog(control.getParent(), true);
        dialog.setTitle(LazyBones.getTranslation("windowtitle_vdrselect", "Select VDR-program"));
        dialog.getContentPane().setLayout(new GridBagLayout());
        dialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.getContentPane().add(
                new JLabel(LazyBones.getTranslation("message_vdrselect", "<html>I couldn\'t find a program,"
                        + " which matches the selected one.<br>Please select the" + " right program in the given list and press OK.</html>")), gbc);

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

        ok.setText(Localizer.getLocalization(Localizer.I18N_OK));
        cancel.setText(Localizer.getLocalization(Localizer.I18N_CANCEL));

        ok.addActionListener(this);
        cancel.addActionListener(this);

        dialog.setSize(1024, 768);
        model.removeAllElements();
        for (int i = 0; i < programs.length; i++) {
            model.addElement(programs[i]);
        }
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                selectedProgram = model.get(list.getSelectedIndex());
                TimerProgram program = (TimerProgram) selectedProgram;
                Timer t = program.getTimer();
                t.setTitle(timerOptions.getTitle());
                t.setDescription(timerOptions.getDescription());
                t.setLifetime(timerOptions.getLifetime());
                t.setPriority(timerOptions.getPriority());
                t.setStartTime(timerOptions.getStartTime());
                t.setEndTime(timerOptions.getEndTime());
                t.setHasFirstTime(timerOptions.hasFirstTime());
                t.setFirstTime(timerOptions.getFirstTime());
                t.setRepeatingDays(timerOptions.getRepeatingDays());
                control.timerSelectionCallBack(selectedProgram, originalProgram);
            }
        }
        dialog.dispose();
    }

    @Override
    public void close() {
        dialog.dispose();
    }

    @Override
    public JRootPane getRootPane() {
        return dialog.getRootPane();
    }
}
