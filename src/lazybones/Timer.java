/* $Id: Timer.java,v 1.15 2007-05-13 11:17:16 hampelratte Exp $
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import lazybones.utils.Period;

import org.hampelratte.svdrp.responses.highlevel.VDRTimer;

public class Timer extends VDRTimer {
    
    public static final int NO_REASON = 0;
    public static final int NO_EPG = 1;
    public static final int NOT_FOUND = 2;
    public static final int NO_CHANNEL = 3;
    public static final int NO_PROGRAM = 4;
    
    /**
     * The point in time, when this program (not the timer) starts 
     */
    private Calendar unbufferedStartTime = GregorianCalendar.getInstance();

    /**
     * The point in time, when this program (not the timer) ends 
     */
    private Calendar unbufferedEndTime = GregorianCalendar.getInstance();
    
    /**
     * The reason, why this Timer couldn't be assigned
     */
    private int reason = Timer.NO_REASON;
    
    /**
     * List of all TV-B programs assigned to this timer
     */
    private List<String> tvBrowserProgIDs = new ArrayList<String>();
    
    /**
     * Contains all time periods, where this timer conflicts with other timers
     */
    private List<Period> conflictPeriods = new ArrayList<Period>();
    
    public Timer() {}
    
    public Timer(VDRTimer timer) {
        super.setID(timer.getID());
        super.setState(timer.getState());
        super.setChannelNumber(timer.getChannelNumber());
        super.setDescription(timer.getDescription());
        super.setEndTime(timer.getEndTime());
        super.setFile(timer.getFile());
        super.setFirstTime(timer.getFirstTime());
        super.setHasFirstTime(timer.hasFirstTime());
        super.setChannelNumber(timer.getChannelNumber());
        super.setLifetime(timer.getLifetime());
        super.setPath(timer.getPath());
        super.setPriority(timer.getPriority());
        super.setRepeatingDays(timer.getRepeatingDays());
        super.setStartTime(timer.getStartTime());
        super.setTitle(timer.getTitle());
    }
    
    /**
     * Returns the IDs of all TV-Browser programs, which are assigned to this timer
     * @return List of IDs of all TV-Browser programs, which are assigned to this timer
     */
    public List<String> getTvBrowserProgIDs() {
        return tvBrowserProgIDs;
    }

    public void setTvBrowserProgIDs(List<String> tvBrowserProgIDs) {
        this.tvBrowserProgIDs = tvBrowserProgIDs;
    }
    
    /**
     * @return Returns if this timer could be assigned to a Program
     */
    public boolean isAssigned() {
        return tvBrowserProgIDs.size() > 0;
    }

    public Object clone() {
        VDRTimer vdrtimer = (VDRTimer)super.clone();
        Timer clone = new Timer(vdrtimer);
        clone.setTvBrowserProgIDs(getTvBrowserProgIDs());
        clone.setReason(getReason());
        clone.setUnbufferedEndTime(unbufferedEndTime);
        clone.setUnbufferedStartTime(unbufferedStartTime);
        return clone;
    }

    /**
     * @return The reason, why this timer couldn't be assigned
     */
    public int getReason() {
        return reason;
    }

    /**
     * Set the reason why this timer couldn't be assigned
     * @param reason the reason why this timer couldn't be assigned
     * @see #NO_CHANNEL
     * @see #NO_EPG
     * @see #NO_PROGRAM
     * @see #NO_REASON
     * @see #NOT_FOUND
     */
    public void setReason(int reason) {
        this.reason = reason;
    }

    /**
     * 
     * @param id
     */
    public void addTvBrowserProgID(String id) {
        tvBrowserProgIDs.add(id);
    }
    
    /**
     * Returns, if <code>this</code> starts between the start time
     * and the end time of the given timer <code>timer</code>.
     * @param timer
     * @return
     */
    public boolean startsDuringTimer(Timer timer) {
        if (this.getStartTime().compareTo(timer.getStartTime()) >= 0 && 
            this.getStartTime().compareTo(timer.getEndTime()) <= 0) 
        {
            return true;
        }
        return false;
    }

    public List<Period> getConflictPeriods() {
        return conflictPeriods;
    }
    
    public void addConflictPeriod(Period period) {
        if(period.getStartTime().before(getStartTime())) {
            period.setStartTime((Calendar) getStartTime().clone());
        }
        if(period.getEndTime().after(getEndTime())) {
            period.setEndTime((Calendar) getEndTime().clone());
        }
        getConflictPeriods().add(period);
    }
    
    public String getDisplayTitle() {
        return getPath() + getTitle();
        
    }

    public Calendar getUnbufferedEndTime() {
        return unbufferedEndTime;
    }

    public void setUnbufferedEndTime(Calendar unbufferedEndTime) {
        this.unbufferedEndTime = unbufferedEndTime;
    }

    public Calendar getUnbufferedStartTime() {
        return unbufferedStartTime;
    }

    public void setUnbufferedStartTime(Calendar unbufferedStartTime) {
        this.unbufferedStartTime = unbufferedStartTime;
    }
    
    /**
     * 
     * @return This timer without time buffers
     */
    public Timer getTimerWithoutBuffers() {
        Timer timer = (Timer)this.clone();
        int buffer_before = Integer.parseInt(LazyBones.getProperties().getProperty("timer.before"));
        timer.getStartTime().add(Calendar.MINUTE, buffer_before);
        int buffer_after = Integer.parseInt(LazyBones.getProperties().getProperty("timer.after"));
        timer.getEndTime().add(Calendar.MINUTE, -buffer_after);
        return timer;
    }
}
