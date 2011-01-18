/* $Id: ModifyTimerAction.java,v 1.4 2011-01-18 13:13:54 hampelratte Exp $
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

import lazybones.Timer;
import lazybones.VDRConnection;
import lazybones.actions.responses.TimersOutOfSync;

import org.hampelratte.svdrp.commands.LSTT;
import org.hampelratte.svdrp.commands.MODT;
import org.hampelratte.svdrp.responses.highlevel.VDRTimer;
import org.hampelratte.svdrp.util.TimerParser;

public class ModifyTimerAction extends VDRAction {

    private Timer newTimer;
    private Timer oldTimer;
    
    public ModifyTimerAction(Timer newTimer, Timer oldTimer) {
        this.newTimer = newTimer;
        this.oldTimer = oldTimer;
    }
    
    boolean execute() {
        response = VDRConnection.send(new LSTT(oldTimer.getID()));
        if(response != null && response.getCode() == 250) {
            List<VDRTimer> list = TimerParser.parse(response.getMessage());
            if(list.size() <= 0) {
                response = new TimersOutOfSync();
                return false;
            }
            
            VDRTimer vdrTimer = (VDRTimer) list.get(0);
            if(vdrTimer.getUniqueKey().equals(oldTimer.getUniqueKey())) {
                response = VDRConnection.send(new MODT(oldTimer.getID(), newTimer));
                if(response.getCode() == 250) {
                    return true;
                } else {
                    return false;
                }
            } else {
                response = new TimersOutOfSync();
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Modify timer " + oldTimer;
    }
}