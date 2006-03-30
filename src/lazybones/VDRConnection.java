/* $Id: VDRConnection.java,v 1.9 2006-03-30 11:03:35 hampelratte Exp $
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

import de.hampelratte.svdrp.Command;
import de.hampelratte.svdrp.Connection;
import de.hampelratte.svdrp.Response;

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

    public synchronized static Response send(Command cmd) {
        Response res = null;
        try {
            connection = new Connection(VDRConnection.host, VDRConnection.port,
                    VDRConnection.timeout);
            Connection.DEBUG = false;
            res = connection.send(cmd);
            connection.close();
        } catch (Exception e1) {
            // TODO wol senden und connectiontester starten
            String mesg = LazyBones.getTranslation(
                    "couldnt_connect", "Couldn't connect to VDR")
                    + ":\n" + e1.toString();
            LOG.log(mesg, Logger.CONNECTION, Logger.ERROR);
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
    protected static void wakeUpVDR(String macStr, String ipStr) {
        final int PORT = 9;
        
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
}