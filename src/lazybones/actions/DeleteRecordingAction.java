/* $Id: DeleteRecordingAction.java,v 1.2 2007-04-30 13:35:36 hampelratte Exp $
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

import javax.swing.JOptionPane;

import lazybones.LazyBones;
import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.VDRConnection;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.DELR;
import org.hampelratte.svdrp.responses.highlevel.Recording;


public class DeleteRecordingAction implements VDRAction {

    private Recording recording;
    private Response res;
    
    public DeleteRecordingAction(Recording recording) {
        this.recording = recording;
    }
    
    public boolean execute() {
        int recordingNumber = recording.getNumber();
        res = VDRConnection.send(new DELR(recordingNumber));
        if(res.getCode() == 250) {
            return true;
        } else if(res.getCode() == 550 && res.getMessage().indexOf("in use by timer") >= 0) {
            // recording is still running, we have to delete the timer first
            int result = JOptionPane.showConfirmDialog(LazyBones.getInstance().getMainDialog(),
                    LazyBones.getTranslation("recording_running_delete", "Timer is still recording. Do you really want to delete this recording?"), 
                    "", JOptionPane.YES_NO_OPTION);
            if(result != JOptionPane.OK_OPTION) {
                return true;
            } else {
                // delete timer and recording
                String msg = res.getMessage();
                String numberString = msg.substring(msg.lastIndexOf(" "));
                int timerNumber = Integer.parseInt( numberString.trim() );
                Timer timer = TimerManager.getInstance().getTimer(timerNumber);
                DeleteTimerAction dta = new DeleteTimerAction(timer, true);
                if(!dta.execute()) {
                    res = dta.getResponse();
                    return false;
                } else {
                    // timer is deleted, now delete the recording
                    res = VDRConnection.send(new DELR(recordingNumber));
                    if(res.getCode() != 250) {
                        return false;
                    }
                    
                    // update recording list
                    
                }
            }
        }
        
        return true;
    }

    public Response getResponse() {
        return res;
    }
}