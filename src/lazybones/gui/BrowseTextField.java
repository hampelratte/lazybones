/* $Id: BrowseTextField.java,v 1.3 2006-08-30 21:41:53 hampelratte Exp $
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
package lazybones.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;


public class BrowseTextField extends JPanel implements ActionListener, PropertyChangeListener {

    private JToggleButton button = new JToggleButton("...");

    private JTextField textfield = new JTextField(10);

    private BrowsePanel panel;

    public BrowseTextField(BrowsePanel panel) {
        this.panel = panel;
        panel.setTextField(textfield);

        /*
         * panel.setBackground(UIManager.getColor("ToolTip.background")); for
         * (int i = 0; i < panel.getComponents().length; i++) {
         * panel.getComponent(i).setBackground(UIManager.getColor("ToolTip.background")); }
         */

        this.setLayout(new BorderLayout());
        this.add(textfield, BorderLayout.CENTER);
        this.add(button, BorderLayout.EAST);

        button.addActionListener(this);
    }

    public void showDialog() {
        int x = (int) button.getWidth() + 10;
        int y = 0;
        JPopupMenu menu = new JPopupMenu();
        menu.add(panel);
        menu.setBorder(BorderFactory.createLineBorder(Color.RED));
        menu.show(button,x,y);
        menu.addPropertyChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            showDialog();
        }
    }

    public void setEditable(boolean editable) {
        textfield.setEditable(editable);
    }

    public void setEnabled(boolean enabled) {
        textfield.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    public void setText(String text) {
        textfield.setText(text);
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if( "visible".equals(pce.getPropertyName()) ) {
            if( ((Boolean)pce.getNewValue()).booleanValue() == false ) {
                button.setSelected(false);
            }
        }
    }
}