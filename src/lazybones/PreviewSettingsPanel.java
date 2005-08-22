/* $Id: PreviewSettingsPanel.java,v 1.2 2005-08-22 15:07:46 hampelratte Exp $
 * 
 * Copyrimport java.awt.GridBagConstraints;
 import java.awt.GridBagLayout;
 import java.awt.Insets;

 import javax.swing.*;
 in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package lazybones;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

public class PreviewSettingsPanel extends JPanel {
    private static final long serialVersionUID = 5046636902877005743L;

    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(PreviewSettingsPanel.class);

    private LazyBones control;

    private JLabel lURL = new JLabel(mLocalizer.msg("url",
            "URL to preview picture"));

    private JTextField url;

    private JLabel lPicturePath = new JLabel(mLocalizer.msg("path",
            "Path to preview picture"));

    private JTextArea description = new JTextArea(
            mLocalizer
                    .msg(
                            "desc",
                            "The URL is the URL, where"
                                    + " VDRRemoteControl can download the preview image. The path is the path to the preview"
                                    + " image on the VDR host. This should be the document root of the webserver, which has"
                                    + " been specified in the URL"), 10, 40);

    private JTextField picturePath;

    public PreviewSettingsPanel(LazyBones control) {
        this.control = control;
        initGUI();
    }

    private void initGUI() {
        url = new JTextField(20);
        url.setText(control.getProperties().getProperty("preview.url"));
        picturePath = new JTextField(20);
        picturePath
                .setText(control.getProperties().getProperty("preview.path"));

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(lURL, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(url, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        add(lPicturePath, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(picturePath, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane scrollpane = new JScrollPane(description);
        add(scrollpane, gbc);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        description.setBackground(UIManager.getColor("JPanel.background"));
    }

    public void saveSettings() {
        control.getProperties().setProperty("preview.url", url.getText());
        control.getProperties().setProperty("preview.path",
                picturePath.getText());
    }
}