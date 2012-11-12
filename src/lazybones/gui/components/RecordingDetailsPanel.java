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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
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

    private final JLabel folder = new JLabel();
    private final JLabel title = new JLabel();
    private final JLabel time = new JLabel();
    private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, Locale.getDefault());
    private final JLabel shortTextLabel = new JLabel();
    private final ExpandToggleButton showStreamsToggle = new ExpandToggleButton();
    private final JLabel streamsLabelShort = new JLabel();
    private final JLabel streamsLabel = new JLabel();
    private final JTextArea desc = new JTextArea();

    public RecordingDetailsPanel() {
        initGUI();
    }

    public RecordingDetailsPanel(Recording recording) {
        this.recording = recording;
        initGUI();
        loadData();
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        loadData();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y++;
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        add(title, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        folder.setIcon((Icon) UIManager.getDefaults().get("FileView.directoryIcon"));
        folder.setVisible(false);
        add(folder, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        add(time, gbc);

        gbc.gridx = 0;
        gbc.gridy = y++;
        add(shortTextLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        add(showStreamsToggle, gbc);
        showStreamsToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean selected = showStreamsToggle.isSelected();
                adjustStreamLabelVisibility(selected);
            }
        });

        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(streamsLabelShort, gbc);
        streamsLabelShort.setText("Audio Stream and more ...");
        add(streamsLabel, gbc);
        streamsLabel.setVisible(false);
        streamsLabel.setText("<html>1<br>2<br>3<br></html>");

        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setBackground(Color.WHITE);
        add(new JScrollPane(desc), gbc);

        adjustStreamLabelVisibility(showStreamsToggle.isSelected());
    }

    private void adjustStreamLabelVisibility(boolean selected) {
        if (recording != null && recording.getStreams().size() > 0) {
            if (recording.getStreams().size() > 1) {
                showStreamsToggle.setVisible(true);
                if (selected) {
                    streamsLabelShort.setVisible(false);
                    streamsLabel.setVisible(true);
                } else {
                    streamsLabelShort.setVisible(true);
                    streamsLabel.setVisible(false);
                }
            } else {
                showStreamsToggle.setVisible(false);
                streamsLabelShort.setVisible(false);
                streamsLabel.setVisible(true);
            }
        } else {
            showStreamsToggle.setVisible(false);
            streamsLabelShort.setVisible(false);
            streamsLabel.setVisible(false);
        }
    }

    private void loadData() {
        title.setText(recording.getDisplayTitle());
        title.setToolTipText(title.getText());
        folder.setText(recording.getFolder());
        folder.setVisible(recording.getFolder() != null);
        time.setText(df.format(recording.getStartTime().getTime()));
        String shortText = (recording.getShortText() != null && recording.getShortText().length() > 0) ? recording.getShortText() : "";
        shortTextLabel.setText(shortText);
        shortTextLabel.setToolTipText(shortTextLabel.getText());
        desc.setText(recording.getDescription());

        if (recording.getStreams().size() > 0) {
            streamsLabelShort.setText(createStreamInfoString(recording.getStreams().get(0)));

            String allStreams = "<html>";
            for (Stream stream : recording.getStreams()) {
                String info = createStreamInfoString(stream);
                allStreams += info + "<br>";
            }
            allStreams += "</html>";
            streamsLabel.setText(allStreams);
        }

        adjustStreamLabelVisibility(showStreamsToggle.isSelected());
    }

    private String createStreamInfoString(Stream stream) {
        String content = LazyBones.getTranslation(stream.getContent().toString(), stream.getContent().toString());
        String language = stream.getLanguage();
        String description = stream.getDescription();
        return content + ' ' + description + " (" + language + ")";
    }

    private void clearDetailsPanel() {
        title.setText(null);
        time.setText(null);
        shortTextLabel.setText(null);
        desc.setText(null);
        recording = null;
        adjustStreamLabelVisibility(showStreamsToggle.isSelected());
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        final TreePath selected = e.getPath();
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
                            clearDetailsPanel();
                            String mesg = LazyBones
                                    .getTranslation("error_retrieve_recording_details", "Couldn't load recording details from VDR: {0}", response.getMessage());
                            logger.error(mesg);
                        }
                        ((JTree) e.getSource()).getModel().valueForPathChanged(selected, rec);
                    }
                };
                GetRecordingDetailsAction grda = new GetRecordingDetailsAction(rec, callback);
                grda.enqueue();
            } else {
                clearDetailsPanel();
            }
        } else {
            title.setText(null);
            time.setText(null);
            shortTextLabel.setText(null);
            desc.setText(null);
            recording = null;
            adjustStreamLabelVisibility(showStreamsToggle.isSelected());
        }
    }
}