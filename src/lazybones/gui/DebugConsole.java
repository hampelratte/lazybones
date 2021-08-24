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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import lazybones.logging.CircularList;
import lazybones.logging.LogObserver;
import lazybones.logging.LoggingArchive;
import lazybones.logging.SimpleFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ui.UiUtilities;
import util.ui.WindowClosingIf;

public class DebugConsole extends JFrame implements LogObserver, WindowClosingIf {

    private static transient Logger logger = LoggerFactory.getLogger(DebugConsole.class);

    private static final boolean AUTOSCROLL = true;

    private transient SimpleFormatter formatter = new SimpleFormatter();

    private JScrollBar scrollbar;

    private JTextPane textpane = new JTextPane();

    private transient Document doc = textpane.getDocument();

    private Level selectedLevel = Level.FINEST;

    public DebugConsole() {
        LoggingArchive.addObserver(this);
        initStyles();
        initGUI();
        UiUtilities.registerForClosing(this);
        showCurrentLog();
    }

    private void initStyles() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style s = textpane.addStyle(Level.FINE.toString(), def);
        StyleConstants.setForeground(s, Color.GRAY);
        StyleConstants.setFontFamily(s, "monospace");

        s = textpane.addStyle(Level.FINER.toString(), def);
        StyleConstants.setForeground(s, Color.LIGHT_GRAY);
        StyleConstants.setFontFamily(s, "monospace");

        s = textpane.addStyle(Level.FINEST.toString(), def);
        StyleConstants.setForeground(s, Color.LIGHT_GRAY);
        StyleConstants.setFontFamily(s, "monospace");

        s = textpane.addStyle(Level.INFO.toString(), def);
        StyleConstants.setForeground(s, Color.BLACK);
        StyleConstants.setFontFamily(s, "monospace");

        s = textpane.addStyle(Level.WARNING.toString(), def);
        StyleConstants.setForeground(s, Color.ORANGE);
        StyleConstants.setFontFamily(s, "monospace");

        s = textpane.addStyle(Level.SEVERE.toString(), def);
        StyleConstants.setForeground(s, Color.RED);
        StyleConstants.setFontFamily(s, "monospace");
    }

    @Override
    public void updateObserver(Object o) {
        LogRecord entry = (LogRecord) o;
        insertLine(entry);
    }

    private void showCurrentLog() {
        textpane.setText("");
        CircularList<LogRecord> log = LoggingArchive.getLog();
        Iterator<LogRecord> i = log.iterator();
        while (i.hasNext()) {
            LogRecord entry = i.next();
            insertLine(entry);
        }
    }

    private synchronized void insertLine(LogRecord logRecord) {
        if (logRecord != null && logRecord.getLevel().intValue() >= selectedLevel.intValue()) {
            String line = formatter.format(logRecord);
            try {
                doc.insertString(doc.getLength(), line, textpane.getStyle(logRecord.getLevel().toString()));
            } catch (Exception e) {
                logger.error("Couldn't insert line", e);
            }

            if (AUTOSCROLL) {
                scrollbar.setValue(scrollbar.getMaximum());
            }
        }
    }

	private void initGUI() {
		textpane.setEditable(false);

		getContentPane().setLayout(new BorderLayout());
		var scrollpane = new JScrollPane(textpane);
		scrollbar = scrollpane.getVerticalScrollBar();
		getContentPane().add(scrollpane, BorderLayout.CENTER);

		ComboBoxModel<Level> comboLevelModel = new DefaultComboBoxModel<>(
				new Level[] { Level.FINEST, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE });
		var comboLevel = new JComboBox<Level>();
		getContentPane().add(comboLevel, BorderLayout.SOUTH);
		comboLevel.setModel(comboLevelModel);
		comboLevel.addItemListener(e -> {
			selectedLevel = (Level) e.getItem();
			showCurrentLog();
		});

		this.setSize(800, 300);
		this.setTitle("Debug");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				setVisible(false);
			}
		});
	}

    @Override
    public void close() {
        setVisible(false);
    }
}
