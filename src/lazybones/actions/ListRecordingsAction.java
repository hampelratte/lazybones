/* $Id: ListRecordingsAction.java,v 1.1 2007-05-27 19:06:01 hampelratte Exp $
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
package lazybones.actions;

import java.util.Iterator;
import java.util.List;

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.VDRConnection;
import lazybones.actions.responses.ConnectionProblem;

import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.util.EPGParser;
import org.hampelratte.svdrp.util.RecordingsParser;

public class ListRecordingsAction implements VDRAction {

    private Logger logger = Logger.getLogger();
    
    private Response res;
    
    private List<Recording> recordings;
    
    public boolean execute() {
        try {
            Connection connection = new Connection(VDRConnection.host, VDRConnection.port, VDRConnection.timeout);
            res = connection.send(new LSTR());
            
            if (res != null && res.getCode() == 250) {
                String recordingsString = res.getMessage();
                recordings = RecordingsParser.parse(recordingsString);
                
                // retrieve infos for all recordings
                for (Iterator iter = recordings.iterator(); iter.hasNext();) {
                    Recording rec = (Recording) iter.next();
                    res = connection.send(new LSTR(rec.getNumber()));
                    if(res != null && res.getCode() == 215) {
                        // workaround for the epg parser, because LSTR does not send an 'e' as entry terminator
                        String[] lines = res.getMessage().split("\n");
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
            } else if (res != null && res.getCode() == 550) {
                // no recordings, do nothing
                logger.log("No recording on VDR",Logger.OTHER, Logger.INFO);
            } else { /* something went wrong */
                logger.log(LazyBones.getTranslation("error_retrieve_recordings",
                    "Couldn't retrieve recordings from VDR."), 
                    Logger.CONNECTION, Logger.ERROR);
            }
            
            connection.close();
        } catch (Exception e1) {
            res = new ConnectionProblem();
            logger.log(res.getMessage(), Logger.CONNECTION, Logger.ERROR);
            return false;
        }   
        return true;
    }

    public Response getResponse() {
        return res;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

}