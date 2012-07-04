package lazybones.gui.components.historycombobox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

/**
 * Extends the standard JComboBox with a history. Works with Strings only.
 * @author henni
 *
 */
public class JHistoryComboBox extends JComboBox implements ActionListener {

    protected ComboBoxHistory model;

    /**
     * Default constructor for GUI editors. Don't use this!!!
     */
    public JHistoryComboBox() {}

    /**
     * @param history the history as a list of strings
     */
    public JHistoryComboBox(List<String> history) {
        model = new ComboBoxHistory(30);
        setModel(model);
        getEditor().addActionListener(this);
        setEditable(true);
        setHistory(history);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addCurrentItemToHistory();
    }

    public void addCurrentItemToHistory() {
        String regex = (String)getEditor().getItem();
        model.addElement(regex);
    }

    public void setText(String text) {
        getEditor().setItem(text);
        setSelectedItem(text);
    }

    public String getText() {
        return getEditor().getItem().toString();
    }

    public void addHistoryChangedListener(HistoryChangedListener l) {
        model.addHistoryChangedListener(l);
    }

    public void removeHistoryChangedListener(HistoryChangedListener l) {
        model.removeHistoryChangedListener(l);
    }

    public void setHistory(List<String> history) {
        model.setItems(history);
    }

    public List<String> getHistory() {
        return model.asList();
    }
}