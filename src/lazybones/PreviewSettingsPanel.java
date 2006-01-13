/* $Id: PreviewSettingsPanel.java,v 1.8 2006-01-13 10:36:03 hampelratte Exp $
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

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PreviewSettingsPanel implements ItemListener {
    private static final long serialVersionUID = 5046636902877005743L;

    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(PreviewSettingsPanel.class);

    private LazyBones control;
    
    private String lMethod = mLocalizer.msg("method", "Method");
    
    private JComboBox method = new JComboBox();
    
    private CardLayout cardLayout = new CardLayout();
    
    private JPanel cardsContainer = new JPanel();

    private JPanel httpPanel = new JPanel();
    
    private final String lURL = mLocalizer.msg("url",
            "URL to preview picture");

    private JTextField url;

    private final String lPicturePath = mLocalizer.msg("path",
            "Path to preview picture");

    private final String lDescription = mLocalizer
			.msg(
					"desc",
					"The URL is the URL, where"
							+ " VDRRemoteControl can download the preview image. The path is the path to the preview"
							+ " image on the VDR host. This should be the document root of the webserver, which has"
							+ " been specified in the URL");
                                    

    private JTextField picturePath;
    
    private JComponent note;

    public PreviewSettingsPanel(LazyBones control) {
        this.control = control;
        initComponents();
    }
    
    private void initComponents() {
        url = new JTextField(20);
        url.setText(control.getProperties().getProperty("preview.url"));
        picturePath = new JTextField(20);
        picturePath
                .setText(control.getProperties().getProperty("preview.path"));

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
        
        FormLayout layout = new FormLayout("left:75dlu, 3dlu, 120dlu",
        "pref, 2dlu, pref, 10dlu, top:pref:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        
        builder.addLabel(lURL,         cc.xy (1,  1));
        builder.add(url,               cc.xyw(3,  1, 1));
        
        builder.addLabel(lPicturePath, cc.xy (1,  3));
        builder.add(picturePath,       cc.xyw(3,  3, 1));
        
        builder.add(note,              cc.xyw(1,  5, 3));
        
        httpPanel = builder.getPanel();
        
        method.addItem("HTTP");
        method.addItem("SVDRP");
        method.addItemListener(this);
        String m = control.getProperties().getProperty("preview.method");
        for(int i=0; i<method.getItemCount(); i++) {
            String item = (String)method.getItemAt(i);
            if(m.equals(item)) {
                method.setSelectedIndex(i);
                break;
            }
        }
        
        cardsContainer.setLayout(cardLayout);
        cardsContainer.add(httpPanel, "HTTP");
        cardsContainer.add(new JPanel(), "SVDRP");
    }

    JPanel getPanel() {
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
        //JPanel dummy = new JPanel(new BorderLayout());
        //dummy.add(httpPanel, BorderLayout.CENTER);
        panel.add(cardsContainer, gbc);
        return panel;
    }

    public void saveSettings() {
        control.getProperties().setProperty("preview.url", url.getText());
        control.getProperties().setProperty("preview.path",
                picturePath.getText());
        control.getProperties().setProperty("preview.method",
                method.getSelectedItem().toString());
    }

    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            cardLayout.show(cardsContainer, e.getItem().toString());
            /*
            if(e.getItem().toString().equals("HTTP")) {
                httpPanel.setVisible(true);
            } else {
                httpPanel.setVisible(false);
            }*/
        }
        
    }
}