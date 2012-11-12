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

import lazybones.VDRCallback;

import org.hampelratte.svdrp.Response;

/**
 * Represents a SVDRP command or a set of SVDRP commands, which are bundled together to execute a task.
 * 
 * @author <a href="hampelratte@users.sf.net">hampelratte@users.sf.net</a>
 */
public abstract class VDRAction {

    protected boolean success;

    protected VDRCallback callback;

    protected Response response;

    /**
     * Default constructor
     */
    public VDRAction() {
    }

    /**
     * @param callback
     *            the {@link VDRCallback} to call, when this VDRAction has finished
     */
    public VDRAction(VDRCallback callback) {
        this.callback = callback;
    }

    /**
     * <b>Don't call this method directly.</b> Use the {@link CommandQueue} instead.
     * 
     * Executes this VDRAction. Here you can execute several Commands to execute one task.
     * 
     * @return true, if the action was successfully executed
     */
    abstract boolean execute();

    // TODO i18n for all inheriting implementations
    public abstract String getDescription();

    /**
     * Returns the response of VDR
     * 
     * @return the response of VDR
     */
    public Response getResponse() {
        return response;
    }

    public VDRCallback getCallback() {
        return callback;
    }

    /**
     * If you want to get informed, when the execution of this VDRAction finished, you have to pass a callback
     * 
     * @param callback
     *            the {@link VDRCallback} to call, when this VDRAction has finished
     */
    public void setCallback(VDRCallback callback) {
        this.callback = callback;
    }

    /**
     * Calls the VDRCallback after the execution of this action
     */
    protected void callback() {
        if (callback != null) {
            callback.receiveResponse(this, response);
        }
    }

    /**
     * Returns, if this VDRAction was successfully executed
     * 
     * @return if this VDRAction was successfully executed
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @see VDRAction#isSuccess()
     * @param success
     *            true, if the VDRAction was successfully executed
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Adds this action to the global {@link CommandQueue}
     */
    public void enqueue() {
        CommandQueue.getInstance().add(this);
    }

    @Override
    public String toString() {
        return getDescription();
    }

}