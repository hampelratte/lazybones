/* $Id: TitleMapping.java,v 1.3 2011-01-18 13:13:52 hampelratte Exp $
 * 
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
package lazybones.gui.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TitleMapping implements Serializable, TableModel {

    private ArrayList<String> vdrTitles = new ArrayList<String>();

    private ArrayList<String> tvbTitles = new ArrayList<String>();

    public void put(String tvbTitle, String vdrTitle) {
        if(vdrTitles.contains(vdrTitle)) {
            int index = vdrTitles.indexOf(vdrTitle);
            tvbTitles.set(index, tvbTitle);
        } else {
            vdrTitles.add(vdrTitle);
            tvbTitles.add(tvbTitle);
        }
        
        fireTableChanged();
    }
    
    public void removeRow(int index) {
        vdrTitles.remove(index);
        tvbTitles.remove(index);
        fireTableChanged();
    }
    
    private void fireTableChanged() {
        for (TableModelListener listener : listeners) {
            listener.tableChanged(new TableModelEvent(this));
        }
    }

    public String getTvbTitle(String vdrTitle) {
        int count = 0;
        for (Iterator<String> iter = vdrTitles.iterator(); iter.hasNext(); count++) {
            String element = iter.next();
            if (element.equals(vdrTitle)) {
                return tvbTitles.get(count);
            }
        }
        return null;
    }
    
    public String getVdrTitle(String tvbTitle) {
        int count = 0;
        for (Iterator<String> iter = tvbTitles.iterator(); iter.hasNext(); count++) {
            String element = iter.next();
            if (element.equals(tvbTitle)) {
                return vdrTitles.get(count);
            }
        }
        return null;
    }

    private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }
    
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "TV-Browser";
        } else {
            return "VDR";
        }
    }

    public int getRowCount() {
        return vdrTitles.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return tvbTitles.get(rowIndex);
        } else {
            return vdrTitles.get(rowIndex);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            tvbTitles.set(rowIndex, aValue.toString());
        } else {
            vdrTitles.set(rowIndex, aValue.toString());
        }
    }
    
    public Map<String, String> getAsMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < tvbTitles.size(); i++) {
            map.put(tvbTitles.get(i), vdrTitles.get(i));
        }
        return map;
    }
    
    public void setMappingFromMap(Map<String, String> map) {
        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            String value = (String) map.get(key);
            tvbTitles.add(key);
            vdrTitles.add(value);
        }
    }
}