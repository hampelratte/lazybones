/*
 * Copyright (c) Henrik Niehaus
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
package lazybones.gui.timers;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.gui.components.timeroptions.TimerOptionsDialog.Mode;
import lazybones.gui.components.timeroptions.TimerOptionsPanel;
import lazybones.programmanager.ProgramManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerManagerPanel extends JPanel implements ActionListener, ListSelectionListener {

    private static transient Logger logger = LoggerFactory.getLogger(TimerManagerPanel.class);

    private JScrollPane scrollPane = null;
    private final JList timerList = new JList(new TimerListAdapter());
    private JButton buttonNew = null;
    private JButton buttonEdit = null;
    private JButton buttonSync = null;
    private JButton buttonRemove = null;
    private final TimerOptionsPanel top = new TimerOptionsPanel(Mode.VIEW);

    public TimerManagerPanel() {
        initGUI();
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
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        timerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timerList.setCellRenderer(new TimerListCellRenderer());
        timerList.addListSelectionListener(this);
        scrollPane = new JScrollPane(timerList);
        this.add(scrollPane, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 2;
        gbc.insets = new java.awt.Insets(10, 0, 10, 10);
        top.setBorder(BorderFactory.createTitledBorder(LazyBones.getTranslation("details", "Details")));
        top.setPreferredSize(new Dimension(300, 300));
        top.setMinimumSize(new Dimension(300, 300));
        top.setMaximumSize(new Dimension(300, 300));
        this.add(top, gbc);

        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.insets = new java.awt.Insets(0, 10, 0, 10);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        buttonSync = new JButton();
        buttonSync.setText(LazyBones.getTranslation("resync", "Synchronize"));
        buttonSync.setIcon(LazyBones.getInstance().createImageIcon("action", "view-refresh", 16));
        buttonSync.addActionListener(this);
        this.add(buttonSync, gbc);

        gbc.insets = new java.awt.Insets(0, 0, 0, 10);
        gbc.gridx = 1;
        buttonNew = new JButton();
        buttonNew.setText(LazyBones.getTranslation("new_timer", "New Timer"));
        buttonNew.setIcon(LazyBones.getInstance().createImageIcon("action", "document-new", 16));
        buttonNew.addActionListener(this);
        this.add(buttonNew, gbc);

        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        gbc.gridx = 2;
        buttonEdit = new JButton();
        buttonEdit.setText(LazyBones.getTranslation("edit", "Edit Timer"));
        buttonEdit.setIcon(LazyBones.getInstance().createImageIcon("action", "document-edit", 16));
        buttonEdit.addActionListener(this);
        this.add(buttonEdit, gbc);

        gbc.insets = new java.awt.Insets(0, 10, 0, 10);
        gbc.gridx = 3;
        buttonRemove = new JButton();
        buttonRemove.setText(LazyBones.getTranslation("dont_capture", "Delete Timer"));
        buttonRemove.setIcon(LazyBones.getInstance().createImageIcon("action", "edit-delete", 16));
        buttonRemove.addActionListener(this);
        this.add(buttonRemove, gbc);

        timerList.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                mayTriggerPopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mayTriggerPopup(e);
            }

            private void mayTriggerPopup(MouseEvent e) {
                int index = timerList.locationToIndex(e.getPoint());
                LazyBonesTimer timer = (LazyBonesTimer) timerList.getModel().getElementAt(index);
                if (e.isPopupTrigger()) {
                    JPopupMenu popup = ProgramManager.getInstance().getContextMenuForTimer(timer);
                    popup.setLocation(e.getPoint());
                    popup.show(e.getComponent(), e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
                    ProgramManager.getInstance().handleTimerDoubleClick(timer);
                }
            }
        });
        timerList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    LazyBonesTimer timer = (LazyBonesTimer) timerList.getSelectedValue();
                    if (timer != null) {
                        deleteTimer(timer);
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonNew) {
            TimerManager.getInstance().createTimer();
        } else if (e.getSource() == buttonEdit) {
            if (timerList.getSelectedIndex() >= 0) {
                LazyBonesTimer timer = (LazyBonesTimer) timerList.getSelectedValue();
                try {
                    TimerManager.getInstance().editTimer(timer);
                } catch (Exception ex) {
                    logger.error("An error occured", ex);
                }
            }
        } else if (e.getSource() == buttonRemove) {
            if (timerList.getSelectedIndex() >= 0) {
                LazyBonesTimer timer = (LazyBonesTimer) timerList.getSelectedValue();
                deleteTimer(timer);
            }
        } else if (e.getSource() == buttonSync) {
            TimerManager.getInstance().synchronize();
        }
    }

    private void deleteTimer(LazyBonesTimer timer) {
        timerList.setEnabled(false);
        buttonNew.setEnabled(false);
        buttonEdit.setEnabled(false);
        buttonSync.setEnabled(false);
        buttonRemove.setEnabled(false);
        final boolean requestFocus = timerList.hasFocus();
        TimerManager.getInstance().deleteTimer(timer, new Runnable() {
            @Override
            public void run() {
                timerList.setEnabled(true);
                buttonNew.setEnabled(true);
                buttonEdit.setEnabled(true);
                buttonSync.setEnabled(true);
                buttonRemove.setEnabled(true);
                if (requestFocus) {
                    timerList.requestFocus();
                }
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        LazyBonesTimer timer = (LazyBonesTimer) timerList.getSelectedValue();
        top.setTimer(timer);
    }

    private class TimerComparator implements Comparator<LazyBonesTimer> {
        public int compare(LazyBonesTimer t1, LazyBonesTimer t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    }

    private class TimerListAdapter extends AbstractListModel implements Observer {

        private final TimerManager tm = TimerManager.getInstance();

        public TimerListAdapter() {
            tm.addObserver(this);
            Collections.sort(tm.getTimers(), new TimerComparator());
        }

        @Override
        public int getSize() {
            return tm.getTimers() != null ? tm.getTimers().size() : 0;
        }

        @Override
        public Object getElementAt(int index) {
            return tm.getTimers().get(index);
        }

        @Override
        public void update(Observable o, Object arg) {
            Collections.sort(tm.getTimers(), new TimerComparator());
            fireContentsChanged(this, 0, tm.getTimers().size() - 1);
        }

        @Override
        protected void fireContentsChanged(Object source, int start, int end) {
            super.fireContentsChanged(source, start, end);
        }
    }
}