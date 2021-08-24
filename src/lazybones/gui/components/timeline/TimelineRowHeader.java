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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.UIManager;

import lazybones.LazyBonesTimer;

public class TimelineRowHeader extends JPanel implements TimelineListener {

    private final List<Integer> channels = new ArrayList<>();
    private Color lineColor;

    public TimelineRowHeader(TimelineList list) {
        setLayout(new TimelineLayout());
        list.addTimelineListener(this);
        setDoubleBuffered(true);
    }

    public void addTimer(LazyBonesTimer timer) {
        if (!channels.contains(timer.getChannelNumber())) {
            add(new TimelineRowHeaderElement(timer));
            channels.add(timer.getChannelNumber());
        }
    }

    @Override
    public void timelineChanged(List<LazyBonesTimer> timers) {
        this.removeAll();
        channels.clear();
        for (LazyBonesTimer timer : timers) {
            addTimer(timer);
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(lineColor);
        for (int i = 0; i <= getComponentCount(); i++) {
            g.drawLine(0, i * (ROW_HEIGHT + PADDING), getWidth(), i * (ROW_HEIGHT + PADDING));
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        lineColor = UIManager.getColor("Panel.background").darker();
    }
}
