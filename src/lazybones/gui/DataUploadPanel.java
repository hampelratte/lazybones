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
package lazybones.gui;

import static devplugin.Plugin.getPluginManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.PUTE;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.DVBChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Date;
import devplugin.Program;
import lazybones.ChannelManager;
import lazybones.ChannelManager.ChannelNotFoundException;
import lazybones.VDRConnection;

public class DataUploadPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JList<Channel> list;
	private final DefaultListModel<Channel> model = new DefaultListModel<>();

	private static transient Logger logger = LoggerFactory.getLogger(DataUploadPanel.class);

	public DataUploadPanel() {
		initGUI();
		loadChannels();
	}

	private void loadChannels() {
		for (Entry<String, Channel> entry : ChannelManager.getChannelMapping().entrySet()) {
			String id = entry.getKey();
			Channel chan = entry.getValue();
			devplugin.Channel tvbChan = ChannelManager.getInstance().getTvbrowserChannelById(id);
			if (tvbChan != null) {
				model.addElement(chan);
			}
		}
	}

	private void initGUI() {
		try {

			GridBagLayout thisLayout = new GridBagLayout();
			this.setPreferredSize(new java.awt.Dimension(562, 431));
			thisLayout.rowWeights = new double[] { 0.1, 0.1 };
			thisLayout.rowHeights = new int[] { 7, 7 };
			thisLayout.columnWeights = new double[] { 0.1 };
			thisLayout.columnWidths = new int[] { 7 };
			this.setLayout(thisLayout);
			list = new JList<>();
			this.add(new JScrollPane(list), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
			list.setModel(model);
			var bUploadData = new JButton();
			this.add(bUploadData, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			bUploadData.setText("upload data");
			bUploadData.addActionListener(e -> uploadData());
		} catch (Exception e) {
			logger.error("Couldn't initialize the gui", e);
		}
	}

	private void uploadData() {
		List<Channel> channels = list.getSelectedValuesList();
		for (Channel chan : channels) {
			devplugin.Channel tvbchan = null;
			try {
				tvbchan = ChannelManager.getInstance().getTvbrowserChannel(chan);
				Set<Program> channelProgram = getChannelProgram(tvbchan);
				if (!channelProgram.isEmpty()) {
					pute((DVBChannel) chan, channelProgram);
				}
			} catch (ChannelNotFoundException e) {
				logger.warn("TV-Browser channel not found for channel {}", chan.getName());
			}
		}
	}

	private void pute(DVBChannel chan, Set<Program> channelProgram) {
		// concatenate PUTE data
		// C channel line
		StringBuilder sb = new StringBuilder("C ");
		sb.append(chan.getSource()).append('-');
		sb.append(chan.getNID()).append('-');
		sb.append(chan.getTID()).append('-');
		sb.append(chan.getSID());
		if (chan.getRID() >= 0) {
			sb.append('-').append(chan.getRID());
		}
		sb.append(' ').append(chan.getName()).append("\n");

		// program entries
		for (Program prog : channelProgram) {
			// E entry
			sb.append("E ");
			// event id
			Calendar progStartCal = prog.getDate().getCalendar();
			progStartCal.set(Calendar.HOUR_OF_DAY, prog.getHours());
			progStartCal.set(Calendar.MINUTE, prog.getMinutes());
			progStartCal.set(Calendar.SECOND, 0);
			long eventID = progStartCal.getTimeInMillis() / 60 % 0xFFFF;
			long startTime = progStartCal.getTimeInMillis() / 1000;
			long duration = prog.getLength() * 60L;
			sb.append(eventID).append(' ').append(startTime).append(' ').append(duration).append(" 0\n");

			// T
			sb.append("T ").append(prog.getTitle().replace("\n", "\\|")).append("\n");

			// D, if exists
			if (prog.getDescription() != null && !prog.getDescription().isEmpty()) {
				sb.append("D ").append(prog.getDescription().replace("\n", "\\|")).append("\n");
			}

			sb.append("e\n");
		}

		sb.append("c");

		logger.warn(sb.toString());
		Response res = VDRConnection.send(new PUTE(sb.toString()));
		logger.warn("Response from VDR: {} {}", res.getCode(), res.getMessage());
	}

	private Set<Program> getChannelProgram(devplugin.Channel tvbchan) {
		Set<Program> channelProgramm = new HashSet<>();
		if (tvbchan != null) {
			Date date = new Date();
			Iterator<Program> cdp = getPluginManager().getChannelDayProgram(date, tvbchan);
			while (cdp.hasNext()) { // if it has next, it is not empty.
				// add all programs to the resulting set
				while (cdp.hasNext()) {
					Program prog = cdp.next();
					channelProgramm.add(prog);
				}

				date = date.addDays(1);
				cdp = getPluginManager().getChannelDayProgram(date, tvbchan);
			}
		}

		return channelProgramm;
	}
}
