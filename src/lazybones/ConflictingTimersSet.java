/* $Id: ConflictingTimersSet.java,v 1.1 2007-01-28 15:06:03 hampelratte Exp $
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

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class ConflictingTimersSet<E> extends HashSet<E> {

    private Calendar conflictStartTime;
    private Calendar conflictEndTime;
    
    public ConflictingTimersSet() {}
    
    public ConflictingTimersSet(Collection<E> runningEvents) {
        super(runningEvents);
    }
    
    public Calendar getConflictEndTime() {
        return conflictEndTime;
    }

    public void setConflictEndTime(Calendar conflictEndTime) {
        this.conflictEndTime = conflictEndTime;
        updateTimersEndTime();
    }

    public Calendar getConflictStartTime() {
        return conflictStartTime;
    }

    public void setConflictStartTime(Calendar conflictStartTime) {
        this.conflictStartTime = conflictStartTime;
        updateTimersStartTime();
    }
    
    private void updateTimersEndTime() {
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            timer.setConflictEndTime(conflictEndTime);
        }
    }
    
    private void updateTimersStartTime() {
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            Timer timer = (Timer) iter.next();
            timer.setConflictStartTime(conflictStartTime);
        }
    }
}