/* $Id: ConflictFinder.java,v 1.1 2006-12-29 23:34:13 hampelratte Exp $
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

import java.util.*;

import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ConflictFinder implements Observer {
    
    private ArrayList conflicts = new ArrayList();
    
    public ConflictFinder() {
        TimerManager.getInstance().addObserver(this);
    }
    
    private void findConflicts() {
        int numberOfCards = Integer.parseInt(LazyBones.getProperties().getProperty("numberOfCards"));
        ArrayList<Channel> channels = VDRChannelList.getInstance().getChannels();
        
        conflicts.clear();
        ArrayList<Timer> timers = TimerManager.getInstance().getTimers();
        for (Iterator<Timer> iter = timers.iterator(); iter.hasNext();) {
            Timer timer = iter.next();

            ArrayList<Timer> conflictsForCurrentTimer = new ArrayList<Timer>();
            for (Iterator<Timer> iterator = timers.iterator(); iterator.hasNext();) {
                Timer timer2 = iterator.next();
                
                // check if these timers run concurrent
                if(timer2.startsDuringTimer(timer)) {
                    
                }
                
            }
        }
    }
    
    public ArrayList getConflicts() {
        findConflicts();
        return conflicts;
    }

    public void update(Observable o, Object obj) {
        if(obj instanceof TimersChangedEvent) {
            TimersChangedEvent tce = (TimersChangedEvent) obj;
            switch(tce.getType()) {
            case TimersChangedEvent.ALL:
                findConflicts();
                break;
            case TimersChangedEvent.TIMER_ADDED:
                break;
            case TimersChangedEvent.TIMER_REMOVED:
                break;
            }
        }
    }
    
}
