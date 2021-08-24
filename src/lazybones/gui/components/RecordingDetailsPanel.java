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

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.Stream;
import org.hampelratte.svdrp.responses.highlevel.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lazybones.LazyBones;
import lazybones.VDRCallback;
import lazybones.actions.GetRecordingDetailsAction;
import lazybones.utils.Period;

public class RecordingDetailsPanel extends AbstractDetailsPanel implements TreeSelectionListener {

    private static final long serialVersionUID = 1L;

    private static transient Logger logger = LoggerFactory.getLogger(RecordingDetailsPanel.class);

    private Recording recording;

    private String warning;

    public RecordingDetailsPanel(Recording recording) {
        super("recording_details.html");
        this.recording = recording;
        updateHtmlPane();
    }

    public RecordingDetailsPanel() {
        this(null);
    }

    public void setRecording(Recording recording) {
        this.recording = recording;
        warning = null;
        updateHtmlPane();
    }

    @Override
    public String replaceTags(String template) {
        if (recording == null) {
            return "";
        }

        template = template.replace("{title}", recording.getDisplayTitle());

        String channel = recording.getChannelName();
        template = template.replace("{channel}", channel);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        template = template.replace("{startDate}", dateFormat.format(recording.getStartTime().getTime()));
        template = template.replace("{startTime}", timeFormat.format(recording.getStartTime().getTime()));
        template = template.replace("{endTime}", timeFormat.format(recording.getEndTime().getTime()));
        template = template.replace("{shortText}", recording.getShortText());
        template = template.replace("{description}", recording.getDescription().replace("\n", "<br>"));
        template = template.replace("{status}", createStatusString());
        template = template.replace("{directory}", recording.getFolder());
        template = template.replace("{lifetime}", Integer.toString(recording.getLifetime()));
        template = template.replace("{priority}", Integer.toString(recording.getPriority()));
        template = template.replace("{streams}", createStreamsString());
        if(warning != null) {
            template = template.replace("{warning}", "<h1 class=\"warning\">"+warning+"</h1>");
        } else {
            template = template.replace("{warning}", "");
        }
        return template;
    }

    private String createStatusString() {
        StringBuilder sb = new StringBuilder();
        sb.append(recording.isCut() ? LazyBones.getTranslation("cut", "cut") : LazyBones.getTranslation("uncut", "uncut"));

        if (recording.isNew()) {
            sb.insert(0, ' ');
            sb.insert(0, LazyBones.getTranslation("and", "and"));
            sb.insert(0, ' ');
            sb.insert(0, LazyBones.getTranslation("new", "new"));
        }
        return sb.toString().trim();
    }

    private String createStreamsString() {
        if (!recording.getStreams().isEmpty()) {
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
				GetRecordingDetailsAction grda = new GetRecordingDetailsAction(rec, createCallback(e, selected, rec));
				grda.enqueue();
			} else {
				reset();
			}
		} else {
			reset();
		}
	}
    
    VDRCallback<GetRecordingDetailsAction> createCallback(TreeSelectionEvent e, TreePath selected, Recording rec) {
    	return (cmd, response) -> {
			if (cmd.isSuccess()) {
				setRecording(cmd.getRecording());
			} else {
				reset();
				String mesg = LazyBones.getTranslation("error_retrieve_recording_details",
						"Couldn't load recording details from VDR: {0}", response.getMessage());
				logger.error(mesg);
			}

			SwingUtilities.invokeLater(() -> {
				((JTree) e.getSource()).getModel().valueForPathChanged(selected, rec);

				if (recording.getDuration() != -1) {
					long duration = recording.getDuration();
					Period programDuration = new Period(recording.getStartTime(), recording.getEndTime());
					long plannedDuration = TimeUnit.SECONDS.toMinutes(programDuration.getDurationInSeconds());
					if (duration < plannedDuration) {
						logger.debug("Recording seems to be too short {} - {}", duration, plannedDuration);
						warning = LazyBones.getTranslation("recording_too_short",
								"The recording duration ({0}min) is shorter than the program duration ({1}min)",
								Long.toString(duration), Long.toString(plannedDuration));
						updateHtmlPane();
					}
				}
			});
		};
    }
}