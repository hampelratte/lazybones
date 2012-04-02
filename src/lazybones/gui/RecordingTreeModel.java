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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import lazybones.RecordingManager;

import org.hampelratte.svdrp.responses.highlevel.Folder;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;
import org.hampelratte.svdrp.util.AlphabeticalRecordingComparator;
import org.hampelratte.svdrp.util.RecordingTreeBuilder;

public class RecordingTreeModel implements TreeModel, Observer {

    private Folder root = new Folder("");

    private final List<TreeModelListener> tmls = new ArrayList<TreeModelListener>();

    private final RecordingManager rm = RecordingManager.getInstance();

    public RecordingTreeModel() {
        rm.addObserver(this);
        Collections.sort(rm.getRecordings(), new AlphabeticalRecordingComparator());
    }

    public void setRecordings(Folder root) {
        this.root = root;
        for (TreeModelListener tml : tmls) {
            tml.treeStructureChanged(new TreeModelEvent(this, new Object[] {root}));
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent instanceof Folder) {
            Folder folder = (Folder) parent;
            return folder.getChildren().get(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent instanceof Folder) {
            Folder folder = (Folder) parent;
            return folder.getChildren().size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Recording;
    }

    @Override
    public void valueForPathChanged(TreePath tp, Object newValue) {
        int index = getIndexOfChild(tp.getParentPath().getLastPathComponent(), newValue);
        TreeModelEvent tme = new TreeModelEvent(this, tp.getParentPath(), new int[] { index }, new Object[] { newValue });
        for (TreeModelListener tml : tmls) {
            tml.treeNodesChanged(tme);
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Folder folder = (Folder) parent;
        for (int i = 0; i < folder.getChildren().size(); i++) {
            if (folder.getChildren().get(i) == child) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        tmls.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        tmls.remove(l);
    }

    public void remove(TreePath tp) {
        Folder current = root;
        for (int i = 1; i < tp.getPathCount(); i++) {
            Object o = tp.getPathComponent(i);
            for (TreeNode node : current.getChildren()) {
                if (i == tp.getPathCount() - 1) {
                    int index = current.getChildren().indexOf(o);
                    if (index >= 0) {
                        current.getChildren().remove(index);
                        TreeModelEvent tme = new TreeModelEvent(this, tp.getParentPath(), new int[] { index }, new Object[] { o });
                        for (TreeModelListener tml : tmls) {
                            tml.treeNodesRemoved(tme);
                        }

                        return;
                    } else {
                        System.err.println("Element wurde nicht aus dem Baum gelöscht");
                    }
                }

                if (node == o) {
                    current = (Folder) node;
                    break;
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List) {
            @SuppressWarnings("unchecked")
            List<Recording> recordings = (List<Recording>) arg;
            Folder tree = RecordingTreeBuilder.buildTree(recordings);
            tree.sort(new AlphabeticalRecordingComparator());
            setRecordings(tree);
        }
    }
}
