/* $Id: ScreenshotSettingsPanel.java,v 1.1 2007-04-09 19:13:15 hampelratte Exp $
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
package lazybones.gui.components.remotecontrol;

import info.clearthought.layout.TableLayout;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import lazybones.LazyBones;

public class ScreenshotSettingsPanel implements ItemListener {
    private String lMethod = LazyBones.getTranslation("method", "Method");
    
    private JComboBox method = new JComboBox();
    
    private CardLayout cardLayout = new CardLayout();
    
    private JPanel cardsContainer = new JPanel(cardLayout);

    private JPanel httpPanel = new JPanel();
    
    private final JLabel lURL = new JLabel(LazyBones.getTranslation("url",
            "URL to preview picture"));

    private JTextField url;

    private final JLabel lPicturePath = new JLabel(LazyBones.getTranslation("path",
            "Path to preview picture"));

    private final String lDescription = LazyBones.getTranslation(
					"desc_url", "The URL is the URL, where"
							+ " VDRRemoteControl can download the preview image. The path is the path to the preview"
							+ " image on the VDR host. This should be the document root of the webserver, which has"
							+ " been specified in the URL");
                                    

    private JTextField picturePath;
    
    private JComponent note;

    public ScreenshotSettingsPanel() {
        initComponents();
    }
    
    private void initComponents() {
        url = new JTextField(20);
        url.setText(LazyBones.getProperties().getProperty("preview.url"));
        picturePath = new JTextField(20);
        picturePath.setText(LazyBones.getProperties().getProperty("preview.path"));

        JTextArea description;
        description = new JTextArea(lDescription, 10, 40);
        description = new JTextArea(lDescription);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(UIManager.getColor("JPanel.background"));

        note = new JScrollPane(description,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        final double P = TableLayout.PREFERRED;
        double[][] size = {{0, P, TableLayout.FILL, 0}, //cols
                           {0, P, P, P}}; // rows
        TableLayout layout = new TableLayout(size);
        layout.setHGap(10);
        layout.setVGap(10);
        httpPanel.setLayout(layout);
                
        httpPanel.add(lURL,         "1,1,1,1");
        httpPanel.add(url,          "2,1,2,1");
        httpPanel.add(lPicturePath, "1,2,1,2");
        httpPanel.add(picturePath,  "2,2,2,2");
        httpPanel.add(note,         "1,3,2,3");
        
        cardsContainer.add(httpPanel, "HTTP");
        cardsContainer.add(new JPanel(), "SVDRP");
        
        method.addItem("HTTP");
        method.addItem("SVDRP");
        method.addItemListener(this);
        String m = LazyBones.getProperties().getProperty("preview.method");
        for(int i=0; i<method.getItemCount(); i++) {
            String item = (String)method.getItemAt(i);
            if(m.equals(item)) {
                method.setSelectedIndex(i);
                break;
            }
        }
    }

    public JPanel getPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10,10,10,10);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(lMethod), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(method, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0,0,0,0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(cardsContainer, gbc);
        return panel;
    }

    public void saveSettings() {
        LazyBones.getProperties().setProperty("preview.url", url.getText());
        LazyBones.getProperties().setProperty("preview.path",
                picturePath.getText());
        LazyBones.getProperties().setProperty("preview.method",
                method.getSelectedItem().toString());
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            cardLayout.show(cardsContainer, e.getItem().toString());
        }
    }
}