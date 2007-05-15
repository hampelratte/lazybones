/* $Id: RecordingManager.java,v 1.2 2007-05-15 19:57:54 hampelratte Exp $
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
import java.util.List;
import java.util.Observable;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.commands.PLAY;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.util.RecordingsParser;

/**
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net</a>
 * 
 * Class to manage all recordings.
 */
public class RecordingManager extends Observable {
    
    private transient static Logger logger = Logger.getLogger();

    private static RecordingManager instance;

    /**
     * Stores all recordings as Recording objects
     */
    private List<Recording> recordings;
   
    private RecordingManager() {
        recordings = new ArrayList<Recording>();
    }

    public synchronized static RecordingManager getInstance() {
        if (instance == null) {
            instance = new RecordingManager();
        }
        return instance;
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

    public void removeAll() {
        recordings.clear();
        setChanged();
        notifyObservers();
    }

    /**
     * @return an List of Recording objects
     */
    public List<Recording> getRecordings() {
        return recordings;
    }
    
    /**
     * Fetches the recording list from vdr
     */
    public void synchronize() {
        Logger.getLogger().log("Getting recordings from VDR", Logger.OTHER, Logger.DEBUG);
        
        // clear recording list
        removeAll();
        
        // fetch current recording list from vdr
        Response res = VDRConnection.send(new LSTR());
        if (res != null && res.getCode() == 250) {
            logger.log("Recordings retrieved from VDR",Logger.OTHER, Logger.INFO);
            String recordingsString = res.getMessage();
            recordings = RecordingsParser.parse(recordingsString);
        } else if (res != null && res.getCode() == 550) {
            // no recordings, do nothing
            logger.log("No recording on VDR",Logger.OTHER, Logger.INFO);
        } else { /* something went wrong */
            logger.log(LazyBones.getTranslation("error_retrieve_recordings",
                "Couldn't retrieve recordings from VDR."), 
                Logger.CONNECTION, Logger.ERROR);
        }
        
        setChanged();
        notifyObservers();
    }

    public void playOnVdr(Recording rec) {
        Response res = VDRConnection.send(new PLAY(rec.getNumber()));
        if(res.getCode() != 250) {
            Logger.getLogger().log(res.getMessage(), Logger.OTHER, Logger.ERROR);
        }
    }
}