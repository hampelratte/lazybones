/* $Id: PreviewPanel.java,v 1.6 2007-02-16 22:20:25 hampelratte Exp $
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

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.VDRConnection;
import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.GRAB;
import de.hampelratte.svdrp.responses.R216;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class PreviewPanel extends JLabel {

    private static final Logger LOG = Logger.getLogger();

    private ImageIcon image;

    private PreviewGrabber pg;

    public PreviewPanel() {
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
        
        private GRAB grab; 
        
        PreviewGrabber() {
            grab = new GRAB();
            grab.setFormat("jpeg");
            grab.setQuality("80");
        }

        public void run() {
            while (running) {
                try {
                    Thread.sleep(1000);
                    grab.setResolution(getWidth() + " " + getHeight());
                    String method = LazyBones.getProperties().getProperty(
                            "preview.method");
                    if ("HTTP".equals(method)) {
                        image = getHTTPImage();
                    } else if ("SVDRP".equals(method)) {
                        image = getSVDRPImage();
                    }
                    if (image != null) {
                        setIcon(image);
                        setText("");
                    } else {
                        LOG.log("Grabbed image is null", Logger.OTHER, Logger.WARN);
                        setFont(new Font("SansSerif", Font.PLAIN, 24));
                        // TODO bessere ausgabe je nach fehler
                        setText("  " + LazyBones.getTranslation("no_preview","Couldn't load screenshot."));
                        setIcon(null);
                        stopGrabbing();
                    }
                } catch (InterruptedException e) {
                    LOG.log("Problem with grabber thread:", Logger.OTHER, Logger.ERROR);
                    e.printStackTrace();
                } 
            }
        }
        
        private ImageIcon getHTTPImage() {
            LOG.log("Grabbing image over HTTP", Logger.OTHER, Logger.DEBUG);
            grab.setFilename(LazyBones.getProperties()
                    .getProperty("preview.path"));
            ImageIcon preview = null;
            try {
                Response res = VDRConnection.send(grab);
                if (res.getCode() == 250) {
                    URL url = new URL(LazyBones.getProperties().getProperty("preview.url"));
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    InputStream in = con.getInputStream();
                    byte[] buffer = new byte[1024];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int length = -1;
                    while ((length = in.read(buffer)) >= 0) {
                        bos.write(buffer, 0, length);
                    }
                    in.close();
                    con.disconnect();
                    preview = new ImageIcon(bos.toByteArray());
                }
            } catch (Exception e) {
                LOG.log("Couldn't grab image: " + e, Logger.OTHER, Logger.DEBUG);
            }
            return preview;
        }
        
        private ImageIcon getSVDRPImage() {
            LOG.log("Grabbing image over SVDRP", Logger.OTHER, Logger.DEBUG);
            grab.setFilename("-");
            Response res = VDRConnection.send(grab);
            if (res != null && res.getCode() == 216) {
                R216 r216 = (R216)res;
                return r216.getImage();
            } else {
                LOG.log(res, Logger.OTHER, Logger.DEBUG);
                return null;
            }
        }

        public void stopThread() {
            running = false;
        }
    }
}