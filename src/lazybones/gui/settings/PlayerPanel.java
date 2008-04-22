/* $Id: PlayerPanel.java,v 1.2 2008-04-22 14:23:57 hampelratte Exp $
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


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PlayerPanel {

    private final String lSwitchBefore = LazyBones.getTranslation(
            "switch_before", "Switch to channel before streaming");

    private final String ttSwitchBefore = LazyBones.getTranslation(
            "switch_before.tooltip", "This is useful, if you only have one DVB Tuner");

    private JPanel panel;
    private JLabel lStreamtype;
    private JTextField url;
    private JLabel lURL;
    private JLabel lUrlRecordings;
    private JCheckBox switchBefore;
    private JPanel dummy;
    private JComboBox streamType;
    private JTextField params;
    private JLabel lParams;
    private JTextField player;
    private JLabel lPlayer;
    private JPanel container;

    public PlayerPanel() {
        initComponents();
    }
    
    private void initComponents() {
    	{
    	    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	    GridBagLayout panelLayout = new GridBagLayout();
    	    panelLayout.rowWeights = new double[] {0.1};
    	    panelLayout.rowHeights = new int[] {7};
    	    panelLayout.columnWeights = new double[] {0.1};
    	    panelLayout.columnWidths = new int[] {7};
    	    panel.setLayout(panelLayout);
            panel.setPreferredSize(new java.awt.Dimension(346, 163));
	    	{
	    		container = new JPanel();
	    		panel.add(container, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	    		GridBagLayout containerLayout = new GridBagLayout();
	    		containerLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
	    		containerLayout.rowHeights = new int[] {0, 0, 0, 0, 7};
	    		containerLayout.columnWeights = new double[] {0.1, 0.1};
	    		containerLayout.columnWidths = new int[] {7, 7};
	    		container.setLayout(containerLayout);
	    		{
	    			lPlayer = new JLabel();
	    			container.add(lPlayer, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    			lPlayer.setText(LazyBones.getTranslation("player","Player"));
	    		}
	    		{
	    			player = new JTextField();
	    			container.add(player, new GridBagConstraints(1, 0, 1, 1, 0.9, 0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
	    		}
	    		{
	    			lParams = new JLabel();
	    			container.add(lParams, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    			lParams.setText(LazyBones.getTranslation("params","Parameters"));
	    		}
	    		{
	    			params = new JTextField();
	    			container.add(params, new GridBagConstraints(1, 1, 1, 1, 0.9, 0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
	    		}
	    		{
	    			streamType = new JComboBox();
	    			container.add(streamType, new GridBagConstraints(1, 3, 1, 1, 0.9, 0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
	    		}
	    		{
	    			dummy = new JPanel();
	    			GridBagLayout dummyLayout = new GridBagLayout();
	    			container.add(dummy, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    			dummyLayout.rowWeights = new double[] {0.1};
	    			dummyLayout.rowHeights = new int[] {7};
	    			dummyLayout.columnWeights = new double[] {0.1, 0.1};
	    			dummyLayout.columnWidths = new int[] {7, 7};
	    			dummy.setLayout(dummyLayout);
	    			{
	    				switchBefore = new JCheckBox();
	    				switchBefore.setToolTipText(ttSwitchBefore);
	    				dummy.add(switchBefore, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
	    			}
	    			{
                        dummy.add(new JLabel(lSwitchBefore), new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                    }
	    		}
	    		{
	    			lUrlRecordings = new JLabel();
	    			container.add(lUrlRecordings, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	    		}
	    		{
	    		    lURL = new JLabel();
	    		    container.add(lURL, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	    		    lURL.setText(LazyBones.getTranslation("url","URL"));
	    		}
	    		{
	    		    url = new JTextField();
	    		    container.add(url, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
	    		}
	    		{
	    		    lStreamtype = new JLabel();
	    		    container.add(lStreamtype, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                    lStreamtype.setText(LazyBones.getTranslation("streamtype","Stream type"));
	    		}
	    	}
    	}
        player.setText(LazyBones.getProperties().getProperty("player"));
        params.setText(LazyBones.getProperties().getProperty("player_params"));
        url.setText(LazyBones.getProperties().getProperty("streamurl"));
        switchBefore.setSelected(new Boolean(LazyBones.getProperties()
                .getProperty("switchBefore")).booleanValue());
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

    public void saveSettings() {
        LazyBones.getProperties().setProperty("player", player.getText());
        LazyBones.getProperties().setProperty("player_params", params.getText());
        LazyBones.getProperties().setProperty("streamurl", url.getText());
        LazyBones.getProperties().setProperty("switchBefore",
                new Boolean(switchBefore.isSelected()).toString());
        LazyBones.getProperties().setProperty("streamtype", streamType.getSelectedItem().toString());
    }

    public JPanel getPanel() {
        return panel;
    }
    
}