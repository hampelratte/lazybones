/* $Id: TimerManager.java,v 1.18 2007-04-30 17:10:32 hampelratte Exp $
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

import lazybones.actions.DeleteTimerAction;
import lazybones.gui.utils.TitleMapping;
import lazybones.utils.Utilities;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTT;
import org.hampelratte.svdrp.responses.highlevel.VDRTimer;
import org.hampelratte.svdrp.util.TimerParser;

import devplugin.Date;
import devplugin.Program;

/**
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 * Class to manage all timers.
 */
public class TimerManager extends Observable {
    
    private transient static Logger logger = Logger.getLogger();

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
    public void setTimers(List vdrTimers, boolean calculateRepeatingTimers) {
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
     * 
     * @param timerNumber The number of the timer
     * @return The timer with the specified number
     */
    public Timer getTimer(int timerNumber) {
        for (Iterator iter = timers.iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            if(timer.getID() == timerNumber) {
                return timer;
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
	public HashMap getTitleMappingValues() {
		return titleMapping.getAsMap();
	}

	/**
     * 
     * @see TimerManager#titleMapping
     */
	public void setTitleMappingValues(HashMap titleMapping) {
		this.titleMapping.setMappingFromMap(titleMapping);
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

    public TitleMapping getTitleMapping() {
        return this.titleMapping;
    }

    public void setTitleMapping(TitleMapping mapping) {
        this.titleMapping = mapping;
    }
    
    /**
     * Fetches the timer list from vdr
     */
    public void synchronize() {
        // unmark all tvbrowser programs
        unmarkPrograms();
        
        // clear timer list
        removeAll();
        
        // fetch current timer list from vdr
        Response res = VDRConnection.send(new LSTT());
        if (res != null && res.getCode() == 250) {
            logger.log("Timers retrieved from VDR",Logger.OTHER, Logger.INFO);
            String timersString = res.getMessage();
            List vdrtimers = TimerParser.parse(timersString);
            setTimers(vdrtimers, true);
        } else if (res != null && res.getCode() == 550) {
            // no timers are defined, do nothing
            logger.log("No timer defined on VDR",Logger.OTHER, Logger.INFO);
        } else { /* something went wrong, we have no timers -> 
                  * load the stored ones */
            logger.log(LazyBones.getTranslation("using_stored_timers",
                "Couldn't retrieve timers from VDR, using stored ones."), 
                Logger.CONNECTION, Logger.ERROR);
            
            ArrayList vdrtimers = getStoredTimers();
            setTimers(vdrtimers, false);
        }
        
        // detect conflicts
        ConflictFinder.getInstance().findConflicts();
        ConflictFinder.getInstance().handleConflicts();
    }
    
    /**
     * Unmarks all programs, which are marked by LazyBones
     */
    private void unmarkPrograms() {
        for (Iterator it = timers.iterator(); it.hasNext();) {
            Timer timer = (Timer) it.next();
            for (Iterator iter = timer.getTvBrowserProgIDs().iterator(); iter.hasNext();) {
                String progID = (String) iter.next();
                if(progID != null) { // timer could be assigned
                    Date date = new Date(timer.getStartTime());
                    Program prog = LazyBones.getPluginManager().getProgram(date, progID);
                    if(prog != null) {
                        prog.unmark(LazyBones.getInstance());
                    }
                }
            }
        }
    }
    
    public void deleteTimer(Timer timer) {
        DeleteTimerAction dta = new DeleteTimerAction(timer);
        if(!dta.execute()) {
            logger.log(LazyBones.getTranslation(
                    "couldnt_delete", "Couldn\'t delete timer:")
                    + " " + dta.getResponse().getMessage(), Logger.OTHER, Logger.ERROR);
            return;
        }

        ArrayList<String> progIDs = timer.getTvBrowserProgIDs();
        for (Iterator iter = progIDs.iterator(); iter.hasNext();) {
            String id = (String) iter.next();
            Program prog = ProgramManager.getInstance().getProgram(timer.getStartTime(), id);
            if(prog != null) {
                prog.unmark(LazyBones.getInstance());
            } else { // can be null, if program time is near 00:00, because then
                     // the wrong day is taken to ask tvb for the programm
                prog = ProgramManager.getInstance().getProgram(timer.getEndTime(), id);
                if(prog != null) {
                    prog.unmark(LazyBones.getInstance());
                }
            }
        }
        TimerManager.getInstance().synchronize();
    }
    
    public void deleteTimer(Program prog) {
        Timer timer = TimerManager.getInstance().getTimer(prog.getID());
        DeleteTimerAction dta = new DeleteTimerAction(timer);
        if(!dta.execute()) {
            logger.log(LazyBones.getTranslation(
                    "couldnt_delete", "Couldn\'t delete timer:")
                    + " " + dta.getResponse().getMessage(), Logger.OTHER, Logger.ERROR);
            return;
        }
        
        //prog.unmark(this);
        TimerManager.getInstance().synchronize();
    }
}