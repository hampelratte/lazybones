/* $Id: EPGInfoPanel.java,v 1.3 2008-05-06 17:32:29 hampelratte Exp $
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
package lazybones.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lazybones.RecordingManager;

import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.hampelratte.svdrp.responses.highlevel.Recording;

public class EPGInfoPanel extends JPanel implements ListSelectionListener {
    
    private EPGEntry epg;
    
    private JLabel title = new JLabel();
    private JLabel time = new JLabel();
    private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault());
    private JLabel shortTextLabel = new JLabel();
    private JTextArea desc = new JTextArea();
    
    public EPGInfoPanel() {
        initGUI();
    }
    
    public EPGInfoPanel(EPGEntry epg) {
        this.epg = epg;
        initGUI();
        loadData();
    }
    
    public void setEpg(EPGEntry epg) {
        this.epg = epg;
        loadData();
    }
    
    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        add(title, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(time, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(shortTextLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setBackground(Color.WHITE);
        add(new JScrollPane(desc), gbc);
    }
    
    private void loadData() {
        title.setText(epg.getTitle());
        time.setText(df.format(epg.getStartTime().getTime()));
        String shortText = (epg.getShortText() != null && epg.getShortText().length() > 0) ? epg.getShortText() : "";
        shortTextLabel.setText(shortText);
        desc.setText(epg.getDescription());
    }

    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList) e.getSource();
        Recording rec = (Recording) list.getSelectedValue();
        if(rec == null) {
            title.setText(null);
            time.setText(null);
            shortTextLabel.setText(null);
            desc.setText(null);
        } else {
            if(rec.getEpgInfo() == null) {
                RecordingManager.getInstance().loadInfo(rec);
            }
            setEpg(rec.getEpgInfo());
        }
    }
}