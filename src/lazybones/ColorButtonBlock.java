/* $Id: ColorButtonBlock.java,v 1.2 2005-08-22 15:07:46 hampelratte Exp $
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
 * Created on 26.03.2005
 *
 */
package lazybones;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 */
public class ColorButtonBlock extends JPanel {

    private static final long serialVersionUID = 3052192662507759492L;

    JButton bRed = new JButton("  ");

    JButton bGreen = new JButton("  ");

    JButton bYellow = new JButton("  ");

    JButton bBlue = new JButton("  ");

    public ColorButtonBlock() {
        initGUI();
    }

    private void initGUI() {

        bRed.setActionCommand("RED");
        bGreen.setActionCommand("GREEN");
        bYellow.setActionCommand("YELLOW");
        bBlue.setActionCommand("BLUE");

        bRed.addActionListener(Controller.getController());
        bGreen.addActionListener(Controller.getController());
        bYellow.addActionListener(Controller.getController());
        bBlue.addActionListener(Controller.getController());

        bRed.setBackground(Color.RED);
        bGreen.setBackground(Color.GREEN);
        bYellow.setBackground(Color.YELLOW);
        bBlue.setBackground(Color.BLUE);

        setLayout(new GridLayout(1, 4, 10, 10));
        add(bRed);
        add(bGreen);
        add(bYellow);
        add(bBlue);
    }
}