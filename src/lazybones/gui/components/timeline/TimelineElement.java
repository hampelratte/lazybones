/* $Id: TimelineElement.java,v 1.11 2008-05-19 13:20:26 hampelratte Exp $
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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
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

import lazybones.ChannelManager;
import lazybones.ProgramManager;
import lazybones.Timer;
import lazybones.utils.Period;
import lazybones.utils.Utilities;
import devplugin.Channel;

public class TimelineElement extends JComponent implements MouseListener {
    private Timer timer;
    private Calendar currentDate;
    
    public final static Color COLOR = UIManager.getColor("TextField.selectionBackground");
    public final static Color TEXT_COLOR = UIManager.getColor("TextField.selectionForeground");
    public final static Color CONFLICT_COLOR = new Color(255,0,0,90);
    
    public TimelineElement(Timer timer, Calendar currentDate) {
        this.timer = timer;
        this.currentDate = currentDate;
        //setBorder(BorderFactory.createLineBorder(COLOR.darker()));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, COLOR, COLOR.darker()));
        this.addMouseListener(this);
        
        // set tooltip
        setToolTipText(createToolTipText(timer));
    }

    private String createToolTipText(Timer timer) {
        String title = timer.getDisplayTitle().replace("|", "");
        DateFormat dateFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        sb.append("<b>"); sb.append(title); sb.append("</b><br>");
        Channel chan = ChannelManager.getInstance().getChannel(timer);
        if(chan != null) {
            sb.append(chan.getName());
        } else {
            sb.append(timer.getChannelNumber());
        }
        sb.append(' ');
        sb.append(dateFormatter.format(timer.getStartTime().getTime()));
        sb.append(" - ");
        sb.append(dateFormatter.format(timer.getEndTime().getTime()));
        sb.append("<br>");sb.append("<br>");
        sb.append("<div width=\"300\">");
        sb.append(timer.getDescription());
        sb.append("</div>");
        sb.append("</html>");
        return sb.toString();
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // paint background
        g.setColor(COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // paint text
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g.drawString(timer.getDisplayTitle(), 5, 12);
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        String time = df.format(timer.getStartTime().getTime()) + " - " + df.format(timer.getEndTime().getTime());
        g.drawString(time, 5, 25);
        
        // paint recording indicator
        if(timer.isRecording()) {
            g.setColor(Color.RED);
            g.fillOval(5, 30, 7, 7);
        }
        
        // paint conflicts
        g.setColor(CONFLICT_COLOR);
        if(timer.getConflictPeriods() != null && timer.getConflictPeriods().size() > 0) {
            for (Period period : timer.getConflictPeriods()) {
                long durationMinutes = Utilities.getDurationInMinutes(timer.getStartTime(), timer.getEndTime());
                double pixelsPerMinute = (double)getWidth() / (double)durationMinutes;
                long startMinute = Utilities.getDurationInMinutes(timer.getStartTime(), period.getStartTime());
                int x = (int)(pixelsPerMinute * startMinute);
                int width = (int)(pixelsPerMinute * Utilities.getDurationInMinutes(period.getStartTime(), period.getEndTime()));
                g.fillRect(x, 0, width, getHeight()-1);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
            ProgramManager.getInstance().handleTimerDoubleClick(getTimer());
        }
    }

    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()) {
            JPopupMenu popup = ProgramManager.getInstance().getContextMenuForTimer(timer);
            popup.setLocation(e.getPoint());
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
        
    }
    
    
}