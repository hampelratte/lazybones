/* $Id: ScreenshotPanel.java,v 1.5 2008-04-25 11:27:05 hampelratte Exp $
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
package lazybones.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import lazybones.LazyBones;
import lazybones.VDRConnection;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.GRAB;
import org.hampelratte.svdrp.responses.R216;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class ScreenshotPanel extends JLabel {

    private static transient Logger logger = LoggerFactory.getLogger(ScreenshotPanel.class);

    private ImageIcon image;

    private PreviewGrabber pg;

    public ScreenshotPanel() {
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
                        logger.warn("Grabbed image is null");
                        setFont(new Font("SansSerif", Font.PLAIN, 24));
                        setText("  " + LazyBones.getTranslation("no_preview","Couldn't load screenshot."));
                        setIcon(null);
                        stopGrabbing();
                    }
                } catch (InterruptedException e) {
                    logger.error("Problem with grabber thread:", e);
                } 
            }
        }
        
        private ImageIcon getHTTPImage() {
            logger.debug("Grabbing image over HTTP");
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
                logger.debug("Couldn't grab image", e);
            }
            return preview;
        }
        
        private ImageIcon getSVDRPImage() {
            logger.debug("Grabbing image over SVDRP");
            grab.setFilename("-");
            Response res = VDRConnection.send(grab);
            if (res != null && res.getCode() == 216) {
                R216 r216 = (R216)res;
                ImageIcon image = null; 
                try {
                    image = r216.getImage();
                } catch (IOException e) {
                    logger.error("Couldn't grab screen", e);
                }
                return image;
            } else {
                logger.debug(res.toString());
                return null;
            }
        }

        public void stopThread() {
            running = false;
        }
    }
}