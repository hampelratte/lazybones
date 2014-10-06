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

import static lazybones.gui.components.timeline.Timeline.PADDING;
import static lazybones.gui.components.timeline.Timeline.ROW_HEIGHT;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;

public class TimelineLayout implements LayoutManager2 {

    private final ArrayList<Component> components = new ArrayList<Component>();

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        components.add(comp);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return target.getMaximumSize();
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        components.add(comp);
    }

    @Override
    public void layoutContainer(Container parent) {
        if (parent.isValid()) {
            return;
        }

        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        int width = parent instanceof TimelineRowHeader ? 0 : parent.getParent().getWidth();
        int height = parent.getHeight();

        double pixelsPerMinute = (double) (width - 1) / (double) (24 * 60);

        int rowCount = 0;
        Map<Integer, Integer> channelRowMap = new HashMap<Integer, Integer>();
        for (Component comp : components) {
            if (comp instanceof TimelineElement) {
                TimelineElement te = (TimelineElement) comp;
                LazyBonesTimer timer = te.getTimer();

                int startPos = calculateStartPosition(pixelsPerMinute, timer);
                int endPos = calculateEndPosition(pixelsPerMinute, timer);

                Calendar currentDate = te.getCurrentDate();
                Calendar selectedDayAtStartHour = (Calendar) currentDate.clone();
                selectedDayAtStartHour.set(Calendar.HOUR_OF_DAY, startHour);
                Calendar dayAfterAtStartHour = (Calendar) selectedDayAtStartHour.clone();
                dayAfterAtStartHour.add(Calendar.DAY_OF_MONTH, 1);
                if (timer.getStartTime().before(selectedDayAtStartHour)) {
                    startPos = 0;
                }
                if (timer.getEndTime().after(dayAfterAtStartHour)) {
                    endPos = width;
                }

                int length = endPos - startPos;
                Integer channelRow = channelRowMap.get(timer.getChannelNumber());
                int row = rowCount;
                if (channelRow == null) {
                    channelRowMap.put(timer.getChannelNumber(), rowCount);
                    rowCount++;
                } else {
                    row = channelRow.intValue();
                }

                te.setLocation(startPos, (ROW_HEIGHT + PADDING) * row);
                te.setSize(length, ROW_HEIGHT);
            } else if (comp instanceof TimelineRowHeaderElement) {
                comp.setSize(comp.getPreferredSize());
                comp.setLocation(0, (ROW_HEIGHT + PADDING) * rowCount);
                rowCount++;
                if (comp.getWidth() > width) {
                    width = comp.getWidth();
                }
            }
        }

        if (components.size() == 0 && parent instanceof TimelineRowHeader) {
            width = 0;
        }

        parent.setPreferredSize(new Dimension(width, height));
        parent.setSize(width, height);
    }

    private int calculateEndPosition(double pixelsPerMinute, LazyBonesTimer timer) {
        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        int minute = timer.getEndTime().get(Calendar.MINUTE);
        int hour = timer.getEndTime().get(Calendar.HOUR_OF_DAY);
        if (hour >= startHour) {
            hour -= startHour;
        } else {
            hour += (24 - startHour);
        }
        int minuteOfDay = hour * 60 + minute;
        int endPos = (int) (minuteOfDay * pixelsPerMinute);
        return endPos;
    }

    private int calculateStartPosition(double pixelsPerMinute, LazyBonesTimer timer) {
        int startHour = Integer.parseInt(LazyBones.getProperties().getProperty("timelineStartHour"));
        int minute = timer.getStartTime().get(Calendar.MINUTE);
        int hour = timer.getStartTime().get(Calendar.HOUR_OF_DAY);
        if (hour >= startHour) {
            hour -= startHour;
        } else {
            hour += (24 - startHour);
        }
        int minuteOfDay = hour * 60 + minute;
        int startPos = (int) (minuteOfDay * pixelsPerMinute);
        return startPos;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Dimension d = new Dimension();
        d.width = parent.getWidth();
        d.height = 0;
        for (Component comp : components) {
            if (comp.getWidth() > d.width) {
                d.width = comp.getWidth();
            }
            d.height += ROW_HEIGHT + PADDING;
        }
        return d;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension d = new Dimension();
        d.width = parent.getWidth();
        d.height = 0;
        for (Component comp : components) {
            if (comp.getWidth() > d.width) {
                d.width = comp.getWidth();
            }
            d.height += ROW_HEIGHT + PADDING;
        }
        return d;
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        components.remove(comp);
    }
}