/* $Id: TimelineWeekdayButton.java,v 1.2 2007-05-27 20:49:32 hampelratte Exp $
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
package lazybones.gui.components.timeline;

import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JToggleButton;

import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.utils.Utilities;

public class TimelineWeekdayButton extends JToggleButton implements Observer {
    
    private Calendar day;
    
    private int timerCount = 0;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.getDefault());
    
    public TimelineWeekdayButton(Calendar day) {
        setDay(day);
        setEnabled(false);
        TimerManager.getInstance().addObserver(this);
        updateIndicators();
    }
    
    public void update(Observable o, Object arg) {
        if(o instanceof TimerManager) {
            updateIndicators();
        }
    }
    
    private void updateIndicators() {
        setTimerCount(0);
        List<Timer> timers = TimerManager.getInstance().getTimers();
        for (Iterator iter = timers.iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            if(timerRunsToday(timer)) {
                timerCount++;
            }
        }

        // enable if there is a timer event today
        setEnabled(getTimerCount() > 0);
        
        repaint();
    }

    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
        setText(sdf.format(day.getTime()));
    }

    public int getTimerCount() {
        return timerCount;
    }

    public void setTimerCount(int timerCount) {
        this.timerCount = timerCount;
    }
    
    private boolean timerRunsToday(Timer timer) {
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
        
        final int INDICATOR_SIZE = 3;
        final int PADDING = 2;
        
        int element_size = INDICATOR_SIZE + PADDING;
        int pos_y = getHeight()- element_size;
        
        
        for (int i = 0; i < getTimerCount(); i++) {
            g.fillRect(PADDING + i*element_size, pos_y, INDICATOR_SIZE, INDICATOR_SIZE);
        }
    }
    
    
}
