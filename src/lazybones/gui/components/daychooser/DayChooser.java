/*
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
package lazybones.gui.components.daychooser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.hampelratte.svdrp.responses.highlevel.Timer;

import lazybones.LazyBones;
import net.sf.nachocalendar.CalendarFactory;
import net.sf.nachocalendar.components.DatePanel;
import net.sf.nachocalendar.event.DateSelectionEvent;
import net.sf.nachocalendar.event.DateSelectionListener;
import net.sf.nachocalendar.model.DateSelectionModel;

public class DayChooser extends BrowsePanel implements ActionListener, DateSelectionListener {

    private static final long serialVersionUID = -2936338063641916673L;

    private Timer timer;

    private final JCheckBox monday = new JCheckBox(LazyBones.getTranslation("monday", "Monday"));
    private final JCheckBox tuesday = new JCheckBox(LazyBones.getTranslation("tuesday", "Tuesday"));
    private final JCheckBox wednesday = new JCheckBox(LazyBones.getTranslation("wednesday", "Wednesday"));
    private final JCheckBox thursday = new JCheckBox(LazyBones.getTranslation("thursday", "Thursday"));
    private final JCheckBox friday = new JCheckBox(LazyBones.getTranslation("friday", "Friday"));
    private final JCheckBox saturday = new JCheckBox(LazyBones.getTranslation("saturday", "Saturday"));
    private final JCheckBox sunday = new JCheckBox(LazyBones.getTranslation("sunday", "Sunday"));

    private final DatePanel cal = CalendarFactory.createDatePanel();

    public DayChooser() {
        initGUI();
    }

    public DayChooser(Timer timer) {
        setTimer(timer);
        initGUI();
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    private void initGUI() {
        JPanel days = new JPanel();
        days.add(monday);
        days.add(tuesday);
        days.add(wednesday);
        days.add(thursday);
        days.add(friday);
        days.add(saturday);
        days.add(sunday);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        add(days, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(cal, gbc);

        monday.addActionListener(this);
        tuesday.addActionListener(this);
        wednesday.addActionListener(this);
        thursday.addActionListener(this);
        friday.addActionListener(this);
        saturday.addActionListener(this);
        sunday.addActionListener(this);

        cal.setSelectionMode(DateSelectionModel.SINGLE_SELECTION);
        cal.getDateSelectionModel().addDateSelectionListener(this);

        this.setSize(300, 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        userInputHappened();
    }

    private void updateTimer() {
        boolean[] repeatingDays = timer.getRepeatingDays();
        repeatingDays[0] = monday.isSelected();
        repeatingDays[1] = tuesday.isSelected();
        repeatingDays[2] = wednesday.isSelected();
        repeatingDays[3] = thursday.isSelected();
        repeatingDays[4] = friday.isSelected();
        repeatingDays[5] = saturday.isSelected();
        repeatingDays[6] = sunday.isSelected();
    }

    @Override
    public void valueChanged(DateSelectionEvent e) {
        Object o = cal.getDateSelectionModel().getSelectedDate();
        if (o != null) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cal.getDate().getTime());

            if (timer.isRepeating()) {
                timer.setFirstTime(startDate);
                timer.setHasFirstTime(true);
            } else {
                Calendar start = timer.getStartTime();
                Calendar end = timer.getEndTime();

                long startEndDiff = end.getTimeInMillis() - start.getTimeInMillis();

                start.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
                start.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
                start.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));

                end.setTimeInMillis(start.getTimeInMillis());
                end.add(Calendar.MILLISECOND, (int) startEndDiff);
            }

            userInputHappened();
        }
    }

    private void userInputHappened() {
        updateTimer();
        String dayString = timer.getDayString();
        super.textfield.setText(dayString);
    }
}