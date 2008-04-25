/* $Id: GeneralPanel.java,v 1.5 2008-04-25 16:54:38 hampelratte Exp $
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
package lazybones.gui.settings;

import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import lazybones.LazyBones;
import lazybones.VDRConnection;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class GeneralPanel {
	private final String lHost = LazyBones.getTranslation("host", "Host");

	private final String lPort = LazyBones.getTranslation("port", "Port");

	private final String lTimeout = LazyBones.getTranslation("timeout", "Timeout");
	
	private final String lCharset = LazyBones.getTranslation("charset", "Charset");

	private final String lExperts = LazyBones.getTranslation("experts", "Experts");

    private final String lFuzzyness = LazyBones.getTranslation("percentageOfEquality",
			"Fuzzylevel program titles");

	private final String ttFuzzyness = LazyBones.getTranslation(
			"percentageOfEquality.tooltip",
			"Percentage of equality of program titles");

    private JTextField host;

    private JComboBox charset;
    
    private JLabel labPercentageOfEquality;
    private JSpinner percentageOfEquality;

    private final String lSupressMatchDialog = LazyBones.getTranslation(
			"supressMatchDialog", "Supress match dialog");
    private JSpinner port;
    private JSpinner timeout;

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
	

	public GeneralPanel() {
        initComponents();
    }
    
    private void initComponents() {
        host = new JTextField(10);
        host.setText(LazyBones.getProperties().getProperty("host"));
        int value = Integer.parseInt(LazyBones.getProperties().getProperty("port"));
        port = new JSpinner(new SpinnerNumberModel(value, 1, 65535, 1));
        port.setEditor(new JSpinner.NumberEditor(port, "#"));
        value = Integer.parseInt(LazyBones.getProperties().getProperty("timeout"));
        timeout = new JSpinner(new SpinnerNumberModel(value, 1, 30000, 1));
        timeout.setEditor(new JSpinner.NumberEditor(timeout, "#"));
        
        int percentageThreshold = Integer.parseInt(LazyBones.getProperties().getProperty(
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
				LazyBones.getProperties().getProperty("supressMatchDialog")));
        supressMatchDialog.setToolTipText(ttSupressMatchDialog);
        
        logConnectionErrors = new JCheckBox();
        logConnectionErrors.setSelected(Boolean.TRUE.toString().equals(
                LazyBones.getProperties().getProperty("logConnectionErrors")));
        
        logEPGErrors = new JCheckBox();
        logEPGErrors.setSelected(Boolean.TRUE.toString().equals(
                LazyBones.getProperties().getProperty("logEPGErrors")));
        
        showTimerOptionsDialog = new JCheckBox();
        showTimerOptionsDialog.setSelected(Boolean.TRUE.toString().equals(
                LazyBones.getProperties().getProperty("showTimerOptionsDialog")));
        
        charset = new JComboBox();
        ComboBoxModel model = new DefaultComboBoxModel(new Object[] {"ISO-8859-1", "ISO-8859-15", "UTF-8"});
        charset.setModel(model);
        String c = LazyBones.getProperties().getProperty("charset");
        charset.setSelectedItem(c);
    }

    public JPanel getPanel() {
        final double P = TableLayout.PREFERRED;
        double[][] size = {{10, P, 10, P, 10},
                           {10, P, 10, P, 10, P, 10, P, 20, P, 10}};
        TableLayout surround = new TableLayout(size);
		JPanel panel = new JPanel(surround);

		panel.add(new JLabel(lHost), "1,1,1,1");
		panel.add(host,              "3,1,3,1");
		
		panel.add(new JLabel(lPort), "1,3,1,3");

		panel.add(new JLabel(lTimeout), "1,5,1,5");

		panel.add(new JLabel(lCharset), "1,7,1,7");
        panel.add(charset,              "3,7,3,7");

        double[][] size2 = {{10, P, 10, P, 10},
                            {10, P, 10, P, 10, P, 10, P, 10, P, 10, P, 10, P, 10, P, 10}};
        TableLayout layout2 = new TableLayout(size2);
        JPanel experts = new JPanel(layout2);
        experts.setBorder(BorderFactory.createTitledBorder(lExperts));

        experts.add(labPercentageOfEquality, "1,1,1,1");
        experts.add(percentageOfEquality,    "3,1,3,1");
		
        experts.add(new JLabel(lSupressMatchDialog),     "1,3,1,3");
        experts.add(supressMatchDialog,                  "3,3,3,3");
        
        experts.add(new JLabel(lLogConnectionErrors),    "1,5,1,5");
        experts.add(logConnectionErrors,                 "3,5,3,5");
        
        experts.add(new JLabel(lLogEPGErrors),           "1,7,1,7");
        experts.add(logEPGErrors,                        "3,7,3,7");
        
        experts.add(new JLabel(lShowTimerOptionsDialog), "1,9,1,9");
        experts.add(showTimerOptionsDialog,              "3,9,3,9");

        panel.add(experts, "1,9,3,9");
        panel.add(port, "3, 3");
        panel.add(timeout, "3, 5");
		return panel;
	}

    public void saveSettings() {
        int p = (Integer) port.getValue();
        int t = (Integer) timeout.getValue();

        String h = host.getText();
        String c = charset.getSelectedItem().toString();
        VDRConnection.host = h;
        VDRConnection.port = p;
        VDRConnection.timeout = t;
        VDRConnection.charset = c;

        LazyBones.getProperties().setProperty("host", h);
        LazyBones.getProperties().setProperty("port", Integer.toString(p));
        LazyBones.getProperties().setProperty("timeout", Integer.toString(t));
        LazyBones.getProperties().setProperty("charset", c);
        LazyBones.getProperties().setProperty("percentageThreshold", percentageOfEquality.getValue().toString());
        LazyBones.getProperties().setProperty("supressMatchDialog", Boolean.toString(supressMatchDialog.isSelected()));
        LazyBones.getProperties().setProperty("logConnectionErrors", Boolean.toString(logConnectionErrors.isSelected()));
        LazyBones.getProperties().setProperty("logEPGErrors", Boolean.toString(logEPGErrors.isSelected()));
        LazyBones.getProperties().setProperty("showTimerOptionsDialog", Boolean.toString(showTimerOptionsDialog.isSelected()));
    }
}