/* $Id: GeneralPanel.java,v 1.4 2006-03-30 13:57:10 hampelratte Exp $
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
package lazybones.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.VDRConnection;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class GeneralPanel implements ActionListener {
    private Logger LOG = Logger.getLogger();
    
	private final String lHost = LazyBones.getTranslation("host", "Host");

	private final String lPort = LazyBones.getTranslation("port", "Port");

	private final String lTimeout = LazyBones.getTranslation("timeout", "Timeout");

	private final String lExperts = LazyBones.getTranslation("experts", "Experts");
    
    private final String lWOLEnabled = LazyBones.getTranslation("WOLEnabled", "Enable Wake-on-LAN");
    
    private final String lWOLMac = LazyBones.getTranslation("WOLMac", "Wake-on-LAN Mac-Adress");
    
    private final String lWOLBroadc = LazyBones.getTranslation("WOLBroadc", "Wake-on-LAN Broadcast-Adress");

	private final String lFuzzyness = LazyBones.getTranslation("percentageOfEquality",
			"Fuzzylevel program titles");

	private final String ttFuzzyness = LazyBones.getTranslation(
			"percentageOfEquality.tooltip",
			"Percentage of equality of program titles");

	private LazyBones control;

    private JTextField host;

    private JTextField port;

    private JTextField timeout;
    
    private JTextField tWOLMac;
    
    private JTextField tWOLBroadc;
    
    private JCheckBox cWOLEnabled;

    private JLabel labPercentageOfEquality;
    private JSpinner percentageOfEquality;

    private final String lSupressMatchDialog = LazyBones.getTranslation(
			"supressMatchDialog", "Supress match dialog");

	private final String ttSupressMatchDialog = LazyBones.getTranslation(
			"supressMatchDialog.tooltip",
			"Do not show EPG selection dialog for non matching VDR timer");

	private JCheckBox supressMatchDialog;
    
    private final String lLogConnectionErrors = LazyBones.getTranslation(
            "logConnectionErrors",
            "Show error dialogs on connection problems");
    
    private JCheckBox logConnectionErrors;
    
    private final String lLogEPGErrors = LazyBones.getTranslation(
            "logEPGErrors",
            "Show error dialogs, if EPG data are missing");
    
    private JCheckBox logEPGErrors;
    
    private final String lShowTimerOptionsDialog = LazyBones.getTranslation(
            "showTimerOptionsDialog",
            "Show timer options dialog on timer creation");
    
    private JCheckBox showTimerOptionsDialog;
	

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
        
        cWOLEnabled = new JCheckBox();
        boolean wolEnabled = Boolean.TRUE.toString().equals(
                control.getProperties().getProperty("WOLEnabled"));
        cWOLEnabled.setSelected(wolEnabled);
        cWOLEnabled.addActionListener(this);
        String mac = control.getProperties().getProperty("WOLMac");
        tWOLMac = new JTextField();
        tWOLMac.setText(mac);
        tWOLMac.setEnabled(wolEnabled);
        String broadc = control.getProperties().getProperty("WOLBroadc");
        tWOLBroadc = new JTextField();
        tWOLBroadc.setText(broadc);
        tWOLBroadc.setEnabled(wolEnabled);

        int percentageThreshold = Integer.parseInt(control.getProperties().getProperty(
                "percentageThreshold"));
        percentageOfEquality = new JSpinner();
        percentageOfEquality.setModel(new SpinnerNumberModel(percentageThreshold,0,100,1));
        percentageOfEquality.setToolTipText(ttFuzzyness);
        ((JSpinner.DefaultEditor) percentageOfEquality.getEditor())
                .getTextField().setColumns(2);
        labPercentageOfEquality = new JLabel(lFuzzyness);
        labPercentageOfEquality.setLabelFor(percentageOfEquality);
		labPercentageOfEquality.setToolTipText(ttFuzzyness);

		supressMatchDialog = new JCheckBox();
        supressMatchDialog.setSelected(Boolean.TRUE.toString().equals(
				control.getProperties().getProperty("supressMatchDialog")));
        supressMatchDialog.setToolTipText(ttSupressMatchDialog);
        
        logConnectionErrors = new JCheckBox();
        logConnectionErrors.setSelected(Boolean.TRUE.toString().equals(
                control.getProperties().getProperty("logConnectionErrors")));
        
        logEPGErrors = new JCheckBox();
        logEPGErrors.setSelected(Boolean.TRUE.toString().equals(
                control.getProperties().getProperty("logEPGErrors")));
        
        showTimerOptionsDialog = new JCheckBox();
        showTimerOptionsDialog.setSelected(Boolean.TRUE.toString().equals(
                control.getProperties().getProperty("showTimerOptionsDialog")));
    }

    public JPanel getPanel() {
		FormLayout layout = new FormLayout("left:150dlu, 3dlu, 120dlu",
				"pref, 2dlu, pref, 2dlu, pref, 15dlu, pref, 2dlu, pref," +
                " 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu," +
                " pref, 2dlu, pref, 2dlu, pref");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.addLabel(lHost,              cc.xy (1,  1));
		builder.add(host,                    cc.xyw(3,  1, 1));
		
		builder.addLabel(lPort,              cc.xy (1,  3));
		builder.add(port,                    cc.xyw(3,  3, 1));
		
		builder.addLabel(lTimeout,           cc.xy (1,  5));
		builder.add(timeout,                 cc.xyw(3,  5, 1));

		builder.addSeparator(lExperts,       cc.xyw(1,  7, 3));
        
        builder.addLabel(lWOLEnabled,        cc.xy (1,  9));
        builder.add(cWOLEnabled,             cc.xy (3,  9));
        
        builder.addLabel(lWOLMac,            cc.xy (1,  11));
        builder.add(tWOLMac,                 cc.xy (3,  11));
        
        builder.addLabel(lWOLBroadc,         cc.xy (1,  13));
        builder.add(tWOLBroadc,              cc.xy (3,  13));
		
		builder.add(labPercentageOfEquality, cc.xy (1,  15));
		builder.add(percentageOfEquality,    cc.xy (3,  15));
		
        builder.addLabel(lSupressMatchDialog,cc.xy (1, 17));
		builder.add(supressMatchDialog,      cc.xyw(3, 17, 1));
        
        builder.addLabel(lLogConnectionErrors,   cc.xy (1, 19));
        builder.add(logConnectionErrors,         cc.xyw(3, 19, 1));
        
        builder.addLabel(lLogEPGErrors,          cc.xy (1, 21));
        builder.add(logEPGErrors,                cc.xyw(3, 21, 1));
        
        builder.addLabel(lShowTimerOptionsDialog,cc.xy (1, 23));
        builder.add(showTimerOptionsDialog,      cc.xyw(3, 23, 1));

		return builder.getPanel();
	}

    public void saveSettings() {
        String h = host.getText();
        int p = 2001;
        int t = 500;
        try {
            p = Integer.parseInt(port.getText());
        } catch (NumberFormatException nfe) {
            String mesg = LazyBones.getTranslation(
                   "invalidPort",
                   "<html>You have entered a wrong value for the port.<br>Port 2001 will be used instead.</html>");
            LOG.log(mesg, Logger.OTHER, Logger.ERROR);
            p = 2001;
            port.setText("2001");
        }
        try {
            t = Integer.parseInt(timeout.getText());
        } catch (NumberFormatException nfe) {
            String mesg = LazyBones.getTranslation(
                                            "invalidTimeout",
                                            "<html>You have entered a wrong value for the timeout.<br>A timeout of 500 ms will be used instead.</html>");
            LOG.log(mesg, Logger.OTHER, Logger.ERROR);
            t = 500;
            port.setText("500");
        }

        VDRConnection.host = h;
        VDRConnection.port = p;
        VDRConnection.timeout = t;

        control.getProperties().setProperty("host", h);
        control.getProperties().setProperty("port", Integer.toString(p));
        control.getProperties().setProperty("timeout", Integer.toString(t));
        control.getProperties().setProperty("WOLMac", tWOLMac.getText());
        control.getProperties().setProperty("WOLBroadc", tWOLBroadc.getText());
        control.getProperties().setProperty("percentageThreshold",
                percentageOfEquality.getValue().toString());
        control.getProperties().setProperty("supressMatchDialog",
				"" + supressMatchDialog.isSelected());
        control.getProperties().setProperty("logConnectionErrors",
                "" + logConnectionErrors.isSelected());
        control.getProperties().setProperty("logEPGErrors",
                "" + logEPGErrors.isSelected());
        control.getProperties().setProperty("showTimerOptionsDialog",
                "" + showTimerOptionsDialog.isSelected());
        control.getProperties().setProperty("WOLEnabled",
                "" + cWOLEnabled.isSelected());
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == cWOLEnabled) {
            tWOLBroadc.setEnabled(cWOLEnabled.isSelected());
            tWOLMac.setEnabled(cWOLEnabled.isSelected());
        }
    }
}