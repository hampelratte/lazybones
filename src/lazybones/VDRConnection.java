/* $Id: VDRConnection.java,v 1.20 2008-05-18 19:18:59 hampelratte Exp $
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

import lazybones.actions.responses.ConnectionProblem;
import lazybones.logging.LoggingConstants;

import org.hampelratte.svdrp.Command;
import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class VDRConnection {
    
    private static transient Logger logger = LoggerFactory.getLogger(LoggingConstants.CONNECTION_LOGGER);

    private static Connection connection;

    public static String host;

    public static int port;

    public static int timeout = 500;
    
    public static String charset;
    
    
    /**
     * Sends a SVDRP command to VDR and returns a response object, which represents the vdr response
     * @param cmd The SVDRP command to send
     * @return The SVDRP response or null, if the Command couldn't be sent
     */
    public synchronized static Response send(final Command cmd) {
        Response res = null;
        try {
            connection = new Connection(VDRConnection.host, VDRConnection.port, VDRConnection.timeout, charset);
            logger.trace("-->{}", cmd.getCommand());
            res = connection.send(cmd);
            connection.close();
            logger.trace("<--{}", res.getMessage());
        } catch (Exception e1) {
            res = new ConnectionProblem();
            logger.error(res.getMessage(), e1);
        }
        return res;
    }
  
    /*
     * private class ConnectionTester implements Runnable {
     * 
     * private int timeout; private ProgressMonitor pm; private boolean running =
     * true;
     * 
     * ConnectionTester(int timeout, ProgressMonitor pm) { this.timeout =
     * timeout; this.pm = pm; }
     * 
     * public boolean isRunning() { return running; }
     * 
     * public void run() { running = true; for (int i = 0; i < timeout; i++) {
     * pm.setProgress(i); pm.setNote( (timeout-i) + " Seconds übrig"); if
     * (pm.isCanceled()) { break; }
     * 
     * try { Connection connection = new Connection(VDRConnection.host,
     * VDRConnection.port, VDRConnection.timeout); Connection.DEBUG = true;
     * Response resp = connection.send(new STAT()); if (resp != null) {
     * LOG.log("WOL-Process finished", Logger.CONNECTION, Logger.DEBUG);
     * pm.close(); break; } connection.close(); } catch (Exception e) { }
     * 
     * try { Thread.sleep(1000); } catch (InterruptedException e) {
     * e.printStackTrace(); } } pm.close(); running = false; } }
     */
}