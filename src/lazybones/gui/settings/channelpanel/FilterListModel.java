package lazybones.gui.settings.channelpanel;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.hampelratte.svdrp.responses.highlevel.Channel;

public class FilterListModel implements ListModel<Channel> {

    private String filter = "";

    private List<Channel> data = new Vector<>();
    private List<Channel> filteredData = new Vector<>();
    private List<ListDataListener> listeners = new Vector<>();
    private int minChannel, maxChannel;

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
                if (filterMatches(chan)) {
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
        data.add(chan);

        if (filter == null || filter.isEmpty()) {
            addToFilteredList(chan);
        } else {
            if (filterMatches(chan)) {
                addToFilteredList(chan);
            }
        }
    }

    private void addToFilteredList(Channel chan) {
        filteredData.add(chan);
        for (ListDataListener l : listeners) {
            int lastIndex = filteredData.size() - 1;
            l.contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, lastIndex, lastIndex));
        }
    }

    private boolean filterMatches(Channel chan) {
        return chan.getName().toLowerCase().contains(filter.toLowerCase());
    }
}
