package lazybones.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class DebugConsole extends JFrame implements LogObserver, WindowClosingIf {
	
	private static transient Logger logger = LoggerFactory.getLogger(DebugConsole.class);
	
    private final boolean AUTOSCROLL = true;

    private SimpleFormatter formatter = new SimpleFormatter();
    private JComboBox comboLevel;

    // gui
    private JScrollPane scrollpane;

    private JScrollBar scrollbar;

    private JTextPane textpane = new JTextPane();

    private Document doc = textpane.getDocument();
    
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

    private synchronized void insertLine(LogRecord record) {
        if(record != null && record.getLevel().intValue() >= selectedLevel.intValue()) {
            String line = formatter.format(record);
            try {
                doc.insertString(doc.getLength(), line, textpane.getStyle(record.getLevel().toString()) );
            } catch (Exception e) {
                logger.error("Couldn't insert line", e);
            }
    
            if (AUTOSCROLL)
                scrollbar.setValue(scrollbar.getMaximum());
        }
    }

    private void initGUI() {
        textpane.setEditable(false);

        getContentPane().setLayout(new BorderLayout());
        scrollpane = new JScrollPane(textpane);
        scrollbar = scrollpane.getVerticalScrollBar();
        getContentPane().add(scrollpane, BorderLayout.CENTER);
        {
            ComboBoxModel comboLevelModel = 
                new DefaultComboBoxModel(
                        new Level[] { Level.FINEST, Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE });
            comboLevel = new JComboBox();
            getContentPane().add(comboLevel, BorderLayout.SOUTH);
            comboLevel.setModel(comboLevelModel);
            comboLevel.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    selectedLevel = (Level) e.getItem();
                    showCurrentLog();
                }
            });
        }

        this.setSize(800, 300);
        this.setTitle("Debug");

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                setVisible(false);
            }
        });
    }

    public void close() {
        setVisible(false);
    }
}
