package lazybones.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class DebugConsoleHandler extends Handler {

	public void close() throws SecurityException {}

	public void flush() {}

	public void publish(LogRecord record) {
		LoggingArchive.log(record);
	}
}
