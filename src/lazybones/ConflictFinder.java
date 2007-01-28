/* $Id: ConflictFinder.java,v 1.3 2007-01-28 15:05:49 hampelratte Exp $
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

import javax.swing.JOptionPane;

import lazybones.gui.TimelinePanel;
import lazybones.utils.StartStopEvent;
import lazybones.utils.Utilities;
import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ConflictFinder implements Observer {
    private static ConflictFinder instance;    
    private HashSet<Set> conflicts = new HashSet<Set>();
    private HashMap<Integer, Integer> transponderUse = new HashMap<Integer,Integer>();
    private HashSet<Timer> runningEvents = new HashSet<Timer>();
    
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
    
    private void reset() {
        conflicts.clear();
        transponderUse.clear();
        runningEvents.clear();
        
        // reset timer conflict times
        for (Iterator iter = TimerManager.getInstance().getTimers().iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            timer.setConflictStartTime(null);
            timer.setConflictEndTime(null);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void findConflicts() {
        Logger.getLogger().log("Looking for conflicts", Logger.OTHER, Logger.DEBUG);
        
        // clear old data
        reset();
       
        int numberOfCards = Integer.parseInt(LazyBones.getProperties().getProperty("numberOfCards"));
        Logger.getLogger().log("Number of cards: " + numberOfCards, Logger.OTHER, Logger.DEBUG);
        ArrayList<Timer> timers = TimerManager.getInstance().getTimers();
        List<StartStopEvent> startStopEvents = Utilities.createStartStopEventList(timers); 
        
        // run over startStopEvents
        boolean conflictFound = false;
        ConflictingTimersSet<Timer> conflictingTimersSet = new ConflictingTimersSet<Timer>();
        for (Iterator<StartStopEvent> iter = startStopEvents.iterator(); iter.hasNext();) {
            StartStopEvent event = iter.next();
            Timer timer = event.getTimer();
            if(event.isStartEvent()) {
                increaseTransponderUse(timer);
                runningEvents.add(timer);
                if(transponderUse.size() > numberOfCards) {
                    conflictFound = true;
                    conflictingTimersSet.addAll(runningEvents);
                    if(conflictingTimersSet.getConflictStartTime() == null) {
                        conflictingTimersSet.setConflictStartTime(event.getEventTime());
                    }
                }
            } else {
                decreaseTransponderUse(timer);
                if(conflictFound && transponderUse.size() <= numberOfCards) {
                    conflictFound = false;
                    conflictingTimersSet.setConflictEndTime(event.getEventTime());
                    conflicts.add(conflictingTimersSet);
                    conflictingTimersSet = new ConflictingTimersSet<Timer>();
                }
                runningEvents.remove(timer);
            }
        }
    }
    
    public void handleConflicts() {
        Logger.getLogger().log("Handling conflicts", Logger.OTHER, Logger.DEBUG);
        // check, if there are timer conflicts
        if(getConflictCount() > 0) {
            String msg = LazyBones.getTranslation("conflict_found", 
                    LazyBones.getInstance().getInfo().getName() + " has detected {0} timer conflict(s)!", 
                    Integer.toString(ConflictFinder.getInstance().getConflictCount()));
            JOptionPane.showMessageDialog(LazyBones.getInstance().getParent(), msg);
            Logger.getLogger().log(msg, Logger.OTHER, Logger.INFO);
            LazyBones.getInstance().getMainDialog().setVisible(true);
            LazyBones.getInstance().getMainDialog().showTimeline();

            // debug output
            StringBuffer logMsg = new StringBuffer();
            Set conflicts = ConflictFinder.getInstance().getConflicts();
            for (Iterator iter = conflicts.iterator(); iter.hasNext();) {
                logMsg.append("Conflict found: ");
                ConflictingTimersSet<Timer> set = (ConflictingTimersSet<Timer>) iter.next();
                for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                    Timer timer = (Timer) iterator.next();
                    logMsg.append(timer.getTitle() + ", ");
                }
                logMsg.append(" Conflict start: " +  DateFormat.getDateTimeInstance().format(set.getConflictStartTime().getTime()));
                logMsg.append(" Conflict end: " +  DateFormat.getDateTimeInstance().format(set.getConflictEndTime().getTime()));
                Logger.getLogger().log(logMsg.toString(), Logger.OTHER, Logger.DEBUG);
            }
            
            // set timeline date to the date of one conflict
            TimelinePanel tp = LazyBones.getInstance().getMainDialog().getTimelinePanel();
            ConflictingTimersSet<Timer> set = (ConflictingTimersSet<Timer>) conflicts.iterator().next();
            tp.setCalendar(set.getConflictStartTime());
        }
        
        LazyBones.getInstance().getMainDialog().getTimelinePanel().repaint();
    }

    public int getConflictCount() {
        return conflicts.size();
    }
    
    public Set<Set> getConflicts() {
        return conflicts;
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
}