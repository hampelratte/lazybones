/* $Id: RecordingManagerPanel.java,v 1.2 2007-04-09 20:23:44 hampelratte Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import lazybones.LazyBones;
import lazybones.ProgramManager;
import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.VDRConnection;
import lazybones.actions.DeleteRecordingAction;
import lazybones.actions.VDRAction;
import lazybones.gui.utils.RecordingListCellRenderer;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.util.RecordingsParser;

public class RecordingManagerPanel extends JPanel implements ActionListener, Observer {

    private JScrollPane scrollPane = null;
    private DefaultListModel model = new DefaultListModel();
    private JList recordingList = new JList(model);
    private JButton buttonNew = null;
    private JButton buttonEdit = null;
    private JButton buttonRemove = null;
    
    public RecordingManagerPanel() {
        initGUI();
        TimerManager.getInstance().addObserver(this);
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
        this.add(scrollPane, gbc);
        
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.insets = new java.awt.Insets(0,10,10,10);
        gbc.gridx = 0;
        buttonNew = new JButton();
        buttonNew.setText(LazyBones.getTranslation("new_timer","New Timer"));
        buttonNew.addActionListener(this);
        this.add(buttonNew, gbc);
        
        gbc.insets = new java.awt.Insets(0,0,10,0);
        gbc.gridx = 1;
        buttonEdit = new JButton();
        buttonEdit.setText(LazyBones.getTranslation("edit","Edit Timer"));
        buttonEdit.addActionListener(this);
        this.add(buttonEdit, gbc);
        
        gbc.insets = new java.awt.Insets(0,10,10,10);
        gbc.gridx = 2;
        buttonRemove = new JButton();
        buttonRemove.setText(LazyBones.getTranslation("dont_capture","Delete Timer"));
        buttonRemove.addActionListener(this);
        this.add(buttonRemove, gbc);
        
        recordingList.addMouseListener(new MouseListener() {
            public void mousePressed(MouseEvent e) {
                if(e.isPopupTrigger()) {
                    int index = recordingList.locationToIndex(e.getPoint());
                    Timer timer = (Timer) recordingList.getModel().getElementAt(index);
                    
                    JPopupMenu popup = ProgramManager.getInstance().getContextMenuForTimer(timer);
                    popup.setLocation(e.getPoint());
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
        
        getRecordings();
    }
    
    private void getRecordings() {
        model.removeAllElements();

        List<Recording> recordings = null;
        
        Response res = VDRConnection.send(new LSTR());
        if(res != null && res.getCode() == 250) {
            recordings = RecordingsParser.parse(res.getMessage(), true);
        }
        
        if(recordings != null) {
            for (Iterator iter = recordings.iterator(); iter.hasNext();) {
                Recording rec = (Recording) iter.next();
                model.addElement(rec);
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonRemove) {
            if(recordingList.getSelectedIndex() >= 0) {
                Recording recording = (Recording)recordingList.getSelectedValue();
                VDRAction deleteRecording = new DeleteRecordingAction(recording);
                if(deleteRecording.execute()) {
                    getRecordings();
                } else {
                    // TODO
                }
            }
        }
    }

    public void update(Observable arg0, Object arg1) {
        if(arg0 == TimerManager.getInstance()) {
            getRecordings();
        }
    }
}
