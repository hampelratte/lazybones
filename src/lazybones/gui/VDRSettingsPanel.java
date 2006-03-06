/* $Id: VDRSettingsPanel.java,v 1.2 2006-03-06 20:42:02 hampelratte Exp $
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
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import lazybones.LazyBones;
import util.ui.ImageUtilities;

/**
 * The root container for the settings tabs
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 */
public class VDRSettingsPanel implements devplugin.SettingsTab {
    private LazyBones control;

    private JTabbedPane tabbedPane;

    private ChannelPanel channelPanel;

    private GeneralPanel generalPanel;

    private PlayerPanel playerPanel;

    private TimerPanel timerPanel;

    private PreviewSettingsPanel previewPanel;

    public VDRSettingsPanel(LazyBones control) {
        this.control = control;
    }


    public JPanel createSettingsPanel() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();
            tabbedPane.setPreferredSize(new Dimension(380, 380));

            generalPanel = new GeneralPanel(control);
            tabbedPane.addTab(LazyBones.getTranslation("general", ""), generalPanel
					.getPanel());

            channelPanel = new ChannelPanel(control);
            tabbedPane.addTab(LazyBones.getTranslation("channels", ""), channelPanel
					.getPanel());

            playerPanel = new PlayerPanel(control);
            tabbedPane.addTab(LazyBones.getTranslation("player", ""), playerPanel
					.getPanel());

            timerPanel = new TimerPanel(control);
            tabbedPane.addTab(LazyBones.getTranslation("timer", ""), timerPanel
					.getPanel());

            previewPanel = new PreviewSettingsPanel(control);
            tabbedPane.addTab(LazyBones.getTranslation("remoteControl", ""), previewPanel
					.getPanel());
        }

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(tabbedPane, BorderLayout.CENTER);
        return p;
    }

    public void saveSettings() {
        channelPanel.saveSettings();
        generalPanel.saveSettings();
        playerPanel.saveSettings();
        timerPanel.saveSettings();
        previewPanel.saveSettings();
    }

    public Icon getIcon() {
        return new ImageIcon(ImageUtilities.createImageFromJar(
                "lazybones/vdr16.png", VDRSettingsPanel.class));
    }

    public String getTitle() {
        return "Lazy Bones";
    }
}