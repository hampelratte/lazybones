package lazybones.gui;

import lazybones.Logger.LoggingLevel;

public class LogMessage {
    private LoggingLevel level;
    private String message;

    public LogMessage(String message, LoggingLevel level) {
        this.message = message;
        this.level = level;
    }

    public LoggingLevel getLevel() {
        return level;
    }

    public void setLevel(LoggingLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
