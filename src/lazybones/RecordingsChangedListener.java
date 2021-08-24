package lazybones;

import java.util.List;

import org.hampelratte.svdrp.responses.highlevel.Recording;

public interface RecordingsChangedListener {

	void recordingsChanged(List<Recording> recordings);
}
