/* $Id: PlayerPanel.java,v 1.1 2007-04-09 19:20:20 hampelratte Exp $
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
package lazybones.gui.settings;

import info.clearthought.layout.TableLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lazybones.LazyBones;

public class PlayerPanel {
    private final JLabel lPlayer = new JLabel(LazyBones.getTranslation("player", "Player"));

    private JTextField player;

    private final JLabel lParams = new JLabel(LazyBones.getTranslation("params", "Parameters"));

    private JTextField params;

    private final String lSwitchBefore = LazyBones.getTranslation(
            "switch_before", "Switch to channel before streaming");
    
    private final String ttSwitchBefore = LazyBones.getTranslation(
            "switch_before.tooltip", "This is useful, if you only have one DVB Tuner");

    private JCheckBox switchBefore;
    
    private JComboBox streamType;

    public PlayerPanel() {
        initComponents();
    }
    
    private void initComponents() {
        player = new JTextField(20);
        player.setText(LazyBones.getProperties().getProperty("player"));
        params = new JTextField(20);
        params.setText(LazyBones.getProperties().getProperty("player_params"));
        switchBefore = new JCheckBox(lSwitchBefore);
        switchBefore.setSelected(new Boolean(LazyBones.getProperties()
                .getProperty("switchBefore")).booleanValue());
        switchBefore.setToolTipText(ttSwitchBefore);
        streamType = new JComboBox();
        streamType.addItem("TS");
        streamType.addItem("PS");
        streamType.addItem("PES");
        streamType.addItem("ES");
        String streamString = LazyBones.getProperties().getProperty("streamtype");
        for(int i=0; i<streamType.getItemCount(); i++) {
            if(streamType.getItemAt(i).equals(streamString)) {
                streamType.setSelectedIndex(i);
            }
        }
    }

    public JPanel getPanel() {
        final double P = TableLayout.PREFERRED;
        double[][] size = {{0, P, P}, //cols
                           {0, P, P, P, P}}; // rows
        TableLayout layout = new TableLayout(size);
        layout.setHGap(10);
        layout.setVGap(10);
        
        JPanel panel = new JPanel(layout);
        panel.add(lPlayer,      "1,1,1,1");
        panel.add(player,       "2,1,2,1");
        panel.add(lParams,      "1,2,1,2");
        panel.add(params,       "2,2,2,2");
        panel.add(streamType,   "2,3,2,3");
        panel.add(switchBefore, "2,4,2,4");

		return panel;
    }

    public void saveSettings() {
        LazyBones.getProperties().setProperty("player", player.getText());
        LazyBones.getProperties().setProperty("player_params", params.getText());
        LazyBones.getProperties().setProperty("switchBefore",
                new Boolean(switchBefore.isSelected()).toString());
        LazyBones.getProperties().setProperty("streamtype", streamType.getSelectedItem().toString());
    }
}