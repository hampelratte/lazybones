/* $Id: TimelinePanel.java,v 1.2 2007-01-07 12:37:56 hampelratte Exp $
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
package lazybones.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lazybones.LazyBones;
import lazybones.TimerManager;
import lazybones.gui.components.timeline.Timeline;

//TODO nur tage zeigen an denen timer starten oder enden
public class TimelinePanel extends JPanel implements ActionListener {
    
    private LazyBones lazyBones;
    private JLabel date = new JLabel();
    private JButton nextDateButton;
    private JButton prevDateButton;
    private Timeline timeline;
    private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    
    public TimelinePanel(LazyBones lazyBones) {
        this.lazyBones = lazyBones;
        timeline = new Timeline();
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        
        nextDateButton = new JButton(lazyBones.getIcon("lazybones/go-next.png"));
        nextDateButton.addActionListener(this);
        nextDateButton.setActionCommand("NEXT_DAY");
        prevDateButton = new JButton(lazyBones.getIcon("lazybones/go-previous.png"));
        prevDateButton.addActionListener(this);
        prevDateButton.setActionCommand("PREVIOUS_DAY");
        
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        northPanel.add(date);
        northPanel.add(prevDateButton);
        northPanel.add(nextDateButton);
        add(northPanel, BorderLayout.NORTH);
        
        date.setText(df.format(new Date()));
        date.setFont(new Font("SansSerif",Font.PLAIN, 18));

        add(timeline, BorderLayout.CENTER);
        timeline.getList().showTimersForCurrentDate(TimerManager.getInstance().getTimers());
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == nextDateButton) {
            timeline.getList().getCalendar().add(Calendar.DAY_OF_MONTH, 1);
        } else if(e.getSource() == prevDateButton) {
            timeline.getList().getCalendar().add(Calendar.DAY_OF_MONTH, -1);
        }
        
        date.setText(df.format(timeline.getList().getCalendar().getTime()));
        timeline.getList().showTimersForCurrentDate(TimerManager.getInstance().getTimers());
    }
}