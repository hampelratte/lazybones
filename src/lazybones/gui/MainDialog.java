/* $Id: MainDialog.java,v 1.19 2011-04-20 12:09:12 hampelratte Exp $
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
package lazybones.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lazybones.LazyBones;
import lazybones.gui.components.ScreenshotPanel;
import lazybones.gui.components.remotecontrol.RemoteControl;
import lazybones.gui.recordings.RecordingManagerPanel;
import lazybones.gui.timers.TimerManagerPanel;
import util.ui.UiUtilities;
import util.ui.WindowClosingIf;

public class MainDialog extends JDialog implements WindowClosingIf {

    private RemoteControl remoteControl = new RemoteControl();

    private ScreenshotPanel screenshotPanel = new ScreenshotPanel();

    private JTabbedPane tabbedPane = new JTabbedPane();

    private TimelinePanel timelinePanel;

    private static final int INDEX_TIMELINE = 0;
    private static final int INDEX_RC = 3;

    public MainDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);
        initGUI();
        UiUtilities.registerForClosing(this);
    }

    private void initGUI() {
        JPanel rcPanel = new JPanel();
        rcPanel.setLayout(new GridBagLayout());
        rcPanel.add(remoteControl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),
                0, 0));
        rcPanel.add(screenshotPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        screenshotPanel.setPreferredSize(new Dimension(720, 540));
        timelinePanel = new TimelinePanel();

        // !! don't change the order without changing INDEX_TIMELINE and INDEX_RC !!
        tabbedPane.add(LazyBones.getTranslation("timeline", "Timeline"), timelinePanel);
        tabbedPane.add(LazyBones.getTranslation("timers", "Timers"), new TimerManagerPanel());
        tabbedPane.add(LazyBones.getTranslation("recordings", "Recordings"), new RecordingManagerPanel());
        tabbedPane.add(LazyBones.getTranslation("remoteControl", "Remote Control"), rcPanel);

        if (Boolean.parseBoolean(LazyBones.getProperties().getProperty("upload.enabled"))) {
            tabbedPane.add("Data upload", new DataUploadPanel());
        }

        this.add(tabbedPane);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (isVisible() && tabbedPane.getSelectedIndex() == INDEX_RC) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            remoteControl.updateVolume();
                        }
                    });
                    screenshotPanel.startGrabbing();
                } else {
                    screenshotPanel.stopGrabbing();
                }
            }
        });

        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        this.pack();

        // position lazy bones centered on tvb
        int parentWidth = LazyBones.getInstance().getParent().getWidth();
        int parentHeight = LazyBones.getInstance().getParent().getHeight();
        int parentX = LazyBones.getInstance().getParent().getX();
        int parentY = LazyBones.getInstance().getParent().getY();
        int posX = (parentWidth - getWidth()) / 2;
        int posY = (parentHeight - getHeight()) / 2;
        setLocation(parentX + posX, parentY + posY);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && tabbedPane.getSelectedIndex() == INDEX_RC) {
            remoteControl.updateVolume();
            screenshotPanel.startGrabbing();
        } else {
            screenshotPanel.stopGrabbing();
        }
    }

    public void showTimeline() {
        tabbedPane.setSelectedIndex(INDEX_TIMELINE);
    }

    public TimelinePanel getTimelinePanel() {
        return timelinePanel;
    }

    public void close() {
        setVisible(false);
    }
}
