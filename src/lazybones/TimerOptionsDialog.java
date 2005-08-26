/* $Id: TimerOptionsDialog.java,v 1.4 2005-08-26 17:14:48 hampelratte Exp $
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;

import javax.swing.*;

import tvbrowser.core.ChannelList;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;
import devplugin.Channel;
import devplugin.Date;
import devplugin.Plugin;
import devplugin.Program;

/**
 * Shown, if a timer should be edited.
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class TimerOptionsDialog extends Thread implements ActionListener,
        MouseListener {
    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(TimerOptionsDialog.class);

    private JLabel lChannels = new JLabel(mLocalizer.msg("channel", "Channel"));

    private JComboBox channels = new JComboBox();

    private JLabel lDay = new JLabel(mLocalizer.msg("day", "Day"));

    private DayChooser dayChooser;

    private BrowseTextField day;

    private JLabel lStarttime = new JLabel(mLocalizer.msg("start", "Start"));

    private JSpinner starttime = new JSpinner();

    private JLabel lEndtime = new JLabel(mLocalizer.msg("stop", "Stop"));

    private JSpinner endtime = new JSpinner();

    private JLabel lPriority = new JLabel(mLocalizer.msg("priority",
            "Priorität"));

    private JSpinner priority = new JSpinner();

    private JLabel lLifetime = new JLabel(mLocalizer
            .msg("lifetime", "Lifetime"));

    private JSpinner lifetime = new JSpinner();

    private JLabel lTitle = new JLabel(mLocalizer.msg("title", "Title"));

    private JTextField title = new JTextField();

    private JTextArea description = new JTextArea();

    private JPopupMenu popupmenu = new JPopupMenu();

    private JMenuItem tvbDescription = new JMenuItem(mLocalizer.msg(
            "tvbDescription", "Take TV-Browsers' description"));

    private JMenuItem vdrDescription = new JMenuItem(mLocalizer.msg(
            "vdrDescription", "Take VDRs' description"));

    private JButton ok = new JButton();

    private JButton cancel = new JButton();

    private LazyBones control;

    private JDialog dialog;

    private JPanel panel = new JPanel();

    private VDRTimer timer;

    private boolean confirmation = false;

    private boolean update = false;

    public TimerOptionsDialog(LazyBones control, VDRTimer timer, boolean update) {
        this.update = update;
        this.control = control;
        this.timer = timer;
        dayChooser = new DayChooser(timer);
        day = new BrowseTextField(dayChooser);
        initGUI();
    }

    private void initGUI() {
        dialog = new JDialog(control.getParent(), true);
        dialog.setTitle(mLocalizer.msg("windowtitle", "Timer Options"));
        panel.setLayout(new GridBagLayout());
        dialog.getContentPane().add(panel);
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
        panel.add(lTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(lChannels, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lDay, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lStarttime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lEndtime, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lPriority, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lLifetime, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(channels, gbc);
        devplugin.Channel[] c = ChannelList.getSubscribedChannels();
        for (int i = 0; i < c.length; i++) {
            channels.addItem(c[i]);
        }
        Date date;
        if (timer.isRepeating()) {
            date = new Date(timer.getProgTime());
        } else {
            date = new Date(timer.getStartTime());
        }

        Program prog = Plugin.getPluginManager().getProgram(date,
                timer.getTvBrowserProgID());

        channels.setSelectedItem(prog.getChannel());

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        description.setRows(10);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        if ("".equals(timer.getDescription())
                && !"".equals(prog.getDescription())) {
            description.setText(prog.getDescription());
            description.append("\n\n" + prog.getChannel().getCopyrightNotice());
        } else {
            description.setText(timer.getDescription());
        }
        panel.add(new JScrollPane(description), gbc);
        popupmenu.add(tvbDescription);
        tvbDescription.addActionListener(this);
        popupmenu.add(vdrDescription);
        vdrDescription.addActionListener(this);
        popupmenu.setLabel(mLocalizer.msg("chooseDescription",
                "Choose Description"));
        description.addMouseListener(this);
        if (!update) {
            description.setToolTipText(mLocalizer.msg(
                    "chooseDescriptionTooltip",
                    "Use right mouse button to choose the description"));
        }

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.weighty = 0.1;
        panel.add(cancel, gbc);

        gbc.weightx = 1.0;

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(title, gbc);
        title.setText(timer.getFile());

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(day, gbc);
        day.setText(timer.getDayString());
        day.setEditable(false);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(starttime, gbc);
        SpinnerTimeModel model = new SpinnerTimeModel();
        int hour = timer.getStartTime().get(Calendar.HOUR_OF_DAY);
        int minute = timer.getStartTime().get(Calendar.MINUTE);
        model.setValue(new Time(hour, minute));
        starttime.setModel(model);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(endtime, gbc);
        model = new SpinnerTimeModel();
        hour = timer.getEndTime().get(Calendar.HOUR_OF_DAY);
        minute = timer.getEndTime().get(Calendar.MINUTE);
        model.setValue(new Time(hour, minute));
        endtime.setModel(model);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(priority, gbc);
        priority
                .setModel(new SpinnerNumberModel(timer.getPriority(), 0, 99, 1));

        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(lifetime, gbc);
        lifetime
                .setModel(new SpinnerNumberModel(timer.getLifetime(), 0, 99, 1));

        gbc.gridx = 1;
        gbc.gridy = 8;
        panel.add(ok, gbc);

        ok.setText(mLocalizer.msg("ok", "OK"));
        cancel.setText(mLocalizer.msg("cancel", "Cancel"));

        ok.addActionListener(this);
        cancel.addActionListener(this);

        if (update) {
            day.setEnabled(false);
            channels.setEnabled(false);
            starttime.setEnabled(false);
            endtime.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) {
            confirmation = true;
            timer.setFile(title.getText());
            Channel c = (Channel) channels.getSelectedItem();
            VDRChannel vdrc = (VDRChannel) control.getChannelMapping().get(
                    c.getId());
            timer.setChannel(vdrc.getId());
            Calendar start = timer.getStartTime();
            start.set(Calendar.HOUR_OF_DAY, ((Time) starttime.getValue())
                    .getHour());
            start.set(Calendar.MINUTE, ((Time) starttime.getValue())
                    .getMinute());
            timer.setStartTime(start);
            Calendar end = timer.getEndTime();
            end
                    .set(Calendar.HOUR_OF_DAY, ((Time) endtime.getValue())
                            .getHour());
            end.set(Calendar.MINUTE, ((Time) endtime.getValue()).getMinute());
            timer.setEndTime(end);
            timer.setPriority(((Integer) priority.getValue()).intValue());
            timer.setLifetime(((Integer) lifetime.getValue()).intValue());
            timer.setDescription(description.getText());
            dialog.dispose();
        } else if (e.getSource() == cancel) {
            confirmation = false;
            dialog.dispose();
        } else if (e.getSource() == tvbDescription) {
            Date date;
            if (timer.isRepeating()) {
                date = new Date(timer.getProgTime());
            } else {
                date = new Date(timer.getStartTime());
            }
            Program prog = Plugin.getPluginManager().getProgram(date,
                    timer.getTvBrowserProgID());
            description.setText(prog.getDescription());
            description.append("\n\n" + prog.getChannel().getCopyrightNotice());
        } else if (e.getSource() == vdrDescription) {
            /*
             * HAMPELRATTE funzt noch nicht Date date; if(timer.isRepeating()) {
             * date = new Date(timer.getProgTime()); } else { date = new
             * Date(timer.getStartTime()); } Program prog =
             * Plugin.getPluginManager().getProgram(date,
             * timer.getTvBrowserProgID()); Calendar tmpCal =
             * GregorianCalendar.getInstance(); if(timer.isRepeating()) {
             * tmpCal.setTimeInMillis(timer.getProgTime().getTimeInMillis()); }
             * else {
             * tmpCal.setTimeInMillis(timer.getStartTime().getTimeInMillis()); }
             * tmpCal.add(Calendar.SECOND, 30); // add 30 seconds to be sure,
             * that the right program is chosen VDRTimer tmp =
             * control.getVDRProgramAt(tmpCal, prog.getChannel()); String desc =
             * tmp == null ? "" : tmp.getDescription();
             * description.setText(desc);
             */
            description.setText(timer.getDescription());
        }
    }

    public void start() {
        dialog.setSize(400, 500);
        dialog.setLocation(50, 50);
        dialog.setVisible(true);
        dialog.pack();
    }

    public boolean getConfirmation() {
        return confirmation;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3 && !update) {
            popupmenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }
}