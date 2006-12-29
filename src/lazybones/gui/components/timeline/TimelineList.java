/* $Id: TimelineList.java,v 1.2 2006-12-29 23:36:57 hampelratte Exp $
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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.TimersChangedEvent;

public class TimelineList extends JPanel implements Observer {
    private ArrayList<Timer> data = new ArrayList<Timer>();
    private ArrayList<TimelineListener> listeners = new ArrayList<TimelineListener>();
    private Calendar calendar = new GregorianCalendar();
    private int rowHeight = 40;
    private int padding = 0;

    public TimelineList(int rowHeight, int padding) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        setBackground(Color.WHITE);
        this.rowHeight = rowHeight;
        this.padding = padding;
        setLayout(new TimelineLayout(rowHeight, padding));
        TimerManager.getInstance().addObserver(this);
    }

    public int getRowCount() {
        return data.size();
    }

    public void addTimer(Timer timer) {
        for (int i = 0; i < data.size(); i++) {
            Timer t = (Timer) data.get(i);
            if(t.getChannelNumber() > timer.getChannelNumber()) {
                data.add(i, timer);
                fireTimelineChanged();
                return;
            }
        }
        
        // the channelNumber was greater than the channelNumber of the other timers
        // -> add this timer at the end of the table
        data.add(timer);
        fireTimelineChanged();
    }
    
    public void removeTimer(Timer timer) {
        int index = data.indexOf(timer);
        if(index >= 0) {
            data.remove(timer);
            fireTimelineChanged();
        }
    }
    
    public void clear() {
        data.clear();
        fireTimelineChanged();
    }
    
    private void fireTimelineChanged() {
        this.removeAll();
        for (Timer timer : data) {
            TimelineElement te = new TimelineElement(timer, getCalendar());
            add(te);
        }
        
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            TimelineListener l = (TimelineListener) iter.next();
            l.timelineChanged(data);
        }
        
        revalidate();
        repaint();
    }
    
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // paint row background
        Color altColor = new Color(250, 250, 220);
        for (int i = 0; i < getComponentCount(); i++) {
            g.setColor( i % 2 == 0 ? Color.WHITE : altColor);
            g.fillRect(0, i*(rowHeight + padding), getWidth(), (rowHeight + padding));
        }
        
        // paint vertical lines
        g.setColor(Color.LIGHT_GRAY);
        double pixelsPerHour = (double)(getWidth()-1) / (double)24;
        for (int i = 0; i < 25; i++) {
            g.drawLine((int)(i * pixelsPerHour), 0, (int)(i * pixelsPerHour), getHeight());
        }
    }

    public void showTimersForCurrentDate(ArrayList<Timer> timers) {
        clear();
        for (Timer timer : timers) {
            if(runsOnCurrentDate(timer)) {
                addTimer(timer);
            }
        }
    }
    
    public void update(Observable o, Object arg) {
        if(o == TimerManager.getInstance()) {
            if(arg instanceof TimersChangedEvent) {
                TimersChangedEvent tce = (TimersChangedEvent) arg;
                switch(tce.getType()) {
                case TimersChangedEvent.ALL:
                    ArrayList<Timer> timers = tce.getTimers();
                    clear();
                    for (Timer timer : timers) {
                        if(runsOnCurrentDate(timer)) {
                            addTimer(timer);
                        }
                    }
                    break;
                case TimersChangedEvent.TIMER_ADDED:
                    Timer timer = tce.getTimer();
                    if(runsOnCurrentDate(timer)) {
                        addTimer(timer);
                    }
                    break;
                case TimersChangedEvent.TIMER_REMOVED:
                    timer = tce.getTimer();
                    if(runsOnCurrentDate(timer)) {
                        removeTimer(timer);
                    }
                    break;
                }
            }
        }
    }
    
    private boolean runsOnCurrentDate(Timer timer) {
        Calendar nextDate = (Calendar) getCalendar().clone();
        nextDate.add(Calendar.DAY_OF_MONTH, 1);
        
        if( timer.getStartTime().after(getCalendar()) & timer.getStartTime().before(nextDate) 
                || timer.getEndTime().after(getCalendar()) & timer.getEndTime().before(nextDate)) 
        {
            return true;
        }
        
        return false;
    }
    
    public void addTimelineListener(TimelineListener l) {
        listeners.add(l);
    }
    
    public void removeTimelineListener(TimelineListener l) {
        listeners.remove(l);
    }
}
