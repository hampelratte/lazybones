/*
 * Copyright (c) Henrik Niehaus
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
