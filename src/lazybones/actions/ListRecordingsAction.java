/* $Id: ListRecordingsAction.java,v 1.7 2011-01-18 13:13:54 hampelratte Exp $
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
package lazybones.actions;

import java.util.List;

import lazybones.LazyBones;
import lazybones.VDRCallback;
import lazybones.VDRConnection;
import lazybones.logging.LoggingConstants;

import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.util.RecordingsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListRecordingsAction extends VDRAction {

    private static transient Logger logger = LoggerFactory.getLogger(ListRecordingsAction.class);
    private static transient Logger conLog = LoggerFactory.getLogger(LoggingConstants.CONNECTION_LOGGER);
    
    private List<Recording> recordings;
    
    public ListRecordingsAction(VDRCallback callback) {
        super(callback);
    }
    
    boolean execute() {
        response = VDRConnection.send(new LSTR());
        
        if (response != null && response.getCode() == 250) {
            String recordingsString = response.getMessage();
            recordings = RecordingsParser.parse(recordingsString);
            
            // retrieve infos for all recordings
            // this is to slow, instead we load the info on demand
            /*
            for (Iterator iter = recordings.iterator(); iter.hasNext();) {
                Recording rec = (Recording) iter.next();
                response = connection.send(new LSTR(rec.getNumber()));
                if(response != null && response.getCode() == 215) {
                    // workaround for the epg parser, because LSTR does not send an 'e' as entry terminator
                    String[] lines = response.getMessage().split("\n");
                    StringBuffer mesg = new StringBuffer();
                    for (int i = 0; i < lines.length; i++) {
                        if(i == lines.length -1) {
                            mesg.append("e\n");
                        }
                        mesg.append(lines[i]+"\n");
                    }
                    
                    // parse epg information
                    List<EPGEntry> epg = EPGParser.parse(mesg.toString());
                    if(epg.size() > 0) {
                        rec.setEpgInfo(epg.get(0));
                    }
                }
            }
            */
        } else if (response != null && response.getCode() == 550) {
            // no recordings, do nothing
            logger.info("No recording on VDR");
        } else { /* something went wrong */
            conLog.error(LazyBones.getTranslation("error_retrieve_recordings",
                "Couldn't retrieve recordings from VDR."));
        }

        return true;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    @Override
    public String getDescription() {
        return "Update list of recordings";
    }
}