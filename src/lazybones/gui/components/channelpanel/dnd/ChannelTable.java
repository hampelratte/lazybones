/* $Id: ChannelTable.java,v 1.2 2007-03-17 12:59:46 hampelratte Exp $
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

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import javax.swing.JTable;
import javax.swing.SwingUtilities;

import lazybones.Logger;
import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ChannelTable extends JTable {
    
    private Logger logger = Logger.getLogger();
    
    private ChannelCollection list;
    private DragSource dragSource;
    private DragGestureListener dragGestureListener;
    private DragSourceListener dragSourceListener;
    private int dragAction = DnDConstants.ACTION_MOVE;
    
    private int[] indices;
    
    public ChannelTable() {
        this.dragSource = DragSource.getDefaultDragSource();
        this.dragGestureListener = new ChannelTableDragGestureListener();
        this.dragSourceListener = new ChannelTableDragSourceListener(this);
        
        dragSource.createDefaultDragGestureRecognizer(this, dragAction, dragGestureListener);
    }
    
    /**
     * 
     * @param chan
     * @return the row index of the given Channel, or -1 if the channel is not found
     */
    public int getRow(Channel chan) {
        for (int i = 0; i < getRowCount(); i++) {
            Channel c = (Channel) getModel().getValueAt(i, 1);
            if(c!= null && c.equals(chan)) {
                return i;
            }
        }
        return -1;
    }
    
    class ChannelTableDragGestureListener implements DragGestureListener {
        @SuppressWarnings("unchecked")
        public void dragGestureRecognized(DragGestureEvent dge) {
            // create list of dragged channels
            list = new ChannelCollection();
            list.setEventSource("ChannelTable");
            indices = getSelectedRows();
            for (int i = 0; i < indices.length; i++) {
                Object o = getModel().getValueAt(indices[i], 1);
                if(o != null && o instanceof Channel) {
                    list.add((Channel)o);
                }
            }
            
            try {
                // initial cursor, transferrable, dsource listener      
                dge.startDrag(DragSource.DefaultMoveDrop, list, dragSourceListener);
            } catch (InvalidDnDOperationException idoe) {
                logger.log(idoe, Logger.OTHER, Logger.WARN);
            }
        }
    }
    
    class ChannelTableDragSourceListener implements DragSourceListener {
        private ChannelTable table;
        
        public ChannelTableDragSourceListener(ChannelTable table) {
            this.table = table;
        }
        
        public void dragDropEnd(DragSourceDropEvent e) {
            
            System.out.println(e.getLocation());
            Transferable tr = e.getDragSourceContext().getTransferable();
            try {
                ChannelCollection cc = (ChannelCollection) tr.getTransferData(ChannelCollection.FLAVOR);
                System.out.println("Von hier " + "ChannelTable".equals(cc.getEventSource()));
                Point point = (Point) e.getLocation().clone();
                SwingUtilities.convertPointFromScreen(point, table);
                System.out.println("Hier drin " + contains(point));
                if(e.getDropSuccess() && !("ChannelTable".equals(cc.getEventSource()) && contains(point)) ) {
                    for (int i = 0; i < indices.length; i++) {
                        getModel().setValueAt(null, indices[i], 1);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
        }

        public void dragEnter(DragSourceDragEvent dsde) {}

        public void dragExit(DragSourceEvent dse) {}

        public void dragOver(DragSourceDragEvent dsde) {}

        public void dropActionChanged(DragSourceDragEvent dsde) {}
    }
}
