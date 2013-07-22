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
package lazybones.gui.timers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import lazybones.ChannelManager;
import lazybones.LazyBones;
import lazybones.LazyBonesTimer;
import devplugin.Channel;

public class TimerListCellRenderer extends JPanel implements ListCellRenderer {

    private final JLabel date = new JLabel();
    private final JLabel channel = new JLabel();
    private final JLabel time = new JLabel();
    private final JLabel title = new JLabel();
    private final JLabel recording = new JLabel();

    private final Color background = Color.WHITE;
    private final Color altBackground = new Color(250, 250, 220);
    private final Color inactive = Color.LIGHT_GRAY;

    public TimerListCellRenderer() {
        initGUI();
    }

    private void initGUI() {
        // set foreground color
        time.setForeground(Color.BLACK);
        title.setForeground(Color.BLACK);
        channel.setForeground(Color.BLACK);
        date.setForeground(Color.BLACK);
        recording.setForeground(Color.BLACK);

        Font bold = time.getFont().deriveFont(Font.BOLD);
        time.setFont(bold);
        title.setFont(bold);
        recording.setFont(recording.getFont().deriveFont(9.0f));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(date, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(channel, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(recording, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(time, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        add(title, gbc);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(UIManager.getColor("List.selectionBackground"));
            time.setForeground(UIManager.getColor("List.selectionForeground"));
            title.setForeground(UIManager.getColor("List.selectionForeground"));
            channel.setForeground(UIManager.getColor("List.selectionForeground"));
            date.setForeground(UIManager.getColor("List.selectionForeground"));
            recording.setForeground(UIManager.getColor("List.selectionForeground"));
        } else {
            setBackground(index % 2 == 0 ? background : altBackground);
            time.setForeground(UIManager.getColor("List.foreground"));
            title.setForeground(UIManager.getColor("List.foreground"));
            channel.setForeground(UIManager.getColor("List.foreground"));
            date.setForeground(UIManager.getColor("List.foreground"));
            recording.setForeground(UIManager.getColor("List.foreground"));
        }

        if (value instanceof LazyBonesTimer) {
            LazyBonesTimer timer = (LazyBonesTimer) value;

            if (!timer.isActive()) {
                time.setForeground(inactive);
                title.setForeground(inactive);
                channel.setForeground(inactive);
                date.setForeground(inactive);
                recording.setForeground(inactive);
            }

            DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
            DateFormat tf = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

            date.setText(df.format(timer.getStartTime().getTime()));
            time.setText(tf.format(timer.getStartTime().getTime()));
            title.setText(timer.getDisplayTitle());

            Channel chan = ChannelManager.getInstance().getTvbrowserChannel(timer);
            if (chan != null) {
                channel.setText(chan.getName());
            } else {
                org.hampelratte.svdrp.responses.highlevel.Channel c = ChannelManager.getInstance().getChannelByNumber(timer.getChannelNumber());
                if (c != null) {
                    channel.setText(c.getName());
                }
            }

            if (timer.isRecording()) {
                recording.setIcon(LazyBones.getInstance().getIcon("lazybones/capture.png"));
                recording.setText(LazyBones.getTranslation("tooltip_recording", "Currently recording"));
            } else {
                recording.setIcon(null);
                recording.setText("");
            }

            setEnabled(list.isEnabled());
            date.setEnabled(list.isEnabled());
            channel.setEnabled(list.isEnabled());
            time.setEnabled(list.isEnabled());
            title.setEnabled(list.isEnabled());
            recording.setEnabled(list.isEnabled());

            return this;
        } else {
            return new JLabel(value.toString());
        }
    }
}
