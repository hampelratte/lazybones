package lazybones.gui;

import javax.swing.JPanel;

import lazybones.LazyBones;
import lazybones.gui.timers.TimerManagerPanel;
import devplugin.PluginCenterPanel;

public class TimersCenterPanel extends PluginCenterPanel {

    private TimerManagerPanel panel;

    public TimersCenterPanel() {
        panel = new TimerManagerPanel();
    }

    @Override
    public String getName() {
        return LazyBones.getTranslation("timers", "Timers");
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

}
