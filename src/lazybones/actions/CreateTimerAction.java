/* $Id: CreateTimerAction.java,v 1.1 2007-10-14 19:04:44 hampelratte Exp $
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

import lazybones.LazyBones;
import lazybones.Logger;
import lazybones.Timer;
import lazybones.TimerManager;
import lazybones.VDRConnection;
import lazybones.actions.responses.ConnectionProblem;

import org.hampelratte.svdrp.commands.NEWT;
import org.hampelratte.svdrp.responses.R250;

public class CreateTimerAction extends VDRAction {

    private Logger logger = Logger.getLogger();
    
    private Timer timer;
    
    public CreateTimerAction(Timer timer) {
        this.timer = timer;
    }
    
    @Override
    boolean execute() {
        response = VDRConnection.send(new NEWT(timer));
        
        if (response == null) {
            response = new ConnectionProblem();
        }
        
        if (response instanceof R250) {
            // since we dont have the ID of the new timer, we
            // have to get the whole timer list again :-(
            TimerManager.getInstance().synchronize();
            return true;
        } else {
            logger.log(LazyBones.getTranslation("couldnt_create",
                    "Couldn\'t create timer:")
                    + " " + response.getMessage(), Logger.OTHER,
                    Logger.ERROR);
            return false;
        }
    }

    @Override
    public String getDescription() {
        // TODO i18n
        return "Create timer " + timer;
    }

}
