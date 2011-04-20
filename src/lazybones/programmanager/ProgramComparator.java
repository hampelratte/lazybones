/* $Id: ProgramComparator.java,v 1.3 2011-04-20 12:09:13 hampelratte Exp $
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
package lazybones.programmanager;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import devplugin.Program;

/**
 * Compares two objects, which implement the interface Program
 * 
 * @author <a href="hampelratte@users.sf.net>hampelratte@users.sf.net </a>
 * 
 */
public class ProgramComparator implements Comparator<Program> {

    public int compare(Program prog1, Program prog2) {
        if (!prog1.getChannel().getId().equals(prog2.getChannel().getId()))
            return -1;

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.YEAR, prog1.getDate().getYear());
        cal.set(Calendar.MONTH, prog1.getDate().getMonth());
        cal.set(Calendar.DAY_OF_MONTH, prog1.getDate().getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, prog1.getHours());
        cal.set(Calendar.MINUTE, prog1.getMinutes());

        Calendar thisCal = GregorianCalendar.getInstance();
        thisCal.set(Calendar.YEAR, prog2.getDate().getYear());
        thisCal.set(Calendar.MONTH, prog2.getDate().getMonth());
        thisCal.set(Calendar.DAY_OF_MONTH, prog2.getDate().getDayOfMonth());
        thisCal.set(Calendar.HOUR_OF_DAY, prog2.getHours());
        thisCal.set(Calendar.MINUTE, prog2.getMinutes());

        if (cal.get(Calendar.YEAR) < thisCal.get(Calendar.YEAR)) {
            return -1;
        } else if (cal.get(Calendar.YEAR) > thisCal.get(Calendar.YEAR)) {
            return 1;
        }
        // at this point: year is equal

        if (cal.get(Calendar.MONTH) < thisCal.get(Calendar.MONTH)) {
            return -1;
        } else if (cal.get(Calendar.MONTH) > thisCal.get(Calendar.MONTH)) {
            return 1;
        }
        // at this point: month is equal

        if (cal.get(Calendar.DAY_OF_MONTH) < thisCal.get(Calendar.DAY_OF_MONTH)) {
            return -1;
        } else if (cal.get(Calendar.DAY_OF_MONTH) > thisCal.get(Calendar.DAY_OF_MONTH)) {
            return 1;
        }
        // at this point: day is equal

        if (cal.get(Calendar.HOUR_OF_DAY) < thisCal.get(Calendar.HOUR_OF_DAY)) {
            return -1;
        } else if (cal.get(Calendar.HOUR_OF_DAY) > thisCal.get(Calendar.HOUR_OF_DAY)) {
            return 1;
        }
        // at this point: hour is equal

        if (cal.get(Calendar.MINUTE) < thisCal.get(Calendar.MINUTE)) {
            return -1;
        } else if (cal.get(Calendar.MINUTE) > thisCal.get(Calendar.MINUTE)) {
            return 1;
        }
        // at this point: minute is equal

        return 0;
    }
}