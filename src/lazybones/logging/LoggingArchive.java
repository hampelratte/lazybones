package lazybones.logging;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.LogRecord;

// TODO check, if can use the MemoryHandler for this
public class LoggingArchive {
    public static final int LOG_HISTORY = 1000;
    
    private static Vector<LogObserver> observers = new Vector<LogObserver>();

    private static CircularList<LogRecord> list = new CircularList<LogRecord>(1000);

    public static void log(LogRecord record) {
        list.add(record);
        notifyObservers(record);
    }

    public static CircularList<LogRecord> getLog() {
        return list;
    }

    public static void addObserver(LogObserver o) {
        observers.add(o);
    }

    public static void notifyObservers(Object o) {
        Iterator<LogObserver> i = observers.iterator();
        while (i.hasNext()) {
            LogObserver l = i.next();
            l.updateObserver(o);
        }
    }
}