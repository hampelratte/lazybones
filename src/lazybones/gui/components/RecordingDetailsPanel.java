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
import java.text.DateFormat;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import lazybones.LazyBones;
import lazybones.VDRCallback;
import lazybones.actions.GetRecordingDetailsAction;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.Stream;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordingDetailsPanel extends JPanel implements TreeSelectionListener {

    private static final long serialVersionUID = 1L;

    private static transient Logger logger = LoggerFactory.getLogger(RecordingDetailsPanel.class);

    private Recording recording;

    private JScrollPane scrollPane;
    private JEditorPane htmlPane;

    private String recordingDetailsTemplate;

    public RecordingDetailsPanel() {
        initGUI();
    }

    public RecordingDetailsPanel(Recording recording) {
        this.recording = recording;
        initGUI();
        updateHtmlPane();
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        updateHtmlPane();
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
            String template = getRecordingDetailsTemplate();
            template = replaceTags(template);
            htmlPane.setText(template);
            htmlPane.setCaretPosition(0);
        } catch (Exception e) {
            htmlPane.setText("Couldn't load template for recording details:\n" + e.getLocalizedMessage());
        }
    }

    private String replaceTags(String template) {
        template = template.replaceAll("\\{title\\}", recording.getDisplayTitle());

        String channel = recording.getChannelName();
        template = template.replaceAll("\\{channel\\}", channel);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        template = template.replaceAll("\\{startDate\\}", dateFormat.format(recording.getStartTime().getTime()));
        template = template.replaceAll("\\{startTime\\}", timeFormat.format(recording.getStartTime().getTime()));
        template = template.replaceAll("\\{endTime\\}", timeFormat.format(recording.getEndTime().getTime()));
        template = template.replaceAll("\\{description\\}", recording.getDescription().replaceAll("\n", "<br>"));
        template = template.replaceAll("\\{i18n_directory\\}", LazyBones.getTranslation("directory", "Directory"));
        template = template.replaceAll("\\{directory\\}", recording.getFolder());
        template = template.replaceAll("\\{i18n_lifetime\\}", LazyBones.getTranslation("lifetime", "Lifetime"));
        template = template.replaceAll("\\{lifetime\\}", Integer.toString(recording.getLifetime()));
        template = template.replaceAll("\\{i18n_priority\\}", LazyBones.getTranslation("priority", "Priority"));
        template = template.replaceAll("\\{priority\\}", Integer.toString(recording.getPriority()));
        template = template.replaceAll("\\{i18n_streams\\}", LazyBones.getTranslation("streams", "Streams"));
        template = template.replaceAll("\\{streams\\}", createStreamsString());
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

    private String createStreamsString() {
        if (recording.getStreams().size() > 0) {
            StringBuilder streamInfo = new StringBuilder("<ul>");
            for (Stream stream : recording.getStreams()) {
                streamInfo.append("<li>");
                streamInfo.append(createStreamInfoString(stream));
                streamInfo.append("</li>");
            }
            streamInfo.append("</ul>");
            return streamInfo.toString();
        } else {
            return "";
        }
    }

    private String createStreamInfoString(Stream stream) {
        String content = LazyBones.getTranslation(stream.getContent().toString(), stream.getContent().toString());
        String language = stream.getLanguage();
        String description = stream.getDescription();
        return content + ' ' + description + " (" + language + ")";
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        final TreePath selected = e.getNewLeadSelectionPath();
        if (selected != null) {
            TreeNode treeNode = (TreeNode) selected.getPathComponent(selected.getPathCount() - 1);
            if (treeNode instanceof Recording) {
                final Recording rec = (Recording) treeNode;
                VDRCallback<GetRecordingDetailsAction> callback = new VDRCallback<GetRecordingDetailsAction>() {
                    @Override
                    public void receiveResponse(GetRecordingDetailsAction cmd, Response response) {
                        if (cmd.isSuccess()) {
                            setRecording(cmd.getRecording());
                        } else {
                            reset();
                            String mesg = LazyBones
                                    .getTranslation("error_retrieve_recording_details", "Couldn't load recording details from VDR: {0}", response.getMessage());
                            logger.error(mesg);
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ((JTree) e.getSource()).getModel().valueForPathChanged(selected, rec);
                            }
                        });
                    }
                };
                GetRecordingDetailsAction grda = new GetRecordingDetailsAction(rec, callback);
                grda.enqueue();
            } else {
                reset();
            }
        } else {
            reset();
        }
    }

    public void reset() {
        htmlPane.setText("<html><head></head><body></body></html>");
    }

    private String getRecordingDetailsTemplate() throws IOException {
        if (recordingDetailsTemplate != null) {
            return recordingDetailsTemplate;
        }

        InputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            in = RecordingDetailsPanel.class.getClassLoader().getResourceAsStream("recording_details.html");
            bos = new ByteArrayOutputStream();
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            recordingDetailsTemplate = new String(bos.toByteArray());
            return recordingDetailsTemplate;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("Couldn't close input stream for recording details template", e);
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