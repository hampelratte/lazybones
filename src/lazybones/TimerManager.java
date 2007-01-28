/* $Id: TimerManager.java,v 1.12 2007-01-28 15:12:50 hampelratte Exp $
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
import java.util.Collections;
import java.util.Iterator;
import java.util.Observable;
import java.util.TreeSet;

import lazybones.gui.TitleMapping;
import lazybones.utils.Utilities;
import de.hampelratte.svdrp.responses.highlevel.VDRTimer;

/**
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 * Class to manage all timers. No program logic, just a container
 */
public class TimerManager extends Observable {

    private static TimerManager instance;

    /**
     * Stores all timers as Timer objects
     */
    private ArrayList<Timer> timers;
    
    /**
     * The VDR timers from the last session, which have been stored to disk
     */
    private ArrayList<Timer> storedTimers = new ArrayList<Timer>();
    
    /**
     * Stores mappings the user has made for later use.
     * The user has to map one Program only once. Later 
     * the mapping will be looked up here 
     */
    private TitleMapping titleMapping = new TitleMapping();

    private TimerManager() {
        timers = new ArrayList<Timer>();
    }

    public synchronized static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void addTimer(Timer timer, boolean calculateRepeatingTimers) {
        if(!timer.isRepeating() || !calculateRepeatingTimers) {
            timers.add(timer);
        } else {
            Calendar startTime = timer.getStartTime();
            Calendar endTime = timer.getEndTime();
            long duration = endTime.getTimeInMillis() - startTime.getTimeInMillis();
                        
            if(timer.hasFirstTime()) {
                Calendar tmp = timer.getFirstTime();
                startTime.set(Calendar.DAY_OF_MONTH, tmp.get(Calendar.DAY_OF_MONTH));
                startTime.set(Calendar.MONTH, tmp.get(Calendar.MONTH));
                startTime.set(Calendar.YEAR, tmp.get(Calendar.YEAR));
            }
            
            if(calculateRepeatingTimers) {
                for (int j = 0; j < 21; j++) { // next 3 weeks, more data is never available in TVB
                    Calendar tmpStart = (Calendar)startTime.clone();
                    tmpStart.add(Calendar.DAY_OF_MONTH, j);
                    if(timer.isDaySet(tmpStart)) {
                        Timer oneDayTimer = (Timer)timer.clone();
                        oneDayTimer.setStartTime(tmpStart);
                        long start = tmpStart.getTimeInMillis();
                        oneDayTimer.getEndTime().setTimeInMillis(start + duration);
                        timers.add(oneDayTimer);
                    }
                }
            }
        }
        
        setChanged();
        notifyObservers(new TimersChangedEvent(TimersChangedEvent.TIMER_ADDED, timer));
    }

    public void removeTimer(Timer timer) {
        timers.remove(timer);
        setChanged();
        notifyObservers(new TimersChangedEvent(TimersChangedEvent.TIMER_REMOVED, timer));
    }
    
    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

    public void removeAll() {
        timers.clear();
        setChanged();
        notifyObservers(new TimersChangedEvent(TimersChangedEvent.ALL, timers));
    }

    /**
     * @return an ArrayList of Timer objects
     */
    public ArrayList<Timer> getTimers() {
        return timers;
    }
    
    /**
     * @param vdrTimers an ArrayList of VDRTimer objects
     */
    public void setTimers(ArrayList vdrTimers, boolean calculateRepeatingTimers) {
        for (Iterator it = vdrTimers.iterator(); it.hasNext();) {
            VDRTimer element = (VDRTimer) it.next();
            addTimer(new Timer(element), calculateRepeatingTimers);
        }
    }

    /**
     * 
     * @param progID
     *            the programID of a devplugin.Program object
     * @return the timer for this program or null
     */
    public Timer getTimer(String progID) {
        for (Iterator it = timers.iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            ArrayList<String> tvBrowserProdIDs = timer.getTvBrowserProgIDs();
            for (Iterator iter = tvBrowserProdIDs.iterator(); iter.hasNext();) {
                if (progID.equals((String) iter.next())) {
                    return timer;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns all timers, which couldn't be assigned to a program
     * @return an ArrayList with Timer objects
     */
    public ArrayList getNotAssignedTimers() {
        ArrayList<Timer> list = new ArrayList<Timer>();
        for (Iterator it = timers.iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            if(!timer.isAssigned()) {
                list.add(timer);
            }
        }
        return list;
    }
    
    public void printTimers() {
        System.out.println("########## Listing timers #################");
        for (Iterator it = timers.iterator(); it.hasNext();) {
            System.out.println(it.next());
        }
        System.out.println("################ End ######################");
    }

    public ArrayList getStoredTimers() {
        return storedTimers;
    }

    public void setStoredTimers(ArrayList<Timer> storedTimers) {
        this.storedTimers = storedTimers;
    }
    
    /**
     * Checks storedTimers, if this timer has been mapped to Program before
     * @param timer
     * @return the ProgramID or null
     */
    public ArrayList<String> hasBeenMappedBefore(Timer timer) {
        for (Iterator it = storedTimers.iterator(); it.hasNext();) {
            Timer storedTimer = (Timer) it.next();
            if(timer.getUniqueKey().equals(storedTimer.getUniqueKey())) {
                if(storedTimer.getReason() == Timer.NO_PROGRAM) {
                    ArrayList<String> timers = new ArrayList<String>();
                    timers.add("NO_PROGRAM");
                    return timers;
                } else {
                    return storedTimer.getTvBrowserProgIDs();
                }
            }
        }
        return null;
    }
    
    
    public void replaceStoredTimer(Timer timer) {
        for (Iterator it = storedTimers.iterator(); it.hasNext();) {
            Timer storedTimer = (Timer) it.next();
            if(timer.getUniqueKey().equals(storedTimer.getUniqueKey())) {
                storedTimers.remove(storedTimer);
                storedTimers.add(timer);
                return;
            }
        }
        
        // timer couldn't be found -> this is a new timer
        storedTimers.add(timer);
    }

    /**
     * 
     * @see TimerManager#titleMapping
     */
	public TitleMapping getTitleMapping() {
		return titleMapping;
	}

	/**
     * 
     * @see TimerManager#titleMapping
     */
	public void setTitleMapping(TitleMapping titleMapping) {
		this.titleMapping = titleMapping;
	}
    
    
    /**
     * Returns the next day, on which a timer events starts or stops, after the given calendar
     * @param currentDay
     * @return the next day, on which a timer events starts or stops, after the given calendar
     */
    public Calendar getNextDayWithEvent(Calendar currentDay) {
        ArrayList<Timer> timers = TimerManager.getInstance().getTimers();
        TreeSet<Calendar> events = new TreeSet<Calendar>();
        for (Iterator<Timer> iter = timers.iterator(); iter.hasNext();) {
            Timer timer = iter.next();
            events.add(timer.getStartTime());
            events.add(timer.getEndTime());
        }
        
        for (Iterator<Calendar> iter = events.iterator(); iter.hasNext();) {
            Calendar event = iter.next();
            if( !event.before(currentDay) & !Utilities.sameDay(event, currentDay)) {
                return event;
            }
        }
        
        return null;
    }
    
    /**
     * @see #getNextDayWithEvent(Calendar)
     * @param currentDay
     * @return
     */
    public Calendar getPreviousDayWithEvent(Calendar currentDay) {
        ArrayList<Timer> timers = TimerManager.getInstance().getTimers();
        TreeSet<Calendar> events = new TreeSet<Calendar>();
        for (Iterator<Timer> iter = timers.iterator(); iter.hasNext();) {
            Timer timer = iter.next();
            events.add(timer.getStartTime());
            events.add(timer.getEndTime());
        }
        
        ArrayList<Calendar> eventList = new ArrayList<Calendar>(events);
        Collections.reverse(eventList);
        for (Iterator<Calendar> iter = eventList.iterator(); iter.hasNext();) {
            Calendar event = iter.next();
            if( !event.after(currentDay) & !Utilities.sameDay(event, currentDay)) {
                return event;
            }
        }
        
        return null;
    }
    
    /**
     * @see #getNextDayWithEvent(Calendar)
     * @param currentDay
     * @return
     */
    public boolean hasNextDayWithEvent(Calendar currentDay) {
        return getNextDayWithEvent(currentDay) != null;
    }
    
    /**
     * @see #getPreviousDayWithEvent(Calendar)
     * @param currentDay
     * @return
     */
    public boolean hasPreviousDayWithEvent(Calendar currentDay) {
        return getPreviousDayWithEvent(currentDay) != null;
    }
}