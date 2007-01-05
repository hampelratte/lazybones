/* $Id: ProgramManager.java,v 1.7 2007-01-05 23:11:26 hampelratte Exp $
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

import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import de.hampelratte.svdrp.responses.highlevel.Channel;
import devplugin.Date;
import devplugin.Program;

public class ProgramManager {
    private static ProgramManager instance;
    
    private static Hashtable channelMapping = new Hashtable();
    
    private ProgramManager() {
    }

    public static ProgramManager getInstance() {
        if (instance == null) {
            instance = new ProgramManager();
        }
        return instance;
    }
    
    /**
     * @param startTime the startTime of the Program
     * @param middleTime the middleTime of the Program
     * @param chan the channel of the Program
     * @return the Program or null
     * 
     * startTime ist notwendig, weil getChannelDayProgram benutzt wird.
     * Bsp.: start 23:30 ende 01:00 middleTime würde dann schon am nächsten tag
     * liegen (00:15), so dass man nicht mehr das richtige channelDayProgram bekommt
     * und das Program nicht findet 
     */
    public Program getProgramAt(Calendar startTime, Calendar middleTime, devplugin.Channel chan) {
        Iterator dayProgram = LazyBones.getPluginManager().getChannelDayProgram(
                new Date(startTime), chan);
        while (dayProgram != null && dayProgram.hasNext()) {
            Program prog = (Program) dayProgram.next();
            
            Calendar progStart = GregorianCalendar.getInstance();
            progStart.set(Calendar.YEAR, prog.getDate().getYear());
            progStart.set(Calendar.MONTH, prog.getDate().getMonth()-1);
            progStart.set(Calendar.DAY_OF_MONTH, prog.getDate().getDayOfMonth());
            progStart.set(Calendar.HOUR_OF_DAY, prog.getHours());
            progStart.set(Calendar.MINUTE, prog.getMinutes());

            Calendar progEnd = GregorianCalendar.getInstance();
            progEnd.setTimeInMillis(progStart.getTimeInMillis());
            progEnd.add(Calendar.MINUTE, prog.getLength());
            
            if (middleTime.after(progStart) && middleTime.before(progEnd)) {
                return prog;
            }
        }
        return null;
    }
    
    public Program getProgram(Timer timer) {
        // determine channel
        devplugin.Channel chan = getChannel(timer);
        
        if(chan == null) 
            return null;
            
        // determine middle of the program
        long startTime = timer.getStartTime().getTimeInMillis();
        long endTime = timer.getEndTime().getTimeInMillis();
        long duration = endTime - startTime;
        Calendar time = GregorianCalendar.getInstance();
        long middleTime = startTime + duration/2;
        time.setTimeInMillis(middleTime);
        
        return getProgramAt(timer.getStartTime(), time, chan);
    }
    
    public Program getProgram(Calendar time, String progID) {
        return LazyBones.getPluginManager().getProgram(new devplugin.Date(time), progID);
    }
    
    public devplugin.Channel getChannel(Timer timer) {
        devplugin.Channel chan = null;
        Enumeration en = ProgramManager.getChannelMapping().keys();
        while (en.hasMoreElements()) {
            String channelID = (String) en.nextElement();
            Channel channel = (Channel) ProgramManager.getChannelMapping().get(channelID);
            if (channel.getChannelNumber() == timer.getChannelNumber()) {
                chan = getChannelById(channelID);
            }
        }
        return chan;
    }

    public devplugin.Channel getChannelById(String id) {
        devplugin.Channel[] channels = LazyBones.getPluginManager().getSubscribedChannels();
        for (int i = 0; i < channels.length; i++) {
            if (channels[i].getId().equals(id)) {
                return channels[i];
            }
        }
        return null;
    }

    public static Hashtable getChannelMapping() {
        return channelMapping;
    }

    public static void setChannelMapping(Hashtable channelMapping) {
        ProgramManager.channelMapping = channelMapping;
    }
}
