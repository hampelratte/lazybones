package lazybones.gui.components.historycombobox;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.PlainDocument;

public class EventConsumingPlainDocument extends PlainDocument {
    private boolean consumeEvents;

    public boolean isConsumeEvents() {
        return consumeEvents;
    }

    public void setConsumeEvents(boolean consumeEvents) {
        this.consumeEvents = consumeEvents;
    }

    @Override
    protected void fireChangedUpdate(DocumentEvent e) {
        if(!consumeEvents) {
            super.fireChangedUpdate(e);
        }
    }

    @Override
    protected void fireInsertUpdate(DocumentEvent e) {
        if(!consumeEvents) {
            super.fireInsertUpdate(e);
        }
    }

    @Override
    protected void fireRemoveUpdate(DocumentEvent e) {
        if(!consumeEvents) {
            super.fireRemoveUpdate(e);
        }
    }

    @Override
    protected void fireUndoableEditUpdate(UndoableEditEvent e) {
        if(!consumeEvents) {
            super.fireUndoableEditUpdate(e);
        }
    }


}
