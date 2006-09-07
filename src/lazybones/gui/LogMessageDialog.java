/* $Id: LogMessageDialog.java,v 1.5 2006-09-07 13:38:27 hampelratte Exp $
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;

import javax.swing.*;

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.Logger.LoggingLevel;

import tvbrowser.ui.mainframe.MainFrame;

public class LogMessageDialog extends JDialog {
    
    private static LogMessageDialog instance;
    
    public JList list;
    public DefaultListModel model;
    private HashMap<LoggingLevel,Icon> icons = new HashMap<LoggingLevel,Icon>();

    private LogMessageDialog() {
        super(MainFrame.getInstance(), true);
        initGUI();
    }
    

    private void initGUI() {
        setTitle("Lazy Bones - " + LazyBones.getTranslation("Error", "Error"));
        setSize(700, 400);
        getContentPane().setLayout(new BorderLayout(10,10));
        
        icons.put(Logger.ERROR, UIManager.getDefaults().getIcon("OptionPane.errorIcon"));
        icons.put(Logger.FATAL, UIManager.getDefaults().getIcon("OptionPane.errorIcon"));
        icons.put(Logger.DEBUG, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Logger.INFO, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Logger.WARN, UIManager.getDefaults().getIcon("OptionPane.warningIcon"));
        
        list = new JList();
        model = new DefaultListModel();
        list.setModel(model);
        list.setCellRenderer(new LogListCellRenderer());
        getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
        JButton okButton = new JButton(LazyBones.getTranslation("OK","OK"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                model.removeAllElements();
                setVisible(false);
            } 
        });
        getContentPane().add(okButton, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        addWindowListener(new WindowListener() {

            public void windowClosed(WindowEvent arg0) {}
            public void windowActivated(WindowEvent arg0) {}
            public void windowClosing(WindowEvent arg0) {
                model.removeAllElements();
                setVisible(false);
            }
            public void windowDeactivated(WindowEvent arg0) {}
            public void windowDeiconified(WindowEvent arg0) {}
            public void windowIconified(WindowEvent arg0) {}
            public void windowOpened(WindowEvent arg0) {}
        });
    }


    synchronized public static LogMessageDialog getInstance() {
        if (instance == null) {
            instance = new LogMessageDialog();
        }
        return instance;
    }
    
    public void setVisible(boolean visible) {
        int parentWidth = MainFrame.getInstance().getWidth();
        int parentHeight = MainFrame.getInstance().getHeight();
        int posX = (parentWidth - getWidth()) / 2;
        int posY = (parentHeight - getHeight()) / 2;
        setLocation(posX, posY);
        super.setVisible(visible);
    }
    
    public void addMessage(LogMessage message) {
        ((DefaultListModel)list.getModel()).addElement(message);
    }
    
    private class LogListCellRenderer extends JLabel implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus) {
            LogMessage log = (LogMessage)value;
            setText(log.getMessage());
            setIcon((Icon)icons.get(log.getLevel()));
            return this;
        }
    }
}
