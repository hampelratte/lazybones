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
package lazybones.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import lazybones.LazyBones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDetailsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static transient Logger logger = LoggerFactory.getLogger(AbstractDetailsPanel.class);

    private JScrollPane scrollPane;
    private JEditorPaneAA htmlPane;

    private String templateFile;
    private String template;

    public AbstractDetailsPanel(String templateFile) {
        this.templateFile = templateFile;
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        htmlPane = new JEditorPaneAA();
        htmlPane.setEditable(false);
        htmlPane.setContentType("text/html");
        htmlPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane = new JScrollPane(htmlPane);
        scrollPane.setMinimumSize(new Dimension(50, 50));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 5, 0);
        gbc.weightx = 1;
        gbc.weighty = 1;

        add(scrollPane, gbc);
    }

    protected void updateHtmlPane() {
        try {
            if (template == null) {
                template = loadFile(templateFile);
            }
            String template = internalReplaceTags(this.template);
            htmlPane.setText(template);
            htmlPane.setCaretPosition(0);
        } catch (Exception e) {
            htmlPane.setText("Couldn't load template for details:\n" + e.getLocalizedMessage());
            logger.warn("Couldn't load template for details", e);
        }
    }

    protected String internalReplaceTags(String template) throws IOException {
        template = injectCss(template);
        template = replaceTags(template);
        template = replaceI18NTags(template);
        template = replaceColors(template);
        return template;
    }


    public abstract String replaceTags(String template);

    private String replaceI18NTags(String template) {
        Pattern p = Pattern.compile("\\{i18n_(.*)\\}");
        Matcher m = p.matcher(template);
        while (m.find()) {
            String key = m.group(1);
            String i18n = LazyBones.getTranslation(key, "");
            template = template.replaceFirst(p.pattern(), i18n);
            m = p.matcher(template);
        }
        return template;
    }

    private String replaceColors(String template) {
        template = template.replaceAll("\\{backgroundColor\\}", toHexString(UIManager.getColor("TextArea.background")));
        template = template.replaceAll("\\{textColor\\}", toHexString(UIManager.getColor("TextArea.foreground")));
        template = template.replaceAll("\\{textInactiveColor\\}", toHexString(UIManager.getColor("TextArea.inactiveForeground")));
        return template;
    }

    private String injectCss(String template) throws IOException {
        String css = loadFile("style.css");
        template = template.replaceAll("\\{style\\}", css);
        return template;
    }

    private String toHexString(Color c) {
        StringBuilder sb = new StringBuilder("#");
        sb.append(toHex(c.getRed()));
        sb.append(toHex(c.getGreen()));
        sb.append(toHex(c.getBlue()));
        sb.append(toHex(c.getAlpha()));
        return sb.toString();
    }

    private String toHex(int i) {
        String hex = Integer.toString(i, 16);
        return hex.length() == 2 ? hex : "0" + hex;
    }

    public void reset() {
        htmlPane.setText("<html><head></head><body></body></html>");
    }

    protected String loadFile(String filename) throws IOException {
        String template = null;
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            in = AbstractDetailsPanel.class.getClassLoader().getResourceAsStream(filename);
            bos = new ByteArrayOutputStream();
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            template = new String(bos.toByteArray());
            return template;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("Couldn't close input stream for file", e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    logger.error("Couldn't close stream", e);
                }
            }
        }
    }
}