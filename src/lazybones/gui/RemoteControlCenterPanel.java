package lazybones.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import lazybones.LazyBones;
import lazybones.gui.components.ScreenshotPanel;
import lazybones.gui.components.remotecontrol.RemoteControl;
import devplugin.PluginCenterPanel;

public class RemoteControlCenterPanel extends PluginCenterPanel {

    private JPanel panel;


    public RemoteControlCenterPanel() {
        RemoteControl remoteControl = new RemoteControl();
        ScreenshotPanel screenshotPanel = new ScreenshotPanel();

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(remoteControl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0,
                0));
        panel.add(screenshotPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5),
                0, 0));
        screenshotPanel.setPreferredSize(new Dimension(720, 540));
    }

    @Override
    public String getName() {
        return LazyBones.getTranslation("remoteControl", "Remote Control");
    }

    @Override
    public JPanel getPanel() {

        return panel;
    }

}
