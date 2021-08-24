/*
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
package lazybones.gui.settings;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.awt.GridBagConstraints.WEST;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lazybones.LazyBones;


public class PlayerPanel {

    private final String lSwitchBefore = LazyBones.getTranslation("switch_before", "Switch to channel before streaming");
    private final String ttSwitchBefore = LazyBones.getTranslation("switch_before.tooltip", "This is useful, if you only have one DVB Tuner");
    private final String lSurviveOnExit = LazyBones.getTranslation("survive_on_exit", "Keep player running when TV-Browser closes");

    private JPanel panel;
    private JTextField urlRecordings;
    private JTextField url;
    private JCheckBox switchBefore;
    private JCheckBox surviveOnExit;
    private JComboBox<String> streamType;
    private JTextField params;
    private JTextField player;

    public PlayerPanel() {
        initComponents();
    }

    private void initComponents() {
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        GridBagLayout panelLayout = new GridBagLayout();
        panel.setLayout(panelLayout);
        panel.setPreferredSize(new java.awt.Dimension(346, 183));

        Insets defaultInsets = new Insets(5, 5, 5, 5);

        var container = new JPanel();
        panel.add(container, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, NORTHWEST, HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        GridBagLayout containerLayout = new GridBagLayout();
        container.setLayout(containerLayout);

        var lPlayer = new JLabel();
        container.add(lPlayer, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));
        lPlayer.setText(LazyBones.getTranslation("player", "Player"));

        player = new JTextField(LazyBones.getProperties().getProperty("player"));
        container.add(player, new GridBagConstraints(1, 0, 1, 1, 0.9, 0.1, WEST, HORIZONTAL, defaultInsets, 0, 0));

        var lParams = new JLabel();
        container.add(lParams, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));
        lParams.setText(LazyBones.getTranslation("params", "Parameters"));

        params = new JTextField(LazyBones.getProperties().getProperty("player_params"));
        container.add(params, new GridBagConstraints(1, 1, 1, 1, 0.9, 0.1, WEST, HORIZONTAL, defaultInsets, 0, 0));

        streamType = new JComboBox<>();
        container.add(streamType, new GridBagConstraints(1, 4, 1, 1, 0.9, 0.1, WEST, HORIZONTAL, defaultInsets, 0, 0));

        switchBefore = new JCheckBox(lSwitchBefore);
        switchBefore.setToolTipText(ttSwitchBefore);
        container.add(switchBefore, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));

        surviveOnExit = new JCheckBox(lSurviveOnExit);
        container.add(surviveOnExit, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));

        var lUrlRecordings = new JLabel();
        container.add(lUrlRecordings, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));
        lUrlRecordings.setText(LazyBones.getTranslation("url", "URL") + " " + LazyBones.getTranslation("recordings", "Recordings"));

        var lURL = new JLabel();
        container.add(lURL, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));
        lURL.setText(LazyBones.getTranslation("url", "URL"));

        url = new JTextField(LazyBones.getProperties().getProperty("streamurl"));
        container.add(url, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, defaultInsets, 0, 0));

        var lStreamtype = new JLabel();
        container.add(lStreamtype, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, WEST, NONE, defaultInsets, 0, 0));
        lStreamtype.setText(LazyBones.getTranslation("streamtype", "Stream type"));

        urlRecordings = new JTextField(LazyBones.getProperties().getProperty("recording.url"));
        container.add(urlRecordings, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, WEST, HORIZONTAL, defaultInsets, 0, 0));


        switchBefore.setSelected(Boolean.parseBoolean(LazyBones.getProperties().getProperty("switchBefore")));
        surviveOnExit.setSelected(Boolean.parseBoolean(LazyBones.getProperties().getProperty("surviveOnExit")));
        streamType.addItem("TS");
        streamType.addItem("PS");
        streamType.addItem("PES");
        streamType.addItem("ES");
        String streamString = LazyBones.getProperties().getProperty("streamtype");
        for (int i = 0; i < streamType.getItemCount(); i++) {
            if (streamType.getItemAt(i).equals(streamString)) {
                streamType.setSelectedIndex(i);
            }
        }
    }

    public void saveSettings() {
        LazyBones.getProperties().setProperty("player", player.getText());
        LazyBones.getProperties().setProperty("player_params", params.getText());
        LazyBones.getProperties().setProperty("streamurl", url.getText());
        LazyBones.getProperties().setProperty("recording.url", urlRecordings.getText());
        LazyBones.getProperties().setProperty("switchBefore", Boolean.toString(switchBefore.isSelected()));
        LazyBones.getProperties().setProperty("surviveOnExit", Boolean.toString(surviveOnExit.isSelected()));
        LazyBones.getProperties().setProperty("streamtype", streamType.getSelectedItem().toString());
    }

    public JPanel getPanel() {
        return panel;
    }

}