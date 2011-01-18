/* $Id: StarttimeCriterion.java,v 1.2 2011-01-18 13:13:56 hampelratte Exp $
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
package lazybones.programmanager.evaluation;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import lazybones.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devplugin.Program;

/**
 * Compares the start time of a {@link Program} and {@link Timer} and returns
 * the percentage of equality
 * 
 * @author <a href="hampelratte@users.berlios.de">hampelratte@users.berlios.de</a>
 */
public class StarttimeCriterion extends AbstractCriterion {

    private static transient Logger logger = LoggerFactory.getLogger(StarttimeCriterion.class);
    
    public int evaluate(Program prog, Timer timer) {
        // program start time
        Calendar progStartCal = prog.getDate().getCalendar();
        progStartCal.set(Calendar.HOUR_OF_DAY, prog.getHours());
        progStartCal.set(Calendar.MINUTE, prog.getMinutes());
        progStartCal.set(Calendar.SECOND, 0);
        long progInMillis = progStartCal.getTimeInMillis();
        
        // timer start time
        Timer bufferless = timer.getTimerWithoutBuffers();
        long timerInMillis = bufferless.getStartTime().getTimeInMillis();
        
        int diffInMin = (int) TimeUnit.MILLISECONDS.toMinutes((Math.abs(progInMillis - timerInMillis)));
        
        // return 100% - the difference in minutes
        int percentage = 100 - diffInMin;
        logger.trace("StarttimeCriterion for timer {} and prog {}: {}", new Object[] {timer.getTitle(), prog.getTitle(), percentage});
        return percentage;
    }

}
