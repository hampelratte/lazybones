/*
 * Copyright (c) Henrik Niehaus & Lazy Bones development team
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
package lazybones.gui.settings;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import lazybones.LazyBones;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please
 * visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS
 * MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ScreenshotSettingsPanel implements ItemListener {
    private String lMethod = LazyBones.getTranslation("method", "Method");

    private JComboBox<String> method = new JComboBox<String>();

    private CardLayout cardLayout = new CardLayout();

    private JPanel cardsContainer = new JPanel(cardLayout);

    private JPanel httpPanel = new JPanel();

    private final JLabel lURL = new JLabel(LazyBones.getTranslation("url", "URL to preview picture"));

    private JTextField url;

    private final JLabel lPicturePath = new JLabel(LazyBones.getTranslation("path", "Path to preview picture"));

    private final String lDescription = LazyBones.getTranslation("desc_url", "The URL is the URL, where"
            + " VDRRemoteControl can download the preview image. The path is the path to the preview"
            + " image on the VDR host. This should be the document root of the webserver, which has" + " been specified in the URL");

    private JTextField picturePath;

    private JComponent note;

    public ScreenshotSettingsPanel() {
        initComponents();
    }

    private void initComponents() {
        url = new JTextField(20);
        url.setText(LazyBones.getProperties().getProperty("preview.url"));
        picturePath = new JTextField(20);
        picturePath.setText(LazyBones.getProperties().getProperty("preview.path"));

        JTextArea description;
        description = new JTextArea(lDescription, 10, 40);
        description = new JTextArea(lDescription);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(UIManager.getColor("JPanel.background"));

        note = new JScrollPane(description, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagLayout httpPanelLayout = new GridBagLayout();
        httpPanel.setLayout(httpPanelLayout);
        httpPanel.add(lURL, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        httpPanel.add(url, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        httpPanel.add(lPicturePath,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        httpPanel.add(picturePath, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
                0, 0));
        httpPanel.add(note,
                new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        httpPanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
        httpPanelLayout.rowHeights = new int[] { 7, 7, 7 };
        httpPanelLayout.columnWeights = new double[] { 0.1, 0.1 };
        httpPanelLayout.columnWidths = new int[] { 7, 7 };
        cardsContainer.add(httpPanel, "HTTP");
        {
            cardsContainer.add(new JPanel(), "SVDRP");
        }

        method.addItem("HTTP");
        method.addItem("SVDRP");
        method.addItemListener(this);
        String m = LazyBones.getProperties().getProperty("preview.method");
        for (int i = 0; i < method.getItemCount(); i++) {
            String item = method.getItemAt(i);
            if (m.equals(item)) {
                method.setSelectedIndex(i);
                break;
            }
        }
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new java.awt.Dimension(473, 264));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(lMethod), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(method, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(cardsContainer, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0,
                0), 0, 0));
        return panel;
    }

    public void saveSettings() {
        LazyBones.getProperties().setProperty("preview.url", url.getText());
        LazyBones.getProperties().setProperty("preview.path", picturePath.getText());
        LazyBones.getProperties().setProperty("preview.method", method.getSelectedItem().toString());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            cardLayout.show(cardsContainer, e.getItem().toString());
        }
    }
}