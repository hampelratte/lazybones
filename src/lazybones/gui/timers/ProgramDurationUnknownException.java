package lazybones.gui.timers;

public class ProgramDurationUnknownException extends RuntimeException {
	public ProgramDurationUnknownException() {
		super("Duration of selected program is unknown");
	}
}
