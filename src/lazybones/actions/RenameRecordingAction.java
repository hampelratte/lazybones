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

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.MOVR;
import org.hampelratte.svdrp.responses.highlevel.Recording;

import lazybones.VDRCallback;
import lazybones.VDRConnection;

public class RenameRecordingAction extends VDRAction {

    private final String newName;
    private final Recording recording;

    public RenameRecordingAction(Recording recording, String newName) {
        this(recording, newName, null);
    }

    public RenameRecordingAction(Recording recording, String newName, VDRCallback<?> callback) {
        super(callback);
        this.recording = recording;
        this.newName = newName;
    }

    @Override
    boolean execute() {
        int recordingNumber = recording.getId();
        response = VDRConnection.send(new MOVR(recordingNumber, newName));
        return response.getCode() == 250;
    }

    @Override
    public Response getResponse() {
        return response;
    }

    @Override
    public String getDescription() {
        return "Rename recording " + recording;
    }
}