/* $Id: Timer.java,v 1.1 2005-11-26 01:29:06 hampelratte Exp $
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

import de.hampelratte.svdrp.responses.highlevel.VDRTimer;

public class Timer extends VDRTimer {
    
    public static final int NO_REASON = 0;
    public static final int NO_EPG = 1;
    public static final int NOT_FOUND = 2;
    
    private int reason = Timer.NO_REASON;
    
    private String tvBrowserProgID;
    
    public Timer() {}
    
    public Timer(VDRTimer timer) {
        super.setActive(timer.isActive());
        super.setChannel(timer.getChannel());
        super.setDescription(timer.getDescription());
        super.setEndTime(timer.getEndTime());
        super.setFile(timer.getFile());
        super.setFirstTime(timer.getFirstTime());
        super.setHasFirstTime(timer.hasFirstTime());
        super.setID(timer.getID());
        super.setLifetime(timer.getLifetime());
        super.setPath(timer.getPath());
        super.setPriority(timer.getPriority());
        super.setRepeatingDays(timer.getRepeatingDays());
        super.setStartTime(timer.getStartTime());
        super.setTitle(timer.getTitle());
    }
    
    public String getTvBrowserProgID() {
        return tvBrowserProgID;
    }

    public void setTvBrowserProgID(String tvBrowserProgID) {
        this.tvBrowserProgID = tvBrowserProgID;
    }
    
    /**
     * @return Returns if this timer could be assigned to a Program
     */
    public boolean isAssigned() {
        return tvBrowserProgID != null;
    }

    public Object clone() {
        VDRTimer vdrtimer = (VDRTimer)super.clone();
        Timer clone = new Timer(vdrtimer);
        clone.setTvBrowserProgID(getTvBrowserProgID());
        clone.setReason(getReason());
        return clone;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }
    
    // TODO zuordnung von unterschiedlichen titeln 
    // IDEE: zuordnung über TVBProgID 
    // alte zuordnungen aus den storedTimers lesen
    // identifizierung der timers über ihre eindeutigkeit, wie es vdr macht
    // also eine zusätzlich methode getUniqueKey oder sowas in vdrtimer einbauen
}
