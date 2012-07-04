package lazybones.gui.components.historycombobox;

public interface SuggestionListener {

    /**
     * Invoked, if an attempt to suggest text has been made
     * @param suggestion The suggested text or null if no suggestion could be found
     */
    public void suggested(String suggestion);
}
