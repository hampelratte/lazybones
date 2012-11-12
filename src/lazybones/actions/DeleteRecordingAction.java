/*
 * Copyright (c) Henrik Niehaus
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
import lazybones.LazyBonesTimer;
import lazybones.RecordingManager;
import lazybones.TimerManager;
import lazybones.VDRCallback;
import lazybones.VDRConnection;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.DELR;
import org.hampelratte.svdrp.responses.highlevel.Recording;

public class DeleteRecordingAction extends VDRAction {

    private final Recording recording;

    public DeleteRecordingAction(Recording recording) {
        this(recording, null);
    }

    public DeleteRecordingAction(Recording recording, VDRCallback<?> callback) {
        super(callback);
        this.recording = recording;
    }

    @Override
    boolean execute() {
        int result = JOptionPane.showConfirmDialog(LazyBones.getInstance().getMainDialog(),
                LazyBones.getTranslation("recording_delete", "Do you really want to delete the recording {0}?", recording.getDisplayTitle()), "",
                JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return true;
        }

        int recordingNumber = recording.getNumber();
        response = VDRConnection.send(new DELR(recordingNumber));
        if (response.getCode() == 250) {
            return true;
        } else if (response.getCode() == 550 && response.getMessage().indexOf("in use by timer") >= 0) {
            // recording is still running, we have to delete the timer first
            result = JOptionPane.showConfirmDialog(LazyBones.getInstance().getMainDialog(),
                    LazyBones.getTranslation("recording_running_delete", "Timer is still recording. Do you really want to delete this recording?"), "",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return true;
            } else {
                // delete timer and recording
                String msg = response.getMessage();
                String numberString = msg.substring(msg.lastIndexOf(" "));
                int timerNumber = Integer.parseInt(numberString.trim());
                LazyBonesTimer timer = TimerManager.getInstance().getTimer(timerNumber);

                VDRCallback<DeleteTimerAction> callback = new VDRCallback<DeleteTimerAction>() {
                    @Override
                    public void receiveResponse(DeleteTimerAction cmd, Response response) {
                        /*
                         * The DeleteTimerAction finished, we can now check again, if we can delete the recording
                         */

                        if (!cmd.isSuccess()) {
                            response = cmd.getResponse();
                            success = false;
                            callback();
                            return;
                        } else {
                            // timer is deleted, now delete the recording
                            response = VDRConnection.send(new DELR(recording.getNumber()));
                            if (response.getCode() != 250) {
                                success = false;
                                callback();
                                return;
                            }
                        }

                        success = true;
                        TimerManager.getInstance().synchronize();
                        callback();
                    }
                };
                DeleteTimerAction dta = new DeleteTimerAction(timer, true);
                dta.setCallback(callback);
                dta.enqueue();
            }
        } else {
            return false;
        }

        // update recording list
        RecordingManager.getInstance().synchronize();
        return true;
    }

    @Override
    public Response getResponse() {
        return response;
    }

    @Override
    public String getDescription() {
        return "Delete recording " + recording;
    }
}