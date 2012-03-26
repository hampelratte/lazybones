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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import lazybones.LazyBones;

import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;

public class RecordingTreeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        String title = value.toString();
        if (value instanceof TreeNode) {
            title = ((TreeNode) value).getDisplayTitle();
            if (value instanceof Recording) {
                Recording recording = (Recording) value;
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
                title = df.format(recording.getStartTime().getTime()) + " - " + title;
            }
        }
        Component renderer = super.getTreeCellRendererComponent(tree, title, sel, expanded, leaf, row, hasFocus);

        if (value instanceof Recording) {
            Recording recording = (Recording) value;
            ((JLabel) renderer).setIcon(null);
            renderer = new Decorator(recording, renderer);
        }

        return renderer;
    }

    private class Decorator extends JPanel {

        private static final long serialVersionUID = 1L;
        private static final int HGAP = 2;
        private final Recording recording;
        private final Component renderer;

        private JLabel cutIcon;
        private JLabel newIcon;

        public Decorator(Recording recording, Component renderer) {
            this.recording = recording;
            this.renderer = renderer;
            initGui();
        }

        private void initGui() {
            setLayout(new GridBagLayout());
            setOpaque(false);
            setToolTipText(recording.getDisplayTitle());

            newIcon = new JLabel();
            newIcon.setPreferredSize(new Dimension(16, 16));
            add(newIcon);
            if (recording.isNew()) {
                newIcon.setIcon(LazyBones.getInstance().getIcon("lazybones/new.png"));
            }

            cutIcon = new JLabel();
            cutIcon.setPreferredSize(new Dimension(16, 16));
            add(cutIcon);
            if (recording.isCut()) {
                cutIcon.setIcon(LazyBones.getInstance().getIcon("lazybones/edit-cut.png"));
            }

            add(renderer);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int x = event.getPoint().x;
            if (newIcon.getIcon() != null && x > HGAP && x < HGAP + 16) {
                return LazyBones.getTranslation("new_recording", "New recording");
            } else if (cutIcon.getIcon() != null && x > HGAP * 2 + 16 && x < HGAP * 2 + 32) {
                return LazyBones.getTranslation("cut_recording", "Cut recording");
            } else {
                return super.getToolTipText(event);
            }
        }
    }
}
