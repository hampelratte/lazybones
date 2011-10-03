/* $Id: GeneralPanel.java,v 1.10 2011-04-20 12:09:13 hampelratte Exp $
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
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please
 * visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS
 * MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ClientPanel extends JPanel {
    private JPanel mainPanel;
    private JLabel lClientHost;
    private JTextField clientHost;
    private JLabel lClientPort;
    private JSpinner clientPort;
    private JLabel lClientUserId;
    private JComboBox clientUserId;

    public ClientPanel() {
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
                    mainPanelLayout.columnWidths = new int[] { 7, 7 };
                    mainPanelLayout.rowHeights = new int[] { 7, 7, 7, 7 };
                    mainPanelLayout.columnWeights = new double[] { 0.1, 0.1 };
                    mainPanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
                    this.add(mainPanel);
                    mainPanel.setLayout(mainPanelLayout);
                }
                {
                    lClientHost = new JLabel();
                    lClientHost.setText(LazyBones.getTranslation("host", "Host"));
                    mainPanel.add(lClientHost, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),
                            0, 0));
                }
                {
                    ComboBoxModel charsetModel = new DefaultComboBoxModel(new Object[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" });
                    clientUserId = new JComboBox();
                    mainPanel.add(clientUserId, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,
                            5, 5, 5), 0, 0));
                    clientUserId.setModel(charsetModel);
                    String c = LazyBones.getProperties().getProperty("clientUserId");
                    clientUserId.setSelectedItem(c);
                }
                {
                    lClientUserId = new JLabel();
                    mainPanel.add(lClientUserId, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5,
                            5), 0, 0));
                    lClientUserId.setText(LazyBones.getTranslation("clientuserid", "ClientUserId"));
                }
                {
                    clientHost = new JTextField();
                    clientHost.setText(LazyBones.getProperties().getProperty("clientHost"));
                }
                {
                    int value = Integer.parseInt(LazyBones.getProperties().getProperty("clientPort"));
                    SpinnerNumberModel portModel = new SpinnerNumberModel(value, 1, 65535, 1);
                    clientPort = new JSpinner();
                    mainPanel.add(clientPort, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,
                            5, 5), 0, 0));
                    clientPort.setModel(portModel);
                    clientPort.setEditor(new JSpinner.NumberEditor(clientPort, "#"));
                }
                {
                    lClientPort = new JLabel();
                    mainPanel.add(lClientPort, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5),
                            0, 0));
                    mainPanel.add(clientHost, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,
                            5, 5), 0, 0));
                    lClientPort.setText(LazyBones.getTranslation("clientPort", "Port"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientPanel getPanel() {
        return this;
    }

    public void saveSettings() {
        int p = (Integer) clientPort.getValue();

        String h = clientHost.getText();
        String u = clientUserId.getSelectedItem().toString();
        VDRConnection.clientHost = h;
        VDRConnection.clientPort = p;
        VDRConnection.clientUserId = u;

        LazyBones.getProperties().setProperty("clientHost", h);
        LazyBones.getProperties().setProperty("clientPort", Integer.toString(p));
        LazyBones.getProperties().setProperty("clientUserId", u);
    }

}
