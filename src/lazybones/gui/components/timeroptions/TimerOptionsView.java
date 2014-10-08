/*
 * Copyright (c) Henrik Niehaus
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
package lazybones.gui.components.timeroptions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Program;

public class TimerOptionsView extends JPanel {
    private static transient Logger logger = LoggerFactory.getLogger(TimerOptionsView.class);

    /**
     * The timer to show in the view
     */
    private LazyBonesTimer timer;

    /**
     * The TVB program, which corresponds to the timer
     */
    private Program prog;

    private JScrollPane scrollPane;
    private JEditorPane htmlPane;

    private String timerDetailsTemplate;

    public TimerOptionsView() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        htmlPane = new JEditorPane();
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

    private void updateHtmlPane() {
        try {
            String template = getTimerDetailsTemplate();
            template = replaceTags(template);
            htmlPane.setText(template);
            htmlPane.setCaretPosition(0);
        } catch (Exception e) {
            htmlPane.setText("Couldn't load template for timer details:\n" + e.getLocalizedMessage());
        }
    }

    private String replaceTags(String template) {
        template = template.replaceAll("\\{title\\}", timer.getDisplayTitle());

        String channel = "";
        if (prog != null) {
            channel = prog.getChannel().getName();
        } else {
            Channel chan = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
            channel = chan.getName();
        }
        template = template.replaceAll("\\{channel\\}", channel);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        template = template.replaceAll("\\{startDate\\}", dateFormat.format(timer.getStartTime().getTime()));
        template = template.replaceAll("\\{startTime\\}", timeFormat.format(timer.getStartTime().getTime()));
        template = template.replaceAll("\\{endTime\\}", timeFormat.format(timer.getEndTime().getTime()));
        template = template.replaceAll("\\{description\\}", timer.getDescription().replaceAll("\n", "<br>"));
        template = template.replaceAll("\\{i18n_directory\\}", LazyBones.getTranslation("directory", "Directory"));
        template = template.replaceAll("\\{directory\\}", timer.getPath());
        template = template.replaceAll("\\{i18n_lifetime\\}", LazyBones.getTranslation("lifetime", "Lifetime"));
        template = template.replaceAll("\\{lifetime\\}", Integer.toString(timer.getLifetime()));
        template = template.replaceAll("\\{i18n_priority\\}", LazyBones.getTranslation("priority", "Priority"));
        template = template.replaceAll("\\{priority\\}", Integer.toString(timer.getPriority()));
        template = template.replaceAll("\\{color\\}", toHexString(UIManager.getColor("TextArea.foreground")));
        template = template.replaceAll("\\{backgroundColor\\}", toHexString(UIManager.getColor("TextArea.background")));
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

    private String getTimerDetailsTemplate() throws IOException {
        if (timerDetailsTemplate != null) {
            return timerDetailsTemplate;
        }

        InputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            in = TimerOptionsView.class.getClassLoader().getResourceAsStream("timer_details.html");
            bos = new ByteArrayOutputStream();
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            timerDetailsTemplate = new String(bos.toByteArray());
            return timerDetailsTemplate;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("Couldn't close input stream for timer details template", e);
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

    public LazyBonesTimer getTimer() {
        return timer;
    }

    public void setTimer(LazyBonesTimer timer) {
        this.timer = timer;
        updateHtmlPane();
    }

    public Program getProgram() {
        return prog;
    }

    public void setProgram(Program prog) {
        this.prog = prog;
        updateHtmlPane();
    }

    public void reset() {
        htmlPane.setText("");
    }

}
