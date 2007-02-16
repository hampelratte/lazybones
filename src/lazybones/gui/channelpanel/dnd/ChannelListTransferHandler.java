/* $Id: ChannelListTransferHandler.java,v 1.1 2007-02-16 22:20:25 hampelratte Exp $
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
package lazybones.gui.channelpanel.dnd;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ChannelListTransferHandler extends ChannelTransferHandler {

    @Override
    protected void cleanup(JComponent c, boolean remove) {
        if(remove) {
            JList list = (JList) c;
            DefaultListModel model = (DefaultListModel) list.getModel();
            int index = list.getSelectedIndex();
            model.remove(index);
        }
    }

    @Override
    protected Channel exportChannel(JComponent c) {
        JList list = (JList) c;
        Channel chan = (Channel) list.getSelectedValue();
        return chan;
    }

    @Override
    protected void importChannel(JComponent c, Channel chan) {
        JList list = (JList) c;
        int row = list.getSelectedIndex();
        DefaultListModel model = (DefaultListModel) list.getModel();
        if(row >= 0) {
            model.add(row, chan);
        } else {
            model.addElement(chan);
        }
        list.repaint();
    }
}
