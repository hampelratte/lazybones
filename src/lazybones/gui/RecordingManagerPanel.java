/* $Id: RecordingManagerPanel.java,v 1.14 2008-10-04 18:36:31 hampelratte Exp $
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import lazybones.LazyBones;
import lazybones.Player;
import lazybones.RecordingManager;
import lazybones.VDRCallback;
import lazybones.actions.DeleteRecordingAction;
import lazybones.actions.VDRAction;
import lazybones.gui.components.EPGInfoPanel;
import lazybones.gui.utils.RecordingListCellRenderer;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ui.Localizer;

public class RecordingManagerPanel extends JPanel implements ActionListener, Observer {
    
    private static transient Logger logger = LoggerFactory.getLogger(RecordingManagerPanel.class);
    
    private JScrollPane scrollPane = null;
    private DefaultListModel model = new DefaultListModel();
    private JList recordingList = new JList(model);
    private int selectedRow = -1;
    private EPGInfoPanel epgInfoPanel = new EPGInfoPanel();
    

    private JPopupMenu popup = new JPopupMenu();
    
    public RecordingManagerPanel() {
        initGUI();
        RecordingManager.getInstance().addObserver(this);
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
        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.gridx = 0;
        recordingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordingList.setCellRenderer(new RecordingListCellRenderer());
        scrollPane = new JScrollPane(recordingList);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        this.add(scrollPane, gbc);
        
        //gbc.fill = java.awt.GridBagConstraints.VERTICAL;
        gbc.gridx = 1;
        gbc.weightx = .1;
        recordingList.addListSelectionListener(epgInfoPanel);
        epgInfoPanel.setBorder(BorderFactory.createTitledBorder(LazyBones.getTranslation("details", "Details")));
        epgInfoPanel.setPreferredSize(new Dimension(300,300));
        epgInfoPanel.setMinimumSize(new Dimension(300,300));
        epgInfoPanel.setMaximumSize(new Dimension(300,300));
        this.add(epgInfoPanel, gbc);

        createContextMenu();
    }
    
    public void actionPerformed(ActionEvent e) {
        Recording rec = (Recording) recordingList.getModel().getElementAt(selectedRow);
        
        if("DELETE".equals(e.getActionCommand())) {
            VDRCallback callback = new VDRCallback() {
                public void receiveResponse(VDRAction cmd, Response response) {
                    if(!cmd.isSuccess()) {
                        logger.error(cmd.getResponse().getMessage());
                    } else {
                        RecordingManager.getInstance().synchronize();
                    }
                }
            };
            DeleteRecordingAction dra = new DeleteRecordingAction(rec, callback);
            dra.enqueue();
        } else if("INFO".equals(e.getActionCommand()) && selectedRow >= 0) {
            createEPGInfoDialog(rec);
        } else if("SYNC".equals(e.getActionCommand())) {
            RecordingManager.getInstance().synchronize();
        } else if("PLAY".equals(e.getActionCommand())) {
            Player.play(rec);
        } else if("PLAY_ON_VDR".equals(e.getActionCommand())) {
            RecordingManager.getInstance().playOnVdr(rec);
        }
    }

    private void createEPGInfoDialog(Recording rec) {
        final JDialog dialog = new JDialog();
        if(rec.getEpgInfo() == null) {
            RecordingManager.getInstance().loadInfo(rec);
        }
        dialog.getContentPane().add(new EPGInfoPanel(rec.getEpgInfo()));
        dialog.setSize(400,300);
        dialog.setLocation(LazyBones.getInstance().getMainDialog().getLocation());
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void update(Observable arg0, Object recordings) {
        if(arg0 == RecordingManager.getInstance()) {
            updateRecordings();
        }
    }
    
    private void updateRecordings() {
        // create a new model, because clear() made problems.
        // sometimes the list was empty after an update
        model = new DefaultListModel();
        List<Recording> recordings = RecordingManager.getInstance().getRecordings();
        if(recordings != null && recordings.size() > 0) {
            Collections.sort(recordings, new RecordingComparator());
            for (Recording recording : recordings) {
                model.addElement(recording);
            }
        }
        recordingList.setModel(model);
    }
    
    private void createContextMenu() {
        JMenuItem menuDelete = new JMenuItem(Localizer.getLocalization(Localizer.I18N_DELETE));
        menuDelete.addActionListener(this);
        menuDelete.setActionCommand("DELETE");
        menuDelete.setIcon(LazyBones.getInstance().createImageIcon("actions", "edit-delete", 16));
        JMenuItem menuInfo = new JMenuItem(LazyBones.getTranslation("recording_info", "Show information"));
        menuInfo.addActionListener(this);
        menuInfo.setActionCommand("INFO");
        menuInfo.setIcon(LazyBones.getInstance().createImageIcon("actions", "edit-find", 16));
        JMenu menuPlay = new JMenu(LazyBones.getTranslation("playback", "Playback"));
        menuPlay.setIcon(LazyBones.getInstance().createImageIcon("actions", "media-playback-start", 16));
        JMenuItem menuPlayLocal = new JMenuItem(LazyBones.getTranslation("playback.local", "Play"));
        menuPlayLocal.addActionListener(this);
        menuPlayLocal.setActionCommand("PLAY");
        menuPlayLocal.setIcon(LazyBones.getInstance().createImageIcon("actions", "media-playback-start", 16));
        JMenuItem menuPlayOnVdr = new JMenuItem(LazyBones.getTranslation("playback.vdr", "Play on VDR"));
        menuPlayOnVdr.addActionListener(this);
        menuPlayOnVdr.setActionCommand("PLAY_ON_VDR");
        menuPlayOnVdr.setIcon(LazyBones.getInstance().createImageIcon("actions", "media-playback-start", 16));
        menuPlay.add(menuPlayLocal);
        menuPlay.add(menuPlayOnVdr);
        JMenuItem menuSync = new JMenuItem(LazyBones.getTranslation("resync", "Synchronize with VDR"));
        menuSync.addActionListener(this);
        menuSync.setActionCommand("SYNC");
        menuSync.setIcon(LazyBones.getInstance().createImageIcon("actions", "view-refresh", 16));
        popup.add(menuInfo);
        popup.add(menuPlay);
        popup.add(menuDelete);
        popup.add(menuSync);
        
        recordingList.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                mayTriggerPopup(e);
            }
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                mayTriggerPopup(e);
            }
            
            private void mayTriggerPopup(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    selectedRow = recordingList.locationToIndex(e.getPoint());
                    recordingList.setSelectedIndex(selectedRow);
                    popup.setLocation(e.getPoint());
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
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
