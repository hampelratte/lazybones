/* $Id: PreviewPanel.java,v 1.4 2005-09-12 17:18:45 hampelratte Exp $
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

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.GRAB;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class PreviewPanel extends JLabel {

    private static final long serialVersionUID = -6728708348263401257L;

    private ImageIcon image = new ImageIcon();

    private PreviewGrabber pg;

    private LazyBones control;

    public PreviewPanel(LazyBones control) {
        this.control = control;
        initGUI();
    }

    private void initGUI() {
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
    }

    public void startGrabbing() {
        pg = new PreviewGrabber();
        pg.start();
    }

    public void stopGrabbing() {
        if (pg != null) {
            pg.stopThread();
        }
    }

    private class PreviewGrabber extends Thread {

        private boolean running = true;

        public void run() {
            GRAB grab = new GRAB(control.getProperties().getProperty(
                    "preview.path"));
            grab.setQuality("80");
            grab.setFormat("jpeg");

            while (running) {
                try {
                    Thread.sleep(1000);
                    int width = getWidth();
                    int height = getHeight();
                    grab.setResolution(width + " " + height);
                    Response res = VDRConnection.send(grab);
                    if (res.getCode() == 250) {
                        URL url = new URL(control.getProperties().getProperty(
                                "preview.url"));
                        HttpURLConnection con = (HttpURLConnection) url
                                .openConnection();
                        InputStream in = con.getInputStream();
                        byte[] buffer = new byte[1024];
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        int length = -1;
                        while ((length = in.read(buffer)) >= 0) {
                            bos.write(buffer, 0, length);
                        }
                        in.close();
                        con.disconnect();
                        image = new ImageIcon(bos.toByteArray());
                        setIcon(image);
                        repaint();
                    }
                } catch (Exception e) {
                    running = false;
                }
            }
        }

        public void stopThread() {
            running = false;
        }
    }
}