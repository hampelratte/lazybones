/* $Id: ChannelManager.java,v 1.1 2007-05-05 20:32:45 hampelratte Exp $
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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTC;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.util.ChannelParser;


public class ChannelManager {

    private static ChannelManager instance;
    
    private static Hashtable channelMapping = new Hashtable();

    private List<Channel> channels = null;
    
    private List<Channel> filteredChannels = new ArrayList<Channel>();

    public void update() {
        Response res = VDRConnection.send(new LSTC());
        if (res != null && res.getCode() == 250) {
            channels = ChannelParser.parse(res.getMessage());
        }
    }

    public static ChannelManager getInstance() {
        if (instance == null) {
            instance = new ChannelManager();
        }
        return instance;
    }
    
    public List<Channel> getFilteredChannels() {
        filteredChannels.clear();
        int min = Integer.parseInt(LazyBones.getProperties().getProperty("minChannelNumber"));
        int max = Integer.parseInt(LazyBones.getProperties().getProperty("maxChannelNumber"));

        for (Iterator iter = channels.iterator(); iter.hasNext();) {
            Channel chan = (Channel) iter.next();
            if(chan.getChannelNumber() >= min && (chan.getChannelNumber() <= max || max == 0 )) {
                filteredChannels.add(chan);
            }
        }
        
        return filteredChannels;
    }
    
    public List<Channel> getChannels() {
        return channels;
    }
    
    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public Channel getChannelByNumber(int channelNumber) {
        if(channels == null) {
            return null;
        }
        
        for (Iterator<Channel> iter = channels.iterator(); iter.hasNext();) {
            Channel chan = iter.next();
            if(chan.getChannelNumber() == channelNumber) {
                return chan;
            }
        }
        
        return null;
    }
    
    public static Hashtable getChannelMapping() {
        return channelMapping;
    }

    public static void setChannelMapping(Hashtable channelMapping) {
        ChannelManager.channelMapping = channelMapping;
    }
    
    public devplugin.Channel getChannel(Timer timer) {
        devplugin.Channel chan = null;
        Enumeration en = ChannelManager.getChannelMapping().keys();
        while (en.hasMoreElements()) {
            String channelID = (String) en.nextElement();
            Channel channel = (Channel) ChannelManager.getChannelMapping().get(channelID);
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
}