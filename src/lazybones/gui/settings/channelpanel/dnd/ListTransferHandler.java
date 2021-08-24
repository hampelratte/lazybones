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
package lazybones.gui.settings.channelpanel.dnd;

import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JList;

import lazybones.gui.settings.channelpanel.FilterListModel;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListTransferHandler extends ChannelSetTransferHandler {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(ListTransferHandler.class);

    private Set<Channel> overwrittenChannels;

    @Override
    protected ChannelSet<Channel> exportChannels(JComponent c) {
        @SuppressWarnings("unchecked")
        JList<Channel> list = (JList<Channel>) c;
        List<Channel> channels = list.getSelectedValuesList();
        ChannelSet<Channel> channelSet = new ChannelSet<>();
        channelSet.addAll(channels);

        return channelSet;
    }

    @Override
    protected void importChannels(JComponent c, ChannelSet<Channel> set) {
        @SuppressWarnings("unchecked")
        JList<Channel> target = (JList<Channel>) c;
        FilterListModel listModel = (FilterListModel) target.getModel();
        for (Channel chan : set) {
            listModel.addElement(chan);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void cleanup(JComponent c, Transferable data, boolean remove) {
        LOGGER.debug("Cleanup in list");
        JList<Channel> list = (JList<Channel>) c;
        FilterListModel listModel = (FilterListModel) list.getModel();
        try {
            ChannelSet<Channel> set = (ChannelSet<Channel>) data.getTransferData(ChannelSet.FLAVOR);
            for (Channel channel : set) {
                listModel.removeElement(channel);
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't delete channels from drag source (list)", e);
        }

        for (Iterator<Channel> iterator = overwrittenChannels.iterator(); iterator.hasNext();) {
            Channel chan = iterator.next();
            listModel.addElement(chan);
            iterator.remove();
        }
    }

    public void setOverwrittenChannels(Set<Channel> overwrittenChannels) {
        this.overwrittenChannels = overwrittenChannels;
    }

}
