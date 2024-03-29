/*
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
import org.hampelratte.svdrp.parsers.RecordingListParser;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListRecordingsAction extends VDRAction {

    private static Logger logger = LoggerFactory.getLogger(ListRecordingsAction.class);
    private static Logger conLog = LoggerFactory.getLogger(LoggingConstants.CONNECTION_LOGGER);

    private List<Recording> recordings;

    public ListRecordingsAction(VDRCallback<ListRecordingsAction> callback) {
        super(callback);
    }

    @Override
    boolean execute() {
        response = VDRConnection.send(new LSTR());

        boolean success = false;
        if (response != null && response.getCode() == 250) {
            String recordingsString = response.getMessage();
            recordings = RecordingListParser.parse(recordingsString);
            success = true;
        } else if (response != null && response.getCode() == 550) {
            // no recordings, do nothing
        	success = true;
            logger.info("No recording on VDR");
        } else { /* something went wrong */
            conLog.error(LazyBones.getTranslation("error_retrieve_recordings", "Couldn't retrieve recordings from VDR."));
        }
        return success;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    @Override
    public String getDescription() {
        return "Update list of recordings";
    }
}