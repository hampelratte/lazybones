/* $Id: VDRConnection.java,v 1.11 2006-10-19 20:01:16 hampelratte Exp $
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import tvbrowser.ui.mainframe.MainFrame;
import de.hampelratte.svdrp.Command;
import de.hampelratte.svdrp.Connection;
import de.hampelratte.svdrp.Response;
import de.hampelratte.svdrp.commands.STAT;

/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class VDRConnection {
    
    private static Logger LOG = Logger.getLogger();

    private static Connection connection;

    public static String host;

    public static int port;

    public static int timeout = 500;
    

    public synchronized static Response send(final Command cmd) {
        Response res = null;
        try {
            connection = new Connection(VDRConnection.host, VDRConnection.port, VDRConnection.timeout);
            Connection.DEBUG = false;
            res = connection.send(cmd);
            connection.close();
        } catch (Exception e1) {
            String mesg = LazyBones.getTranslation("couldnt_connect", "Couldn't connect to VDR") + ":\n" + e1.toString();
            LOG.log(mesg, Logger.WAKE_ON_LAN, Logger.ERROR);
            
            boolean wolEnabled = Boolean.TRUE.toString().equals(
                    LazyBones.getProperties().getProperty("WOLEnabled"));
            if(wolEnabled) {
                String wakeOnLan = LazyBones.getTranslation("wakeOnLanQuestion","Do you want to send a Wake-on-Lan packet to {0}", VDRConnection.host);
                int option = JOptionPane.showConfirmDialog(null, mesg+"\n\n"+wakeOnLan,"",JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.OK_OPTION) {
                    LOG.log("Starting WOL-Process",Logger.CONNECTION, Logger.DEBUG);
                    //ConnectionTester.getInstance().startThread();
                    VDRConnection.wakeUpVDR();
                    
                    // TODO WOL timeout als option bereitstellen
                    int timeout = 30; // wait 120 secs for VDR to boot
                    ProgressMonitor pm = new ProgressMonitor(MainFrame.getInstance(), "Warten auf VDR", timeout + " secs remaining", 0, timeout);
                    
                    ConnectionTester ct = new VDRConnection().new ConnectionTester(timeout, pm);
                    new Thread(ct).start();
                    
                    while(ct.isRunning()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {}    
                    }
                    
                    return send(cmd);
                }
            } else {
                LOG.log(mesg, Logger.CONNECTION, Logger.ERROR);
            }
        }
        return res;
    }

    /**
     * @deprecated Please have a look at the class ConnectionTester
     * @return the connection state
     */
    public static boolean isAvailable() {
        try {
            connection = new Connection(VDRConnection.host, VDRConnection.port,
                    VDRConnection.timeout);
            connection.close();
            return true;
        } catch (Exception e1) {
        }
        return false;
    }

    public static Connection getConnection() {
        return connection;
    }
    
    /**
     * Sends a Wake-on-Lan packet to broadcast ipStr and MAC macStr
     * @param macStr The MAC-address of the host, which shall be woken up
     * @param ipStr The broadcast-address of the net of the host
     */
    protected static void wakeUpVDR() {
        final int PORT = 9;
        String macStr = LazyBones.getProperties().getProperty("WOLMac");
        String ipStr = LazyBones.getProperties().getProperty("WOLBroadc");
        
        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            
            LOG.log("Wake-on-LAN packet sent.", Logger.CONNECTION, Logger.INFO);
        }
        catch (Exception e) {
            LOG.log("Couldn't send Wake-on-Lan packet: \n\t" + e, Logger.CONNECTION, Logger.ERROR);
        }
        
    }
    
    /**
     * Creates a byte[] from a MAC-address String like "00:C0:26:20:33:2F"
     * @param macStr like "00:C0:26:20:33:2F"
     * @return a byte array according to the MAC-String
     * @throws IllegalArgumentException
     */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
    
    private class ConnectionTester implements Runnable {

        private int timeout;
        private ProgressMonitor pm;
        private boolean running = true;
        
        ConnectionTester(int timeout, ProgressMonitor pm) {
            this.timeout = timeout;
            this.pm = pm;
        }
        
        public boolean isRunning() {
            return running;
        }

        public void run() {
            running = true;
            for (int i = 0; i < timeout; i++) {
                pm.setProgress(i);
                pm.setNote( (timeout-i) + " Seconds übrig");
                if (pm.isCanceled()) {
                    break;
                }

                try {
                    Connection connection = new Connection(VDRConnection.host,
                            VDRConnection.port, VDRConnection.timeout);
                    Connection.DEBUG = true;
                    Response resp = connection.send(new STAT());
                    if (resp != null) {
                        LOG.log("WOL-Process finished", Logger.CONNECTION,
                                Logger.DEBUG);
                        pm.close();
                        break;
                    }
                    connection.close();
                } catch (Exception e) {
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pm.close();
            running = false;
        }
    }
}