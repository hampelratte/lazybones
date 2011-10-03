/* $Id: RemoteControl.java,v 1.7 2011-04-20 12:09:11 hampelratte Exp $
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
package lazybones.gui.components.remotecontrol;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import lazybones.LazyBones;
import lazybones.Player;
import lazybones.VDRConnection;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.CHAN;
import org.hampelratte.svdrp.commands.VOLU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class RemoteControl extends JPanel implements ActionListener {
    private static transient Logger logger = LoggerFactory.getLogger(RemoteControl.class);

    private NumberBlock numBlock;

    private NavigationBlock navBlock;

    private VolumeBlock volBlock;

    private ColorButtonBlock colorButtonBlock;

    private JButton watch = new JButton(LazyBones.getTranslation("watch", "Watch"));

    public RemoteControl() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new GridBagLayout());

        numBlock = new NumberBlock();
        this.add(numBlock, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5),
                0, 0));
        volBlock = new VolumeBlock();
        this.add(volBlock, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5),
                0, 0));
        navBlock = new NavigationBlock();
        this.add(navBlock, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5),
                0, 0));
        colorButtonBlock = new ColorButtonBlock();
        this.add(colorButtonBlock, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5,
                10, 5), 0, 0));

        watch.addActionListener(this);
        watch.setIcon(LazyBones.getInstance().createImageIcon("action", "media-playback-start", 16));
        this.add(watch,
                new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 5, 5), 0, 0));
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == watch) {
            Response res = VDRConnection.sendClient(new CHAN());
            if (res != null && res.getCode() == 250) {
                int chan = Integer.parseInt(res.getMessage().split(" ")[0]);
                Player.play(chan);
            }
        }
    }

    public void updateVolume() {
        logger.info("Updating volume slider");
        Response res = VDRConnection.sendClient(new VOLU(""));
        if (res != null && res.getCode() == 250) {
            String[] words = res.getMessage().trim().split(" ");
            String volString = words[words.length - 1];
            int volu = Integer.parseInt(volString);
            logger.info("Volume is {}", volu);
            volBlock.setVolume(volu);
        }
    }
}