package lazybones.gui;

import javax.swing.JPanel;

import lazybones.LazyBones;
import lazybones.gui.recordings.RecordingManagerPanel;
import devplugin.PluginCenterPanel;

public class RecordingsCenterPanel extends PluginCenterPanel {

    private RecordingManagerPanel panel;

    public RecordingsCenterPanel() {
        panel = new RecordingManagerPanel();
    }

    @Override
    public String getName() {
        return LazyBones.getTranslation("recordings", "Recordings");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

}
