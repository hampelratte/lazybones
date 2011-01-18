/* $Id: TimerOptionsPanel.java,v 1.17 2011-01-18 17:26:51 hampelratte Exp $
 * 
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
package lazybones.gui.components.timeroptions;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.Timer;
import lazybones.gui.components.daychooser.BrowseTextField;
import lazybones.gui.components.daychooser.DayChooser;
import lazybones.gui.components.timeroptions.TimerOptionsDialog.Mode;
import lazybones.programmanager.ProgramManager;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.VDRTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tvbrowser.core.ChannelList;
import util.ui.Localizer;
import devplugin.Date;
import devplugin.Plugin;
import devplugin.Program;

public class TimerOptionsPanel extends JPanel implements ActionListener, ItemListener, WindowListener, ChangeListener {
    private static transient Logger logger = LoggerFactory.getLogger(TimerOptionsPanel.class);

    public static final int DESC_VDR = 0;
    public static final int DESC_TVB = 1;
    public static final int DESC_LONGEST = 2;

    private JLabel lChannels = new JLabel(Localizer.getLocalization(Localizer.I18N_CHANNEL));

    private JComboBox channels = new JComboBox();

    private JLabel lDay = new JLabel(LazyBones.getTranslation("day", "Day"));

    private DayChooser dayChooser;

    private BrowseTextField day;

    private JLabel lStarttime = new JLabel(LazyBones.getTranslation("start", "Start"));

    private SpinnerCalendarModel spinnerStarttimeModel;// = new SpinnerCalendarModel();
    private JSpinner spinnerStarttime;// = new JSpinner(spinnerStarttimeModel);

    private JLabel lEndtime = new JLabel(LazyBones.getTranslation("stop", "Stop"));

    private SpinnerCalendarModel spinnerEndtimeModel;// = new SpinnerCalendarModel();
    private JSpinner spinnerEndtime; // = new JSpinner(spinnerEndtimeModel);

    private JLabel lPriority = new JLabel(LazyBones.getTranslation("priority", "Priority"));

    private JSpinner priority = new JSpinner();

    private JLabel lLifetime = new JLabel(LazyBones.getTranslation("lifetime", "Lifetime"));

    private JSpinner lifetime = new JSpinner();

    private JLabel lTitle = new JLabel(LazyBones.getTranslation("title", "Title"));

    private JTextField title = new JTextField();

    private JLabel lDescription = new JLabel(LazyBones.getTranslation("description", "Description"));

    private JComboBox comboDesc = new JComboBox();

    private JTextArea description = new JTextArea();

    private JLabel lActive = new JLabel(LazyBones.getTranslation("active", "Active"));

    private JCheckBox cbActive = new JCheckBox();

    private JLabel lVps = new JLabel(LazyBones.getTranslation("vps", "VPS"));

    private JLabel lVpsTimeHint = new JLabel(LazyBones.getTranslation("vpsTimeHint", "Starttime has been changed for VPS"));

    private JCheckBox cbVps = new JCheckBox();

    /**
     * The actual timer
     */
    private Timer timer;

    /**
     * A clone of the timer containing the old settings
     */
    private Timer oldTimer;

    private Program prog;

    private Mode mode;

    public TimerOptionsPanel(Timer timer, Program prog, Mode mode) {
        this.mode = mode;

        initGUI();

        setProgram(prog);
        setTimer(timer);
    }

    public TimerOptionsPanel(Mode mode) {
        this.mode = mode;
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0.3;
        gbc.weighty = 0.1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lActive, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lVps, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(lChannels, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lDay, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        add(lVpsTimeHint, gbc);
        lVpsTimeHint.setForeground(Color.RED);
        lVpsTimeHint.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(lStarttime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(lEndtime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(lPriority, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(lLifetime, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(channels, gbc);
        devplugin.Channel[] c = ChannelList.getSubscribedChannels();
        for (int i = 0; i < c.length; i++) {
            channels.addItem(c[i]);
        }

        gbc.gridx = 0;
        gbc.gridy = 10;
        add(lDescription, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        description.setRows(10);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        add(new JScrollPane(description), gbc);

        gbc.weighty = 0.1;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(cbActive, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(cbVps, gbc);
        cbVps.addActionListener(this);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(title, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        dayChooser = new DayChooser();
        day = new BrowseTextField(dayChooser);
        day.setEditable(false);
        add(day, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        spinnerStarttime = new JSpinner();
        spinnerStarttime.addChangeListener(this);
        add(spinnerStarttime, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        spinnerEndtime = new JSpinner();
        spinnerEndtime.addChangeListener(this);
        add(spinnerEndtime, gbc);

        gbc.gridx = 1;
        gbc.gridy = 8;
        add(priority, gbc);

        gbc.gridx = 1;
        gbc.gridy = 9;
        add(lifetime, gbc);

        gbc.gridx = 1;
        gbc.gridy = 10;
        add(comboDesc, gbc);
        comboDesc.addItem("VDR");
        comboDesc.addItem("TV-Browser");
        if (mode == Mode.UPDATE || mode == Mode.VIEW) {
            comboDesc.addItem("Timer");
        }
        comboDesc.addItemListener(this);
        // comboDesc.setEnabled(!update);

        if (mode == Mode.VIEW) { // set components to readonly
            cbActive.setEnabled(false);
            cbVps.setEnabled(false);
            title.setEnabled(false);
            channels.setEnabled(false);
            day.setEnabled(false);
            spinnerStarttime.setEnabled(false);
            spinnerEndtime.setEnabled(false);
            lifetime.setEnabled(false);
            priority.setEnabled(false);
            comboDesc.setEnabled(false);
            description.setEnabled(false);
        }
    }

    public void setTimer(Timer timer) {
        if (timer != null) {
            this.timer = timer;
            this.oldTimer = (Timer) timer.clone();

            // we have to remove the buffers again, to get the right start date
            // example: start time is 00.00 h with time buffers we have 23.45
            // Calendar then decreases the start date, so that we don't have the right date, but
            // the date of the day before.
            Timer tmp = timer.getTimerWithoutBuffers();
            Program prog = null;
            if (this.prog == null) {
                prog = ProgramManager.getInstance().getProgram(tmp);
            } else {
                prog = this.prog;
            }

            if (prog != null) {
                channels.setSelectedItem(prog.getChannel());
            } else {
                Channel chan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
                channels.addItem(chan);
                channels.setSelectedItem(chan);
            }

            // set the description
            String descVdr = timer.getDescription() == null ? "" : timer.getDescription();
            String descTvb = prog != null ? prog.getDescription() != null ? prog.getDescription() : "" : "";
            int useTvbDescription = Integer.parseInt(LazyBones.getProperties().getProperty("descSourceTvb"));

            if (mode == Mode.UPDATE || mode == Mode.VIEW) {
                description.setText(oldTimer.getDescription());
                comboDesc.setSelectedIndex(2);
            } else {
                switch (useTvbDescription) {
                case DESC_VDR:
                    description.setText(descVdr);
                    comboDesc.setSelectedIndex(DESC_VDR);
                    break;
                case DESC_TVB:
                    description.setText(descTvb);
                    comboDesc.setSelectedIndex(DESC_TVB);
                    break;
                case DESC_LONGEST:
                    if (descVdr.length() < descTvb.length()) {
                        description.setText(descTvb);
                        comboDesc.setSelectedIndex(DESC_TVB);
                    } else {
                        description.setText(descVdr);
                        comboDesc.setSelectedIndex(DESC_VDR);
                    }
                    break;
                default:
                    description.setText(descVdr);
                    comboDesc.setSelectedIndex(DESC_VDR);
                    break;
                }
            }

            // set timer is active switch
            cbActive.setSelected(timer.isActive());

            // set timer title
            title.setText(timer.getFile());

            day.setText(timer.getDayString());

            // initialize the start and end time spinners
            spinnerStarttimeModel = new SpinnerCalendarModel(timer.getStartTime());
            spinnerStarttime.setModel(spinnerStarttimeModel);
            SpinnerCalendarEditor editor = new SpinnerCalendarEditor(spinnerStarttime, spinnerStarttimeModel);
            spinnerStarttime.setEditor(editor);
            spinnerEndtimeModel = new SpinnerCalendarModel(timer.getEndTime());
            spinnerEndtime.setModel(spinnerEndtimeModel);
            editor = new SpinnerCalendarEditor(spinnerEndtime, spinnerEndtimeModel);
            spinnerEndtime.setEditor(editor);

            priority.setModel(new SpinnerNumberModel(timer.getPriority(), 0, 99, 1));
            lifetime.setModel(new SpinnerNumberModel(timer.getLifetime(), 0, 99, 1));

            dayChooser.setTimer(timer);

            // set timer uses VPS switch
            if (mode == Mode.NEW) {
                boolean vpsDefault = Boolean.parseBoolean(LazyBones.getProperties().getProperty("vps.default"));
                cbVps.setSelected(vpsDefault);
                setVps(vpsDefault);
            } else {
                cbVps.setSelected(timer.hasState(VDRTimer.VPS));
            }
        }
    }

    public void setProgram(Program prog) {
        this.prog = prog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbVps) {
            setVps(cbVps.isSelected());
            lVpsTimeHint.setVisible(true);
            spinnerStarttime.setBorder(BorderFactory.createLineBorder(Color.RED));
            spinnerStarttime.repaint();
        }
    }

    // FIXME warum wird hier timer.getStartTime().clone() benutzt
    private void setVps(boolean activated) {
        /*
         * to set the right vps time we use start time of the tvb program the start time of the vdr epg should be rather used, but it's to complicated for this
         * simple case
         */
        if (activated) {
            if (prog != null) {
                // VPS needs the unbuffered start time
                timer.setStartTime(prog.getDate().getCalendar());
                timer.getStartTime().set(Calendar.HOUR_OF_DAY, prog.getHours());
                timer.getStartTime().set(Calendar.MINUTE, prog.getMinutes());
                spinnerStarttimeModel.setValue(timer.getStartTime().clone());
                day.setText(Integer.toString(prog.getDate().getDayOfMonth()));
                logger.debug("Setting start time to start time of the TVB-program {}", timer.getStartTime().getTime());
            } else {
                logger.warn("No programm found to determine the VPS time");
            }
        } else {
            if (oldTimer != null) {
                // set the timer to the previous startTime
                spinnerStarttimeModel.setValue(oldTimer.getStartTime().clone());
                day.setText(Integer.toString(oldTimer.getStartTime().get(Calendar.DAY_OF_MONTH)));
                timer.getStartTime().set(Calendar.DAY_OF_MONTH, oldTimer.getStartTime().get(Calendar.DAY_OF_MONTH));
            }
        }
    }

    public Timer getTimer() {
        timer.setFile(title.getText());
        Channel vdrc = null;
        Object selected = channels.getSelectedItem();
        if (selected instanceof devplugin.Channel) {
            devplugin.Channel c = (devplugin.Channel) selected;
            vdrc = ChannelManager.getChannelMapping().get(c.getId());
        } else if (selected instanceof Channel) {
            vdrc = (Channel) selected;
        }
        timer.setChannelNumber(vdrc.getChannelNumber());
        timer.setPriority(((Integer) priority.getValue()).intValue());
        timer.setLifetime(((Integer) lifetime.getValue()).intValue());
        timer.setDescription(description.getText());
        timer.changeStateTo(VDRTimer.ACTIVE, cbActive.isSelected());
        timer.changeStateTo(VDRTimer.VPS, cbVps.isSelected());
        return timer;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            String item = (String) e.getItem();
            if ("VDR".equals(item)) {
                Date date = new Date(timer.getStartTime());
                Program prog = Plugin.getPluginManager().getProgram(date, timer.getTvBrowserProgIDs().get(0));
                Calendar tmpCal = (Calendar) timer.getStartTime().clone();
                tmpCal.set(Calendar.HOUR_OF_DAY, prog.getHours());
                tmpCal.set(Calendar.MINUTE, prog.getMinutes());
                tmpCal.add(Calendar.MINUTE, prog.getLength() / 2); // take the middle of the playtime to be sure, that the right program is chosen
                getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                VDRTimer tmp = ProgramManager.getInstance().getTimerForTime(tmpCal, prog.getChannel());
                String desc = tmp == null ? "" : tmp.getDescription();
                description.setText(desc);
                getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if ("TV-Browser".equals(item)) {
                Date date = new Date(timer.getTimerWithoutBuffers().getStartTime());
                Program prog = null;
                if (this.prog != null) {
                    prog = this.prog;
                } else {
                    prog = Plugin.getPluginManager().getProgram(date, timer.getTvBrowserProgIDs().get(0));
                }

                if (prog != null) {
                    description.setText(prog.getDescription());
                    description.append("\n\n" + prog.getChannel().getCopyrightNotice());
                } else {
                    description.setText("");
                }
            } else if ("Timer".equals(item)) {
                description.setText(oldTimer.getDescription());
            }
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
        title.requestFocus();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == spinnerStarttime) {
            Calendar startTime = (Calendar) spinnerStarttimeModel.getValue();
            day.setText(Integer.toString(startTime.get(Calendar.DAY_OF_MONTH)));
        }
    }
}
