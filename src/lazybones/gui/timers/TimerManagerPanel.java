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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lazybones.ChannelManager.ChannelNotFoundException;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import lazybones.TimerManager;
import lazybones.VDRCallback;
import lazybones.actions.ModifyTimerAction;
import lazybones.actions.VDRAction;
import lazybones.gui.components.timeroptions.TimerOptionsDialog.Mode;
import lazybones.gui.components.timeroptions.TimerOptionsEditor;
import lazybones.gui.components.timeroptions.TimerOptionsView;
import lazybones.programmanager.ProgramManager;

import org.hampelratte.svdrp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerManagerPanel extends JPanel implements ActionListener, ListSelectionListener, ListDataListener {

    private static transient Logger logger = LoggerFactory.getLogger(TimerManagerPanel.class);

    private JScrollPane scrollPane = null;
    private final JList<LazyBonesTimer> timerList = new JList<LazyBonesTimer>(new TimerListAdapter());
    private JToolBar buttonBar = null;
    private JButton buttonNew = null;
    private JButton buttonEdit = null;
    private JButton buttonSync = null;
    private JButton buttonRemove = null;
    private JButton buttonSave = null;
    private JButton buttonCancel = null;

    private final CardLayout timerOptionsLayout = new CardLayout();
    private final JPanel timerOptionsPanel = new JPanel(timerOptionsLayout);
    private final TimerOptionsEditor timerOptionsEditor = new TimerOptionsEditor(Mode.UPDATE);
    private final TimerOptionsView timerOptionsView = new TimerOptionsView();

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

        // add toolbar
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new java.awt.Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        buttonBar = createButtonBar();
        this.add(buttonBar, gbc);

        // add timer list
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new java.awt.Insets(0, 10, 10, 10);
        gbc.gridx = 0;
        timerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timerList.setCellRenderer(new TimerListCellRenderer());
        timerList.addListSelectionListener(this);
        timerList.getModel().addListDataListener(this);
        scrollPane = new JScrollPane(timerList);
        this.add(scrollPane, gbc);

        // add cardlayout for timer details view and timer editor
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.gridheight = 2;
        gbc.insets = new java.awt.Insets(10, 0, 5, 10);
        this.add(timerOptionsPanel, gbc);
        timerOptionsView.setEnabled(false);
        timerOptionsPanel.add(timerOptionsView, "VIEW");
        timerOptionsPanel.setPreferredSize(new Dimension(200, 800));
        timerOptionsPanel.add(timerOptionsEditor, "EDITOR");

        timerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mayTriggerPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mayTriggerPopup(e);
            }

            private void mayTriggerPopup(MouseEvent e) {
                int index = timerList.locationToIndex(e.getPoint());
                LazyBonesTimer timer = timerList.getModel().getElementAt(index);
                if (e.isPopupTrigger()) {
                    JPopupMenu popup = ProgramManager.getInstance().getContextMenuForTimer(timer);
                    popup.setLocation(e.getPoint());
                    popup.show(e.getComponent(), e.getX(), e.getY());
                } else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {
                    ProgramManager.getInstance().handleTimerDoubleClick(timer, e);
                }
            }
        });

        timerList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    LazyBonesTimer timer = timerList.getSelectedValue();
                    if (timer != null) {
                        deleteTimer(timer);
                    }
                }
            }
        });
    }

    private JToolBar createButtonBar() {
        JToolBar buttonBar = new JToolBar();
        buttonBar.setBorder(BorderFactory.createEmptyBorder());
        buttonBar.setRollover(true);
        buttonBar.setFloatable(false);

        buttonSync = new JButton();
        buttonSync.setToolTipText(LazyBones.getTranslation("resync", "Synchronize"));
        buttonSync.setIcon(LazyBones.getInstance().createImageIcon("action", "view-refresh", 22));
        buttonSync.addActionListener(this);
        buttonBar.add(buttonSync);

        buttonNew = new JButton();
        buttonNew.setToolTipText(LazyBones.getTranslation("new_timer", "New Timer"));
        buttonNew.setIcon(LazyBones.getInstance().createImageIcon("action", "document-new", 22));
        buttonNew.addActionListener(this);
        buttonBar.add(buttonNew);

        buttonBar.addSeparator();

        buttonSave = new JButton();
        buttonSave.setToolTipText(LazyBones.getTranslation("save", "Update Timer"));
        buttonSave.setIcon(LazyBones.getInstance().createImageIcon("action", "document-save", 22));
        buttonSave.addActionListener(this);
        buttonSave.setVisible(false);
        buttonBar.add(buttonSave);

        buttonCancel = new JButton();
        buttonCancel.setToolTipText(LazyBones.getTranslation("cancel", "Cancel editing"));
        buttonCancel.setIcon(LazyBones.getInstance().createImageIcon("action", "process-stop", 22));
        buttonCancel.addActionListener(this);
        buttonCancel.setVisible(false);
        buttonBar.add(buttonCancel);

        buttonEdit = new JButton();
        buttonEdit.setToolTipText(LazyBones.getTranslation("edit", "Edit Timer"));
        buttonEdit.setIcon(LazyBones.getInstance().createImageIcon("action", "document-edit", 22));
        buttonEdit.addActionListener(this);
        buttonEdit.setEnabled(false);
        buttonBar.add(buttonEdit);

        buttonRemove = new JButton();
        buttonRemove.setToolTipText(LazyBones.getTranslation("dont_capture", "Delete Timer"));
        buttonRemove.setIcon(LazyBones.getInstance().createImageIcon("action", "edit-delete", 22));
        buttonRemove.addActionListener(this);
        buttonRemove.setEnabled(false);
        buttonBar.add(buttonRemove);
        return buttonBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonNew) {
            try {
                cancelEditing();
                TimerManager.getInstance().createTimerFromScratch();
            } catch (ChannelNotFoundException ex) {
                logger.error("An error occured", ex);
            }
        } else if (e.getSource() == buttonEdit) {
            if (timerList.getSelectedIndex() >= 0) {
                LazyBonesTimer timer = timerList.getSelectedValue();
                timerOptionsEditor.setTimer(timer);
                startEditing();
            }
        } else if (e.getSource() == buttonRemove) {
            if (timerList.getSelectedIndex() >= 0) {
                LazyBonesTimer timer = timerList.getSelectedValue();
                deleteTimer(timer);
                cancelEditing();
            }
        } else if (e.getSource() == buttonSync) {
            cancelEditing();
            TimerManager.getInstance().synchronize();
        } else if (e.getSource() == buttonCancel) {
            cancelEditing();
            TimerManager.getInstance().synchronize();
        } else if (e.getSource() == buttonSave) {
            LazyBonesTimer oldTimer = timerOptionsEditor.getOldTimer();
            LazyBonesTimer changedTimer = timerOptionsEditor.getTimer();
            ModifyTimerAction mta = new ModifyTimerAction(changedTimer, oldTimer);
            mta.setCallback(new VDRCallback<VDRAction>() {
                @Override
                public void receiveResponse(VDRAction cmd, Response response) {
                    cancelEditing();
                    TimerManager.getInstance().synchronize();
                    if (!cmd.isSuccess()) {
                        String mesg = LazyBones.getTranslation("couldnt_change", "Couldn\'t change timer:") + " " + cmd.getResponse().getMessage();
                        logger.error(mesg);
                    }
                }
            });
            mta.enqueue();
        }
    }

    private void startEditing() {
        timerOptionsLayout.show(timerOptionsPanel, "EDITOR");
        buttonSave.setVisible(true);
        buttonCancel.setVisible(true);
        buttonEdit.setVisible(false);
        buttonSync.setEnabled(false);
        buttonNew.setEnabled(false);
        buttonRemove.setEnabled(false);
    }

    private void cancelEditing() {
        buttonSave.setVisible(false);
        buttonCancel.setVisible(false);
        buttonEdit.setVisible(true);
        buttonSync.setEnabled(true);
        buttonNew.setEnabled(true);
        buttonRemove.setEnabled(true);
        timerOptionsLayout.show(timerOptionsPanel, "VIEW");
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
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        timerList.setEnabled(true);
                        buttonSync.setEnabled(true);
                        buttonNew.setEnabled(true);
                        if (requestFocus) {
                            timerList.requestFocus();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateGuiState();
    }

    private synchronized void updateGuiState() {
        try {
            cancelEditing();
            int selectedIndex = timerList.getSelectedIndex();

            if (selectedIndex >= 0 && timerList.getModel().getSize() - 1 >= selectedIndex) {
                LazyBonesTimer timer = timerList.getModel().getElementAt(selectedIndex);
                timerOptionsView.setTimer(timer);
                buttonEdit.setEnabled(true);
                buttonRemove.setEnabled(true);
            } else {
                timerOptionsView.reset();
                buttonEdit.setEnabled(false);
                buttonRemove.setEnabled(false);
            }
        } catch (RuntimeException t) {
            logger.error(t.getMessage(), t);
            throw t;
        }
    }

    private class TimerComparator implements Comparator<LazyBonesTimer> {
        @Override
        public int compare(LazyBonesTimer t1, LazyBonesTimer t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    }

    private class TimerListAdapter extends AbstractListModel<LazyBonesTimer> implements Observer {

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
        public LazyBonesTimer getElementAt(int index) {
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

    @Override
    public void intervalAdded(ListDataEvent e) {
        updateGuiState();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        updateGuiState();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        updateGuiState();
    }
}
