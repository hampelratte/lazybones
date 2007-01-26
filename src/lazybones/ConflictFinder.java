/* $Id: ConflictFinder.java,v 1.2 2007-01-26 22:43:42 hampelratte Exp $
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

import java.text.DateFormat;
import java.util.*;

import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ConflictFinder implements Observer {
    private static ConflictFinder instance;    
    private ArrayList conflicts = new ArrayList();
    private ArrayList<StartStopEvent> startStopEvents = new ArrayList<StartStopEvent>();
    private HashMap<Integer, Integer> transponderUse = new HashMap<Integer,Integer>();
    private HashSet<Timer> runningEvents = new HashSet<Timer>();
    private int conflictCount = 0;
    
    private ConflictFinder() {
        TimerManager.getInstance().addObserver(this);
        findConflicts();
    }
    
    public static ConflictFinder getInstance() {
        if (instance == null) {
            instance = new ConflictFinder();
        }
        return instance;
    }
    
    private void clear() {
        conflicts.clear();
        startStopEvents.clear();
        transponderUse.clear();
        runningEvents.clear();
        conflictCount = 0;
    }
    
    private void findConflicts() {
        // clear old data
        clear();
       
        int numberOfCards = Integer.parseInt(LazyBones.getProperties().getProperty("numberOfCards"));
        ArrayList<Timer> timers = TimerManager.getInstance().getTimers();
        
        // fill startStopEvents // TODO repeating timers berücksichtigen
        for (Iterator<Timer> iter = timers.iterator(); iter.hasNext();) {
            Timer timer = iter.next();
            startStopEvents.add(new StartStopEvent(timer, true));
            startStopEvents.add(new StartStopEvent(timer, false));
        }
        Collections.sort(startStopEvents);
        
        // run over startStopEvents
        for (Iterator<StartStopEvent> iter = startStopEvents.iterator(); iter.hasNext();) {
            StartStopEvent event = iter.next();
            Timer timer = event.getTimer();
            if(event.isStartEvent()) {
                increaseTransponderUse(timer);
                runningEvents.add(timer);
                if(transponderUse.size() > numberOfCards) {
                    // TODO we have a conflict;
                    conflictCount++;
                }
            } else {
                decreaseTransponderUse(timer);
                runningEvents.remove(timer);
            }
        }
    }
    
    public int getConflictCount() {
        return conflictCount;
    }
    
    private void increaseTransponderUse(Timer timer) {
        Channel chan = VDRChannelList.getInstance().getChannelByNumber(timer.getChannelNumber());
        if(transponderUse.containsKey(chan.getFrequency())) {
            int count = transponderUse.get(chan.getFrequency());
            count++;
            transponderUse.put(chan.getFrequency(), count);
        } else {
            transponderUse.put(chan.getFrequency(), 1);
        }
    }
    
    private void decreaseTransponderUse(Timer timer) {
        Channel chan = VDRChannelList.getInstance().getChannelByNumber(timer.getChannelNumber());
        if(transponderUse.containsKey(chan.getFrequency())) {
            int count = transponderUse.get(chan.getFrequency());
            if(count == 1) {
                transponderUse.remove(chan.getFrequency());
            } else {
                count--;
                transponderUse.put(chan.getFrequency(), count);
            }
        }
    }
    
    public void update(Observable o, Object obj) {
        findConflicts();
    }
    
    private class StartStopEvent implements Comparable<StartStopEvent> {
        private Timer timer;

        private boolean startEvent = true;

        /**
         * @param timer
         * @param startEvent
         */
        public StartStopEvent(Timer timer, boolean startEvent) {
            super();
            this.timer = timer;
            this.startEvent = startEvent;
        }

        public boolean isStartEvent() {
            return startEvent;
        }

        public void setStartEvent(boolean startEvent) {
            this.startEvent = startEvent;
        }

        public Timer getTimer() {
            return timer;
        }

        public void setTimer(Timer timer) {
            this.timer = timer;
        }
        
        public Calendar getEventTime() {
            return isStartEvent() ? timer.getStartTime() : timer.getEndTime();
        }

        public int compareTo(StartStopEvent o) {
            return getEventTime().compareTo(o.getEventTime());
        }
        
        public String toString() {
            DateFormat df = DateFormat.getDateTimeInstance();
            Calendar cal = isStartEvent() ? timer.getStartTime() : timer.getEndTime();
            return (df.format(cal.getTime()) 
                    + " Transponder:" + VDRChannelList.getInstance().getChannelByNumber(
                            timer.getChannelNumber()).getFrequency()+ " " 
                    + timer);
        }
    }
}