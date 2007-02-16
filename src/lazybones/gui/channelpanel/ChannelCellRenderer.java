/* $Id: ChannelCellRenderer.java,v 1.1 2007-02-16 22:20:25 hampelratte Exp $
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
package lazybones.gui.channelpanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import tvbrowser.ui.programtable.ChannelLabel;
import de.hampelratte.svdrp.responses.highlevel.Channel;

public class ChannelCellRenderer extends DefaultTableCellRenderer {

    private Color uneven = new Color(240,240,240);

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            if( !(value instanceof devplugin.Channel) ) {
                return null;
            }
            
            ChannelLabel channelLabel = new ChannelLabel((devplugin.Channel) value);
            channelLabel.setFont(table.getFont());
            channelLabel.setBorder(noFocusBorder);
            channelLabel.setHorizontalAlignment(JLabel.LEFT);
            channelLabel.setOpaque(true);
            
            // set colors
            if (isSelected) {
                channelLabel.setForeground(table.getSelectionForeground());
                channelLabel.setBackground(table.getSelectionBackground());
            } else {
                if( (row & 1) == 0 ) { // even lines
                    channelLabel.setBackground(table.getBackground());
                    channelLabel.setForeground(table.getForeground());
                } else { // uneven lines
                    channelLabel.setBackground(uneven);
                    channelLabel.setForeground(table.getForeground());
                }
            }
            return channelLabel;
        } else {
            if (isSelected) {
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else {
                if( (row & 1) == 0 ) { // even lines
                    super.setForeground(table.getForeground());
                    super.setBackground(table.getBackground());
                } else { // uneven lines
                    super.setForeground(table.getForeground());
                    super.setBackground(uneven);
                }
            }

            setFont(table.getFont());

            if (hasFocus) {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }

            if(value != null) {
                Channel chan = (Channel)value;
                setValue(chan.getShortName().length() > 0 ? chan.getShortName() : chan.getName());
            } else {
                setValue(null);
            }
        }

        return this;
    }
}