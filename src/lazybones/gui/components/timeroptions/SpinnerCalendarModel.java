/* $Id: SpinnerCalendarModel.java,v 1.1 2010-10-08 15:44:37 hampelratte Exp $
 * 
 * Copyright (c), Henrik Niehaus & Lazy Bones development team
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
package lazybones.gui.components.timeroptions;

import java.util.Calendar;

import javax.swing.AbstractSpinnerModel;

public class SpinnerCalendarModel extends AbstractSpinnerModel {

    private Calendar value = Calendar.getInstance();
    private int calendarField = Calendar.MINUTE;
    
    public SpinnerCalendarModel() {}
    
    public SpinnerCalendarModel(Calendar value) {
        this.value = value;
    }
    
    public SpinnerCalendarModel(Calendar value, int calendarField) {
        this.value = value;
        this.calendarField = calendarField;
    }
    
    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if(value instanceof Calendar) {
            this.value = (Calendar) value;
            fireStateChanged();
        } else {
            throw new IllegalArgumentException("Calendar expected");
        }
    }

    @Override
    public Object getNextValue() {
        Calendar next = Calendar.getInstance();
        next.setTimeInMillis(value.getTimeInMillis());
        next.add(calendarField, 1);
        return next;
    }

    @Override
    public Object getPreviousValue() {
        Calendar prev = Calendar.getInstance();
        prev.setTimeInMillis(value.getTimeInMillis());
        prev.add(calendarField, -1);
        return prev;
    }

    public void setCalendarField(int calendarField) {
        this.calendarField = calendarField;
    }
}
