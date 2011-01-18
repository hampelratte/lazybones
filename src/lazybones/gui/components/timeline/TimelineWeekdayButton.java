/* $Id: TimelineWeekdayButton.java,v 1.7 2011-01-18 13:13:55 hampelratte Exp $
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
package lazybones.gui.components.timeline;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JToggleButton;

import lazybones.LazyBones;
import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.utils.Period;
import lazybones.utils.Utilities;

public class TimelineWeekdayButton extends JToggleButton implements Observer {
    
    boolean hasChanged = false;
    
    private Calendar day;
    
    private int timerCount = 0;
    
    private int conflictCount = 0;
    
    private SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
    private SimpleDateFormat longFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    
    public TimelineWeekdayButton(Calendar day) {
        TimerManager.getInstance().addObserver(this);
        setDay(day);
    }
    
    public void update(Observable o, Object arg) {
        if(o instanceof TimerManager) {
            hasChanged = true;
        }
    }
    
    /**
     * Updates the small dots on the button,
     * the tooltip text and the enabled state
     */
    private void updateIndicators() {
        timerCount = 0;
        conflictCount = 0;
        List<Timer> timers = TimerManager.getInstance().getTimers();
        for (Timer timer : timers) {
            if(timerRunsOnThisDay(timer)) {
                timerCount++;
                for (Iterator<Period> iterator = timer.getConflictPeriods().iterator(); iterator.hasNext();) {
                    Period period = (Period) iterator.next();
                    if(Utilities.sameDay(day, period.getStartTime()) || Utilities.sameDay(day, period.getEndTime())) {
                        conflictCount++;
                        break; // only count a timer once, though it has more than one conflict
                    }
                }
            }
        }

        // enable if there is a timer event today
        setEnabled(timerCount > 0);
        
        // update tooltip text
        setToolTipText(LazyBones.getTranslation("weekdayButton.tooltip", 
                "{0} timers with {1} conflicts on {2}", 
                Integer.toString(timerCount),
                Integer.toString(conflictCount),
                longFormat.format(day.getTime() )));
        
        repaint();
    }

    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
        setText(dayFormat.format(day.getTime()));
        updateIndicators();
    }

    private boolean timerRunsOnThisDay(Timer timer) {
        Calendar startTime = timer.getStartTime();
        Calendar endTime = timer.getEndTime();
        if(Utilities.sameDay(startTime, getDay()) || Utilities.sameDay(endTime, getDay())) {
            return true;
        }
        
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(hasChanged) {
            hasChanged = false;
            updateIndicators();
        }
        
        final int INDICATOR_SIZE = 3;
        final int PADDING = 2;
        
        int element_size = INDICATOR_SIZE + PADDING;
        int pos_y = getHeight()- element_size;
        
        int i = 0;
        
        g.setColor(Color.RED);
        for (i = 0; i < conflictCount; i++) {
            g.fillRect(PADDING + i*element_size, pos_y, INDICATOR_SIZE, INDICATOR_SIZE);
        }
        
        g.setColor(Color.BLACK);
        for (; i < timerCount; i++) {
            g.fillRect(PADDING + i*element_size, pos_y, INDICATOR_SIZE, INDICATOR_SIZE);
        }
    }
}
