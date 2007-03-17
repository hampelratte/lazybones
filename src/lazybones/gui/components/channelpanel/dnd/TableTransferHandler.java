/* $Id: TableTransferHandler.java,v 1.1 2007-03-17 15:08:31 hampelratte Exp $
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

import java.util.HashSet;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.hampelratte.svdrp.responses.highlevel.Channel;

public class TableTransferHandler extends ChannelSetTransferHandler {
    private int[] rows = null;
    private int addIndex = -1; //Location where items were added
    private int addCount = 0;  //Number of items added.
    
    private JList channelList;
    
    public TableTransferHandler(JList channelList) {
        this.channelList = channelList;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected ChannelSet exportChannels(JComponent c) {
        JTable table = (JTable)c;
        rows = table.getSelectedRows();
        ChannelSet channelSet = new ChannelSet();
        for (int i = 0; i < rows.length; i++) {
            channelSet.add(table.getValueAt(rows[i], 1));
        }
        return channelSet;
    }

    @Override
    protected void importChannels(JComponent c, ChannelSet set) {
        JTable target = (JTable)c;
        DefaultTableModel model = (DefaultTableModel)target.getModel();
        int index = target.getSelectedRow();

        //Prevent the user from dropping data back on itself.
        //For example, if the user is moving rows #4,#5,#6 and #7 and
        //attempts to insert the rows after row #5, this would
        //be problematic when removing the original rows.
        //So this is not allowed.
        if (rows != null && index >= rows[0] - 1 &&
              index <= rows[rows.length - 1]) {
            rows = null;
            return;
        }

        int max = model.getRowCount();
        if (index < 0) {
            index = max;
        } 
        
        addIndex = index;
        addCount = set.size();
        HashSet removedChannels = new HashSet();
        for (Iterator iter = set.iterator(); iter.hasNext();) {
            Channel chan = (Channel) iter.next();
            Object o = model.getValueAt(index, 1);
            if(o != null) {
                removedChannels.add(o);
            }
            model.setValueAt(chan, index, 1);
            index++;
        }
        
        DefaultListModel listModel = (DefaultListModel) channelList.getModel();
        for (Iterator iter = removedChannels.iterator(); iter.hasNext();) {
            Channel chan = (Channel) iter.next();
            listModel.addElement(chan);
        }
    }
    
    @Override
    protected void cleanup(JComponent c, boolean remove) {
        JTable source = (JTable)c;
        if (remove && rows != null) {
            DefaultTableModel model =
                 (DefaultTableModel)source.getModel();

            //If we are moving items around in the same table, we
            //need to adjust the rows accordingly, since those
            //after the insertion point have moved.
            /*if (addCount > 0) {
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] > addIndex) {
                        rows[i] += addCount;
                    }
                }
            }*/
            
            for (int i = rows.length - 1; i >= 0; i--) {
                model.setValueAt(null, rows[i], 1);
            }
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }
}
