/* $Id: NavigationBlock.java,v 1.2 2006-03-06 20:42:02 hampelratte Exp $
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
import lazybones.LazyBones;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class NavigationBlock extends JPanel {
    private static final long serialVersionUID = 7226115547859845252L;

    JButton bMenu = new JButton(LazyBones.getTranslation("Menu", "Menu"));

    JButton bBack = new JButton(LazyBones.getTranslation("Back", "Back"));

    JButton bUp = new JButton(LazyBones.getTranslation("Up", "Up"));

    JButton bDown = new JButton(LazyBones.getTranslation("Down", "Down"));

    JButton bLeft = new JButton(LazyBones.getTranslation("Left", "Left"));

    JButton bRight = new JButton(LazyBones.getTranslation("Right", "Right"));

    JButton bOk = new JButton(LazyBones.getTranslation("OK", "OK"));

    public NavigationBlock() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridLayout(3, 3, 10, 10));

        bMenu.setActionCommand("MENU");
        bBack.setActionCommand("BACK");
        bUp.setActionCommand("UP");
        bDown.setActionCommand("DOWN");
        bLeft.setActionCommand("LEFT");
        bRight.setActionCommand("RIGHT");
        bOk.setActionCommand("OK");

        bMenu.addActionListener(Controller.getController());
        bBack.addActionListener(Controller.getController());
        bUp.addActionListener(Controller.getController());
        bDown.addActionListener(Controller.getController());
        bLeft.addActionListener(Controller.getController());
        bRight.addActionListener(Controller.getController());
        bOk.addActionListener(Controller.getController());

        add(bBack);
        add(bUp);
        add(bMenu);
        add(bLeft);
        add(bOk);
        add(bRight);
        add(new JLabel());
        add(bDown);
        add(new JLabel());
    }
}