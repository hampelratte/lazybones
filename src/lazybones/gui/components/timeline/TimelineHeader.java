/* $Id: TimelineHeader.java,v 1.4 2011-04-20 12:09:13 hampelratte Exp $
 * 
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.UIManager;

public class TimelineHeader extends JComponent {

    public final static int HEIGHT = 20;

    public TimelineHeader() {
        setPreferredSize(new Dimension(getWidth(), HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // enable font anti aliasing
        Graphics2D g2d = (Graphics2D) g;
        // storing original anitalising flag
        Object state = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        if (state != RenderingHints.VALUE_TEXT_ANTIALIAS_ON) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        int w = getWidth();
        int h = getHeight();

        int fontSize = 8;
        Font font = new Font("SansSerif", Font.PLAIN, fontSize);
        FontMetrics fm = g.getFontMetrics(font);

        double pixelsPerHour = (double) (w - 1) / (double) 24;

        g.setColor(Color.GRAY);
        g.drawLine(0, h - 1, w - 1, h - 1); // draw the bottom border
        for (int i = 0; i < 25; i++) {
            g.setColor(Color.GRAY);
            g.drawLine((int) (i * pixelsPerHour), 0, (int) (i * pixelsPerHour), h);
            if (i < 24) {
                int hourWidth = fm.stringWidth(Integer.toString(i));
                int hourHeight = fm.getHeight();
                int x = (int) (i * pixelsPerHour) + ((int) pixelsPerHour - hourWidth) / 2 - 2;
                int y = (h - hourHeight) / 2;
                g.setColor(UIManager.getColor("Label.foreground"));
                g.drawString(Integer.toString(i), x, y + 9);
            }
        }
    }
}
