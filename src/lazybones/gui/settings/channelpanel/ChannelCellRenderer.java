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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.hampelratte.svdrp.responses.highlevel.Channel;

import util.ui.ChannelLabel;

public class ChannelCellRenderer extends DefaultTableCellRenderer {

    private Color uneven;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
        	return createChannelLabel(table, value, isSelected, row);
        } else {
        	setColors(table, row, isSelected);
            setFont(table.getFont());

            if (hasFocus) {
            	enableFocusedMode(table, row, column, isSelected);
            } else {
                setBorder(noFocusBorder);
            }

            if (value != null) {
                Channel chan = (Channel) value;
                String channelName = chan.getShortName().length() > 0 ? chan.getShortName() : chan.getName();
                setValue("[" + chan.getChannelNumber() + "] " + channelName);
            } else {
                setValue(null);
            }
        }

        return this;
    }

    private void enableFocusedMode(JTable table, int row, int column, boolean isSelected) {
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
	}

	private void setColors(JTable table, int row, boolean isSelected) {
    	if (isSelected) {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            if ((row & 1) == 0) { // even lines
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            } else { // uneven lines
                super.setForeground(table.getForeground());
                super.setBackground(uneven);
            }
        }
	}

	private Component createChannelLabel(JTable table, Object value, boolean isSelected, int row) {
    	if (!(value instanceof devplugin.Channel)) {
            return null;
        }

        ChannelLabel channelLabel = new ChannelLabel((devplugin.Channel) value);
        channelLabel.setFont(table.getFont());
        channelLabel.setBorder(noFocusBorder);
        channelLabel.setHorizontalAlignment(LEFT);
        channelLabel.setOpaque(true);

        // set colors
        if (isSelected) {
            channelLabel.setForeground(table.getSelectionForeground());
            channelLabel.setBackground(table.getSelectionBackground());
        } else {
            if ((row & 1) == 0) { // even lines
                channelLabel.setBackground(table.getBackground());
                channelLabel.setForeground(table.getForeground());
            } else { // uneven lines
                channelLabel.setBackground(uneven);
                channelLabel.setForeground(table.getForeground());
            }
        }
        return channelLabel;
	}

	@Override
    public void updateUI() {
        super.updateUI();
        uneven = UIManager.getColor("Panel.background");
    }
}