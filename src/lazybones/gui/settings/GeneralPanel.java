/* $Id: GeneralPanel.java,v 1.9 2011-01-18 13:13:54 hampelratte Exp $
 * 
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
package lazybones.gui.settings;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import lazybones.LazyBones;
import lazybones.VDRConnection;
import lazybones.gui.DebugConsole;

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
public class GeneralPanel extends JPanel {
    private JLabel lHost;
    private JTextField host;
    private JSpinner port;
    private JCheckBox cbLoadRecordInfo;
    private JLabel lLoadRecordInfo;
    private JPanel mainPanel;
    private JButton bShowLog;
    private JCheckBox showTimerOptions;
    private JLabel lShowTimerOptions;
    private JCheckBox logEpgErr;
    private JLabel lLogEpgErr;
    private JLabel lLogConnectionErr;
    private JCheckBox logConnectionErr;
    private JLabel lSupressMatchDialog;
    private JCheckBox supressMatchDialog;
    private JSpinner percentage;
    private JLabel lFuzzyness;
    private JPanel expertsPanel;
    private JLabel lCharset;
    private JComboBox charset;
    private JLabel lTimeout;
    private JSpinner timeout;
    private JLabel lPort;

    public GeneralPanel() {
        initGUI();
    }
    
    private void initGUI() {
        try {
            {
                FlowLayout thisLayout = new FlowLayout();
                thisLayout.setAlignment(FlowLayout.LEFT);
                this.setLayout(thisLayout);
                this.setPreferredSize(new java.awt.Dimension(517, 343));
                {
                    mainPanel = new JPanel();
                    GridBagLayout mainPanelLayout = new GridBagLayout();
                    mainPanelLayout.columnWidths = new int[] {7, 7};
                    mainPanelLayout.rowHeights = new int[] {7, 7, 7, 7};
                    mainPanelLayout.columnWeights = new double[] {0.1, 0.1};
                    mainPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
                    this.add(mainPanel);
                    mainPanel.setLayout(mainPanelLayout);
                }
                {
                    lHost = new JLabel();
                    lHost.setText(LazyBones.getTranslation("host", "Host"));
                    mainPanel.add(lHost, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                }
                {
                    expertsPanel = new JPanel();
                    GridBagLayout expertsPanelLayout = new GridBagLayout();
                    expertsPanel.setLayout(expertsPanelLayout);
                    expertsPanel.setBorder(BorderFactory.createTitledBorder(LazyBones.getTranslation("experts", "Experts")));
                    {
                        lFuzzyness = new JLabel();
                        lFuzzyness.setText(LazyBones.getTranslation("percentageOfEquality", "Fuzzylevel program titles"));
                    }
                    {
                        logConnectionErr = new JCheckBox();
                        expertsPanel.add(logConnectionErr, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        logConnectionErr.setSelected(Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("logConnectionErrors")));
                    }
                    {
                        lLogConnectionErr = new JLabel(LazyBones.getTranslation("logConnectionErrors", "Show error dialogs on connection problems"));
                        lLogConnectionErr.setText(LazyBones.getTranslation("logConnectionErrors", "Show error dialogs on connection problems"));
                    }
                    {
                        int percentageThreshold = Integer.parseInt(LazyBones.getProperties().getProperty("percentageThreshold"));
                        SpinnerModel percentageModel = new SpinnerNumberModel(percentageThreshold,0,100,1);
                        percentage = new JSpinner();
                        percentage.setModel(percentageModel);
                        percentage.getEditor().setPreferredSize(new java.awt.Dimension(138, 18));
                        percentage.setToolTipText(LazyBones.getTranslation("percentageOfEquality.tooltip", "Percentage of equality of program titles"));
                    }
                    {
                        supressMatchDialog = new JCheckBox();
                        expertsPanel.add(supressMatchDialog, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        expertsPanel.add(percentage, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                        expertsPanel.add(lFuzzyness, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        supressMatchDialog.setToolTipText(LazyBones.getTranslation("supressMatchDialog.tooltip", "Do not show EPG selection dialog for non matching VDR timer"));
                        supressMatchDialog.setSelected(Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("supressMatchDialog")));
                    }
                    {
                        lSupressMatchDialog = new JLabel();
                        expertsPanel.add(lSupressMatchDialog, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        expertsPanel.add(lLogConnectionErr, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        {
                            lLogEpgErr = new JLabel();
                            expertsPanel.add(lLogEpgErr, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                            lLogEpgErr.setText(LazyBones.getTranslation("logEPGErrors", "Show error dialogs, if EPG data are missing"));
                        }
                        {
                            logEpgErr = new JCheckBox();
                            logEpgErr.setSelected(Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("logEPGErrors")));
                            expertsPanel.add(logEpgErr, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        }
                        {
                            lShowTimerOptions = new JLabel();
                            expertsPanel.add(lShowTimerOptions, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                            lShowTimerOptions.setText(LazyBones.getTranslation("showTimerOptionsDialog", "Show timer options dialog on timer creation"));
                        }
                        {
                            showTimerOptions = new JCheckBox();
                            showTimerOptions.setSelected(Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("showTimerOptionsDialog")));
                            expertsPanel.add(showTimerOptions, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        }
                        {
                            bShowLog = new JButton();
                            expertsPanel.add(bShowLog, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                            bShowLog.setText(LazyBones.getTranslation("show_log", "Show log"));
                            bShowLog.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent evt) {
                                    DebugConsole dc = new DebugConsole();
                                    dc.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                                    dc.setVisible(true);
                                }
                            });
                        }
                        {
                            lLoadRecordInfo = new JLabel();
                            expertsPanel.add(lLoadRecordInfo, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                            lLoadRecordInfo.setText(LazyBones.getTranslation("load_recording_information", "Load recording information"));
                        }
                        {
                            cbLoadRecordInfo = new JCheckBox();
                            cbLoadRecordInfo.setSelected(Boolean.TRUE.toString().equals(LazyBones.getProperties().getProperty("loadRecordInfos")));
                            expertsPanel.add(cbLoadRecordInfo, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                        }
                        lSupressMatchDialog.setText(LazyBones.getTranslation("supressMatchDialog", "Supress match dialog"));
                    }
                    expertsPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
                    expertsPanelLayout.rowHeights = new int[] {7, 7, 7, 7, 7, 7, 7};
                    expertsPanelLayout.columnWeights = new double[] {0.1, 0.1};
                    expertsPanelLayout.columnWidths = new int[] {7, 7};
                }
                {
                    ComboBoxModel charsetModel = new DefaultComboBoxModel(new Object[] {"ISO-8859-1", "ISO-8859-15", "UTF-8"});
                    charset = new JComboBox();
                    mainPanel.add(charset, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    charset.setModel(charsetModel);
                    String c = LazyBones.getProperties().getProperty("charset");
                    charset.setSelectedItem(c);
                }
                {
                    lCharset = new JLabel();
                    mainPanel.add(lCharset, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                    lCharset.setText(LazyBones.getTranslation("charset", "Charset"));
                }
                {
                    int value = Integer.parseInt(LazyBones.getProperties().getProperty("timeout"));
                    SpinnerNumberModel timeoutModel = new SpinnerNumberModel(value, 1, 30000, 1);
                    timeout = new JSpinner();
                    mainPanel.add(timeout, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    timeout.setModel(timeoutModel);
                    timeout.setEditor(new JSpinner.NumberEditor(timeout, "#"));
                }
                {
                    lTimeout = new JLabel();
                    mainPanel.add(lTimeout, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                    lTimeout.setText("Timeout");
                }
                {
                    host = new JTextField();
                    host.setText(LazyBones.getProperties().getProperty("host"));
                }
                {
                    int value = Integer.parseInt(LazyBones.getProperties().getProperty("port"));
                    SpinnerNumberModel portModel = new SpinnerNumberModel(value, 1, 65535, 1);
                    port = new JSpinner();
                    mainPanel.add(port, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    port.setModel(portModel);
                    port.setEditor(new JSpinner.NumberEditor(port, "#"));
                }
                {
                    lPort = new JLabel();
                    mainPanel.add(lPort, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
                    mainPanel.add(host, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    mainPanel.add(expertsPanel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    expertsPanel.setSize(10, 10);
                    lPort.setText(LazyBones.getTranslation("port", "Port"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public GeneralPanel getPanel() {
        return this;
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
        LazyBones.getProperties().setProperty("percentageThreshold", percentage.getValue().toString());
        LazyBones.getProperties().setProperty("supressMatchDialog", Boolean.toString(supressMatchDialog.isSelected()));
        LazyBones.getProperties().setProperty("logConnectionErrors", Boolean.toString(logConnectionErr.isSelected()));
        LazyBones.getProperties().setProperty("logEPGErrors", Boolean.toString(logEpgErr.isSelected()));
        LazyBones.getProperties().setProperty("showTimerOptionsDialog", Boolean.toString(showTimerOptions.isSelected()));
        LazyBones.getProperties().setProperty("loadRecordInfos", Boolean.toString(cbLoadRecordInfo.isSelected()));
    }

}