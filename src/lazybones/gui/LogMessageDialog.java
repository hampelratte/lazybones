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
package lazybones.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.AbstractListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lazybones.LazyBones;
import lazybones.logging.SimpleFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.i18n.Localizer;
import util.ui.UiUtilities;
import util.ui.WindowClosingIf;

public class LogMessageDialog extends JDialog implements ListSelectionListener, WindowClosingIf {

    private static transient Logger logger = LoggerFactory.getLogger(LogMessageDialog.class.getName());

    private static LogMessageDialog instance;

    public JList<LogRecord> list;
    public LogMessageListModel model;
    private JSplitPane splitPane;
    private JScrollPane listScrollpane;
    private JTextArea taDetails;
    private JScrollPane textScrollpane;
    private transient HashMap<Level, Icon> levelIcons = new HashMap<>();

    private LogMessageDialog() {
        super(LazyBones.getInstance().getParent(), false);
        initGUI();
        UiUtilities.registerForClosing(this);
    }

    private void initGUI() {
        setTitle("Lazy Bones - " + LazyBones.getTranslation("msg", "Message"));
        setSize(700, 400);
        getContentPane().setLayout(new BorderLayout(10, 10));

        levelIcons.put(Level.SEVERE, UIManager.getDefaults().getIcon("OptionPane.errorIcon"));
        levelIcons.put(Level.WARNING, UIManager.getDefaults().getIcon("OptionPane.warningIcon"));
        levelIcons.put(Level.INFO, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        levelIcons.put(Level.CONFIG, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        levelIcons.put(Level.FINE, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        levelIcons.put(Level.FINER, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));
        levelIcons.put(Level.FINEST, UIManager.getDefaults().getIcon("OptionPane.informationIcon"));

        JButton okButton = new JButton(Localizer.getLocalization(Localizer.I18N_OK));
        okButton.addActionListener(event -> close());
        getContentPane().add(okButton, BorderLayout.SOUTH);
        getContentPane().add(getSplitPane(), BorderLayout.CENTER);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                logger.info("Window closing {}", we);
                close();
            }
        });

        // requires java 1.6
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    }

    @Override
    public synchronized void close() {
        setVisible(false);
        model.clear();
        taDetails.setText("Details...");
    }

    public static synchronized LogMessageDialog getInstance() {
        if (instance == null) {
            instance = new LogMessageDialog();
            int parentWidth = LazyBones.getInstance().getParent().getWidth();
            int parentHeight = LazyBones.getInstance().getParent().getHeight();
            int parentX = LazyBones.getInstance().getParent().getX();
            int parentY = LazyBones.getInstance().getParent().getY();
            int posX = (parentWidth - instance.getWidth()) / 2;
            int posY = (parentHeight - instance.getHeight()) / 2;
            instance.setLocation(parentX + posX, parentY + posY);
        }
        return instance;
    }

    public synchronized void addMessage(LogRecord message) {
        model.addMessage(message);
        if (!isVisible()) {
            setVisible(true);
        }
    }

    private JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane();
            splitPane.add(getListScrollpane(), JSplitPane.LEFT);
            splitPane.add(getTextScrollpane(), JSplitPane.RIGHT);
            splitPane.setOrientation(SwingConstants.HORIZONTAL);
            splitPane.setDividerLocation(150);
        }
        return splitPane;
    }

    private JScrollPane getListScrollpane() {
        if (listScrollpane == null) {
            list = new JList<>();
            list.addListSelectionListener(this);
            model = new LogMessageListModel();
            list.setModel(model);
            list.setCellRenderer(new LogListCellRenderer());
            listScrollpane = new JScrollPane(list);
        }
        return listScrollpane;
    }

    private JScrollPane getTextScrollpane() {
        if (textScrollpane == null) {
            textScrollpane = new JScrollPane();
            textScrollpane.setViewportView(getTaDetails());
        }
        return textScrollpane;
    }

    private JTextArea getTaDetails() {
        if (taDetails == null) {
            taDetails = new JTextArea();
            taDetails.setText("Details...");
            taDetails.setWrapStyleWord(true);
            taDetails.setEditable(false);
        }
        return taDetails;
    }

    private class LogListCellRenderer extends JLabel implements ListCellRenderer<LogRecord> {
        private Color listBackground = UIManager.getColor("List.background");
        private Color listBackgroundAlt = UIManager.getColor("Panel.background");
        private Color textColor = UIManager.getColor("List.foreground");
        private Color textColorSelected = UIManager.getColor("List.selectionForeground");

        public LogListCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends LogRecord> list, LogRecord log, int index, boolean selected, boolean hasFocus) {
            StringBuilder sb = new StringBuilder("<html>");
            sb.append(log.getMessage().replace("\n", "<br>"));
            sb.append("</html>");

            setText(sb.toString());
            setIcon(levelIcons.get(log.getLevel()));
            setForeground(textColor);

            if (selected) {
                setForeground(textColorSelected);
                setBackground(UIManager.getColor("List.selectionBackground"));
            } else {
                setForeground(textColor);
                setBackground(index % 2 == 0 ? listBackground : listBackgroundAlt);
            }

            return this;
        }

        @Override
        public void updateUI() {
            super.updateUI();
            listBackground = UIManager.getColor("List.background");
            listBackgroundAlt = UIManager.getColor("Panel.background");
            textColor = UIManager.getColor("List.foreground");
            textColorSelected = UIManager.getColor("List.selectionForeground");
        }
    }

    private transient SimpleFormatter formatter = new SimpleFormatter();

    @Override
    public void valueChanged(ListSelectionEvent e) {
        LogRecord log = list.getSelectedValue();
        if (log != null) {
            taDetails.setText(formatter.format(log));
            taDetails.setCaretPosition(0);
        }
    }

    private class LogMessageListModel extends AbstractListModel<LogRecord> {

        private List<LogRecord> messages = new ArrayList<>();

        public void addMessage(LogRecord message) {
            messages.add(message);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    fireIntervalAdded(this, messages.size() - 1, messages.size() - 1);
                }
            });
        }

        public void clear() {
            messages.clear();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int lastIndex = Math.max(0, messages.size() - 1);
                    fireIntervalRemoved(this, 0, lastIndex);
                }
            });
        }

        @Override
        public int getSize() {
            return messages.size();
        }

        @Override
        public LogRecord getElementAt(int index) {
            return messages.get(index);
        }
    }

}
