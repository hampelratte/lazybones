package lazybones;

import org.hampelratte.svdrp.responses.highlevel.DiskStatus;

public interface DiskStatusListener {
	void diskStatusChanged(DiskStatus status);
}
