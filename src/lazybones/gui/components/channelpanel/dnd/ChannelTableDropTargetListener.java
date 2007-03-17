/* $Id: ChannelTableDropTargetListener.java,v 1.2 2007-03-17 12:59:46 hampelratte Exp $
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

import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;

import lazybones.Logger;
import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ChannelTableDropTargetListener implements DropTargetListener {

    private Logger logger = Logger.getLogger();
    
    private ChannelTable table;
    
    private ChannelList channelList;
    
    public ChannelTableDropTargetListener(ChannelTable table, ChannelList channelList) {
        this.table = table;
        this.channelList = channelList;
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void dragOver(DropTargetDragEvent e) {
    }

    public void drop(DropTargetDropEvent e) {
        Transferable tr = e.getTransferable();
        
        DataFlavor[] flavors = tr.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (ChannelCollection.FLAVOR.equals(flavors[i])) {
                ChannelCollection list;
                try {
                    list = (ChannelCollection) tr.getTransferData(ChannelCollection.FLAVOR);
                    e.acceptDrop(e.getDropAction());
                } catch (Exception e1) {
                    logger.log(e1, Logger.OTHER, Logger.WARN);
                    e1.printStackTrace();
                    e.rejectDrop();
                    return;
                }

                int row = table.rowAtPoint(e.getLocation());
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    Channel chan = (Channel) iter.next();
                    Object o = table.getModel().getValueAt(row, 1);
                    if( o != null ) {
                        int oldRow = table.getRow(chan);
                        if(oldRow >= 0 ) {
                            if(oldRow < row) {
                                moveUpRows(oldRow+1, row);
                            } else {
                                moveDownRows(row, oldRow-1);
                            }
                        } else {
                            DefaultTableModel model = (DefaultTableModel) table.getModel();
                            Object channel = model.getValueAt(table.getRowCount()-1, 1);
                            DefaultListModel listModel = (DefaultListModel) channelList.getModel();
                            if(channel != null) {
                                listModel.addElement(channel);
                            }
                            moveDownRows(row, table.getRowCount()-2);
                        }
                    }
                    table.getModel().setValueAt(chan, row, 1);
                    row++;
                }
            }
        }
        e.dropComplete(true);
    }

    private void moveDownRows(int start, int end) {
        for (int i = end; i >= start; i--) {
            Object o = table.getValueAt(i, 1);
            System.out.println("Moving down " + o + " from " + i + " to " + (i+1));
            table.setValueAt(o, i+1, 1);
        }
    }
    
    private void moveUpRows(int start, int end) {
        for (int i = start; i <= end; i++) {
            Object o = table.getValueAt(i, 1);
            System.out.println("Moving up " + o + " from " + i + " to " + (i-1));
            table.setValueAt(o, i-1, 1);
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
}