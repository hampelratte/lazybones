/* $Id: RecordingManagerPanel.java,v 1.5 2007-04-30 15:45:38 hampelratte Exp $
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.RecordingManager;
import lazybones.actions.DeleteRecordingAction;
import lazybones.gui.components.EPGInfoPanel;
import lazybones.gui.utils.RecordingListCellRenderer;

import org.hampelratte.svdrp.responses.highlevel.Recording;

public class RecordingManagerPanel extends JPanel implements ActionListener, Observer {

    private JScrollPane scrollPane = null;
    private DefaultListModel model = new DefaultListModel();
    private JList recordingList = new JList(model);
    private int selectedRow = -1;
    

    private JPopupMenu popup = new JPopupMenu();
    
    public RecordingManagerPanel() {
        initGUI();
        RecordingManager.getInstance().addObserver(this);
        RecordingManager.getInstance().synchronize();
    }

    /**
     * This method initializes the GUI
     * 
     */
    private void initGUI() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 3;
        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.gridx = 0;
        recordingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordingList.setCellRenderer(new RecordingListCellRenderer());
        scrollPane = new JScrollPane(recordingList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        this.add(scrollPane, gbc);
        
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.insets = new java.awt.Insets(0,10,10,10);

        JMenuItem menuDelete = new JMenuItem(LazyBones.getTranslation("delete", "Delete"));
        menuDelete.addActionListener(this);
        menuDelete.setActionCommand("DELETE");
        menuDelete.setIcon(LazyBones.getInstance().getIcon("lazybones/cancel.png"));
        JMenuItem menuInfo = new JMenuItem(LazyBones.getTranslation("recording_info", "Show information"));
        menuInfo.addActionListener(this);
        menuInfo.setActionCommand("INFO");
        menuInfo.setIcon(LazyBones.getInstance().createImageIcon("actions", "edit-find", 16));
        JMenuItem menuSync = new JMenuItem(LazyBones.getTranslation("resync", "Synchronize with VDR"));
        menuSync.addActionListener(this);
        menuSync.setActionCommand("SYNC");
        menuSync.setIcon(LazyBones.getInstance().getIcon("lazybones/reload.png"));
        popup.add(menuInfo);
        popup.add(menuDelete);
        popup.add(menuSync);
        
        recordingList.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    selectedRow = recordingList.locationToIndex(e.getPoint());
                    recordingList.setSelectedIndex(selectedRow);
                    popup.setLocation(e.getPoint());
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
    }
    
    public void actionPerformed(ActionEvent e) {
        Recording rec = (Recording) recordingList.getModel().getElementAt(selectedRow);
        
        if("DELETE".equals(e.getActionCommand())) {
            DeleteRecordingAction dra = new DeleteRecordingAction(rec);
            if(!dra.execute()) {
                Logger.getLogger().log(dra.getResponse().getMessage(), Logger.OTHER, Logger.ERROR);
            } else {
                updateRecordings();
            }
        } else if("INFO".equals(e.getActionCommand()) && selectedRow >= 0) {
            JDialog dialog = new JDialog();
            dialog.getContentPane().add(new EPGInfoPanel(rec.getEpgInfo()));
            dialog.setSize(400,300);
            dialog.setVisible(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } else if("SYNC".equals(e.getActionCommand())) {
            RecordingManager.getInstance().synchronize();
        }
    }

    public void update(Observable arg0, Object recordings) {
        if(arg0 == RecordingManager.getInstance()) {
            updateRecordings();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void updateRecordings() {
        model.removeAllElements();
        List recordings = RecordingManager.getInstance().getRecordings();
        if(recordings != null && recordings.size() > 0) {
            Collections.sort(recordings, new RecordingComparator());
            for (Iterator iter = recordings.iterator(); iter.hasNext();) {
                Recording rec = (Recording) iter.next();
                model.addElement(rec);
            }
        }
    }
    
    private class RecordingComparator implements Comparator<Recording> {
        public int compare(Recording r1, Recording r2) {
            String title1 = r1.getTitle();
            String title2 = r2.getTitle();
            
            if(title1.charAt(0) == '%' || title1.charAt(0) == '@') {
                title1 = title1.substring(1);
            }
            if(title2.charAt(0) == '%' || title2.charAt(0) == '@') {
                title2 = title2.substring(1);
            }
            
            return title1.toLowerCase().compareTo(title2.toLowerCase());
        }
    }
}