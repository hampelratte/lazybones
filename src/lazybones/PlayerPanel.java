/* $Id: PlayerPanel.java,v 1.5 2005-08-27 20:07:57 emsker Exp $
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
package lazybones;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.ui.Localizer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PlayerPanel {
    private static final long serialVersionUID = 151916691928714906L;

    private static final Localizer mLocalizer = Localizer
            .getLocalizerFor(PlayerPanel.class);

    private LazyBones control;

    private final String lPlayer = mLocalizer.msg("player", "Player");

    private JTextField player;

    private final String lParams = mLocalizer.msg("params", "Parameters");

    private JTextField params;

    private final String lSwitchBefore = mLocalizer.msg(
            "switch_before", "Switch to channel before streaming");
    
    private final String ttSwitchBefore = mLocalizer.msg(
            "switch_before.tooltip", "This is useful, if you only have one DVB Tuner");

    private JCheckBox switchBefore;

    public PlayerPanel(LazyBones control) {
        this.control = control;
        initComponents();
    }
    
    private void initComponents() {
        player = new JTextField(20);
        player.setText(control.getProperties().getProperty("player"));
        params = new JTextField(20);
        params.setText(control.getProperties().getProperty("player_params"));
        switchBefore = new JCheckBox(lSwitchBefore);
        switchBefore.setSelected(new Boolean(control.getProperties()
                .getProperty("switchBefore")).booleanValue());
        switchBefore.setToolTipText(ttSwitchBefore);
    }

    JPanel getPanel() {
		FormLayout layout = new FormLayout(VDRSettingsPanel.FORMBUILDER_DEFAULT_COLUMNS,
			"pref, 2dlu, pref, 2dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel(lPlayer, cc.xy (1,  1));
		builder.add(player,       cc.xyw(3,  1, 3));
		
		builder.addLabel(lParams, cc.xy (1,  3));
		builder.add(params,       cc.xyw(3,  3, 3));

		builder.add(switchBefore, cc.xyw(3,  5, 3));

		return builder.getPanel();
    }

    public void saveSettings() {
        control.getProperties().setProperty("player", player.getText());
        control.getProperties().setProperty("player_params", params.getText());
        control.getProperties().setProperty("switchBefore",
                new Boolean(switchBefore.isSelected()).toString());
    }
}