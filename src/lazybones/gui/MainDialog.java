/* $Id: MainDialog.java,v 1.6 2007-04-09 19:21:50 hampelratte Exp $
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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import lazybones.LazyBones;
import lazybones.gui.components.ScreenshotPanel;
import lazybones.gui.components.remotecontrol.RemoteControl;

public class MainDialog extends JDialog {

    private LazyBones lazyBones;
    
    private ScreenshotPanel pp = new ScreenshotPanel();
    
    private JTabbedPane tabbedPane = new JTabbedPane();
    
    private TimelinePanel timelinePanel;
    
    public MainDialog(Frame parent, String title, boolean modal, LazyBones lazyBones) {
        super(parent, title, modal);
        this.lazyBones = lazyBones;
        initGUI();
    }

    private void initGUI() {
        this.setSize(800, 450);
        JPanel remoteControl = new JPanel();
        remoteControl.setLayout(new GridBagLayout());
        remoteControl.add(new RemoteControl(lazyBones), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        remoteControl.add(pp, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        tabbedPane.add(LazyBones.getTranslation("remoteControl", "Remote Control"), remoteControl);
        tabbedPane.add(LazyBones.getTranslation("timer", "Timers"), new TimerManagerPanel(lazyBones));
        timelinePanel = new TimelinePanel(lazyBones);
        tabbedPane.add(LazyBones.getTranslation("timeline", "Timeline"), timelinePanel);
        
        this.add(tabbedPane);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                pp.stopGrabbing();
                setVisible(false);
            }
        });
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible) {
            pp.startGrabbing();
        }
    }

    public void showTimeline() {
        tabbedPane.setSelectedIndex(2);
    }

    public TimelinePanel getTimelinePanel() {
        return timelinePanel;
    }
}
