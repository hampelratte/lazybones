/* $Id: ChannelList.java,v 1.1 2007-02-17 14:29:51 hampelratte Exp $
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
package lazybones.gui.components.channelpanel.dnd;

import java.awt.dnd.*;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ChannelList extends JList {
    private ChannelCollection list;
    private DragSource dragSource;
    private DragGestureListener dragGestureListener;
    private DragSourceListener dragSourceListener;
    private int dragAction = DnDConstants.ACTION_MOVE;
    
    private int[] indices;
    
    public ChannelList() {
        this.dragSource = DragSource.getDefaultDragSource();
        this.dragGestureListener = new ChannelListDragGestureListener();
        this.dragSourceListener = new ChannelListDragSourceListener();
        
        dragSource.createDefaultDragGestureRecognizer(this, dragAction, dragGestureListener);
    }
    
    class ChannelListDragGestureListener implements DragGestureListener {
        @SuppressWarnings("unchecked")
        public void dragGestureRecognized(DragGestureEvent dge) {
            // create list of dragged channels
            list = new ChannelCollection();
            indices = getSelectedIndices();
            for (int i = 0; i < indices.length; i++) {
                list.add(getModel().getElementAt(indices[i]));
            }
            
            try {
                // initial cursor, transferrable, dsource listener
                dge.startDrag(DragSource.DefaultMoveDrop, list, dragSourceListener);
            } catch (InvalidDnDOperationException idoe) {
                System.err.println(idoe);
            }
        }
    }
    
    class ChannelListDragSourceListener implements DragSourceListener {

        public void dragDropEnd(DragSourceDropEvent e) {
            DefaultListModel model = (DefaultListModel) getModel();
            for (int i = indices.length-1; i >= 0; i--) {
                model.removeElementAt(indices[i]);
            }
        }

        public void dragEnter(DragSourceDragEvent dsde) {}

        public void dragExit(DragSourceEvent dse) {}

        public void dragOver(DragSourceDragEvent dsde) {}

        public void dropActionChanged(DragSourceDragEvent dsde) {}
    }
}
