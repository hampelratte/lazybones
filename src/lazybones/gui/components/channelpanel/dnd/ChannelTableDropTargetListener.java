/* $Id: ChannelTableDropTargetListener.java,v 1.1 2007-02-17 14:29:51 hampelratte Exp $
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Iterator;

import javax.swing.JTable;

import lazybones.Logger;
import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ChannelTableDropTargetListener implements DropTargetListener {

    private Logger logger = Logger.getLogger();
    
    private JTable table;
    
    public ChannelTableDropTargetListener(JTable table) {
        this.table = table;
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent e) {
        DataFlavor[] flavors = e.getCurrentDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (ChannelCollection.FLAVOR.equals(flavors[i])) {
                // flavor supported
                e.acceptDrag(e.getDropAction());
                return;
            }
        }
        
        e.rejectDrag();
    }

    public void drop(DropTargetDropEvent e) {
        e.acceptDrop(e.getDropAction());
        Transferable tr = e.getTransferable();
        
        DataFlavor[] flavors = tr.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (ChannelCollection.FLAVOR.equals(flavors[i])) {
                int row = table.rowAtPoint(e.getLocation());
                ChannelCollection list;
                try {
                    list = (ChannelCollection) tr.getTransferData(ChannelCollection.FLAVOR);
                } catch (Exception e1) {
                    logger.log(e1, Logger.OTHER, Logger.WARN);
                    e1.printStackTrace();
                    return;
                } 
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Channel chan = (Channel) iter.next();
                    table.getModel().setValueAt(chan, row++, 1);
                }
            }
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
}