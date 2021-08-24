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
package lazybones.gui.settings.channelpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.hampelratte.svdrp.responses.highlevel.Channel;

public class FilterListModel implements ListModel<Channel> {

    private String filter = "";

    private List<Channel> data = Collections.synchronizedList(new ArrayList<>());
    private List<Channel> filteredData = Collections.synchronizedList(new ArrayList<>());
    private List<ListDataListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private int minChannel;
    private int maxChannel;

    public void setFilter(String filter) {
        this.filter = filter;
        filter();
    }

    public void setMinChannel(int minChannel) {
        this.minChannel = minChannel;
        filter();
    }

    public void setMaxChannel(int maxChannel) {
        this.maxChannel = maxChannel;
        filter();
    }

    private void filter() {
        filteredData.clear();
        for (Channel chan : data) {
            if (chan.getChannelNumber() >= minChannel && (chan.getChannelNumber() <= maxChannel || maxChannel == 0)) {
                if (filterMatches(chan)) { // NOSONAR
                    filteredData.add(chan);
                }
            }
        }

        int lastIndex = filteredData.size() - 1;
        for (ListDataListener l : listeners) {
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, lastIndex));
        }
    }

    public void setData(List<Channel> channels) {
        data = Collections.synchronizedList(channels);
        filter();
    }

    @Override
    public int getSize() {
        return filteredData.size();
    }

    @Override
    public Channel getElementAt(int index) {
        return filteredData.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void clear() {
        data.clear();
        filteredData.clear();
        for (ListDataListener l : listeners) {
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
        }
    }

    public void addElement(Channel chan) {
        boolean inserted = false;
        for (int i = 0; i < data.size(); i++) {
            Channel listChannel = data.get(i);
            if (listChannel.getChannelNumber() > chan.getChannelNumber()) {
                data.add(i, chan);
                inserted = true;
                break;
            }
        }

        if (!inserted) {
            data.add(chan);
        }

        filter();
    }

    private boolean filterMatches(Channel chan) {
        return chan.getName().toLowerCase().contains(filter.toLowerCase()) || Integer.toString(chan.getChannelNumber()).contains(filter);
    }

    public void removeElement(Channel channel) {
        data.remove(channel);
        filter();
    }
}
