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
package lazybones.gui.components.timeline;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JToggleButton;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.TimersChangedEvent;
import lazybones.TimersChangedListener;
import lazybones.conflicts.Conflict;
import lazybones.utils.Period;

public class TimelineWeekdayButton extends JToggleButton implements TimersChangedListener {

    boolean hasChanged = false;

    private Calendar day;
    private Calendar selectedDayAtStartHour;
    private Calendar dayAfterAtStartHour;

    private int timerCount = 0;

    private int conflictCount = 0;

    private final SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
    private final SimpleDateFormat longFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private transient TimerManager timerManager;

    public TimelineWeekdayButton(TimerManager timerManager, Calendar day) {
        this.timerManager = timerManager;
        setDay(day);
    }

    @Override
    public void timersChanged(TimersChangedEvent evt) {
    	hasChanged = true;
    	repaint();
    }
    
    /**
     * Updates the small dots on the button, the tooltip text and the enabled state
     */
    private void updateIndicators() {
        timerCount = 0;
        conflictCount = 0;
        List<LazyBonesTimer> timers = timerManager.getTimers();
        for (LazyBonesTimer timer : timers) {
            if (timerRunsOnThisDay(timer)) {
                timerCount++;
                for (Conflict conflict : timer.getConflicts()) {
                    Period period = conflict.getPeriod();
                    Calendar startTime = period.getStartTime();
                    Calendar endTime = period.getEndTime();
                    if (startTime.after(selectedDayAtStartHour) && startTime.before(dayAfterAtStartHour)
                            || endTime.after(selectedDayAtStartHour) && endTime.before(dayAfterAtStartHour)) {
                        conflictCount++;
                        break; // only count a timer once, though it has more than one conflict
                    }
                }
            }
        }

        // enable if there is a timer event today
        setEnabled(timerCount > 0);

        // update tooltip text
        setToolTipText(LazyBones.getTranslation("weekdayButton.tooltip", "{0} timers with {1} conflicts on {2}", Integer.toString(timerCount),
                Integer.toString(conflictCount), longFormat.format(day.getTime())));

        repaint();
    }

    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
        setText(dayFormat.format(day.getTime()));

        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        selectedDayAtStartHour = (Calendar) day.clone();
        selectedDayAtStartHour.set(Calendar.HOUR_OF_DAY, startHour);
        dayAfterAtStartHour = (Calendar) selectedDayAtStartHour.clone();
        dayAfterAtStartHour.add(Calendar.DAY_OF_MONTH, 1);

        updateIndicators();
    }

    private boolean timerRunsOnThisDay(LazyBonesTimer timer) {
        Calendar startTime = timer.getStartTime();
        Calendar endTime = timer.getEndTime();
        return (startTime.after(selectedDayAtStartHour) && startTime.before(dayAfterAtStartHour)
                || endTime.after(selectedDayAtStartHour) && endTime.before(dayAfterAtStartHour));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (hasChanged) {
            hasChanged = false;
            updateIndicators();
        }

        final int INDICATOR_SIZE = 3;
        final int PADDING = 2;

        int elementSize = INDICATOR_SIZE + PADDING;
        int posY = getHeight() - elementSize;

        int i = 0;

        g.setColor(Color.RED);
        for (i = 0; i < conflictCount; i++) {
            g.fillRect(PADDING + i * elementSize, posY, INDICATOR_SIZE, INDICATOR_SIZE);
        }

        g.setColor(Color.BLACK);
        for (; i < timerCount; i++) {
            g.fillRect(PADDING + i * elementSize, posY, INDICATOR_SIZE, INDICATOR_SIZE);
        }
    }
}
