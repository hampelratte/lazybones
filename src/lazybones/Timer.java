/* $Id: Timer.java,v 1.12 2007-03-19 17:20:41 hampelratte Exp $
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
import java.util.List;

import org.hampelratte.svdrp.responses.highlevel.VDRTimer;

import lazybones.utils.Period;

public class Timer extends VDRTimer {
    
    public static final int NO_REASON = 0;
    public static final int NO_EPG = 1;
    public static final int NOT_FOUND = 2;
    public static final int NO_CHANNEL = 3;
    public static final int NO_PROGRAM = 4;
    
    private int reason = Timer.NO_REASON;
    
    private ArrayList<String> tvBrowserProgIDs = new ArrayList<String>();
    
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
    
    public ArrayList<String> getTvBrowserProgIDs() {
        return tvBrowserProgIDs;
    }

    public void setTvBrowserProgIDs(ArrayList<String> tvBrowserProgIDs) {
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
        return clone;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

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
}
