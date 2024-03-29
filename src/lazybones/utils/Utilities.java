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
package lazybones.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTable;
import javax.swing.JViewport;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;

import org.hampelratte.svdrp.responses.highlevel.EPGEntry;

import devplugin.Program;

/**
 * @author <a href="hampelratte@users.sf.net">hampelratte@users.sf.net </a>
 *
 *         Utility class with different functions
 */
public class Utilities {

	private Utilities() {}
	
    public static int percentageOfEquality(String s, String t) {
        // check if strings are empty
        if (s == null || t == null || s.length() == 0 || t.length() == 0) {
            return 0;
        }

        // check if the strings are equal
        if (s.equals(t)) {
            return 100;
        }

        // check if one string is a substring of the other
        String shorter;
        String longer;
        if (s.length() > t.length()) {
            shorter = t;
            longer = s;
        } else {
            shorter = s;
            longer = t;
        }
        if (longer.startsWith(shorter) && longer.length() > shorter.length()) {
            if (longer.charAt(shorter.length()) == ' ') {
                return 99;
            } else {
                return 98;
            }
        }

        s = s.toLowerCase();
        s = s.replace("-", " ");
        s = s.replace(":", " ");
        s = s.replace(";", " ");
        s = s.replace("|", " ");
        s = s.replace("_", " ");
        s = s.replace(".", ". ");
        s = s.trim();
        t = t.toLowerCase();
        t = t.replace("-", " ");
        t = t.replace(":", " ");
        t = t.replace(";", " ");
        t = t.replace("|", " ");
        t = t.replace("_", " ");
        t = t.replace(".", ". ");
        t = t.trim();

        // calculate levenshteinDistance
        int levenshteinDistance = Utilities.getLevenshteinDistance(s, t);
        int length = Math.max(s.length(), t.length());

        // calculate the percentage of equality
        int percentage = 100 - (int) ((double) levenshteinDistance * 100 / length);
        return percentage;
    }

    public static int getLevenshteinDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
        int[][] d = new int[n + 1][m + 1];
        int i;
        int j;
        int cost;

        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }

        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++) {
            for (j = 1; j <= m; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                d[i][j] = min(d[i - 1][j] + 1, // insertion
                        d[i][j - 1] + 1, // deletion
                        d[i - 1][j - 1] + cost); // substitution
            }
        }
        return d[n][m];
    }

    private static int min(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }

    public static boolean isCellVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return false;
        }
        JViewport viewport = (JViewport) table.getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0)
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);

        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);

        // Check if view completely contains cell
        return new Rectangle(viewport.getExtentSize()).contains(rect);
    }

    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);

        // The location of the viewport relative to the table
        Point pt = viewport.getViewPosition();

        // Translate the cell location so that it is relative
        // to the view, assuming the northwest corner of the
        // view is (0,0)
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);

        // Scroll the area into view
        viewport.scrollRectToVisible(rect);
    }

    /**
     *
     * @param a
     *            Calendar
     * @param b
     *            Calendar
     * @return true if the Calendars describe the same day (day, month, year)
     */
    public static boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR) && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR);
    }

    public static List<StartStopEvent> createStartStopEventList(List<LazyBonesTimer> timers) {
        ArrayList<StartStopEvent> startStopEvents = new ArrayList<>();
        for (LazyBonesTimer timer : timers) {
            if (timer.isActive()) {
                startStopEvents.add(new StartStopEvent(timer, true));
                startStopEvents.add(new StartStopEvent(timer, false));
            }
        }
        Collections.sort(startStopEvents);
        return startStopEvents;
    }

    /**
     * Returns the difference of two calendars in minutes
     *
     * @param startTime
     * @param endTime
     * @return difference of two calendars in minutes
     */
    public static long getDiffInMinutes(Calendar startTime, Calendar endTime) {
        long diffMillis = Math.abs(endTime.getTimeInMillis() - startTime.getTimeInMillis());
        return TimeUnit.MILLISECONDS.toMinutes(diffMillis);
    }

    /**
     * Filters a list of EPGEntries by a provided VDR channel name and a time which has to be in between a EPGEntries start and end time.
     *
     * @param epgList
     *            list of EPGEntries retrieved from VDR
     * @param vdrChannelName
     *            VDR channel name which has to match a EPGEntry channel name
     * @param middleTime
     *            time of a program which has to be between start and end time of a EPGEntry
     * @return EPGEntry from the list which matches channel and time
     */
    public static EPGEntry filterEPGDate(List<EPGEntry> epgList, String vdrChannelName, long middleTime) {
        for (EPGEntry entry : epgList) {
            if (entry.getStartTime().getTimeInMillis() <= middleTime && middleTime <= entry.getEndTime().getTimeInMillis()
                    && vdrChannelName.equals(entry.getChannelName())) {
                return entry;
            }
        }
        return null;
    }

    public static Calendar getStartTime(Program prog) {
        Calendar cal = getCalendar(prog);
        cal.set(Calendar.HOUR_OF_DAY, prog.getHours());
        cal.set(Calendar.MINUTE, prog.getMinutes());
        return cal;
    }

    public static Calendar getEndTime(Program prog) {
        Calendar cal = getStartTime(prog);
        cal.add(Calendar.MINUTE, prog.getLength());
        return cal;
    }

    private static Calendar getCalendar(Program prog) {
        Calendar cal = prog.getDate().getCalendar();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static boolean timerRunsOnDate(LazyBonesTimer timer, Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        Calendar selectedDayAtStartHour = (Calendar) date.clone();
        selectedDayAtStartHour.set(Calendar.HOUR_OF_DAY, startHour);

        Calendar dayAfterAtStartHour = (Calendar) selectedDayAtStartHour.clone();
        dayAfterAtStartHour.add(Calendar.DAY_OF_MONTH, 1);

        return timer.getStartTime().after(selectedDayAtStartHour) && timer.getStartTime().before(dayAfterAtStartHour)
                || timer.getEndTime().after(selectedDayAtStartHour) && timer.getEndTime().before(dayAfterAtStartHour);
    }

}