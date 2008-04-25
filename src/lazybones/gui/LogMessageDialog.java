/* $Id: LogMessageDialog.java,v 1.12 2008-04-25 12:00:37 hampelratte Exp $
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import lazybones.LazyBones;
import util.ui.Localizer;



public class LogMessageDialog extends JDialog {
    
    // TODO add a threadsafe queue for saving the messages
    
    private static LogMessageDialog instance;
    
    public JList list;
    public DefaultListModel model;
    private HashMap<Level,Icon> icons = new HashMap<Level,Icon>();

    private LogMessageDialog() {
        super(LazyBones.getInstance().getParent(), true);
        initGUI();
    }
    

    private void initGUI() {
        setTitle("Lazy Bones - " + Localizer.getLocalization(Localizer.I18N_ERROR));
        setSize(700, 400);
        getContentPane().setLayout(new BorderLayout(10,10));
        
        icons.put(Level.SEVERE,  UIManager.getDefaults().getIcon("OptionPane.errorIcon"));
        icons.put(Level.WARNING, UIManager.getDefaults().getIcon("OptionPane.warningIcon"));
        icons.put(Level.INFO,    UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Level.CONFIG,  UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Level.FINE,    UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Level.FINER,   UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        icons.put(Level.FINEST,  UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        
        
        list = new JList();
        model = new DefaultListModel();
        list.setModel(model);
        list.setCellRenderer(new LogListCellRenderer());
        getContentPane().add(new JScrollPane(list), BorderLayout.CENTER);
        JButton okButton = new JButton(Localizer.getLocalization(Localizer.I18N_OK));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                closeWindow();
            } 
        });
        getContentPane().add(okButton, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                closeWindow();
            }
        });
    }

    
    private void closeWindow() {
        model.removeAllElements();
        setVisible(false);
    }

    public synchronized static LogMessageDialog getInstance() {
        if (instance == null) {
            instance = new LogMessageDialog();
        }
        return instance;
    }
    
    public void setVisible(boolean visible) {
        int parentWidth = LazyBones.getInstance().getParent().getWidth();
        int parentHeight = LazyBones.getInstance().getParent().getHeight();
        int posX = (parentWidth - getWidth()) / 2;
        int posY = (parentHeight - getHeight()) / 2;
        setLocation(posX, posY);
        super.setVisible(visible);
    }
    
    public synchronized void addMessage(LogRecord message) {
        ((DefaultListModel)list.getModel()).addElement(message);
    }
    
    private class LogListCellRenderer extends JLabel implements ListCellRenderer {

        private Color altColor = new Color(250, 250, 220);
        
        public LogListCellRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus) {
            LogRecord log = (LogRecord)value;
            StringBuilder sb = new StringBuilder("<html>");
            sb.append(log.getSourceClassName());
            sb.append('.');
            sb.append(log.getSourceMethodName());
            sb.append("<br>");
            sb.append(log.getMessage().replaceAll("\n", "<br>"));
            sb.append("</html>");
            
            setText(sb.toString());
            setIcon(icons.get(log.getLevel()));
            setForeground(Color.BLACK);
            setBackground(index % 2 == 0 ? Color.WHITE : altColor);
            
            return this;
        }
    }
}
