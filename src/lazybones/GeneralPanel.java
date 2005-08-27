/* $Id: GeneralPanel.java,v 1.5 2005-08-27 20:07:57 emsker Exp $
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

import util.ui.Localizer;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class GeneralPanel {
    private static final long serialVersionUID = 4141920557801647729L;

    private static final Localizer mLocalizer = Localizer
            .getLocalizerFor(GeneralPanel.class);

	private final String lHost = mLocalizer.msg("host", "Host");

	private final String lPort = mLocalizer.msg("port", "Port");

	private final String lTimeout = mLocalizer.msg("timeout", "Timeout");

	private final String lExperts = mLocalizer.msg("experts", "Experts");

	private final String lFuzzyness = mLocalizer.msg("percentageOfEquality",
			"Fuzzylevel program titles");

	private final String ttFuzzyness = mLocalizer.msg(
			"percentageOfEquality.tooltip",
			"Percentage of equality of program titles");

	private LazyBones control;

    private JTextField host;

    private JTextField port;

    private JTextField timeout;

    private JLabel labPercentageOfEquality;
    private JSpinner percentageOfEquality;

    public GeneralPanel(LazyBones control) {
        this.control = control;
        initComponents();
    }
    
    private void initComponents() {
        host = new JTextField(10);
        host.setText(control.getProperties().getProperty("host"));
        port = new JTextField(10);
        port.setText(control.getProperties().getProperty("port"));
        timeout = new JTextField(10);
        timeout.setText(control.getProperties().getProperty("timeout"));

        int percentageThreshold = Integer.parseInt(control.getProperties().getProperty(
                "percentageThreshold"));
        percentageOfEquality = new JSpinner();
        percentageOfEquality.setToolTipText(ttFuzzyness);
        ((JSpinner.DefaultEditor) percentageOfEquality.getEditor())
                .getTextField().setColumns(2);
        percentageOfEquality.setValue(new Integer(percentageThreshold));

        labPercentageOfEquality = new JLabel(lFuzzyness);
        labPercentageOfEquality.setLabelFor(percentageOfEquality);
		labPercentageOfEquality.setToolTipText(ttFuzzyness);
    }

    JPanel getPanel() {
		FormLayout layout = new FormLayout(VDRSettingsPanel.FORMBUILDER_DEFAULT_COLUMNS,
				"pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.addLabel(lHost,              cc.xy (1,  1));
		builder.add(host,                    cc.xyw(3,  1, 3));
		
		builder.addLabel(lPort,              cc.xy (1,  3));
		builder.add(port,                    cc.xyw(3,  3, 3));
		
		builder.addLabel(lTimeout,           cc.xy (1,  5));
		builder.add(timeout,                 cc.xyw(3,  5, 3));

		builder.addSeparator(lExperts,       cc.xyw(1,  7, 5));
		
		builder.add(labPercentageOfEquality, cc.xy (1,  9));
		builder.add(percentageOfEquality,    cc.xy (3,  9));

		return builder.getPanel();
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