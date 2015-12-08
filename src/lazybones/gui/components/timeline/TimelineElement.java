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
package lazybones.gui.components.timeline;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import lazybones.ChannelManager;
import lazybones.ChannelManager.ChannelNotFoundException;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.conflicts.Conflict;
import lazybones.programmanager.ProgramManager;
import lazybones.utils.Period;
import lazybones.utils.Utilities;
import devplugin.Channel;

public class TimelineElement extends JComponent implements MouseListener {
    private LazyBonesTimer timer;
    private Calendar currentDate;
    private Calendar timelineStart;
    private Calendar timelineEnd;
    private final DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    public final static Color COLOR_ACTIVE = UIManager.getColor("List.selectionBackground");
    public final static Color COLOR_INACTIVE = Color.LIGHT_GRAY;
    public final static Color TEXT_COLOR = UIManager.getColor("List.selectionForeground");
    public final static Color TEXT_COLOR_INACTIVE = Color.GRAY;
    public final static Color CONFLICT_COLOR = new Color(255, 0, 0, 90);

    public TimelineElement(LazyBonesTimer timer, Calendar currentDate) {
        this.timer = timer;
        this.addMouseListener(this);
        setCurrentDate(currentDate);

        // set tooltip
        setToolTipText(createToolTipText(timer));
    }

    private String createToolTipText(LazyBonesTimer timer) {
        String title = timer.getDisplayTitle().replace("|", "");
        DateFormat dateFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<b>");
        sb.append(title);
        sb.append("</b><br>");
        Channel chan;
        try {
            chan = ChannelManager.getInstance().getTvbrowserChannel(timer);
            sb.append(chan.getName());
        } catch (ChannelNotFoundException e) {
            sb.append(timer.getChannelNumber());
        }
        sb.append(' ');
        sb.append(dateFormatter.format(timer.getStartTime().getTime()));
        sb.append(" - ");
        sb.append(dateFormatter.format(timer.getEndTime().getTime()));
        sb.append("<br>");
        sb.append("<br>");
        sb.append("<div width=\"300\">");
        sb.append(timer.getDescription());
        sb.append("</div>");
        sb.append("</html>");
        return sb.toString();
    }

    public LazyBonesTimer getTimer() {
        return timer;
    }

    public void setTimer(LazyBonesTimer timer) {
        this.timer = timer;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;

        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        timelineStart = (Calendar) currentDate.clone();
        timelineStart.set(Calendar.HOUR_OF_DAY, startHour);
        timelineEnd = (Calendar) timelineStart.clone();
        timelineEnd.add(Calendar.DAY_OF_MONTH, 1);
    }

    @Override
    public Border getBorder() {
        if (timer.isActive()) {
            return BorderFactory.createBevelBorder(BevelBorder.RAISED, COLOR_ACTIVE, COLOR_ACTIVE.darker());
        } else {
            return BorderFactory.createBevelBorder(BevelBorder.RAISED, COLOR_INACTIVE, COLOR_INACTIVE.darker());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // enable font anti aliasing
        Graphics2D g2d = (Graphics2D) g;
        // storing original anitalising flag
        Object state = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        if (state != RenderingHints.VALUE_TEXT_ANTIALIAS_ON) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        // paint background
        g.setColor(timer.isActive() ? COLOR_ACTIVE : COLOR_INACTIVE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // paint text
        g.setColor(timer.isActive() ? TEXT_COLOR : TEXT_COLOR_INACTIVE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.drawString(timer.getTitle(), 5, 12);
        String time = df.format(timer.getStartTime().getTime()) + " - " + df.format(timer.getEndTime().getTime());
        g.drawString(time, 5, 25);

        // paint recording indicator
        if (timer.isRecording()) {
            g.setColor(Color.RED);
            g.fillOval(5, 30, 7, 7);
        }

        // paint conflicts
        g.setColor(CONFLICT_COLOR);
        if (!timer.getConflicts().isEmpty()) {
            for (Conflict conflict : timer.getConflicts()) {
                Period period = conflict.getPeriod();
                Calendar conflictStart = (Calendar) period.getStartTime().clone();
                Calendar conflictEnd = (Calendar) period.getEndTime().clone();
                Calendar timerStart = (Calendar) timer.getStartTime().clone();
                Calendar timerEnd = (Calendar) timer.getEndTime().clone();

                if (conflictStart.before(timelineStart)) {
                    conflictStart.setTime(timelineStart.getTime());
                }
                if (conflictStart.before(timerStart)) {
                    conflictStart.setTime(timerStart.getTime());
                }
                if (conflictEnd.after(timelineEnd)) {
                    conflictEnd.setTime(timelineEnd.getTime());
                }
                if (timerStart.before(timelineStart)) {
                    timerStart.setTime(timelineStart.getTime());
                }
                if (timerEnd.after(timelineEnd)) {
                    timerEnd.setTime(timelineEnd.getTime());
                }

                long durationMinutes = Utilities.getDiffInMinutes(timerStart, timerEnd);
                double pixelsPerMinute = (double) getWidth() / (double) durationMinutes;
                long startMinute = Utilities.getDiffInMinutes(timerStart, conflictStart);
                int x = (int) Math.ceil(pixelsPerMinute * startMinute);
                int width = (int) (pixelsPerMinute * Utilities.getDiffInMinutes(conflictStart, conflictEnd));
                g.fillRect(x, 0, width, getHeight() - 1);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
            ProgramManager.getInstance().handleTimerDoubleClick(getTimer(), e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mayTriggerPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mayTriggerPopup(e);
    }

    /**
     * Checks, if the mouse event is a popup trigger for this OS and shows the popup menu in case it is a trigger
     *
     * @param e
     *            MouseEvent to check
     */
    private void mayTriggerPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = ProgramManager.getInstance().getContextMenuForTimer(timer);
            popup.setLocation(e.getPoint());
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}