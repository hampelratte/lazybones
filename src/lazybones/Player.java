/* $Id: Player.java,v 1.14 2007-03-17 15:08:30 hampelratte Exp $
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

import java.io.InputStream;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.CHAN;
import org.hampelratte.svdrp.responses.highlevel.Channel;

import devplugin.Program;

/**
 * Starts a player to watch a channel via streamdev
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class Player {
    private static PlayerThread playerThread;
    
    private static Logger LOG = Logger.getLogger();

    public static void play(Program prog) {
    	Object o = ProgramManager.getChannelMapping().get(prog.getChannel().getId());
        if (o != null) {
            Channel chan = (Channel) o;
            int id = chan.getChannelNumber();
            Player.play(id);
        } else {
            LOG.log("Couldn't start Player", Logger.OTHER, Logger.ERROR);
        }
    }

    public static void play(int channel) {
        try {
            if (playerThread != null && playerThread.isRunning()) {
                playerThread.stopThread();
            }

            boolean switchBefore = new Boolean(LazyBones.getProperties()
                    .getProperty("switchBefore")).booleanValue();
            if (switchBefore) {
                Response res = VDRConnection.send(new CHAN(Integer
                        .toString(channel)));
                if (res == null || res.getCode() != 250) {
                    String mesg = LazyBones.getTranslation("Error",
                    "Error") + ": " + res.getMessage();
                    LOG.log(mesg, Logger.OTHER, Logger.ERROR);
                    return;
                }
            }

            String parameters = LazyBones.getProperties().getProperty(
                    "player_params");
            String[] arguments;
            if (parameters.trim().length() > 0) {
                String[] params = parameters.split(" ");
                arguments = new String[params.length + 2];
                System.arraycopy(params, 0, arguments, 1, params.length);
            } else {
                arguments = new String[2];
            }
            arguments[0] = LazyBones.getProperties().getProperty("player");
            String host = LazyBones.getProperties().getProperty("host");
            String streamtype = LazyBones.getProperties().getProperty("streamtype");
            arguments[arguments.length - 1] = "http://" + host + ":3000/" +
                    streamtype + "/" + channel;
            playerThread = new PlayerThread(arguments);
        } catch (Exception e1) {
            String mesg =  LazyBones.getTranslation("Error", "Error")+ ": " + e1;
            LOG.log(mesg, Logger.OTHER, Logger.ERROR);
        }
    }

    public static void stop() {
        if (playerThread != null)
            playerThread.stopThread();
    }

    private static class PlayerThread extends Thread {
        private boolean running = false;

        private String[] playerParams;

        private Process p;

        PlayerThread(String[] playerParams) {
            this.playerParams = playerParams;
            start();
        }

        public void run() {
            running = true;
            Runtime rt = Runtime.getRuntime();
            try {
                p = rt.exec(playerParams);
                new PlayerOutputter(p.getInputStream());
                new PlayerOutputter(p.getErrorStream());
                p.waitFor();
            } catch (Exception e) {
                String mesg = LazyBones.getTranslation("couldnt_start", "Couldn't start player")+ ": " + e;
                LOG.log(mesg, Logger.OTHER, Logger.ERROR);
            }
            running = false;
        }

        public boolean isRunning() {
            return running;
        }

        public void stopThread() {
            if (p != null)
                p.destroy();
        }
    }

    private static class PlayerOutputter extends Thread {
        private InputStream in;

        PlayerOutputter(InputStream in) {
            this.in = in;
            start();
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int length = -1;
            try {
                while ((length = in.read(buffer)) > 0) {
                    LOG.log("PLAYER: " + new String(buffer, 0, length), Logger.OTHER, Logger.DEBUG);
                }
            } catch (Exception e) {
            }
        }
    }
}