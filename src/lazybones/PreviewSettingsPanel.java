/* $Id: PreviewSettingsPanel.java,v 1.5 2005-08-27 20:07:58 emsker Exp $
 * 
 * Copyright (c) 2005, Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
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

import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PreviewSettingsPanel {
    private static final long serialVersionUID = 5046636902877005743L;

    private static final util.ui.Localizer mLocalizer = util.ui.Localizer
            .getLocalizerFor(PreviewSettingsPanel.class);

    private LazyBones control;

    private final String lURL = mLocalizer.msg("url",
            "URL to preview picture");

    private JTextField url;

    private final String lPicturePath = mLocalizer.msg("path",
            "Path to preview picture");

    private final String lDescription = mLocalizer
			.msg(
					"desc",
					"The URL is the URL, where"
							+ " VDRRemoteControl can download the preview image. The path is the path to the preview"
							+ " image on the VDR host. This should be the document root of the webserver, which has"
							+ " been specified in the URL");
                                    

    private JTextField picturePath;
    
    private JComponent note;

    public PreviewSettingsPanel(LazyBones control) {
        this.control = control;
        initComponents();
    }
    
    private void initComponents() {
        url = new JTextField(20);
        url.setText(control.getProperties().getProperty("preview.url"));
        picturePath = new JTextField(20);
        picturePath
                .setText(control.getProperties().getProperty("preview.path"));

        JTextArea description;
        description = new JTextArea(lDescription, 10, 40);
        description = new JTextArea(lDescription);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(UIManager.getColor("JPanel.background"));

        note = new JScrollPane(description,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    JPanel getPanel() {
		FormLayout layout = new FormLayout(VDRSettingsPanel.FORMBUILDER_DEFAULT_COLUMNS,
			"pref, 2dlu, pref, 2dlu, top:pref:grow");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		
		builder.addLabel(lURL,         cc.xy (1,  1));
		builder.add(url,               cc.xyw(3,  1, 3));
		
		builder.addLabel(lPicturePath, cc.xy (1,  3));
		builder.add(picturePath,       cc.xyw(3,  3, 3));
		
		builder.add(note,              cc.xyw(1, 5, 5));
		
		return builder.getPanel();
    }

    public void saveSettings() {
        control.getProperties().setProperty("preview.url", url.getText());
        control.getProperties().setProperty("preview.path",
                picturePath.getText());
    }
}