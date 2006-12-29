/* $Id: TimelineRowHeader.java,v 1.1 2006-12-29 23:34:14 hampelratte Exp $
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
package lazybones.gui.components.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import lazybones.Timer;

public class TimelineRowHeader extends JPanel implements TimelineListener {

    private ArrayList<Integer> channels = new ArrayList<Integer>();
    private int rowHeight = 40;
    private int padding = 0;
    
    public TimelineRowHeader(TimelineList list, int rowHeight, int padding) {
        this.rowHeight = rowHeight;
        this.padding = padding;
        setPreferredSize(new Dimension(80, 0));
        setLayout(new TimelineLayout(rowHeight, padding));
        list.addTimelineListener(this);
    }
    
    public void addTimer(Timer timer) {
        if(!channels.contains(timer.getChannelNumber())) {
            add(new TimelineRowHeaderElement(timer));
            channels.add(timer.getChannelNumber());
        }
    }
    
    public void timelineChanged(ArrayList<Timer> timers) {
        this.removeAll();
        channels.clear();
        for (Timer timer : timers) {
            addTimer(timer);
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.GRAY);
        for (int i = 0; i <= getComponentCount(); i++) {
            g.drawLine(0, i*(rowHeight + padding), getWidth(), i*(rowHeight + padding));
        }
    }
}
