/*
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lazybones.LazyBones;
import lazybones.Player;
import lazybones.RecordingManager;
import lazybones.VDRCallback;
import lazybones.actions.DeleteRecordingAction;
import lazybones.actions.VDRAction;
import lazybones.gui.components.RecordingDetailsPanel;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.responses.highlevel.Folder;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ui.Localizer;

public class RecordingManagerPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static transient Logger logger = LoggerFactory.getLogger(RecordingManagerPanel.class);

    private JScrollPane scrollPane = null;
    private final RecordingTreeModel recordingTreeModel = new RecordingTreeModel();
    private final JTree recordingTree = new JTree();

    private final RecordingDetailsPanel recordingDetailsPanel = new RecordingDetailsPanel();
    private JButton buttonSync = null;
    private JButton buttonRemove = null;

    private final JPopupMenu popup = new JPopupMenu();

    public RecordingManagerPanel() {
        initGUI();
    }

    /**
     * This method initializes the GUI
     * 
     */
    private void initGUI() {
        createContextMenu();

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        recordingTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // initialize out special tree renderer
        recordingTree.setRowHeight(25);
        boolean renderBackground = UIManager.getDefaults().getBoolean("Tree.rendererFillBackground");
        UIManager.getDefaults().put("Tree.rendererFillBackground", false);
        recordingTree.setCellRenderer(new RecordingTreeRenderer());
        UIManager.getDefaults().put("Tree.rendererFillBackground", renderBackground);

        recordingTree.setModel(recordingTreeModel);
        scrollPane = new JScrollPane(recordingTree);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        this.add(scrollPane, gbc);
        ToolTipManager.sharedInstance().registerComponent(recordingTree);

        // gbc.fill = java.awt.GridBagConstraints.VERTICAL;
        gbc.gridx = 1;
        gbc.weightx = .1;
        gbc.insets = new java.awt.Insets(10, 0, 10, 10);
        recordingTree.addTreeSelectionListener(recordingDetailsPanel);
        recordingDetailsPanel.setBorder(BorderFactory.createTitledBorder(LazyBones.getTranslation("details", "Details")));
        recordingDetailsPanel.setPreferredSize(new Dimension(300, 300));
        recordingDetailsPanel.setMinimumSize(new Dimension(300, 300));
        recordingDetailsPanel.setMaximumSize(new Dimension(300, 300));
        this.add(recordingDetailsPanel, gbc);

        gbc.insets = new java.awt.Insets(0, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0;
        buttonSync = new JButton(LazyBones.getTranslation("resync", "Synchronize"));
        buttonSync.setIcon(LazyBones.getInstance().createImageIcon("action", "view-refresh", 16));
        buttonSync.addActionListener(this);
        buttonSync.setActionCommand("SYNC");
        this.add(buttonSync, gbc);

        gbc.insets = new java.awt.Insets(0, 0, 10, 10);
        gbc.gridx = 1;
        buttonRemove = new JButton(LazyBones.getTranslation("delete_recording", "Delete Recording"));
        buttonRemove.setIcon(LazyBones.getInstance().createImageIcon("action", "edit-delete", 16));
        buttonRemove.addActionListener(this);
        buttonRemove.setActionCommand("DELETE");
        this.add(buttonRemove, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Recording rec = null;
        boolean itemSelected = false;
        TreePath selection = recordingTree.getSelectionPath();
        if (selection != null) {
            itemSelected = true;
            TreeNode treeNode = (TreeNode) selection.getLastPathComponent();
            if (treeNode instanceof Recording) {
                rec = (Recording) treeNode;
            }
        }

        if ("DELETE".equals(e.getActionCommand()) && itemSelected) {
            deleteRecording(rec, selection);
        } else if ("INFO".equals(e.getActionCommand()) && itemSelected) {
            createRecordingDetailsDialog(rec);
        } else if ("SYNC".equals(e.getActionCommand())) {
            RecordingManager.getInstance().synchronize();
        } else if ("PLAY".equals(e.getActionCommand()) && itemSelected) {
            Player.play(rec);
        } else if ("PLAY_ON_VDR".equals(e.getActionCommand()) && itemSelected) {
            RecordingManager.getInstance().playOnVdr(rec);
        }
    }

    private void deleteRecording(final Recording rec, final TreePath tp) {
        recordingTree.setEnabled(false);
        buttonRemove.setEnabled(false);
        buttonSync.setEnabled(false);
        final boolean hasFocus = recordingTree.hasFocus();
        VDRCallback callback = new VDRCallback() {
            @Override
            public void receiveResponse(VDRAction cmd, Response response) {
                if (!cmd.isSuccess()) {
                    logger.error(cmd.getResponse().getMessage());
                    recordingTree.setEnabled(true);
                    buttonRemove.setEnabled(true);
                    buttonSync.setEnabled(true);
                    if (hasFocus) {
                        recordingTree.requestFocus();
                    }
                } else {
                    // RecordingManager.getInstance().getRecordings().remove(rec);
                    // recordingTreeModel.remove(tp);

                    RecordingManager.getInstance().synchronize(new Runnable() {
                        @Override
                        public void run() {
                            recordingTree.setEnabled(true);
                            buttonRemove.setEnabled(true);
                            buttonSync.setEnabled(true);
                            if (hasFocus) {
                                recordingTree.requestFocus();
                            }
                        }
                    });
                }
            }
        };
        DeleteRecordingAction dra = new DeleteRecordingAction(rec, callback);
        dra.enqueue();
    }

    private void createRecordingDetailsDialog(Recording rec) {
        final JDialog dialog = new JDialog();
        RecordingManager.getInstance().loadInfo(rec);
        dialog.getContentPane().add(new RecordingDetailsPanel(rec));
        dialog.setSize(400, 300);
        dialog.setLocation(LazyBones.getInstance().getMainDialog().getLocation());
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

        recordingTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mayTriggerPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mayTriggerPopup(e);
            }

            private void mayTriggerPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point p = e.getPoint();
                    int selectedRow = recordingTree.getRowForLocation(p.x, p.y);
                    recordingTree.setSelectionRow(selectedRow);
                    TreeNode treeNode = (TreeNode) recordingTree.getSelectionPath().getLastPathComponent();
                    if (treeNode instanceof Recording) {
                        popup.setLocation(e.getPoint());
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        recordingTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getSource() == recordingTree) {
                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        TreePath selected = recordingTree.getSelectionPath();
                        if (selected != null) {
                            TreeNode treeNode = (TreeNode) selected.getPathComponent(selected.getPathCount() - 1);
                            if (treeNode instanceof Folder) {
                                logger.warn("Deletion of folders is not supported.");
                            } else {
                                logger.info("Deleting recording [{}]", treeNode.getDisplayTitle());
                                deleteRecording((Recording) treeNode, selected);
                            }
                        }
                    }
                }
            }
        });
    }
}
