package lazybones.gui.components.historycombobox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

public class ComboBoxHistory extends DefaultComboBoxModel implements Iterable<String> {

    private int maxSize = 10;

    private List<HistoryChangedListener> listeners = new ArrayList<HistoryChangedListener>();

    public ComboBoxHistory(int size) {
        maxSize = size;
    }

    /**
     * Adds or moves an element to the top of the history
     */
    @Override
    public void addElement(Object o) {
        String newEntry = (String)o;

        // if history contains this object already, delete it,
        // so that it looks like a move to the top
        for (int i = 0; i < getSize(); i++) {
            String oldEntry = (String) getElementAt(i);
            if(oldEntry.equals(newEntry)) {
                removeElementAt(i);
            }
        }

        // insert element at the top
        insertElementAt(o, 0);

        // remove an element, if the history gets too large
        if(getSize()> maxSize) {
            removeElementAt(getSize()-1);
        }

        // set selected item to the one just added
        setSelectedItem(o);

        fireHistoryChanged();
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            private int position = -1;

            @Override
            public void remove() {
                removeElementAt(position);
            }

            @Override
            public boolean hasNext() {
                if(position < getSize()-1 && getSize()>0) {
                    return true;
                }
                return false;
            }

            @Override
            public String next() {
                position++;
                return getElementAt(position).toString();
            }

        };
    }

    public void setItems(List<String> items) {
        removeAllElements();
        Collections.reverse(items);
        for (String item : items) {
            addElement(item);
        }
        Collections.reverse(items);
    }

    public List<String> asList() {
        List<String> list = new ArrayList<String>(maxSize);
        for (String item : this) {
            list.add(item);
        }
        return list;
    }

    public void addHistoryChangedListener(HistoryChangedListener l) {
        listeners.add(l);
    }

    public void removeHistoryChangedListener(HistoryChangedListener l) {
        listeners.remove(l);
    }

    private void fireHistoryChanged() {
        for (HistoryChangedListener l : listeners) {
            l.historyChanged(asList());
        }
    }
}
