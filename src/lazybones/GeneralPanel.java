/* $Id: GeneralPanel.java,v 1.1 2005-08-22 18:31:20 hampelratte Exp $
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import util.ui.Localizer;

public class GeneralPanel extends JPanel {
    private static final long serialVersionUID = 4141920557801647729L;

    private static final Localizer mLocalizer = Localizer
            .getLocalizerFor(GeneralPanel.class);

    private LazyBones control;

    private JLabel lHost = new JLabel(mLocalizer.msg("host", "Host"));

    private JTextField host;

    private JLabel lPort = new JLabel(mLocalizer.msg("port", "Port"));

    private JTextField port;

    private JLabel lTimeout = new JLabel(mLocalizer.msg("timeout", "Timeout"));

    private JTextField timeout;

    private JLabel lPercentageOfEquality = new JLabel(mLocalizer.msg(
            "percentageOfEquality", "Percentage of equality"));

    private JSpinner percentageOfEquality;

    public GeneralPanel(LazyBones control) {
        this.control = control;
        initGUI();
    }

    private void initGUI() {
        host = new JTextField(20);
        host.setText(control.getProperties().getProperty("host"));
        port = new JTextField(20);
        port.setText(control.getProperties().getProperty("port"));
        timeout = new JTextField(20);
        timeout.setText(control.getProperties().getProperty("timeout"));

        int percentageThreshold = Integer.parseInt(control.getProperties().getProperty(
                "percentageThreshold"));
        percentageOfEquality = new JSpinner();
        ((JSpinner.DefaultEditor) percentageOfEquality.getEditor())
                .getTextField().setColumns(2);
        percentageOfEquality.setValue(new Integer(percentageThreshold));

        setLayout(new GridBagLayout());

        JPanel connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder(mLocalizer
                .msg("connection", "Connection")));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        connectionPanel.add(lHost, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        connectionPanel.add(host, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectionPanel.add(lPort, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        connectionPanel.add(port, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        connectionPanel.add(lTimeout, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        connectionPanel.add(timeout, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(connectionPanel, gbc);
        
        JPanel experts = new JPanel(new GridBagLayout());
        experts.setBorder(BorderFactory.createTitledBorder(mLocalizer
                .msg("experts", "Experts")));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints. WEST;
        gbc.fill = GridBagConstraints.NONE;
        experts.add(lPercentageOfEquality, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        experts.add(percentageOfEquality, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(experts, gbc);
    }

    public void saveSettings() {
        String h = host.getText();
        int p = 2001;
        int t = 500;
        try {
            p = Integer.parseInt(port.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            mLocalizer
                                    .msg(
                                            "invalidPort",
                                            "<html>You have entered a wrong value for the port.<br>Port 2001 will be used instead.</html>"));
            p = 2001;
            port.setText("2001");
        }
        try {
            t = Integer.parseInt(timeout.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            mLocalizer
                                    .msg(
                                            "invalidTimeout",
                                            "<html>You have entered a wrong value for the timeout.<br>A timeout of 500 ms will be used instead.</html>"));
            t = 500;
            port.setText("500");
        }

        VDRConnection.host = h;
        VDRConnection.port = p;
        VDRConnection.timeout = t;

        control.getProperties().setProperty("host", h);
        control.getProperties().setProperty("port", Integer.toString(p));
        control.getProperties().setProperty("timeout", Integer.toString(t));
        control.getProperties().setProperty("percentageThreshold",
                percentageOfEquality.getValue().toString());
    }
}