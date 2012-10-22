package lazybones.gui;

import javax.swing.JPanel;

import lazybones.LazyBones;
import devplugin.PluginCenterPanel;

public class TimelineCenterPanel extends PluginCenterPanel {

    private TimelinePanel panel;

    public TimelineCenterPanel() {
        panel = new TimelinePanel();
    }

    @Override
    public String getName() {
        return LazyBones.getTranslation("timeline", "Timeline");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

}
