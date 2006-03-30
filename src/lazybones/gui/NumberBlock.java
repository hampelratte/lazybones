/* $Id: NumberBlock.java,v 1.2 2006-03-30 11:03:37 hampelratte Exp $
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
/*
 * Created on 25.03.2005
 *
 */
package lazybones.gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lazybones.Controller;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 */
public class NumberBlock extends JPanel {

    JButton power = new JButton("Power");
    
    JButton b0 = new JButton("0");

    JButton b1 = new JButton("1");

    JButton b2 = new JButton("2");

    JButton b3 = new JButton("3");

    JButton b4 = new JButton("4");

    JButton b5 = new JButton("5");

    JButton b6 = new JButton("6");

    JButton b7 = new JButton("7");

    JButton b8 = new JButton("8");

    JButton b9 = new JButton("9");

    public NumberBlock() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridLayout(5, 3, 10, 10));

        power.setActionCommand("POWER");
        b0.setActionCommand("0");
        b1.setActionCommand("1");
        b2.setActionCommand("2");
        b3.setActionCommand("3");
        b4.setActionCommand("4");
        b5.setActionCommand("5");
        b6.setActionCommand("6");
        b7.setActionCommand("7");
        b8.setActionCommand("8");
        b9.setActionCommand("9");

        power.addActionListener(Controller.getController());
        b0.addActionListener(Controller.getController());
        b1.addActionListener(Controller.getController());
        b2.addActionListener(Controller.getController());
        b3.addActionListener(Controller.getController());
        b4.addActionListener(Controller.getController());
        b5.addActionListener(Controller.getController());
        b6.addActionListener(Controller.getController());
        b7.addActionListener(Controller.getController());
        b8.addActionListener(Controller.getController());
        b9.addActionListener(Controller.getController());

        add(new JLabel()); // dummy
        add(new JLabel()); // dummy
        add(power);
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
        add(b7);
        add(b8);
        add(b9);
        add(new JLabel()); // dummy
        add(b0);

    }
}
