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
package lazybones.gui.components.timeroptions;

import static lazybones.gui.settings.DescriptionSelectorItem.TIMER;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_DESC;
import static lazybones.gui.settings.DescriptionSelectorItem.TVB_PREFIX;
import static lazybones.gui.settings.DescriptionSelectorItem.VDR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import devplugin.Plugin;
import devplugin.Program;
import devplugin.ProgramFieldType;
import lazybones.ChannelManager;
import lazybones.ChannelManager.ChannelNotFoundException;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.RecordingManager;
import lazybones.TimerManager;
import lazybones.gui.components.daychooser.BrowseTextField;
import lazybones.gui.components.daychooser.DayChooser;
import lazybones.gui.components.historycombobox.SuggestingJHistoryComboBox;
import lazybones.gui.components.timeroptions.TimerOptionsDialog.Mode;
import lazybones.gui.settings.DescriptionComboBoxModel;
import lazybones.gui.settings.DescriptionSelectorItem;
import lazybones.gui.settings.SeriesTitleSelectorItem;
import lazybones.programmanager.ProgramDatabase;
import lazybones.programmanager.ProgramManager;
import tvbrowser.core.ChannelList;
import util.ui.Localizer;

public class TimerOptionsEditor extends JPanel implements ActionListener, ItemListener, WindowListener, ChangeListener {
    private static transient Logger logger = LoggerFactory.getLogger(TimerOptionsEditor.class);

    private final JLabel lChannels = new JLabel(Localizer.getLocalization(Localizer.I18N_CHANNEL));

    private final JComboBox<Object> channels = new JComboBox<Object>();

    private final JLabel lDay = new JLabel(LazyBones.getTranslation("day", "Day"));

    private DayChooser dayChooser;

    private BrowseTextField day;

    private final JLabel lStarttime = new JLabel(LazyBones.getTranslation("start", "Start"));

    private SpinnerCalendarModel spinnerStarttimeModel;// = new SpinnerCalendarModel();
    private JSpinner spinnerStarttime;// = new JSpinner(spinnerStarttimeModel);

    private final JLabel lEndtime = new JLabel(LazyBones.getTranslation("stop", "Stop"));

    private SpinnerCalendarModel spinnerEndtimeModel;// = new SpinnerCalendarModel();
    private JSpinner spinnerEndtime; // = new JSpinner(spinnerEndtimeModel);

    private final JLabel lPriority = new JLabel(LazyBones.getTranslation("priority", "Priority"));

    private final JSpinner priority = new JSpinner();

    private final JLabel lLifetime = new JLabel(LazyBones.getTranslation("lifetime", "Lifetime"));

    private final JSpinner lifetime = new JSpinner();

    private final JLabel lTitle = new JLabel(LazyBones.getTranslation("title", "Title"));

    private final JTextField title = new JTextField();

    private final JLabel lDescription = new JLabel(LazyBones.getTranslation("description", "Description"));

    private DescriptionComboBoxModel comboDescModel;
    private final JComboBox<DescriptionSelectorItem> comboDesc = new JComboBox<DescriptionSelectorItem>();

    private final JTextArea description = new JTextArea();

    private final JCheckBox cbActive = new JCheckBox(LazyBones.getTranslation("active", "Active"));

    private final JLabel lVpsTimeHint = new JLabel(LazyBones.getTranslation("vpsTimeHint", "Starttime has been changed for VPS"));

    private final JCheckBox cbVps = new JCheckBox(LazyBones.getTranslation("vps", "VPS"));

    private final JCheckBox cbSeries = new JCheckBox(LazyBones.getTranslation("series", "Series"));

    private final JLabel lDirectory = new JLabel(LazyBones.getTranslation("directory", "Directory"));

    private SuggestingJHistoryComboBox comboDirectory;

    private TimerManager timerManager;
    private RecordingManager recordingManager;

    /**
     * The actual timer
     */
    private LazyBonesTimer timer;

    /**
     * A clone of the timer containing the old settings
     */
    private LazyBonesTimer oldTimer;

    private Program prog;

    private final Mode mode;

    private String originalTitel = "";
    private String originalPath = "";

    public TimerOptionsEditor(TimerManager timerManager, RecordingManager recordingManager, LazyBonesTimer timer, Mode mode) {
        this.timerManager = timerManager;
        this.recordingManager = recordingManager;
        this.mode = mode;

        logger.debug("Creating timer options panel");
        initGUI();

        setTimer(timer);

        logger.debug("Timer options panel ready");
    }

    public TimerOptionsEditor(TimerManager timerManager, RecordingManager recordingManager, Mode mode) {
        this.timerManager = timerManager;
        this.recordingManager = recordingManager;
        this.mode = mode;
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        int row = 0;
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        add(createCheckboxGrid(), gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(lTitle, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lDirectory, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lChannels, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lDay, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        add(lVpsTimeHint, gbc);
        lVpsTimeHint.setForeground(Color.RED);
        lVpsTimeHint.setHorizontalAlignment(JLabel.TRAILING);
        lVpsTimeHint.setVisible(false);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(lStarttime, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lEndtime, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lPriority, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lLifetime, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        add(lDescription, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        description.setRows(10);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        add(new JScrollPane(description), gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 1;
        row = 1;
        gbc.gridy = row++;
        add(title, gbc);

        comboDirectory = new SuggestingJHistoryComboBox(Arrays.asList(""));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(comboDirectory, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        add(channels, gbc);
        devplugin.Channel[] c = ChannelList.getSubscribedChannels();
        for (int i = 0; i < c.length; i++) {
            channels.addItem(c[i]);
        }

        gbc.gridx = 1;
        gbc.gridy = row++;
        dayChooser = new DayChooser();
        day = new BrowseTextField(dayChooser);
        day.setEditable(false);
        add(day, gbc);

        gbc.gridx = 1;
        row++;
        gbc.gridy = row++;
        spinnerStarttime = new JSpinner();
        spinnerStarttime.addChangeListener(this);
        add(spinnerStarttime, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        spinnerEndtime = new JSpinner();
        spinnerEndtime.addChangeListener(this);
        add(spinnerEndtime, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        add(priority, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        add(lifetime, gbc);

        gbc.gridx = 1;
        gbc.gridy = row++;
        add(comboDesc, gbc);

        comboDescModel = new DescriptionComboBoxModel(false, mode == Mode.UPDATE);
        comboDesc.setModel(comboDescModel);
        comboDesc.addItemListener(this);
    }

    private Component createCheckboxGrid() {
        JPanel checkboxGrid = new JPanel(new GridLayout(1, 3));

        checkboxGrid.add(cbActive);

        cbVps.addActionListener(this);
        checkboxGrid.add(cbVps);

        cbSeries.addActionListener(this);
        checkboxGrid.add(cbSeries);

        return checkboxGrid;
    }

    public void setTimer(LazyBonesTimer timer) {
        logger.debug("Updating gui with timer values");
        if (timer != null) {
            this.timer = timer;
            this.prog = null;
            this.oldTimer = (LazyBonesTimer) timer.clone();
            lVpsTimeHint.setVisible(false);
            spinnerStarttime.setBorder(spinnerEndtime.getBorder());
            spinnerStarttime.repaint();

            // we have to remove the buffers again, to get the right start date
            // example: start time is 00.00 h with time buffers we have 23.45
            // Calendar then decreases the start date, so that we don't have the right date, but
            // the date of the day before.
            LazyBonesTimer tmp = timer.getTimerWithoutBuffers();
            Program prog = null;
            try {
                prog = ProgramDatabase.getProgram(tmp);
                this.prog = prog;
            } catch (ChannelNotFoundException e) {
                // fail silently
            }

            if (prog != null) {
                channels.setSelectedItem(prog.getChannel());
            } else {
                Channel chan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
                channels.addItem(chan);
                channels.setSelectedItem(chan);
            }

            // set the description
            if (mode == Mode.UPDATE) {
                description.setText(oldTimer.getDescription());
                comboDescModel.setSelected(DescriptionSelectorItem.TIMER);
            } else {
                String descVdr = timer.getDescription() == null ? "" : timer.getDescription();
                String selectedDescriptionId = LazyBones.getProperties().getProperty("descSourceTvb");
                String descriptionText = LazyBonesTimer.createDescription(selectedDescriptionId, descVdr, prog);

                description.setText(descriptionText);
                comboDescModel.setSelected(selectedDescriptionId);
            }

            // set timer is active switch
            cbActive.setSelected(timer.isActive());

            // set timer title
            title.setText(timer.getTitle());

            // set path
            List<String> dirSuggestions = new ArrayList<String>();
            // add directory suggestions from existing recordings
            List<Recording> recordings = recordingManager.getRecordings();
            if (recordings != null) { // if the user is fast enough, the recordings might not have been loaded
                for (Recording recording : recordings) {
                    // abuse Timer class to parse path and title
                    Timer dummy = new Timer();
                    dummy.setFile(recording.getTitle());
                    if (!dummy.getPath().isEmpty()) {
                        dirSuggestions.add(dummy.getPath());
                    }
                }
            }
            // add directory suggestions from upcoming timers
            List<LazyBonesTimer> timers = timerManager.getTimers();
            for (LazyBonesTimer _timer : timers) {
                if (!_timer.getPath().isEmpty()) {
                    dirSuggestions.add(_timer.getPath());
                }
            }
            Collections.sort(dirSuggestions);
            Collections.reverse(dirSuggestions);
            for (String suggestion : dirSuggestions) {
                comboDirectory.addItem(suggestion);
            }
            // add suggestions from the default directory setting history
            XStream xstream = new XStream();
            try {
                @SuppressWarnings("unchecked")
                List<String> defaultDirectoryHistory = (List<String>) xstream.fromXML(LazyBones.getProperties().getProperty("default.directory.history"));
                for (String dir : defaultDirectoryHistory) {
                    comboDirectory.addItem(dir);
                }
            } catch (Exception e) {
                logger.warn("Couldn't load history of default directories", e);
            }
            comboDirectory.setText(timer.getPath().replace('~', '/'));

            // if title is EPISODE and path is TITLE, this is a series
            cbSeries.setSelected("TITLE".equals(timer.getPath()) && "EPISODE".equals(timer.getTitle()));

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
            day.setText(timer.getDayString());

            // set timer uses VPS switch
            if (mode == Mode.NEW) {
                boolean vpsDefault = Boolean.parseBoolean(LazyBones.getProperties().getProperty("vps.default"));
                cbVps.setSelected(vpsDefault);
                setVps(vpsDefault);
            } else {
                cbVps.setSelected(timer.hasState(Timer.VPS));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbVps) {
            setVps(cbVps.isSelected());
            lVpsTimeHint.setVisible(true);
            spinnerStarttime.setBorder(BorderFactory.createLineBorder(Color.RED));
            spinnerStarttime.repaint();
        } else if (e.getSource() == cbSeries) {
            if (cbSeries.isSelected()) {
                originalTitel = title.getText();
                originalPath = comboDirectory.getText();

                String format = LazyBones.getProperties().getProperty("timer.series.title");
                if(SeriesTitleSelectorItem.TVB.equals(format)) {
                    String episodeNumber = prog.getIntFieldAsString(ProgramFieldType.EPISODE_NUMBER_TYPE);
                    String episode = prog.getTextField(ProgramFieldType.EPISODE_TYPE);
                    String series = prog.getTitle();
                    logger.debug("Series {}- {} {}", new Object[] {series, episodeNumber, episode});

                    if(isNotBlank(episodeNumber) || isNotBlank(episode)) {
                        StringBuilder episodeSb = new StringBuilder();
                        if(isNotBlank(episodeNumber)) {
                            episodeSb.append(episodeNumber);
                            if(isNotBlank(episode)) {
                                episodeSb.append(" - ");
                            }
                        }
                        if(isNotBlank(episode)) {
                            episodeSb.append(episode);
                        }
                        title.setText(episodeSb.toString());
                        comboDirectory.setText(prog.getTitle());
                    } else {
                        // fallback to VDR keywords
                        title.setText("EPISODE");
                        comboDirectory.setText("TITLE");
                    }
                } else {
                    title.setText("EPISODE");
                    comboDirectory.setText("TITLE");
                }



            } else {
                if (originalTitel.trim().isEmpty() && prog != null) {
                    title.setText(prog.getTitle());
                } else {
                    title.setText(originalTitel);
                }
                comboDirectory.setText(originalPath);
            }
        }
    }

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
            if (oldTimer != null && !oldTimer.hasState(Timer.VPS)) {
                // set the timer to the previous startTime
                Calendar startTime = (Calendar) oldTimer.getStartTime().clone();
                spinnerStarttimeModel.setValue(startTime);
                day.setText(Integer.toString(oldTimer.getStartTime().get(Calendar.DAY_OF_MONTH)));
                timer.setStartTime(startTime);
            } else {
                if (prog != null) {
                    Calendar calStart = prog.getDate().getCalendar();
                    calStart.set(Calendar.HOUR_OF_DAY, prog.getHours());
                    calStart.set(Calendar.MINUTE, prog.getMinutes());
                    // start the recording x min before the beginning of the program
                    int bufferBefore = Integer.parseInt(LazyBones.getProperties().getProperty("timer.before"));
                    calStart.add(Calendar.MINUTE, -bufferBefore);

                    timer.setStartTime(calStart);
                    spinnerStarttimeModel.setValue(timer.getStartTime());
                }
            }
        }
    }

    public LazyBonesTimer getOldTimer() {
        return oldTimer;
    }

    public LazyBonesTimer getTimer() {
        timer.setTitle(title.getText());
        timer.setPath(comboDirectory.getText().trim().replace('/', '~'));
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
        timer.changeStateTo(Timer.ACTIVE, cbActive.isSelected());
        timer.changeStateTo(Timer.VPS, cbVps.isSelected());

        timer.getStartTime().set(Calendar.HOUR_OF_DAY, ((Calendar) spinnerStarttimeModel.getValue()).get(Calendar.HOUR_OF_DAY));
        timer.getStartTime().set(Calendar.MINUTE, ((Calendar) spinnerStarttimeModel.getValue()).get(Calendar.MINUTE));
        timer.getStartTime().set(Calendar.SECOND, 0);
        timer.getEndTime().set(Calendar.HOUR_OF_DAY, ((Calendar) spinnerEndtimeModel.getValue()).get(Calendar.HOUR_OF_DAY));
        timer.getEndTime().set(Calendar.MINUTE, ((Calendar) spinnerEndtimeModel.getValue()).get(Calendar.MINUTE));
        timer.getEndTime().set(Calendar.SECOND, 0);
        return timer;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            DescriptionSelectorItem selectedDescription = (DescriptionSelectorItem) e.getItem();

            if (selectedDescription.getId().equals(TIMER)) {
                description.setText(oldTimer.getDescription());
            } else if (selectedDescription.getId().equals(VDR)) {
                Program prog = Plugin.getPluginManager().getProgram(timer.getTvBrowserProgIDs().get(0));
                Calendar tmpCal = (Calendar) timer.getStartTime().clone();
                tmpCal.set(Calendar.HOUR_OF_DAY, prog.getHours());
                tmpCal.set(Calendar.MINUTE, prog.getMinutes());
                tmpCal.add(Calendar.MINUTE, prog.getLength() / 2); // take the middle of the playtime to be sure, that the right program is chosen
                getParent().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                Timer tmp = ProgramManager.getInstance().getTimerForTime(tmpCal, prog.getChannel());
                String desc = tmp == null ? "" : tmp.getDescription();
                description.setText(desc);
                getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else if (selectedDescription.getId().equals(TVB_DESC)) {
                Program prog = null;
                if (this.prog != null) {
                    prog = this.prog;
                } else {
                    prog = Plugin.getPluginManager().getProgram(timer.getTvBrowserProgIDs().get(0));
                }

                if (prog != null && prog.getDescription() != null) {
                    description.setText(prog.getDescription());
                } else {
                    description.setText("");
                }
            } else if (selectedDescription.getId().startsWith(TVB_PREFIX)) {
                Program prog = null;
                if (this.prog != null) {
                    prog = this.prog;
                } else {
                    prog = Plugin.getPluginManager().getProgram(timer.getTvBrowserProgIDs().get(0));
                }
                String desc = LazyBonesTimer.createDescription(selectedDescription.getId(), "", prog);
                description.setText(desc);
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
