/* $Id: SpinnerTimeModel.java,v 1.1 2006-03-06 19:51:51 hampelratte Exp $
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
package lazybones.gui;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lazybones.Time;

public class SpinnerTimeModel implements SpinnerModel {

    private Time time = new Time();

    private Vector changeListener = new Vector();

    public Object getNextValue() {
        time.increase();
        fireStateChanged();
        return time;
    }

    public Object getPreviousValue() {
        time.decrease();
        fireStateChanged();
        return time;
    }

    public Object getValue() {
        return time;
    }

    public void setValue(Object o) {
        if (o instanceof Time) {
            time = (Time) o;
            fireStateChanged();
        }
    }

    public void addChangeListener(ChangeListener arg0) {
        changeListener.add(arg0);
    }

    public void removeChangeListener(ChangeListener arg0) {
        changeListener.remove(arg0);
    }

    private void fireStateChanged() {
        Iterator it = changeListener.iterator();
        while (it.hasNext()) {
            ChangeListener cl = (ChangeListener) it.next();
            cl.stateChanged(new ChangeEvent(this));
        }
    }
}