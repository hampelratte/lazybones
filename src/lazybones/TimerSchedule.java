package lazybones;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lazybones.utils.Utilities;

public class TimerSchedule {
    /**
     * Returns the next day, on which a timer event starts or stops, after the given calendar
     * 
     * @param currentDay
     * @return the next day, on which a timer event starts or stops, after the given calendar
     */
    public Calendar getNextDayWithEvent(Calendar currentDay) {
        Set<Calendar> events = createEventSet();
        for (Iterator<Calendar> iter = events.iterator(); iter.hasNext();) {
            Calendar event = iter.next();
            if (!event.before(currentDay) & !Utilities.sameDay(event, currentDay)) {
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
        Set<Calendar> events = createEventSet();
        ArrayList<Calendar> eventList = new ArrayList<Calendar>(events);
        Collections.reverse(eventList);
        for (Iterator<Calendar> iter = eventList.iterator(); iter.hasNext();) {
            Calendar event = iter.next();
            if (!event.after(currentDay) & !Utilities.sameDay(event, currentDay)) {
                return event;
            }
        }

        return null;
    }

    private Set<Calendar> createEventSet() {
        List<LazyBonesTimer> timers = TimerManager.getInstance().getTimers();
        TreeSet<Calendar> events = new TreeSet<Calendar>();
        for (Iterator<LazyBonesTimer> iter = timers.iterator(); iter.hasNext();) {
            LazyBonesTimer timer = iter.next();
            events.add(timer.getStartTime());
            events.add(timer.getEndTime());
        }
        return events;
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
